package org.jbpm.units;

import java.util.Map;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManagerFactory;
import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.jbpm.units.signals.Event;

// for technical reasons, we cannot instantiate this explicitly, nor
// can we set the ProcessUnitSubsystem directly in the constructor.
// This is an artificial KieConfiguration limitation, which takes a property
// instead of a lambda/factory instance.
// todo: consider whether we can avoid this limitation if the above becomes false

public class WorkItemManager extends DefaultWorkItemManager {

    public static class Factory implements WorkItemManagerFactory {

        @Override
        public org.drools.core.process.instance.WorkItemManager createWorkItemManager(InternalKnowledgeRuntime internalKnowledgeRuntime) {
            return new WorkItemManager(internalKnowledgeRuntime);
        }
    }

    private ProcessUnitSubsystem subsystem;

    public WorkItemManager(InternalKnowledgeRuntime session) {
        super(session);
    }

    public void setProcessUnitSubsystem(ProcessUnitSubsystem subsystem) {
        this.subsystem = subsystem;
    }

    @Override
    public void completeWorkItem(long id, Map<String, Object> results) {
        WorkItem workItem = super.getWorkItem(id);
        workItem.setResults(results);
        workItem.setState(2);

        Event signal = new Event(
                workItem.getProcessInstanceId(),
                "workItemCompleted",
                workItem);
        subsystem.signal(signal);
        // fixme: must call this.workItems.remove(id);
    }
}
