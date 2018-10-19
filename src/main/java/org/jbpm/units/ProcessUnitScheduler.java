package org.jbpm.units;

import java.util.LinkedList;

public class ProcessUnitScheduler {

    private ProcessUnitSession stackPointer;
    private LinkedList<ProcessUnitSession> units = new LinkedList<>();

    public void schedule(ProcessUnitSession sess) {
        units.add(sess);
    }

    public ProcessUnitSession next() {
        stackPointer = units.poll();
        return stackPointer;
    }

}
