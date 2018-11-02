package org.jbpm.units.internal;

import java.util.Map;

import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.kie.api.runtime.process.ProcessInstance;

public class VariableBinder {
    private final BeanMap beanMap;

    public VariableBinder(Object beanMap) {
        this.beanMap = new BeanMap(beanMap);
    }

    public Map<String, Object> asMap() {
        return beanMap;
    }

    public void updateBindings(ProcessInstance processInstance) {
        VariableScopeInstance variableScopeInstance = getVariableScopeInstance(processInstance);
        beanMap.putAll(variableScopeInstance.getVariables());

    }

    private VariableScopeInstance getVariableScopeInstance(ProcessInstance processInstance) {
        RuleFlowProcessInstance wpi = (RuleFlowProcessInstance) processInstance;
        return (VariableScopeInstance) wpi.getContextInstance("VariableScope");
    }
}
