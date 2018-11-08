package org.jbpm.units;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSupport;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.internal.LegacySessionWrapper;
import org.kie.api.runtime.KieSession;

import static org.kie.api.runtime.process.ProcessInstance.STATE_ABORTED;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
import static org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;
import static org.kie.api.runtime.process.ProcessInstance.STATE_PENDING;
import static org.kie.api.runtime.process.ProcessInstance.STATE_SUSPENDED;

public class ProcessUnitSupport implements UnitSupport {

    private static final Properties props;
    static {
        props = new Properties();
        props.put(
                "drools.workItemManagerFactory",
                WorkItemManager.Factory.class.getTypeName());
    }

    private final KieSession session;
    private final Map<Long, ProcessUnitInstance> instances;
    private final WorkItemManager workItemManager;

    public ProcessUnitSupport(UnitExecutor executor) {
        this.session = ((LegacySessionWrapper) executor).getSession();
        StatefulKnowledgeSessionImpl session =
                (StatefulKnowledgeSessionImpl) this.session;
        session.getSessionConfiguration().addDefaultProperties(props);
        this.workItemManager = ((WorkItemManager) this.session.getWorkItemManager());

        this.session.addEventListener(processEventListener);
        this.instances = new HashMap<>();
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

    private final ProcessEventListener processEventListener = new DefaultProcessEventListener() {
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
        public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
            transition(instances.get(event.getProcessInstance().getId()));
        }

        @Override
        public void afterNodeLeft(ProcessNodeLeftEvent event) {
            transition(instances.get(event.getProcessInstance().getId()));
        }

        private void transition(ProcessUnitInstance unitInstance) {
            if (unitInstance == null) {
                return;
            }
            UnitInstance.State state = stateOf(unitInstance.getProcessInstance().getState());
            transitionToState(unitInstance, state);
        }
    };

    ProcessUnitInstance getProcessUnitInstance(long processInstanceId) {
        return instances.get(processInstanceId);
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