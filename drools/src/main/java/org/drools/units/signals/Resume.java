package org.drools.units.signals;

import java.util.Optional;

import org.kie.api.UnitExecutor;
import org.kie.api.UnitScheduler;

public class Resume implements UnitExecutor.Signal.Broacast {

    public static final Resume Instance = new Resume();

    @Override
    public void exec(UnitScheduler scheduler) {
        Optional.ofNullable(scheduler.current()).ifPresent(u -> u.unit().onResume());
    }
}
