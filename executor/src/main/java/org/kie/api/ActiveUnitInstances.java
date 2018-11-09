package org.kie.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class ActiveUnitInstances {
    private final UnitSupport sessionFactory;
    private final Set<UnitInstance> activeInstances;

    public ActiveUnitInstances(UnitSupport sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.activeInstances = new HashSet<>();
    }

    public Optional<UnitInstance> createInstance(UnitInstance.Proto proto) {
        Optional<UnitInstance> optionalInstance = sessionFactory.createInstance(proto);
        optionalInstance.ifPresent(activeInstances::add);
        return optionalInstance;
    }

    public Collection<UnitInstance> get() {
        return Collections.unmodifiableCollection(activeInstances);
    }
}
