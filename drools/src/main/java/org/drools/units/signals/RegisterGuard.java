package org.drools.units.signals;

import org.drools.core.spi.Activation;
import org.drools.units.GuardedUnitInstance;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitScheduler;

public class RegisterGuard implements UnitExecutor.Signal.Scoped {

    private final UnitInstance.Proto proto;
    private final Activation activation;

    public RegisterGuard(UnitInstance.Proto proto, Activation activation) {
        this.proto = proto;
        this.activation = activation;
    }

    @Override
    public UnitInstance.Proto proto() {
        return proto;
    }

    @Override
    public void exec(UnitInstance instance, UnitScheduler scheduler) {
        // fixme we should probably keep track of guard instances (1:1 to ruleUnit)
        GuardedUnitInstance guarded = new GuardedUnitInstance(instance);
        scheduler.current().references().add(guarded);
        guarded.addActivation(activation);
    }
}
