package org.drools.units;

import java.util.HashSet;
import java.util.Set;

import org.drools.core.impl.RuleUnitInternals.EntryPoint;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.spi.Activation;
import org.kie.api.Unit;
import org.kie.api.UnitBinding;
import org.kie.api.UnitInstance;

public class GuardedRuleUnitInstance extends RuleUnitInstance {

    public static class Proto extends UnitInstance.Proto {
        public Proto(Unit unit, UnitBinding... bindings) {
            super(unit, bindings);
        }
    }

    private Set<Activation> activations = new HashSet<>();

    public GuardedRuleUnitInstance(
            RuleUnit unit,
            StatefulKnowledgeSessionImpl session,
            EntryPoint entryPoint) {
        super(unit, session, entryPoint);
    }

    public void addActivation(Activation activation) {
        activations.add(activation);
    }

    public void removeActivation(Activation activation) {
        activations.remove(activation);
    }

    public boolean isActive() {
        return !activations.isEmpty();
    }

    @Override
    public String toString() {
        return "GS:" + unit();
    }
}
