package org.drools.units;

import java.util.Optional;

import org.drools.core.spi.Activation;
import org.kie.api.Unit;
import org.kie.api.UnitBinding;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitScheduler;
import org.kie.api.UnitSchedulerSignal;

public interface RuleUnitSignal {

    static RegisterGuardSignal registerGuard(
            GuardedRuleUnitSession guarded, Activation activation) {
        return new RegisterGuardSignal(guarded, activation);
    }

    static UnregisterGuardSignal unregisterGuard(Activation activation) {
        return new UnregisterGuardSignal(activation);
    }

    static YieldSignal yield(Unit unit, UnitBinding[] bindings, String ruleUnitClassName) {
        return new YieldSignal(unit, bindings, ruleUnitClassName);
    }

    static UnitSchedulerSignal suspend() {
        return scheduler -> Optional.ofNullable(scheduler.current()).ifPresent(u -> u.unit().onSuspend());
    }

    static UnitSchedulerSignal resume() {
        return scheduler -> Optional.ofNullable(scheduler.current()).ifPresent(u -> u.unit().onResume());
    }

    class RegisterGuardSignal implements UnitSchedulerSignal {

        private final GuardedRuleUnitSession guarded;
        private final Activation activation;

        private RegisterGuardSignal(GuardedRuleUnitSession guarded, Activation activation) {
            this.guarded = guarded;
            this.activation = activation;
        }

        @Override
        public void exec(UnitScheduler scheduler) {
            scheduler.current().references().add(guarded);
            guarded.addActivation(activation);
        }
    }

    class UnregisterGuardSignal implements UnitSchedulerSignal {

        private final Activation activation;

        private UnregisterGuardSignal(Activation activation) {
            this.activation = activation;
        }

        @Override
        public void exec(UnitScheduler scheduler) {
            scheduler.current().references()
                    .stream()
                    .filter(u -> u instanceof GuardedRuleUnitSession)
                    .forEach(u -> ((GuardedRuleUnitSession) u).removeActivation(activation));
        }
    }

    class YieldSignal implements UnitExecutor.Signal {

        private final Unit unit;
        private final UnitBinding[] bindings;
        private final String ruleUnitClassName;

        private YieldSignal(Unit unit, UnitBinding[] bindings, String ruleUnitClassName) {
            this.unit = unit;
            this.bindings = bindings;
            this.ruleUnitClassName = ruleUnitClassName;
        }

        public Unit unit() {
            return unit;
        }

        public UnitBinding[] bindings() {
            return bindings;
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
