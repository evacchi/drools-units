package org.jbpm.example;

import org.jbpm.units.ProcessUnitSupport;
import org.junit.Test;
import org.kie.api.KieUnitExecutor;
import org.kie.api.DynamicUnitSupport;

import static org.junit.Assert.assertEquals;

public class RunUnitTest {

    @Test
    public void runHelloUnit() {
        HelloUnit u = new HelloUnit();
        KieUnitExecutor.create(
                DynamicUnitSupport.register(
                        ProcessUnitSupport::new)).run(u);
    }

    @Test
    public void runHelloVariableUnit() {
        HelloVariableUnit u = new HelloVariableUnit(10);
        KieUnitExecutor
                .create(ProcessUnitSupport::new)
                .run(u);

        assertEquals(u.getCount(), 100);
    }
}
