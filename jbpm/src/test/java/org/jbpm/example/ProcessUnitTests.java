package org.jbpm.example;

import org.jbpm.units.ProcessUnitSupport;
import org.junit.Test;
import org.kie.api.DynamicUnitSupport;
import org.kie.api.KieUnitExecutor;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.api.UnitInstance.State.Completed;
import static org.kie.api.UnitInstance.State.Created;
import static org.kie.api.UnitInstance.State.Entering;
import static org.kie.api.UnitInstance.State.Exiting;
import static org.kie.api.UnitInstance.State.Faulted;
import static org.kie.api.UnitInstance.State.Running;

public class ProcessUnitTests {

    @Test
    public void lifecycleShouldMatch() {
        HelloUnit u = new HelloUnit();
        KieUnitExecutor.create(
                DynamicUnitSupport.register(
                        ProcessUnitSupport::new)).run(u);
        assertEquals(asList(Created, Entering, Running, Exiting, Completed),
                     u.stateSequence);
    }

    @Test
    public void failedLifecycleShouldMatch() {
        FailingUnit u = new FailingUnit();
        KieUnitExecutor.create(
                DynamicUnitSupport.register(
                        ProcessUnitSupport::new)).run(u);
        assertEquals(asList(Created, Entering, Running, Faulted, Completed),
                     u.stateSequence);
    }

    @Test
    public void resultShouldBeSetToPojo() {
        HelloVariableUnit u = new HelloVariableUnit(10);

        KieUnitExecutor
                .create(ProcessUnitSupport::new)
                .run(u);

        assertEquals(u.getCount(), 100);
    }
}
