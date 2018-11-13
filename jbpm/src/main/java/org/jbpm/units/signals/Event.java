package org.jbpm.units.signals;

import org.jbpm.units.ProcessUnitInstance;
import org.jbpm.units.ProcessUnitInstanceSignal;
import org.kie.api.UnitInstance;

/**
 * Represents a process signal event
 */
public class Event implements ProcessUnitInstanceSignal {

    private final long id;
    private final String type;
    private final Object payload;

    public Event(long id, String type, Object payload) {
        this.id = id;
        this.type = type;
        this.payload = payload;
    }

    public long id() {
        return id;
    }

    public String type() {
        return type;
    }

    public Object payload() {
        return payload;
    }

    @Override
    public void exec(UnitInstance unitInstance) {
        ProcessUnitInstance processUnitInstance = (ProcessUnitInstance) unitInstance;
        if (processUnitInstance.id() == id()) {
            processUnitInstance
                    .getProcessInstance()
                    .signalEvent(type(), payload());
        }
    }
}
