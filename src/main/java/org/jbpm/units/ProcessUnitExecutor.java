package org.jbpm.units;

import java.util.function.Function;

import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.KieBase;

public class ProcessUnitExecutor {

    public static ProcessUnitExecutor create() {
        return new ProcessUnitExecutor();
    }
    private final ProcessUnitScheduler scheduler = new ProcessUnitScheduler();
    private KieBase kieBase;
    private Function<ProcessUnit, ProcessUnitSession> sessionFactory;

    public ProcessUnitExecutor bind(KieBase kieBase) {
        this.kieBase = kieBase;
        this.sessionFactory = pu ->
                new ProcessUnitSessionDelegate(
                        pu, (StatefulKnowledgeSessionImpl) kieBase.newKieSession());

        return this;
    }

    public void run(ProcessUnit unit) {
        StatefulKnowledgeSessionImpl session =
                (StatefulKnowledgeSessionImpl) kieBase.newKieSession();
        ProcessUnitSessionDelegate pu =
                new ProcessUnitSessionDelegate(unit, session);

        ProcessUnitSession puSession = sessionFactory.apply(unit);
        runSession(puSession);

    }

    private int runSession(ProcessUnitSession session) {
        int fired = 0;
        scheduler.schedule(session);
        session = scheduler.next();
        while (session != null) {
            session.start();
            session = scheduler.next();
        }
        return fired;
    }

}
