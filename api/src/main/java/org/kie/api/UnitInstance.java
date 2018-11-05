package org.kie.api;

import java.util.Collection;

/**
 * A runnable instance of a Unit.
 * <p>
 * A UnitSession implements all the memory and lifecycle -related concerns
 * of a running Unit instance.
 */
public interface UnitInstance {

    enum State {
        Created,
        Running,
        Suspended,
        Completed,
        Aborted;
    }

    Unit unit();

    void start();

    void halt();

    /**
     * Sessions that reference this session.
     * E.g. "Guards" reference the units they guard
     */
    Collection<UnitInstance> references();

    void signal(Signal signal);

    void yield(UnitInstance next);

    State state();

    interface Signal {
        void exec(UnitInstance unitInstance);
    }

    final class Proto {
        private final Unit unit;
        private final UnitBinding[] bindings;

        public Proto(Unit unit, UnitBinding... bindings) {
            this.unit = unit;
            this.bindings = bindings;
        }

        public Unit unit() {
            return unit;
        }

        public UnitBinding[] bindings() {
            return bindings;
        }
    }

}
