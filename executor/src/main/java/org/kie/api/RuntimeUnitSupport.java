package org.kie.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.kie.api.runtime.KieSession;

public class RuntimeUnitSupport implements UnitSupport {

    public static UnitSupport.Provider register(UnitSupport.Provider... unitInstanceFactories) {
        return session -> new RuntimeUnitSupport(session, Arrays.asList(unitInstanceFactories));
    }

    private Collection<UnitSupport> unitInstanceFactories;



    private RuntimeUnitSupport(
            KieSession session,
            Collection<UnitSupport.Provider> unitInstanceFactories) {
        this.unitInstanceFactories =
                unitInstanceFactories.stream()
                        .map(f -> f.get(session))
                        .collect(Collectors.toList());
    }

    public UnitInstance createInstance(Unit unit) {
        return unitInstanceFactories.stream()
                .filter(factory -> factory.handles(unit))
                .findFirst()
                .map(factory -> factory.createInstance(unit))
                .orElseThrow(() -> new UnsupportedOperationException(
                        "Unsupported type of Unit: " + unit.getClass().getCanonicalName()));
    }

    @Override
    public boolean handles(Unit u) {
        return unitInstanceFactories.stream().anyMatch(factory -> factory.handles(u));
    }
}
