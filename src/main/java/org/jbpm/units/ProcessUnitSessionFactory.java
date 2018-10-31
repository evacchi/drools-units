package org.jbpm.units;

import org.kie.api.Unit;
import org.kie.api.UnitInstance;
import org.kie.api.runtime.KieSession;

public class ProcessUnitSessionFactory {

    private final KieSession session;

    public ProcessUnitSessionFactory(KieSession session) {
        this.session = session;
    }

    public UnitInstance create(Unit unit) {
        return new ProcessUnitSessionDelegate((ProcessUnit) unit, session);
    }
}