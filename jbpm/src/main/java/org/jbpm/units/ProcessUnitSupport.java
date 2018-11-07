package org.jbpm.units;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.process.instance.WorkItemManagerFactory;
import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.jbpm.process.instance.event.DefaultSignalManager;
import org.jbpm.process.instance.event.SignalManager;
import org.jbpm.process.instance.event.SignalManagerFactory;
import org.jbpm.units.signals.Event;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSupport;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.internal.LegacySessionWrapper;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemHandler;

import static org.kie.api.runtime.process.ProcessInstance.STATE_ABORTED;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
import static org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;
import static org.kie.api.runtime.process.ProcessInstance.STATE_PENDING;
import static org.kie.api.runtime.process.ProcessInstance.STATE_SUSPENDED;

public class ProcessUnitSupport implements UnitSupport {

    static {
        System.setProperty("drools.processSignalManagerFactory", My.class.getTypeName());
        System.setProperty("drools.workItemManagerFactory", WIMF.class.getTypeName());
    }

    private final KieSession session;
    private final Map<Long, ProcessUnitInstance> instances;
    private final WorkItemManagerInterceptor workItemManager;

    public ProcessUnitSupport(UnitExecutor executor) {
        this.session = ((LegacySessionWrapper) executor).getSession();
        this.session.addEventListener(processEventListener);
        this.instances = new HashMap<>();
        this.workItemManager = ((WorkItemManagerInterceptor) session.getWorkItemManager());
        workItemManager.setProcessUnitSupport(this);
    }

    public Optional<UnitInstance> createInstance(UnitInstance.Proto proto) {

        if (proto.unit() instanceof ProcessUnit) {
            ProcessUnitInstance instance =
                    new ProcessUnitInstance((ProcessUnit) proto.unit(), session);
            instances.put(instance.id(), instance);
            return Optional.of(instance);
        } else {
            return Optional.empty();
        }
    }

    private final ProcessEventListener processEventListener = new ProcessEventListener() {

        @Override
        public void beforeProcessStarted(ProcessStartedEvent event) {
//            ProcessUnitInstance unitInstance =
//                    instances.get(event.getProcessInstance().getId());
//            transitionToState(unitInstance, UnitInstance.State.Entering);
        }

        @Override
        public void afterProcessStarted(ProcessStartedEvent event) {
        }

        @Override
        public void beforeProcessCompleted(ProcessCompletedEvent event) {
            ProcessUnitInstance unitInstance =
                    instances.get(event.getProcessInstance().getId());
            transitionToState(unitInstance, UnitInstance.State.Exiting);
        }

        @Override
        public void afterProcessCompleted(ProcessCompletedEvent event) {
            ProcessUnitInstance unitInstance =
                    instances.get(event.getProcessInstance().getId());
            transitionToState(unitInstance, UnitInstance.State.Completed);
        }

        @Override
        public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {

        }

        @Override
        public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
            transition(instances.get(event.getProcessInstance().getId()));
        }

        @Override
        public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        }

        @Override
        public void afterNodeLeft(ProcessNodeLeftEvent event) {
            transition(instances.get(event.getProcessInstance().getId()));
        }

        @Override
        public void beforeVariableChanged(ProcessVariableChangedEvent event) {

        }

        @Override
        public void afterVariableChanged(ProcessVariableChangedEvent event) {

        }

        private void transition(ProcessUnitInstance unitInstance) {
            if (unitInstance == null) {
                return;
            }
            UnitInstance.State state = stateOf(unitInstance.processInstance.getState());
            transitionToState(unitInstance, state);
        }
    };

    public static class My implements SignalManagerFactory {

        public My() {
            System.out.println("MyWorkItemManagerFactory");
        }

        @Override
        public SignalManager createSignalManager(InternalKnowledgeRuntime internalKnowledgeRuntime) {
            return new SignalManagerInterceptor(internalKnowledgeRuntime);
        }
    }

    public static class SignalManagerInterceptor extends DefaultSignalManager {

        private ProcessUnitSupport processUnitSupport;

        public SignalManagerInterceptor(InternalKnowledgeRuntime kruntime) {
            super(kruntime);
        }

        public void setUnitExecutor(ProcessUnitSupport processUnitSupport) {
            this.processUnitSupport = processUnitSupport;
        }

        @Override
        public void signalEvent(String type, Object event) {
//            ProcessInstance pi = (ProcessInstance) event;
//            ProcessUnitInstance processUnitInstance = processUnitSupport.instances.get(pi.getId());
//            processUnitInstance.signal((UnitExecutor.Signal.Scoped) (inst, sched) ->  );
            super.signalEvent(type, event);
        }

        @Override
        public void internalSignalEvent(String type, Object event) {
            super.internalSignalEvent(type, event);
        }

        @Override
        public void signalEvent(long processInstanceId, String type, Object event) {
            processUnitSupport.instances.get(processInstanceId).signal(new Event(type, event));
        }
    }

    public static class WIMF implements WorkItemManagerFactory {

        @Override
        public WorkItemManager createWorkItemManager(InternalKnowledgeRuntime internalKnowledgeRuntime) {
            return new WorkItemManagerInterceptor(new DefaultWorkItemManager(internalKnowledgeRuntime));
        }
    }

    public static class WorkItemManagerInterceptor implements WorkItemManager {

        private ProcessUnitSupport processUnitSupport;
        private WorkItemManager delegate;

        public WorkItemManagerInterceptor(WorkItemManager delegate) {
            this.delegate = delegate;
        }

        public void setProcessUnitSupport(ProcessUnitSupport processUnitSupport) {
            this.processUnitSupport = processUnitSupport;
        }

        @Override
        public void completeWorkItem(long id, Map<String, Object> results) {
            WorkItem workItem = delegate.getWorkItem(id);

            ProcessUnitInstance pui = processUnitSupport.instances.get(workItem.getProcessInstanceId());
            pui.state = UnitInstance.State.Resuming;

            pui.signal(new WorkItemCompletedSignal(workItem, results, pui, id));
        }

        @Override
        public void abortWorkItem(long id) {
            delegate.abortWorkItem(id);
        }

        @Override
        public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
            delegate.registerWorkItemHandler(workItemName, handler);
        }

        @Override
        public void internalExecuteWorkItem(WorkItem workItem) {
            delegate.internalExecuteWorkItem(workItem);
        }

        @Override
        public void internalAddWorkItem(WorkItem workItem) {
            delegate.internalAddWorkItem(workItem);
        }

        @Override
        public void internalAbortWorkItem(long l) {
            delegate.internalAbortWorkItem(l);
        }

        @Override
        public Set<WorkItem> getWorkItems() {
            return delegate.getWorkItems();
        }

        @Override
        public WorkItem getWorkItem(long l) {
            return delegate.getWorkItem(l);
        }

        @Override
        public void clear() {
            delegate.clear();
        }

        @Override
        public void signalEvent(String s, Object o) {
            delegate.signalEvent(s, o);
        }

        @Override
        public void signalEvent(String s, Object o, long l) {
            delegate.signalEvent(s, o, l);
        }

        @Override
        public void dispose() {
            delegate.dispose();
        }

        @Override
        public void retryWorkItem(Long aLong, Map<String, Object> map) {
            delegate.retryWorkItem(aLong, map);
        }

        private class WorkItemCompletedSignal implements UnitInstance.Signal {

            private final WorkItem workItem;
            private final Map<String, Object> results;
            private final ProcessUnitInstance pui;
            private final long id;

            public WorkItemCompletedSignal(WorkItem workItem, Map<String, Object> results, ProcessUnitInstance pui, long id) {
                this.workItem = workItem;
                this.results = results;
                this.pui = pui;
                this.id = id;
            }

            @Override
            public void exec(UnitInstance unitInstance) {
                workItem.setResults(results);
                workItem.setState(2);
                pui.processInstance.signalEvent("workItemCompleted", workItem);
                delegate.getWorkItems().remove(id);
            }
        }
    }

    private static void transitionToState(ProcessUnitInstance instance, UnitInstance.State next) {
        if (next == instance.state) {
            return;
        }
        instance.state = next;
        ProcessUnit unit = instance.unit();
        switch (next) {
            case Created:
                unit.onCreate();
                break;
            case Entering:
                unit.onEnter();
                break;
            case Running:
                break;
            case Suspended:
                unit.onSuspend();
                break;
            case Exiting:
                unit.onExit();
                break;
            case Completed:
                unit.onEnd();
                break;
            case Resuming:
                unit.onResume();
                break;
            case ReEntering:
                unit.onReEnter();
                break;
            case Aborting:
                unit.onAbort();
                break;
            case Faulted:
                unit.onFault(null);
                break;
        }
    }

    private static UnitInstance.State stateOf(int state) {
        switch (state) {
            case STATE_PENDING:
                return UnitInstance.State.Created;
            case STATE_ACTIVE:
                return UnitInstance.State.Running;
            case STATE_COMPLETED:
                return UnitInstance.State.Completed;
            case STATE_ABORTED:
                return UnitInstance.State.Completed; // fixme shall we support an Aborted state?
            case STATE_SUSPENDED:
                return UnitInstance.State.Suspended;
            default:
                throw new IllegalStateException(String.valueOf(state));
        }
    }
}