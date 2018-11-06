package org.jbpm.example;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.units.ProcessUnit;
import org.kie.api.Unit;
import org.kie.api.UnitInstance;
import org.kie.api.UnitInstance.State;

public class FailingUnit implements ProcessUnit {
    
    List<State> stateSequence = new ArrayList<>();

    @Override
    public void onCreate() {
        log(State.Created);
    }

    @Override
    public void onStart() {
        log(State.Running);
    }

    @Override
    public void onEnd() {
        log(State.Completed);
    }

    @Override
    public void onSuspend() {
        log(State.Suspended);
    }

    @Override
    public void onEnter() {
        log(State.Entering);
    }

    @Override
    public void onExit() {
        log(State.Exiting);
    }

    @Override
    public void onReEnter() {
        log(State.ReEntering);
    }

    @Override
    public void onFault(Throwable e) {
        log(State.Faulted);
    }

    @Override
    public void onAbort() {
        log(State.Aborting);
    }

    @Override
    public void onResume() {
        System.out.println("RESUME");
    }

    @Override
    public void onYield(Unit other) {
        System.out.println("YIELD");
    }

    private void log(State state) {
        System.out.println(state);
        stateSequence.add(state);
    }
}
