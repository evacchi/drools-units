package org.jbpm.units;

import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitInstanceSignal;
import org.kie.api.UnitSubsystem;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.internal.LegacySessionWrapper;
import org.kie.api.runtime.KieSession;

public class ProcessUnitSubsystem implements UnitSubsystem {

    private static final Properties props;

    static {
        props = new Properties();
        props.put(
                "drools.workItemManagerFactory",
                WorkItemManager.Factory.class.getTypeName());
    }

    private final KieSession session;
    private final UnitExecutor executor;
    private final WorkItemManager workItemManager;
    private final Set<ProcessUnitInstance> instances;

    public ProcessUnitSubsystem(UnitExecutor executor) {
        this.instances = new HashSet<>();
        this.session = ((LegacySessionWrapper) executor).getSession();
        this.executor = executor;
        StatefulKnowledgeSessionImpl session =
                (StatefulKnowledgeSessionImpl) this.session;
        session.getSessionConfiguration().addDefaultProperties(props);
        this.workItemManager = ((WorkItemManager) this.session.getWorkItemManager());

        this.session.addEventListener(processEventListener);
        this.workItemManager.setProcessUnitSubsystem(this);
    }

    public Optional<UnitInstance> createInstance(UnitInstance.Proto proto) {
        if (proto.unit() instanceof ProcessUnit) {
            ProcessUnitInstance instance =
                    new ProcessUnitInstance((ProcessUnit) proto.unit(), this, session);
            instances.add(instance);
            return Optional.of(instance);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void signal(UnitInstanceSignal signal) {
        instances.forEach(i -> i.signal(signal));
    }

    void terminated(ProcessUnitInstance processUnitInstance) {
        instances.remove(processUnitInstance);
    }

    private final ProcessEventListener processEventListener = new DefaultProcessEventListener() {
        @Override
        public void beforeProcessCompleted(ProcessCompletedEvent event) {
            instances.forEach(instance -> instance.transitionToState(UnitInstance.State.Exiting));
        }

        @Override
        public void afterProcessCompleted(ProcessCompletedEvent event) {
            instances.forEach(instance -> instance.transitionToState(UnitInstance.State.Completed));
        }

        @Override
        public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
            instances.forEach(ProcessUnitInstance::syncInternalState);
        }

        @Override
        public void afterNodeLeft(ProcessNodeLeftEvent event) {
            instances.forEach(ProcessUnitInstance::syncInternalState);
        }
    };
}