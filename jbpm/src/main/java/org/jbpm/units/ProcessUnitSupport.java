package org.jbpm.units;

import java.util.Optional;

import org.kie.api.Unit;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSupport;
import org.kie.api.internal.LegacySessionWrapper;
import org.kie.api.runtime.KieSession;

public class ProcessUnitSupport implements UnitSupport {

    public static Provider get() {
        return ProcessUnitSupport::new;
    }

    private final KieSession session;

    public ProcessUnitSupport(UnitExecutor executor) {
        this.session = ((LegacySessionWrapper) executor).getSession();
    }

    public Optional<UnitInstance> createInstance(Unit unit) {
        if (unit instanceof ProcessUnit) {
            return Optional.of(new ProcessUnitInstanceDelegate((ProcessUnit) unit, session));
        } else {
            return Optional.empty();
        }
    }

}