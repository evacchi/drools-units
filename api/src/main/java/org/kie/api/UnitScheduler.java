package org.kie.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Keeps track of the currently-scheduled UnitSessions.
 *
 * A UnitScheduler schedules UnitSessions, i.e. runnable instances of a Unit
 */
public class UnitScheduler {

    private UnitInstance stackPointer;
    private final LinkedList<UnitInstance> units = new LinkedList<>();

    public UnitScheduler() {

    }

    /**
     * Current UnitSession pointed at by the scheduler
     *
     * @return null if no UnitSession has been scheduled currently
     */
    public UnitInstance current() {
        return stackPointer;
    }

    /**
     * Schedules a UnitSession for execution
     * @param session
     */
    public void schedule(UnitInstance session) {
        stackPointer = null;
        units.push(session);
    }

    /**
     * Schedules a Unit for execution after an insertion point
     * @param candidate
     * @param isInsertionPoint true when the given UnitSession is the desired insertion point
     */
    public void scheduleAfter(UnitInstance candidate, Predicate<UnitInstance> isInsertionPoint) {
        UnitInstance current = this.current();
        if (current != null && isInsertionPoint.test(current)) {
            units.push(current);
            units.push(candidate);
        } else {
            int idx = indexOf(units, isInsertionPoint);
            units.add(idx, candidate);
        }
    }

    /**
     * Advances the internal stack pointer, returns the next active UnitSession
     * for execution
     */
    public UnitInstance next() {
        if (stackPointer != null) {
            units.addAll(stackPointer.references());
        }
        stackPointer = units.poll();
        while (stackPointer != null &&
                stackPointer.state() == UnitInstance.State.Completed) {
            stackPointer = units.poll();
        }
        return stackPointer;
    }

    /**
     * Halts the (current) session
     */
    public void halt() {
        current().halt();
    }

    @Override
    public String toString() {
        return "UnitScheduler(" + stackPointer + "::" + units + ")";
    }

    private static <T> int indexOf(List<T> queue, Predicate<T> isCandidate) {
        for (int i = 0; i < queue.size(); i++) {
            T el = queue.get(i);
            if (isCandidate.test(el)) {
                return i;
            }
        }
        return -1;
    }
}
