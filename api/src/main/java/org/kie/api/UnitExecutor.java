package org.kie.api;

import java.util.Collection;

public interface UnitExecutor {

    /**
     * Schedule for execution the given Unit, and keeps running
     * until the system reaches a stable state
     */

    UnitInstance create(UnitInstance.Proto proto);

    UnitInstance run(UnitInstance.Proto proto);

    UnitInstance current();

    Collection<UnitInstance> active();

    void signal(Signal signal);

    interface Signal {
        void exec(UnitExecutor executor);
    }
}
