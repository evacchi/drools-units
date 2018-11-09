package org.kie.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class ActiveUnitInstances {
    private final UnitSupport sessionFactory;
    private final Set<UnitInstance> activeInstances;

    ActiveUnitInstances(UnitSupport sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.activeInstances = new HashSet<>();
    }

    Optional<UnitInstance> createInstance(UnitInstance.Proto proto) {
        Optional<UnitInstance> optionalInstance = sessionFactory.createInstance(proto);
        optionalInstance.ifPresent(activeInstances::add);
        return optionalInstance;
    }

    Collection<UnitInstance> get() {
        return Collections.unmodifiableCollection(activeInstances);
    }

    void remove(UnitInstance instance) {
        if (!activeInstances.remove(instance)) {
            // log that no instance was removed
            System.out.println("no instances were removed");
        }
    }
}
