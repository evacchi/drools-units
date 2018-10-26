package org.kie.api;

import org.jbpm.units.ProcessUnit;
import org.jbpm.units.ProcessUnitSessionFactory;
import org.kie.api.runtime.KieSession;

public class TypeDrivenUnitSessionFactory {

    private final ProcessUnitSessionFactory processUnitSessionFactory;

    public TypeDrivenUnitSessionFactory(KieSession session, UnitExecutor executor) {
        this.processUnitSessionFactory = new ProcessUnitSessionFactory(session);
    }

    public UnitSession create(Unit unit) {
        if (unit instanceof ProcessUnit) {
            return processUnitSessionFactory.create(unit);
        }
        // else, handle other supported unit types
        throw new UnsupportedOperationException(
                "Unsupported type of Unit: " + unit.getClass().getCanonicalName());
    }
}
