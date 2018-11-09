package org.kie.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class DynamicUnitSupport implements UnitSubsystem {

    public static UnitSubsystem.Provider register(UnitSubsystem.Provider... unitInstanceFactories) {
        return executor -> new DynamicUnitSupport(
                Arrays.stream(unitInstanceFactories)
                        .map(f -> f.get(executor))
                        .collect(Collectors.toList()));
    }

    private Collection<UnitSubsystem> unitInstanceFactories;

    private DynamicUnitSupport(
            Collection<UnitSubsystem> unitInstanceFactories) {
        this.unitInstanceFactories = unitInstanceFactories;
    }

    public Optional<UnitInstance> createInstance(UnitInstance.Proto proto) {
        for (UnitSubsystem factory : unitInstanceFactories) {
            Optional<UnitInstance> instance = factory.createInstance(proto);
            if (instance.isPresent()) {
                return instance;
            }
        }
        return Optional.empty();
    }

    @Override
    public void signal(UnitInstanceSignal signal) {
        unitInstanceFactories.forEach(
                unitSubsystem -> unitSubsystem.signal(signal));
    }
}
