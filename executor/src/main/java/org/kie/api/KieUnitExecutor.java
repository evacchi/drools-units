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

    @Override
    public void run(Unit unit) {
        UnitInstance unitInstance = sessionFactory.createInstance(unit)
                .orElseThrow(() -> new UnsupportedOperationException(
                        "Unit type is not supported: " + unit.getClass()));
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
    public KieSession getSession() {
        return session;
    }
}
