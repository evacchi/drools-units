package org.jbpm.example;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.KieUnitExecutor;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class RunUnitTest {

    @Test
    public void runHelloUnit() {
        KieSession kieSession = kieSession();
        HelloUnit u = new HelloUnit();
        KieUnitExecutor
                .create(kieSession)
                .run(u);
    }

    @Test
    public void runHelloVariableUnit() {
        KieSession kieSession = kieSession();
        HelloVariableUnit u = new HelloVariableUnit(10);
        KieUnitExecutor
                .create(kieSession)
                .run(u);

        assertEquals(u.getCount(), 100);
    }

    private KieSession kieSession() {
        return KieServices.Factory.get()
                .getKieClasspathContainer()
                .getKieBase()
                .newKieSession();
    }
}
