package org.drools.units.signals;

import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitScheduler;

public class Yield implements UnitExecutor.Signal.Scoped {

    private final UnitInstance.Proto proto;
    private final String ruleUnitClassName;

    public Yield(UnitInstance.Proto proto, String ruleUnitClassName) {
        this.proto = proto;
        this.ruleUnitClassName = ruleUnitClassName;
    }

    @Override
    public UnitInstance.Proto proto() {
        return proto;
    }

    @Override
    public void exec(UnitInstance instance, UnitScheduler scheduler) {
        UnitInstance current = scheduler.current();
        if (current != null) {
            current.yield(instance);
        }
        scheduler.scheduleAfter(
                instance,
                us -> us.unit().getClass().getName().equals(ruleUnitClassName));
    }
}