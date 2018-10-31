package org.jbpm.units;

import org.kie.api.Unit;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSupport;
import org.kie.api.runtime.KieSession;

public class ProcessUnitSupport implements UnitSupport {

    public static Provider get() {
        return ProcessUnitSupport::new;
    }

    private final KieSession session;

    public ProcessUnitSupport(KieSession session) {
        this.session = session;
    }

    public UnitInstance createInstance(Unit unit) {
        return new ProcessUnitInstanceDelegate((ProcessUnit) unit, session);
    }

    @Override
    public boolean handles(Unit u) {
        return u instanceof ProcessUnit;
    }
}