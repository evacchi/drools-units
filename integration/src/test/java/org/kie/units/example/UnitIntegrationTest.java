package org.kie.units.example;

import org.drools.units.RuleUnit;
import org.drools.units.RuleUnitSupport;
import org.jbpm.units.ProcessUnit;
import org.jbpm.units.ProcessUnitSupport;
import org.junit.Test;
import org.kie.api.DynamicUnitSupport;
import org.kie.api.KieUnitExecutor;

public class UnitIntegrationTest {

    @Test
    public void runtimeUnitSupport() {
        KieUnitExecutor kieUnitExecutor = KieUnitExecutor.create(
                DynamicUnitSupport.register(
                        ProcessUnitSupport::new,
                        RuleUnitSupport::new));

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
