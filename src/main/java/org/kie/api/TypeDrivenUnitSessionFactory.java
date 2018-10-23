package org.kie.api;

import org.jbpm.units.ProcessUnit;
import org.jbpm.units.ProcessUnitSessionFactory;
import org.kie.api.runtime.KieSession;

public class TypeDrivenUnitSessionFactory implements UnitSession.Factory<Unit, UnitSession<? extends Unit>> {

    private final ProcessUnitSessionFactory processUnitSessionFactory;

    public TypeDrivenUnitSessionFactory(KieSession session) {
        processUnitSessionFactory = new ProcessUnitSessionFactory(session);
    }

    @Override
    public UnitSession<? extends Unit> create(Unit unit) {
        if (unit instanceof ProcessUnit) {
            return processUnitSessionFactory.create((ProcessUnit) unit);
        }
        throw new UnsupportedOperationException(
                "Unsupported type of Unit: " + unit.getClass().getCanonicalName());
    }
}
