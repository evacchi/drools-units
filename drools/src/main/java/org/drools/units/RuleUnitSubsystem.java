package org.drools.units;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.drools.core.impl.RuleUnitInternals.Factory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.units.internal.LegacyRuleUnitExecutor;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitInstanceSignal;
import org.kie.api.UnitSubsystem;
import org.kie.api.internal.LegacySessionWrapper;
import org.kie.api.runtime.KieSession;

public class RuleUnitSubsystem implements UnitSubsystem {

    private final Factory delegate;
    private final KieSession session;
    private final Set<RuleUnitInstance> instances;

    public RuleUnitSubsystem(UnitExecutor executor) {
        this.instances = new HashSet<>();
        this.session = ((LegacySessionWrapper) executor).getSession();
        this.delegate = new Factory((StatefulKnowledgeSessionImpl) session);
        this.delegate.setLegacyExecutor(new LegacyRuleUnitExecutor(executor));
        this.delegate.bind(((StatefulKnowledgeSessionImpl) session).getKnowledgeBase());
    }

    public Optional<UnitInstance> createInstance(UnitInstance.Proto proto) {
        if (proto.unit() instanceof RuleUnit) {
            RuleUnitInstance instance = delegate.create(proto);
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
}