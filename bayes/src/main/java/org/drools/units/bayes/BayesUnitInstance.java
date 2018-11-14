package org.drools.units.bayes;

import java.util.Collection;
import java.util.Collections;

import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.runtime.BayesRuntime;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.Unit;
import org.kie.api.UnitInstance;
import org.kie.api.UnitInstanceSignal;
import org.kie.api.runtime.KieSession;

public class BayesUnitInstance implements UnitInstance {

    private final BayesUnit unit;
    private final BayesInstance<?> bayesInstance;
    private final BayesRuntime runtime;
    private final KieSession session;

    public BayesUnitInstance(
            BayesUnit unit,
            BayesInstance<?> bayesInstance,
            BayesRuntime runtime,
            KieSession session) {
        this.unit = unit;
        this.bayesInstance = bayesInstance;
        this.runtime = runtime;
        this.session = session;
    }

    @Override
    public Unit unit() {
        return unit;
    }

    @Override
    public void start() {
        session.fireAllRules();

        if (bayesInstance.isDecided()) {
            bayesInstance.globalUpdate();
            bayesInstance.marginalize();
        }
    }

    @Override
    public void halt() {

    }

    @Override
    public Collection<UnitInstance> references() {
        return Collections.emptyList();
    }

    @Override
    public void yield(UnitInstance next) {

    }

    @Override
    public void suspend() {

    }

    @Override
    public void resume() {

    }

    @Override
    public State state() {
        return null;
    }

    @Override
    public void signal(UnitInstanceSignal signal) {

    }

    @Override
    public String toString() {
        return "B:" + unit().getClass().getSimpleName();
    }
}
