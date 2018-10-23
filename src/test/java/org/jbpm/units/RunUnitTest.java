package org.jbpm.units;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.KieUnitExecutor;
import org.kie.api.runtime.KieSession;

public class RunUnitTest {

    @Test
    public void runUnit() {

        KieSession kieSession =
                KieServices.Factory.get()
                        .getKieClasspathContainer()
                        .getKieBase()
                        .newKieSession();

        HelloUnit u = new HelloUnit();

        KieUnitExecutor
                .create(kieSession)
                .run(u);
    }
}
