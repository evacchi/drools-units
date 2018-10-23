package org.kie.api;

import java.util.LinkedList;

public class UnitScheduler {

    private UnitSession<?> stackPointer;
    private LinkedList<UnitSession<?>> units = new LinkedList<>();

    public void schedule(UnitSession<?> sess) {
        units.add(sess);
    }

    public UnitSession<?> next() {
        stackPointer = units.poll();
        return stackPointer;
    }

    public UnitSession current() {
        return stackPointer;
    }

    public void halt() {
        current().halt();
    }

}
