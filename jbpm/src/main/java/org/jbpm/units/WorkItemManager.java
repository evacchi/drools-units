package org.jbpm.units;

import java.util.Map;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManagerFactory;
import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;

// for technical reasons, this must be broadcast to all children
// of the executor, recursively, because we cannot instantiate it
// from the context of the ProcessUnitSubsystem... but this is
// an artificial KieConfiguration limitation, which takes a property
// instead of a lambda/factory instance.
// todo: consider whether we can avoid this limitation if the above becomes false

public class WorkItemManager extends DefaultWorkItemManager {

    private UnitExecutor executor;

    public static class Factory implements WorkItemManagerFactory {

        @Override
        public org.drools.core.process.instance.WorkItemManager createWorkItemManager(InternalKnowledgeRuntime internalKnowledgeRuntime) {
            return new WorkItemManager(internalKnowledgeRuntime);
        }
    }

    public WorkItemManager(InternalKnowledgeRuntime session) {
        super(session);
    }

    public void setUnitExecutor(UnitExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void completeWorkItem(long id, Map<String, Object> results) {
        WorkItem workItem = super.getWorkItem(id);
        WorkItemCompletedSignal signal = new WorkItemCompletedSignal(workItem, results);

        executor.signal(signal);
    }
}

class WorkItemCompletedSignal implements ProcessUnitInstanceSignal {

    private final WorkItem workItem;
    private final Map<String, Object> results;

    public WorkItemCompletedSignal(WorkItem workItem, Map<String, Object> results) {
        this.workItem = workItem;
        this.results = results;
    }

    @Override
    public void exec(UnitInstance unitInstance) {
        ProcessUnitInstance pui = (ProcessUnitInstance) unitInstance;
        if (pui.id() != workItem.getProcessInstanceId()) {
            return;
        }
        workItem.setResults(results);
        workItem.setState(2);
        pui.getProcessInstance().signalEvent("workItemCompleted", workItem);
        // fixme workItems.remove(workItem); // fixme this does not remove it from storage, it's a copy of the collection
    }
}
