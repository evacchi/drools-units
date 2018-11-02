package org.drools.units;

import java.util.Optional;

import org.drools.units.internal.LegacyRuleUnitExecutor;
import org.drools.core.impl.RuleUnitInternals.Factory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.Unit;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSupport;
import org.kie.api.internal.LegacySessionWrapper;
import org.kie.api.runtime.KieSession;

public class RuleUnitSupport implements UnitSupport {

    public static Provider get() {
        return RuleUnitSupport::new;
    }

    private final Factory delegate;
    private final KieSession session;

    public RuleUnitSupport(UnitExecutor executor) {
        this.session = ((LegacySessionWrapper) executor).getSession();
        this.delegate = new Factory((StatefulKnowledgeSessionImpl) session);
        this.delegate.setLegacyExecutor(new LegacyRuleUnitExecutor(executor, delegate));
        this.delegate.bind(((StatefulKnowledgeSessionImpl) session).getKnowledgeBase());
    }

    public Optional<UnitInstance> createInstance(Unit unit) {
        if (unit instanceof RuleUnit) {
            return Optional.of(delegate.create((RuleUnit) unit));
        } else {
            return Optional.empty();
        }
    }
}