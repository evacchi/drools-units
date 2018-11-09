package org.kie.units.example;

import org.drools.units.RuleUnit;
import org.drools.units.RuleUnitSubsystem;
import org.jbpm.units.ProcessUnit;
import org.jbpm.units.ProcessUnitSubsystem;
import org.junit.Test;
import org.kie.api.DynamicUnitSupport;
import org.kie.api.KieUnitExecutor;

public class UnitIntegrationTest {

    @Test
    public void runtimeUnitSupport() {
        KieUnitExecutor kieUnitExecutor = KieUnitExecutor.create(
                DynamicUnitSupport.register(
                        ProcessUnitSubsystem::new,
                        RuleUnitSubsystem::new));

        ProcessUnit pu = new HelloProcessUnit("Ed");
        RuleUnit ru = new HelloRuleUnit("Ed");
        kieUnitExecutor.run(pu);
        kieUnitExecutor.run(ru);
    }

    @Test
    public void staticUnitSupport() {
        KieUnitExecutor kieUnitExecutor =
                KieUnitExecutor.create(GeneratedUnitSupport::new);

        ProcessUnit pu = new HelloProcessUnit("Ed");
        RuleUnit ru = new HelloRuleUnit("Ed");
        kieUnitExecutor.run(pu);
        kieUnitExecutor.run(ru);
    }
}
