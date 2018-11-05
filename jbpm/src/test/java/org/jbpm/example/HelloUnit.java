package org.jbpm.example;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.units.ProcessUnit;
import org.kie.api.Unit;
import org.kie.api.UnitInstance;
import org.kie.api.UnitInstance.State;

public class HelloUnit implements ProcessUnit {
    
    List<State> stateSequence = new ArrayList<>();

    @Override
    public void onCreate() {
        System.out.println(State.Created);
        stateSequence.add(State.Created);
    }

    @Override
    public void onStart() {
        System.out.println(State.Running);
        stateSequence.add(State.Running);
    }

    @Override
    public void onEnd() {
        System.out.println(State.Completed);
        stateSequence.add(State.Completed);
    }

    @Override
    public void onSuspend() {
        System.out.println(State.Suspended);
        stateSequence.add(State.Suspended);
    }

    @Override
    public void onEnter() {
        System.out.println(State.Entering);

    }

    @Override
    public void onExit() {
        System.out.println(State.Exiting);

    }

    @Override
    public void onReEnter() {
        System.out.println(State.ReEntering);

    }

    @Override
    public void onFault(Throwable e) {
        System.out.println(State.Faulted);
    }

    @Override
    public void onAbort() {
        System.out.println(State.Aborting);

    }

    @Override
    public void onResume() {
        System.out.println("RESUME");
    }

    @Override
    public void onYield(Unit other) {
        System.out.println("YIELD");
    }
}
