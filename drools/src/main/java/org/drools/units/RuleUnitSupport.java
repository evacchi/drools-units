package org.drools.units;

import java.util.Optional;

import org.drools.core.impl.RuleUnitInternals.Factory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.ruleunit.RuleUnitFactory;
import org.drools.units.internal.LegacyRuleUnitExecutor;
import org.kie.api.Unit;
import org.kie.api.UnitBinding;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSupport;
import org.kie.api.internal.LegacySessionWrapper;
import org.kie.api.runtime.KieSession;

public class RuleUnitSupport implements UnitSupport {

    private final Factory delegate;
    private final KieSession session;
    private final RuleUnitFactory ruleUnitFactory;
    private final LegacyRuleUnitExecutor legacyExecutor;

    public RuleUnitSupport(UnitExecutor executor) {
        this.session = ((LegacySessionWrapper) executor).getSession();
        this.delegate = new Factory((StatefulKnowledgeSessionImpl) session);
        ruleUnitFactory = new RuleUnitFactory();
        legacyExecutor = new LegacyRuleUnitExecutor(executor);
        this.delegate.setLegacyExecutor(legacyExecutor);
        this.delegate.bind(((StatefulKnowledgeSessionImpl) session).getKnowledgeBase());
    }

    public Optional<UnitInstance> createInstance(Unit unit, UnitBinding... bindings) {
        if (unit instanceof RuleUnit) {
            RuleUnitInstance instance =
                    delegate.create((RuleUnit) unit, bindings);
            for (UnitBinding binding : bindings) {
                ruleUnitFactory.bindVariable(binding.name(), binding.value());
            }
            ruleUnitFactory.injectUnitVariables(legacyExecutor, unit);
            return Optional.of(instance);
        } else {
            return Optional.empty();
        }
    }
}