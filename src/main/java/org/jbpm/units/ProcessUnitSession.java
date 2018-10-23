package org.jbpm.units;

import org.kie.api.UnitSession;

public interface ProcessUnitSession extends UnitSession<ProcessUnit> {
    <T> void signal(T signal);
}
