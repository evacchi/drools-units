package org.jbpm.units;

import java.util.Map;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManagerFactory;
import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.kie.api.UnitInstance;

public class WorkItemManager extends DefaultWorkItemManager {

    public static class Factory implements WorkItemManagerFactory {
        @Override
        public org.drools.core.process.instance.WorkItemManager createWorkItemManager(InternalKnowledgeRuntime internalKnowledgeRuntime) {
            return new WorkItemManager(internalKnowledgeRuntime);
        }
    }

    private ProcessUnitSupport processUnitSupport;

    public WorkItemManager(InternalKnowledgeRuntime session) {
        super(session);
    }

    public void setProcessUnitSupport(ProcessUnitSupport processUnitSupport) {
        this.processUnitSupport = processUnitSupport;
    }

    @Override
    public void completeWorkItem(long id, Map<String, Object> results) {
        WorkItem workItem = super.getWorkItem(id);

        ProcessUnitInstance pui =
                processUnitSupport.getProcessUnitInstance(
                        workItem.getProcessInstanceId());
        pui.state = UnitInstance.State.Resuming;

        pui.signal(new WorkItemCompletedSignal(workItem, results));
    }
}

class WorkItemCompletedSignal implements UnitInstance.Signal {

    private final WorkItem workItem;
    private final Map<String, Object> results;

    public WorkItemCompletedSignal(WorkItem workItem, Map<String, Object> results) {
        this.workItem = workItem;
        this.results = results;
    }

    @Override
    public void exec(UnitInstance unitInstance) {
        ProcessUnitInstance pui = (ProcessUnitInstance) unitInstance;
        workItem.setResults(results);
        workItem.setState(2);
        pui.getProcessInstance().signalEvent("workItemCompleted", workItem);
        // fixme workItems.remove(workItem); // fixme this does not remove it from storage, it's a copy of the collection
    }
}
