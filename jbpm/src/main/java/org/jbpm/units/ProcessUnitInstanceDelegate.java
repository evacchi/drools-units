package org.jbpm.units;

import java.util.Collection;
import java.util.Collections;

import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.units.ProcessUnitInstanceSignal.Event;
import org.jbpm.units.internal.BeanMap;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSessionSignal;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * A UnitSession for BPM Processes, delegating internally to
 * jBPM's ProcessInstance
 */
class ProcessUnitInstanceDelegate implements ProcessUnitInstance {

    private final KieSession session;
    private final ProcessUnit processUnit;
    private final ProcessInstance processInstance;
    private final BeanMap beanMap;

    public ProcessUnitInstanceDelegate(
            ProcessUnit processUnit,
            KieSession session) {
        String processId = processUnit.getUnitIdentity().toString();
        this.processUnit = processUnit;
        this.session = session;
        this.beanMap = new BeanMap(processUnit);
        this.processInstance =
                session.createProcessInstance(
                        processId, beanMap);
    }

    public ProcessUnit unit() {
        return processUnit;
    }

    public void start() {
        processUnit.onStart();
        session.startProcessInstance(processInstance.getId());
        VariableScopeInstance variableScope = getVariableScopeInstance();
        beanMap.putAll(variableScope.getVariables());
    }

    @Override
    public void halt() {
        session.abortProcessInstance(id());
    }

    @Override
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
        return State.Active;
    }

    private VariableScopeInstance getVariableScopeInstance() {
        RuleFlowProcessInstance wpi = (RuleFlowProcessInstance) this.processInstance;
        return (VariableScopeInstance) wpi.getContextInstance("VariableScope");
    }
}
