package org.drools.units;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.drools.core.spi.Activation;
import org.kie.api.Unit;
import org.kie.api.UnitInstance;

public class GuardedUnitInstance implements UnitInstance {

    private final UnitInstance unitInstance;
    private final Set<Activation> activations = new HashSet<>();

    public GuardedUnitInstance(UnitInstance unitInstance) {
        this.unitInstance = unitInstance;
    }

    public void addActivation(Activation activation) {
        activations.add(activation);
    }

    public void removeActivation(Activation activation) {
        activations.remove(activation);
    }

    @Override
    public State state() {
        return activations.isEmpty() ? State.Completed : unitInstance.state();
    }

    @Override
    public String toString() {
        return "GS:" + unit();
    }

    @Override
    public Unit unit() {
        return unitInstance.unit();
    }

    @Override
    public void start() {
        unitInstance.start();
    }

    @Override
    public void halt() {
        unitInstance.halt();
    }

    @Override
    public void suspend() {
        unitInstance.suspend();
    }

    @Override
    public void resume() {
        unitInstance.resume();
    }

    @Override
    public Collection<UnitInstance> references() {
        return unitInstance.references();
    }

    @Override
    public void signal(Signal signal) {
        unitInstance.signal(signal);
    }

    @Override
    public void yield(UnitInstance next) {
        unitInstance.yield(next);
    }

}
