package org.kie.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class RuntimeUnitSupport implements UnitSupport {

    public static UnitSupport.Provider register(UnitSupport.Provider... unitInstanceFactories) {
        return executor -> new RuntimeUnitSupport(executor, Arrays.asList(unitInstanceFactories));
    }

    private Collection<UnitSupport> unitInstanceFactories;

    private RuntimeUnitSupport(
            UnitExecutor executor,
            Collection<UnitSupport.Provider> unitInstanceFactories) {
        this.unitInstanceFactories =
                unitInstanceFactories.stream()
                        .map(f -> f.get(executor))
                        .collect(Collectors.toList());
    }

    public Optional<UnitInstance> createInstance(Unit unit) {
        return unitInstanceFactories.stream()
                .map(factory -> factory.createInstance(unit))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
