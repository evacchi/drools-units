package org.drools.units;

import org.drools.core.impl.RuleUnitInstance;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.Unit;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSupport;
import org.kie.api.runtime.KieSession;

public class RuleUnitSupport implements UnitSupport {

    public static Provider get() {
        return RuleUnitSupport::new;
    }

    private final RuleUnitInstance.Factory delegate;
    private final KieSession session;

    public RuleUnitSupport(KieSession session) {
        this.session = session;
        this.delegate = new RuleUnitInstance.Factory((StatefulKnowledgeSessionImpl) session);
        delegate.bind(((StatefulKnowledgeSessionImpl) session).getKnowledgeBase());
    }

    public UnitInstance createInstance(Unit unit) {
        return delegate.create((RuleUnit) unit);
    }

    @Override
    public boolean handles(Unit u) {
        return u instanceof RuleUnit;
    }
}