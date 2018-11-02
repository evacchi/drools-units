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

    void signal(UnitSessionSignal signal);

    void yield(UnitInstance next);

    State state();
}
