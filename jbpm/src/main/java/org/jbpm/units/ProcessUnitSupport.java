package org.jbpm.units;

import java.util.Optional;
import java.util.Properties;

import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSupport;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.internal.LegacySessionWrapper;
import org.kie.api.runtime.KieSession;

import static org.kie.api.runtime.process.ProcessInstance.STATE_ABORTED;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
import static org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;
import static org.kie.api.runtime.process.ProcessInstance.STATE_PENDING;
import static org.kie.api.runtime.process.ProcessInstance.STATE_SUSPENDED;

public class ProcessUnitSupport implements UnitSupport {

    private static final Properties props;

    static {
        props = new Properties();
        props.put(
                "drools.workItemManagerFactory",
                WorkItemManager.Factory.class.getTypeName());
    }

    private final KieSession session;
    private final UnitExecutor executor;
    private final WorkItemManager workItemManager;

    public ProcessUnitSupport(UnitExecutor executor) {
        this.session = ((LegacySessionWrapper) executor).getSession();
        this.executor = executor;
        StatefulKnowledgeSessionImpl session =
                (StatefulKnowledgeSessionImpl) this.session;
        session.getSessionConfiguration().addDefaultProperties(props);
        this.workItemManager = ((WorkItemManager) this.session.getWorkItemManager());

        this.session.addEventListener(processEventListener);
        workItemManager.setUnitExecutor(executor);
    }

    public Optional<UnitInstance> createInstance(UnitInstance.Proto proto) {
        if (proto.unit() instanceof ProcessUnit) {
            ProcessUnitInstance instance =
                    new ProcessUnitInstance((ProcessUnit) proto.unit(), session);
            return Optional.of(instance);
        } else {
            return Optional.empty();
        }
    }

    private final ProcessUnitInstanceSignal TransitionToInternalState = (unitInstance) -> {
        ProcessUnitInstance instance = (ProcessUnitInstance) unitInstance;
        UnitInstance.State state = stateOf(instance.getProcessInstance().getState());
        transitionToState(instance, state);
    };

    private final ProcessEventListener processEventListener = new DefaultProcessEventListener() {
        @Override
        public void beforeProcessCompleted(ProcessCompletedEvent event) {
            TransitionToState signal = new TransitionToState(UnitInstance.State.Exiting);
            executor.signal(signal);
        }

        @Override
        public void afterProcessCompleted(ProcessCompletedEvent event) {
            TransitionToState signal = new TransitionToState(UnitInstance.State.Completed);
            executor.signal(signal);
        }

        @Override
        public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
            executor.signal(TransitionToInternalState);
        }

        @Override
        public void afterNodeLeft(ProcessNodeLeftEvent event) {
            executor.signal(TransitionToInternalState);
        }

    };

    private static void transitionToState(ProcessUnitInstance instance, UnitInstance.State next) {
        if (next == instance.state) {
            return;
        }
        instance.state = next;
        ProcessUnit unit = instance.unit();
        switch (next) {
            case Created:
                unit.onCreate();
                break;
            case Entering:
                unit.onEnter();
                break;
            case Running:
                break;
            case Suspended:
                unit.onSuspend();
                break;
            case Exiting:
                unit.onExit();
                break;
            case Completed:
                unit.onEnd();
                break;
            case Resuming:
                unit.onResume();
                break;
            case ReEntering:
                unit.onReEnter();
                break;
            case Aborting:
                unit.onAbort();
                break;
            case Faulted:
                unit.onFault(null);
                break;
        }
    }

    private static UnitInstance.State stateOf(int state) {
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

    private static class TransitionToState implements ProcessUnitInstanceSignal {

        final UnitInstance.State nextState;

        public TransitionToState(UnitInstance.State nextState) {
            this.nextState = nextState;
        }

        @Override
        public void exec(UnitInstance unitInstance) {
            transitionToState((ProcessUnitInstance) unitInstance, nextState);
        }
    }
}