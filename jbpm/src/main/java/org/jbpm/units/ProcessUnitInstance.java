package org.jbpm.units;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;

import org.jbpm.units.internal.VariableBinder;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.api.UnitInstance;
import org.kie.api.UnitInstanceSignal;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

import static org.kie.api.runtime.process.ProcessInstance.STATE_ABORTED;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
import static org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;
import static org.kie.api.runtime.process.ProcessInstance.STATE_PENDING;
import static org.kie.api.runtime.process.ProcessInstance.STATE_SUSPENDED;

/**
 * A UnitSession for BPM Processes, delegating internally to
 * jBPM's ProcessInstance
 */
public class ProcessUnitInstance implements UnitInstance {

    private final ProcessUnitSubsystem parent;
    private final Deque<UnitInstanceSignal> pendingSignals;
    private final KieSession session;
    private final ProcessUnit processUnit;
    private final VariableBinder variableBinder;
    private final WorkflowProcessInstance processInstance;
    State state;

    public ProcessUnitInstance(
            ProcessUnit processUnit,
            ProcessUnitSubsystem parent,
            KieSession session) {
        String processId = processUnit.getUnitIdentity().toString();
        this.processUnit = processUnit;
        this.session = session;
        this.parent = parent;
        this.variableBinder = new VariableBinder(processUnit);
        this.processInstance =
                (WorkflowProcessInstance) session.createProcessInstance(
                        processId, variableBinder.asMap());

        this.state = State.Created;
        this.processUnit.onCreate();
        pendingSignals = new ArrayDeque<>();
    }

    public ProcessUnit unit() {
        return processUnit;
    }

    public void start() {
        switch (state) {
            case Created:
                run();
                break;
            case Suspended:
                resume();
                break;
            default:
                throw new IllegalStateException(state.name());
        }
        variableBinder.updateBindings(processInstance);
    }

    private void run() {
        try {
            this.state = State.Entering;
            processUnit.onEnter();
            session.startProcessInstance(processInstance.getId());
            if (processInstance.getState() == ProcessInstance.STATE_ACTIVE) {
                this.state = State.Suspended;
                processUnit.onSuspend();
            }
        } catch (Throwable t) {
            this.state = State.Faulted;
            processUnit.onFault(t);
            this.state = State.Completed;
            processUnit.onEnd();
            parent.terminated(this);
        }
    }

    @Override
    public void halt() {
        session.abortProcessInstance(id());
    }

    @Override
    public void suspend() {
        state = State.Suspended;
        processUnit.onSuspend();
    }

    @Override
    public void resume() {
        state = State.Resuming;
        processUnit.onResume();
        state = State.ReEntering;
        processUnit.onReEnter();
        this.state = State.Running;
        UnitInstanceSignal sig = pendingSignals.poll();
        while (sig != null) {
            sig.exec(this);
            sig = pendingSignals.poll();
        }
    }

    public long id() {
        return processInstance.getId();
    }

    @Override
    public void signal(UnitInstanceSignal signal) {
        if (signal instanceof ProcessUnitInstanceSignal) {
            if (state == State.Suspended) {
                pendingSignals.add(signal);
            } else {
                signal.exec(this);
            }
        }
        // drop anything else
    }

    @Override
    public void yield(UnitInstance next) {
        // unsupported
    }

    @Override
    public Collection<UnitInstance> references() {
        // processes that point at this (currently unsupported)
        return Collections.emptyList();
    }

    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    @Override
    public State state() {
        return state;
    }

    void transitionToState(UnitInstance.State next) {
        if (next == this.state) {
            return;
        }
        this.state = next;
        switch (next) {
            case Created:
                processUnit.onCreate();
                break;
            case Entering:
                processUnit.onEnter();
                break;
            case Running:
                break;
            case Suspended:
                processUnit.onSuspend();
                break;
            case Exiting:
                processUnit.onExit();
                break;
            case Completed:
                processUnit.onEnd();
                break;
            case Resuming:
                processUnit.onResume();
                break;
            case ReEntering:
                processUnit.onReEnter();
                break;
            case Aborting:
                processUnit.onAbort();
                break;
            case Faulted:
                processUnit.onFault(null);
                break;
        }
    }

    UnitInstance.State stateOf(int state) {
        switch (state) {
            case STATE_PENDING:
                return UnitInstance.State.Created;
            case STATE_ACTIVE:
                return UnitInstance.State.Running;
            case STATE_COMPLETED:
                return UnitInstance.State.Completed;
            case STATE_ABORTED:
                return UnitInstance.State.Completed; // fixme shall we support an Aborted state?
            case STATE_SUSPENDED:
                return UnitInstance.State.Suspended;
            default:
                throw new IllegalStateException(String.valueOf(state));
        }
    }

    void syncInternalState() {
        State state = stateOf(processInstance.getState());
        transitionToState(state);
    }
}
