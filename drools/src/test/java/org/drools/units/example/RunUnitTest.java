package org.drools.units.example;

import org.drools.units.RuleUnitSupport;
import org.junit.Test;
import org.kie.api.KieUnitExecutor;

public class RunUnitTest {

    @Test
    public void runHelloUnit() throws Exception {

        HelloUnit u = new HelloUnit("Ed");

        KieUnitExecutor kieUnitExecutor =
                KieUnitExecutor.create(RuleUnitSupport::new);

        kieUnitExecutor.run(u);
    }
}
