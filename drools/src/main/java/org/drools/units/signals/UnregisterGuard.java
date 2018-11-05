package org.drools.units.signals;

import org.drools.core.spi.Activation;
import org.drools.units.GuardedRuleUnitInstance;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitScheduler;

public class UnregisterGuard implements UnitExecutor.Signal.Broacast {

    private final Activation activation;

    public UnregisterGuard(Activation activation) {
        this.activation = activation;
    }

    @Override
    public void exec(UnitScheduler scheduler) {
        scheduler.current().references()
                .stream()
                .filter(u -> u instanceof GuardedRuleUnitInstance)
                .forEach(u -> ((GuardedRuleUnitInstance) u).removeActivation(activation));
    }
}