package org.kie.api;

import java.util.Arrays;

import org.kie.api.runtime.KieSession;

public interface Unit {

    default Unit.Identity getUnitIdentity() {
        return new Unit.Identity(this.getClass());
    }

    default void onStart() {
    }

    default void onEnd() {
    }

    default void onSuspend() {
    }

    default void onResume() {
    }

    default void onYield(Unit other) {
    }

    public static class Identity {

        private final Class<? extends Unit> cls;
        private final Object[] keys;
        private final String id;

        public Identity(Class<? extends Unit> cls, Object... keys) {
            this.cls = cls;
            this.keys = keys;
            String baseName = cls.getCanonicalName();
            if (keys.length != 0) {
                this.id = baseName + Arrays.toString(keys);
            } else {
                this.id = baseName;
            }
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                Unit.Identity identity = (Unit.Identity) o;
                return this.cls.equals(identity.cls) && Arrays.equals(this.keys, identity.keys);
            } else {
                return false;
            }
        }

        public int hashCode() {
            int result = this.cls.hashCode();
            result = 31 * result + Arrays.hashCode(this.keys);
            return result;
        }

        @Override
        public String toString() {
            return id;
        }
    }
}