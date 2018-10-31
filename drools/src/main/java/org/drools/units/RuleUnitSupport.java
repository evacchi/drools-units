package org.drools.units;

import org.kie.api.Unit;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSupport;
import org.kie.api.runtime.KieSession;

public class RuleUnitSupport implements UnitSupport {

    public static Provider get() {
        return RuleUnitSupport::new;
    }

    private final KieSession session;

    public RuleUnitSupport(KieSession session) {
        this.session = session;
    }

    public UnitInstance createInstance(Unit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean handles(Unit u) {
        return u instanceof RuleUnit;
    }
}