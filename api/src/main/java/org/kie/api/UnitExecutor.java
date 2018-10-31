package org.kie.api;

public interface UnitExecutor {

    /**
     * Schedule for execution the given Unit, and keeps running
     * until the system reaches a stable state
     */
    void run(Unit unit);
}
