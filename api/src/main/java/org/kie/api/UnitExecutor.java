package org.kie.api;

public interface UnitExecutor {

    /**
     * Schedule for execution the given Unit, and keeps running
     * until the system reaches a stable state
     */

    void run(Unit unit, UnitBinding... bindings);

    void run(UnitInstance instance);

    UnitInstance current();

    void signal(Signal signal);

    interface Signal {
        Unit unit();
        UnitBinding[] bindings();
        void exec(UnitInstance instance, UnitScheduler scheduler);
    }
}
