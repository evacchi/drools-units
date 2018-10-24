package org.jbpm.units;

import org.kie.api.UnitSession;

public interface ProcessUnitSession extends UnitSession<ProcessUnit> {
    long id();
    void signal(String type, Object value);
}
