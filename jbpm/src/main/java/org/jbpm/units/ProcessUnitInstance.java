package org.jbpm.units;

import java.util.Collection;
import java.util.Collections;

import org.jbpm.units.ProcessUnitInstanceSignal.Event;
import org.jbpm.units.internal.VariableBinder;
import org.kie.api.UnitInstance;
import org.kie.api.UnitInstance.State;
import org.kie.api.UnitSessionSignal;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * A UnitSession for BPM Processes, delegating internally to
 * jBPM's ProcessInstance
 */
public class ProcessUnitInstance implements UnitInstance {

    private final KieSession session;
    private final ProcessUnit processUnit;
    private final VariableBinder variableBinder;
    final ProcessInstance processInstance;
    State state;

    public ProcessUnitInstance(
            ProcessUnit processUnit,
            KieSession session) {
        String processId = processUnit.getUnitIdentity().toString();
        this.processUnit = processUnit;
        this.session = session;
        this.variableBinder = new VariableBinder(processUnit);
        this.processInstance =
                session.createProcessInstance(
                        processId, variableBinder.asMap());

        this.state = State.Created;
        this.processUnit.onCreate();
    }

    public ProcessUnit unit() {
        return processUnit;
    }

    public void start() {
        processUnit.onStart();
        session.startProcessInstance(processInstance.getId());
        variableBinder.updateBindings(processInstance);
    }

    @Override
    public void halt() {
        session.abortProcessInstance(id());
    }

    public long id() {
        return processInstance.getId();
    }

    @Override
    public void signal(UnitSessionSignal signal) {
        if (signal instanceof Event) {
            Event sig = (Event) signal;
            processInstance.signalEvent(sig.type(), sig.payload());
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

    @Override
    public State state() {
        return state;
    }
}
