package org.drools.units;

import java.util.Optional;

import org.drools.core.spi.Activation;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitScheduler;

public interface RuleUnitSignal {

    static RegisterGuardSignal registerGuard(
            GuardedRuleUnitInstance.Proto guarded, Activation activation) {
        return new RegisterGuardSignal(guarded, activation);
    }

    static UnregisterGuardSignal unregisterGuard(Activation activation) {
        return new UnregisterGuardSignal(activation);
    }

    static YieldSignal yield(UnitInstance.Proto proto, String ruleUnitClassName) {
        return new YieldSignal(proto, ruleUnitClassName);
    }

    static UnitExecutor.Signal.Broacast suspend() {
        return scheduler -> Optional.ofNullable(scheduler.current()).ifPresent(u -> u.unit().onSuspend());
    }

    static UnitExecutor.Signal.Broacast resume() {
        return scheduler -> Optional.ofNullable(scheduler.current()).ifPresent(u -> u.unit().onResume());
    }

    class RegisterGuardSignal implements UnitExecutor.Signal.Scoped {

        private final GuardedRuleUnitInstance.Proto proto;
        private final Activation activation;

        private RegisterGuardSignal(GuardedRuleUnitInstance.Proto proto, Activation activation) {
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

    class UnregisterGuardSignal implements UnitExecutor.Signal.Broacast {

        private final Activation activation;

        private UnregisterGuardSignal(Activation activation) {
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

    class YieldSignal implements UnitExecutor.Signal.Scoped {

        private final UnitInstance.Proto proto;
        private final String ruleUnitClassName;

        private YieldSignal(UnitInstance.Proto proto, String ruleUnitClassName) {
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
}
