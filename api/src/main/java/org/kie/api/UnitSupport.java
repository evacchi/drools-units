package org.kie.api;

import java.util.Optional;

public interface UnitSupport {

    interface Provider {
        UnitSupport get(UnitExecutor executor);
    }

    /**
     * Returns Optional.empty when it does not support the type of the given unit
     */
    Optional<UnitInstance> createInstance(UnitInstance.Proto proto);
}
