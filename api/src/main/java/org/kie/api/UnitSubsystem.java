package org.kie.api;

import java.util.Optional;

public interface UnitSubsystem extends UnitInstanceSignalReceiver {

    interface Provider {

        UnitSubsystem get(UnitExecutor executor);
    }

    /**
     * Returns Optional.empty when it does not support the type of the given unit
     */
    Optional<UnitInstance> createInstance(UnitInstance.Proto proto);

    void signal(UnitInstanceSignal signal);
}
