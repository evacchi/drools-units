package org.kie.api;

import java.util.Arrays;
import java.util.Collection;

import org.kie.api.runtime.KieSession;

public class KieUnitExecutor implements UnitExecutor {

    private KieSession session;
    private final KieBase kieBase;
    private final UnitScheduler scheduler;
    private final TypeDrivenUnitSessionFactory sessionFactory;

    public KieUnitExecutor(KieSession session, Collection<UnitSupport.Provider> supportProvider) {
        this.session = session;
        this.kieBase = session.getKieBase();
        this.scheduler = new UnitScheduler();
        this.sessionFactory = new TypeDrivenUnitSessionFactory(session, this, supportProvider);
    }

    public static KieUnitExecutor create(UnitSupport.Provider... factories) {
        KieSession session = KieServices.Factory.get()
                .getKieClasspathContainer()
                .getKieBase()
                .newKieSession();

        return new KieUnitExecutor(session, Arrays.asList(factories));
    }

    @Override
    public void run(Unit unit) {
        UnitInstance unitSession = sessionFactory.create(unit);
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
