package org.kie.units.example;

import org.drools.units.RuleUnit;
import org.drools.units.RuleUnitSupport;
import org.jbpm.units.ProcessUnit;
import org.jbpm.units.ProcessUnitSupport;
import org.junit.Test;
import org.kie.api.KieUnitExecutor;
import org.kie.api.RuntimeUnitSupport;

public class RunUnitTest {

    @Test
    public void runHelloUnit() {
        KieUnitExecutor kieUnitExecutor = KieUnitExecutor.create(
                RuntimeUnitSupport.register(
                        ProcessUnitSupport.get(),
                        RuleUnitSupport.get()));

        ProcessUnit pu = new HelloProcessUnit("Ed");
        RuleUnit ru = new HelloRuleUnit("Ed");
        kieUnitExecutor.run(pu);
        kieUnitExecutor.run(ru);
    }

}
