package org.drools.units.bayes;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.drools.beliefs.bayes.BayesBeliefSystem;
import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.BayesModeFactoryImpl;
import org.drools.beliefs.bayes.runtime.BayesRuntime;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.rule.EntryPointId;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitInstanceSignal;
import org.kie.api.UnitSubsystem;
import org.kie.api.internal.LegacySessionWrapper;
import org.kie.api.runtime.KieSession;

public class BayesUnitSubsystem implements UnitSubsystem {

    private final KieSession session;
    private final Set<BayesUnitInstance> instances;
    private final NamedEntryPoint entryPoint;
    private final BayesBeliefSystem bayesBeliefSystem;
    private final BayesModeFactoryImpl bayesModeFactory;
    private final BayesRuntime runtime;

    public BayesUnitSubsystem(UnitExecutor executor) {
        this.instances = new HashSet<>();
        this.session = ((LegacySessionWrapper) executor).getSession();
        this.entryPoint = (NamedEntryPoint) session.getEntryPoint(EntryPointId.DEFAULT.getEntryPointId());
        this.bayesBeliefSystem = new BayesBeliefSystem(entryPoint, entryPoint.getTruthMaintenanceSystem());
        this.bayesModeFactory = new BayesModeFactoryImpl(bayesBeliefSystem);
        this.session.setGlobal( "bsFactory", bayesModeFactory);
        this.runtime = session.getKieRuntime(BayesRuntime.class);
    }

    public Optional<UnitInstance> createInstance(UnitInstance.Proto proto) {
        if (proto.unit() instanceof BayesUnit) {
            BayesUnit bayesUnit = (BayesUnit) proto.unit();
            // FIXME: we are ignoring the instance to use the class-based API
            //        consider adding/passing to the class-based API for uniformity
            //        we assume that UNIT == Bayes
            BayesInstance bayesInstance =
                    runtime.createInstance(bayesUnit.getClass());
            BayesUnitInstance instance =
                    new BayesUnitInstance(bayesUnit, bayesInstance, runtime, session);
            instances.add(instance);
            return Optional.of(instance);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void signal(UnitInstanceSignal signal) {
        instances.forEach(i -> i.signal(signal));
    }
}