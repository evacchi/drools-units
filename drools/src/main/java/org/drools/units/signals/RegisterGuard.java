package org.drools.units.signals;

import org.drools.core.spi.Activation;
import org.drools.units.GuardedRuleUnitInstance;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitScheduler;

public class RegisterGuard implements UnitExecutor.Signal.Scoped {

    private final GuardedRuleUnitInstance.Proto proto;
    private final Activation activation;

    public RegisterGuard(GuardedRuleUnitInstance.Proto proto, Activation activation) {
        this.proto = proto;
        this.activation = activation;
    }

    @Override
    public GuardedRuleUnitInstance.Proto proto() {
        return proto;
    }

    @Override
    public void exec(UnitInstance instance, UnitScheduler scheduler) {
        GuardedRuleUnitInstance guarded = (GuardedRuleUnitInstance) instance;
        scheduler.current().references().add(guarded);
        guarded.addActivation(activation);
    }
}
