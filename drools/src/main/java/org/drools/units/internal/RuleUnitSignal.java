package org.drools.units.internal;

import java.util.Optional;

import org.drools.core.impl.GuardedRuleUnitSession;
import org.drools.core.spi.Activation;
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

    static YieldSignal yield(UnitInstance session, String ruleUnitClassName) {
        return new YieldSignal(session, ruleUnitClassName);
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

    class YieldSignal implements UnitSchedulerSignal {

        private final UnitInstance session;
        private final String ruleUnitClassName;

        private YieldSignal(UnitInstance session, String ruleUnitClassName) {
            this.session = session;
            this.ruleUnitClassName = ruleUnitClassName;
        }

        @Override
        public void exec(UnitScheduler scheduler) {
            UnitInstance current = scheduler.current();
            if (current != null) {
                throw new UnsupportedOperationException();
                //current.yield(session.unit());
            }
            scheduler.scheduleAfter(
                    session,
                    us -> us.unit().getClass().getName().equals(ruleUnitClassName));
        }
    }

}
