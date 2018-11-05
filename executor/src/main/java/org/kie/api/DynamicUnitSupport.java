package org.kie.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class DynamicUnitSupport implements UnitSupport {

    public static UnitSupport.Provider register(UnitSupport.Provider... unitInstanceFactories) {
        return executor -> new DynamicUnitSupport(
                Arrays.stream(unitInstanceFactories)
                        .map(f -> f.get(executor))
                        .collect(Collectors.toList()));
    }

    private Collection<UnitSupport> unitInstanceFactories;

    private DynamicUnitSupport(
            Collection<UnitSupport> unitInstanceFactories) {
        this.unitInstanceFactories = unitInstanceFactories;
    }

    public Optional<UnitInstance> createInstance(Unit unit, UnitBinding... bindings) {
        return unitInstanceFactories.stream()
                .map(factory -> factory.createInstance(unit))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
