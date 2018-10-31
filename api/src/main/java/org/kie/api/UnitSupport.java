package org.kie.api;

import org.kie.api.runtime.KieSession;

public interface UnitSupport {

    interface Provider {

        UnitSupport get(KieSession session);
    }

    UnitInstance createInstance(Unit unit);
    boolean handles(Unit u);
}
