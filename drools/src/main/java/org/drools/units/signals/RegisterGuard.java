package org.drools.units.signals;

import org.drools.core.spi.Activation;
import org.drools.units.GuardedUnitInstance;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;

public class RegisterGuard implements UnitExecutor.Signal {

    private final UnitInstance.Proto proto;
    private final Activation activation;

    public RegisterGuard(UnitInstance.Proto proto, Activation activation) {
        this.proto = proto;
        this.activation = activation;
    }

    @Override
    public void exec(UnitExecutor executor) {
        UnitInstance instance = executor.create(proto);
        // fixme we should probably keep track of guard instances (1:1 to ruleUnit)
        GuardedUnitInstance guarded = new GuardedUnitInstance(instance);
        executor.current().references().add(guarded);
        guarded.addActivation(activation);
    }
}
