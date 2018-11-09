package org.kie.api;

public interface UnitExecutor extends UnitInstanceSignalReceiver {

    /**
     * Schedule for execution the given Unit, and keeps running
     * until the system reaches a stable state
     */

    UnitInstance create(UnitInstance.Proto proto);

    UnitInstance run(UnitInstance.Proto proto);

    UnitInstance current();

}
