package org.jbpm.units;

import org.kie.api.UnitSessionSignal;

public interface ProcessUnitInstanceSignal extends UnitSessionSignal {

    /**
     * Represents a process signal event
     */
    class Event implements ProcessUnitInstanceSignal {
        private final String type;
        private final Object payload;

        public Event(String type, Object payload) {
            this.type = type;
            this.payload = payload;
        }

        public String type() {
            return type;
        }

        public Object payload() {
            return payload;
        }
    }

}
