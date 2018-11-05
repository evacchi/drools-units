package org.kie.api;

public interface UnitExecutor {

    /**
     * Schedule for execution the given Unit, and keeps running
     * until the system reaches a stable state
     */

    void run(UnitInstance.Proto proto);

    UnitInstance current();

    void signal(Signal signal);

    interface Signal {

        /**
         * A signal that is scoped to an instance of the given Proto
         */
        interface Scoped extends Signal {
            UnitInstance.Proto proto();
            void exec(UnitInstance instance, UnitScheduler scheduler);
        }

        /**
         * A signal that may have effect on any element of the scheduler
         */
        interface Broacast extends Signal {
            void exec(UnitScheduler scheduler);
        }
    }
}
