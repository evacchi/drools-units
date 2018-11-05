package org.kie.api;

import org.kie.api.internal.LegacySessionWrapper;
import org.kie.api.runtime.KieSession;

public class KieUnitExecutor implements UnitExecutor,
                                        LegacySessionWrapper {

    private KieSession session;
    private final KieBase kieBase;
    private final UnitScheduler scheduler;
    private final UnitSupport sessionFactory;

    public KieUnitExecutor(KieSession session, UnitSupport.Provider supportProvider) {
        this.session = session;
        this.kieBase = session.getKieBase();
        this.scheduler = new UnitScheduler();
        this.sessionFactory = supportProvider.get(this);
    }

    public static KieUnitExecutor create(UnitSupport.Provider factory) {
        KieSession session = KieServices.Factory.get()
                .getKieClasspathContainer()
                .getKieBase()
                .newKieSession();

        return new KieUnitExecutor(session, factory);
    }

    public void run(Unit u) {
        run(new UnitInstance.Proto(u));
    }

    @Override
    public void run(UnitInstance.Proto proto) {
        UnitInstance unitInstance = sessionFactory.createInstance(proto)
                .orElseThrow(() -> new UnsupportedOperationException(
                        "Unit type is not supported: " + proto.unit().getClass()));
        runSession(unitInstance);
    }

    private void runSession(UnitInstance session) {
        scheduler.schedule(session);
        session = scheduler.next();
        while (session != null) {
            session.start();
            session = scheduler.next();
        }
    }

    @Override
    public UnitInstance current() {
        return scheduler.current();
    }

    @Override
    public void signal(Signal sig) {
        if (sig instanceof Signal.Scoped) {
            Signal.Scoped signal = (Signal.Scoped) sig;
            UnitInstance instance = sessionFactory.createInstance(signal.proto())
                    .orElseThrow(() -> new UnsupportedOperationException(
                            "Unit type is not supported: " + signal.proto().unit().getClass()));
            signal.exec(instance, scheduler);
        } else if (sig instanceof Signal.Broacast) {
            Signal.Broacast signal = (Signal.Broacast) sig;
            signal.exec(scheduler);
        }
        // else discard
    }

    @Override
    public KieSession getSession() {
        return session;
    }
}
