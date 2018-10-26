package org.kie.api;

import org.kie.api.runtime.KieSession;

public class KieUnitExecutor implements UnitExecutor {

    private KieSession session;
    private final KieBase kieBase;
    private final UnitScheduler scheduler;
    private final TypeDrivenUnitSessionFactory sessionFactory;

    public KieUnitExecutor(KieSession session) {
        this.session = session;
        this.kieBase = session.getKieBase();
        this.scheduler = new UnitScheduler();
        this.sessionFactory = new TypeDrivenUnitSessionFactory(session, this);
    }

    public static KieUnitExecutor create(KieSession session) {
        return new KieUnitExecutor(session);
    }

    @Override
    public void run(Unit unit) {
        UnitSession unitSession = sessionFactory.create(unit);
        runSession(unitSession);
    }

    private void runSession(UnitSession session) {
        scheduler.schedule(session);
        session = scheduler.next();
        while (session != null) {
            session.start();
            session = scheduler.next();
        }
    }
}
