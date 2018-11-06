package org.drools.units;

import java.util.Optional;

import org.drools.core.impl.RuleUnitInternals.Factory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.units.internal.LegacyRuleUnitExecutor;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSupport;
import org.kie.api.internal.LegacySessionWrapper;
import org.kie.api.runtime.KieSession;

public class RuleUnitSupport implements UnitSupport {

    private final Factory delegate;
    private final KieSession session;

    public RuleUnitSupport(UnitExecutor executor) {
        this.session = ((LegacySessionWrapper) executor).getSession();
        this.delegate = new Factory((StatefulKnowledgeSessionImpl) session);
        this.delegate.setLegacyExecutor(new LegacyRuleUnitExecutor(executor));
        this.delegate.bind(((StatefulKnowledgeSessionImpl) session).getKnowledgeBase());
    }

    public Optional<UnitInstance> createInstance(UnitInstance.Proto proto) {
        if (proto.unit() instanceof RuleUnit) {
            return Optional.of(delegate.create(proto));
        } else {
            return Optional.empty();
        }
    }
}