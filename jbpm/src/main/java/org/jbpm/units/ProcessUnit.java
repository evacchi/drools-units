package org.jbpm.units;

import org.kie.api.Unit;

public interface ProcessUnit extends Unit {

    @Override
    default Identity getUnitIdentity() {
        return new ProcessIdentity(getClass());
    }

    class ProcessIdentity extends Unit.Identity {

        private final Class<?> cls;
        private final Object[] keys;

        public ProcessIdentity(Class<?> cls, Object... keys) {
            super(cls, keys);
            this.cls = cls;
            this.keys = keys;
        }

        @Override
        public String toString() {
            return cls.getCanonicalName();
        }
    }
}
