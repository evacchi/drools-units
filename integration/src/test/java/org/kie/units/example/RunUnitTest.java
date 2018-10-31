package org.kie.units.example;

import org.drools.units.RuleUnitSupport;
import org.jbpm.units.ProcessUnitSupport;
import org.junit.Test;
import org.kie.api.KieUnitExecutor;
import org.kie.api.RuntimeUnitSupport;

public class RunUnitTest {

    @Test
    public void runHelloUnit() {
        HelloUnit u = new HelloUnit();
        KieUnitExecutor kieUnitExecutor = KieUnitExecutor.create(
                RuntimeUnitSupport.register(
                        ProcessUnitSupport.get(),
                        RuleUnitSupport.get()));

        kieUnitExecutor.run(u);
    }

}
