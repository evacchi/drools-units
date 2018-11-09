package org.drools.units.signals;

import org.drools.core.spi.Activation;
import org.drools.units.GuardedUnitInstance;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitScheduler;

public class UnregisterGuard implements UnitExecutor.Signal.Broadcast {

    private final Activation activation;

    public UnregisterGuard(Activation activation) {
        this.activation = activation;
    }

    @Override
    public void exec(UnitScheduler scheduler) {
        scheduler.current().references()
                .stream()
                .filter(u -> u instanceof GuardedUnitInstance)
                .forEach(u -> ((GuardedUnitInstance) u).removeActivation(activation));
    }
}