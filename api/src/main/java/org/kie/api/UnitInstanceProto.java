package org.kie.api;

public class UnitInstanceProto {
    private final Unit unit;
    private final UnitBinding[] bindings;

    public UnitInstanceProto(Unit unit, UnitBinding... bindings) {
        this.unit = unit;
        this.bindings = bindings;
    }

    public Unit unit() {
        return unit;
    }

    public UnitBinding[] bindings() {
        return bindings;
    }
}
