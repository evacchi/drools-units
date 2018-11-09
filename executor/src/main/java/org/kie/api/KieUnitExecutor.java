package org.kie.api;

import org.kie.api.internal.LegacySessionWrapper;
import org.kie.api.runtime.KieSession;

public class KieUnitExecutor implements UnitExecutor,
                                        LegacySessionWrapper {

    private KieSession session;
    private final KieBase kieBase;
    private final UnitScheduler scheduler;
    private final UnitSubsystem instanceFactory;

    public KieUnitExecutor(KieSession session, UnitSubsystem.Provider supportProvider) {
        this.session = session;
        this.kieBase = session.getKieBase();
        this.scheduler = new UnitScheduler();
        this.instanceFactory = supportProvider.get(this);
    }

    public static KieUnitExecutor create(UnitSubsystem.Provider factory) {
        KieSession session = KieServices.Factory.get()
                .getKieClasspathContainer()
                .getKieBase()
                .newKieSession();

        return new KieUnitExecutor(session, factory);
    }

    public static KieUnitExecutor create(KieBase kieBase, UnitSubsystem.Provider factory) {
        KieSession session = kieBase.newKieSession();
        return new KieUnitExecutor(session, factory);
    }

    @Override
    public UnitInstance create(UnitInstance.Proto proto) {
        return instanceFactory.createInstance(proto)
                .orElseThrow(() -> new UnsupportedOperationException(
                        "Unit type is not supported: " + proto.unit().getClass()));
    }

    public UnitInstance run(Unit u) {
        return run(new UnitInstance.Proto(u));
    }

    @Override
    public UnitInstance run(UnitInstance.Proto proto) {
        UnitInstance unitInstance = create(proto);
        run(unitInstance);
        return unitInstance;
    }

    public void run(UnitInstance session) {
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
    public void signal(UnitInstanceSignal signal) {
        instanceFactory.signal(signal);
    }

    @Override
    public KieSession getSession() {
        return session;
    }
}
