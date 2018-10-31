package org.kie.api;

import java.util.Collection;
import java.util.stream.Collectors;

import org.kie.api.runtime.KieSession;

public class TypeDrivenUnitSessionFactory {

    private final KieSession session;
    private final UnitExecutor executor;
    private Collection<UnitSupport> unitInstanceFactories;

    public TypeDrivenUnitSessionFactory(
            KieSession session,
            UnitExecutor executor,
            Collection<UnitSupport.Provider> unitInstanceFactories) {
        this.session = session;
        this.executor = executor;
        this.unitInstanceFactories =
                unitInstanceFactories.stream()
                        .map(f -> f.get(session))
                        .collect(Collectors.toList());
    }

    public UnitInstance create(Unit unit) {
        return unitInstanceFactories.stream()
                .filter(factory -> factory.handles(unit))
                .findFirst()
                .map(factory -> factory.createInstance(unit))
                .orElseThrow(() -> new UnsupportedOperationException(
                        "Unsupported type of Unit: " + unit.getClass().getCanonicalName()));
    }
}
