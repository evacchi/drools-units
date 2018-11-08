package org.jbpm.example;

import org.jbpm.example.units.FailingUnit;
import org.jbpm.example.units.HelloUnit;
import org.jbpm.example.units.HelloVariableUnit;
import org.jbpm.example.units.PausingUnit;
import org.jbpm.units.ProcessUnitInstance;
import org.jbpm.units.ProcessUnitSupport;
import org.junit.Test;
import org.kie.api.DynamicUnitSupport;
import org.kie.api.KieUnitExecutor;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.api.UnitInstance.State.Completed;
import static org.kie.api.UnitInstance.State.Created;
import static org.kie.api.UnitInstance.State.Entering;
import static org.kie.api.UnitInstance.State.Exiting;
import static org.kie.api.UnitInstance.State.Faulted;
import static org.kie.api.UnitInstance.State.ReEntering;
import static org.kie.api.UnitInstance.State.Running;
import static org.kie.api.UnitInstance.State.Suspended;

public class ProcessUnitTests {

    @Test
    public void lifecycleShouldMatch() {
        HelloUnit u = new HelloUnit();
        KieUnitExecutor.create(
                DynamicUnitSupport.register(
                        ProcessUnitSupport::new)).run(u);
        assertEquals(asList(Created, Entering, Exiting, Completed),
                     u.stateSequence);
    }

    @Test
    public void failedLifecycleShouldMatch() {
        FailingUnit u = new FailingUnit();
        KieUnitExecutor.create(
                DynamicUnitSupport.register(
                        ProcessUnitSupport::new)).run(u);
        assertEquals(asList(Created, Entering, Faulted, Completed),
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

    private static class MyWorkItemHandler implements WorkItemHandler {

        @Override
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            System.out.println("execute work item");
        }

        @Override
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            System.out.println("abort work item");
        }
    }
}
