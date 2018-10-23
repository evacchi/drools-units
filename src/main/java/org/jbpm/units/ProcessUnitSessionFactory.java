package org.jbpm.units;

import org.kie.api.UnitSession;
import org.kie.api.runtime.KieSession;

public class ProcessUnitSessionFactory implements UnitSession.Factory<ProcessUnit, ProcessUnitSession> {

    private final KieSession session;

    public ProcessUnitSessionFactory(KieSession session) {
        this.session = session;
    }

    @Override
    public ProcessUnitSession create(ProcessUnit unit) {
        return new ProcessUnitSessionDelegate(unit, session);
    }
}