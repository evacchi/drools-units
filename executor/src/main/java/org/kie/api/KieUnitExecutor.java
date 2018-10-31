package org.kie.api;

import org.kie.api.runtime.KieSession;

public class KieUnitExecutor implements UnitExecutor {

    private KieSession session;
    private final KieBase kieBase;
    private final UnitScheduler scheduler;
    private final UnitSupport sessionFactory;

    public KieUnitExecutor(KieSession session, UnitSupport supportProvider) {
        this.session = session;
        this.kieBase = session.getKieBase();
        this.scheduler = new UnitScheduler();
        this.sessionFactory = supportProvider;
    }

    public static KieUnitExecutor create(UnitSupport.Provider factory) {
        KieSession session = KieServices.Factory.get()
                .getKieClasspathContainer()
                .getKieBase()
                .newKieSession();

        return new KieUnitExecutor(
                session, factory.get(session));
    }

    @Override
    public void run(Unit unit) {
        UnitInstance unitSession = sessionFactory.createInstance(unit);
        runSession(unitSession);
    }

    private void runSession(UnitInstance session) {
        scheduler.schedule(session);
        session = scheduler.next();
        while (session != null) {
            session.start();
            session = scheduler.next();
        }
    }
}
