package org.kie.api;

public interface SystemUnitSessionSignal extends UnitSessionSignal {

    class Yield implements UnitSchedulerSignal {

        private final UnitSession session;
        private final Unit.Identity identity;

        private Yield(UnitSession session, Unit.Identity identity) {
            this.session = session;
            this.identity = identity;
        }

        @Override
        public void exec(UnitScheduler scheduler) {
            UnitSession current = scheduler.current();
            if (current != null) {
                current.yield(session);
            }
            scheduler.scheduleAfter(
                    session,
                    us -> us.unit()
                            .getUnitIdentity()
                            .equals(identity));
        }
    }
}
