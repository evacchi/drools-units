package org.drools.units.signals;

import java.util.Optional;

import org.kie.api.UnitExecutor;
import org.kie.api.UnitScheduler;

public class Suspend implements UnitExecutor.Signal.Broacast {

    public static final Suspend Instance = new Suspend();

    @Override
    public void exec(UnitScheduler scheduler) {
        Optional.ofNullable(scheduler.current()).ifPresent(u -> u.unit().onSuspend());
    }
}
