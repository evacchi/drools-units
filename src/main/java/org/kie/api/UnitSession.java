package org.kie.api;

import java.util.Collection;

/**
 * A runnable instance of a Unit.
 *
 * A UnitSession implements all the memory and lifecycle -related concerns
 * of a running Unit instance.
 *
 */
public interface UnitSession {

    interface Factory {

        UnitSession create(Unit unit);
    }

    enum State {
        Active,
        Suspended,
        Completed;
    }

    Unit unit();

    void start();

    void halt();

    /**
     * 
     * @return
     */
    Collection<UnitSession> references();

    void signal(UnitSessionSignal signal);

    void yield(UnitSession next);

    State state();
}
