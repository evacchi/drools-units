package org.jbpm.units;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

import static org.kie.api.runtime.process.ProcessInstance.STATE_ABORTED;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
import static org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;
import static org.kie.api.runtime.process.ProcessInstance.STATE_PENDING;
import static org.kie.api.runtime.process.ProcessInstance.STATE_SUSPENDED;

public class ProcessUnitSupport implements UnitSupport {

    private final KieSession session;
    private final Map<Long, ProcessUnitInstance> instances;

    public ProcessUnitSupport(UnitExecutor executor) {
        this.session = ((LegacySessionWrapper) executor).getSession();
        this.session.addEventListener(processEventListener);
        this.instances = new HashMap<>();
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

        }

        @Override
        public void afterProcessStarted(ProcessStartedEvent event) {

        }

        @Override
        public void beforeProcessCompleted(ProcessCompletedEvent event) {

        }

        @Override
        public void afterProcessCompleted(ProcessCompletedEvent event) {
            ProcessUnitInstance unitInstance =
                    instances.remove(event.getProcessInstance().getId());
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

        private void transitionToState(ProcessUnitInstance instance, UnitInstance.State next) {
            if (next == instance.state) {
                return;
            }
            instance.state = next;
            switch (next) {
                case Created:
                    instance.unit().onCreate();
                    break;
                case Running:
                    break;
                case Suspended:
                    instance.unit().onSuspend();
                    break;
                case Completed:
                    instance.unit().onEnd();
                    break;
                case Aborted:
                    break;
            }
        }

        private UnitInstance.State stateOf(int state) {
            switch (state) {
                case STATE_PENDING:
                    return UnitInstance.State.Created;
                case STATE_ACTIVE:
                    return UnitInstance.State.Running;
                case STATE_COMPLETED:
                    return UnitInstance.State.Completed;
                case STATE_ABORTED:
                    return UnitInstance.State.Aborted;
                case STATE_SUSPENDED:
                    return UnitInstance.State.Suspended;
                default:
                    throw new IllegalStateException(String.valueOf(state));
            }
        }
    };
}