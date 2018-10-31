package org.jbpm.example;

import org.drools.units.RuleUnitSupport;
import org.junit.Test;
import org.kie.api.KieUnitExecutor;

public class RunUnitTest {

    @Test
    public void runHelloUnit() {
        HelloUnit u = new HelloUnit();
        KieUnitExecutor
                .create(RuleUnitSupport.get())
                .run(u);
    }

}
