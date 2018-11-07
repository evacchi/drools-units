package org.drools.units;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.RuleUnitInternals.Agenda;
import org.drools.core.impl.RuleUnitInternals.EntryPoint;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.ruleunit.RuleUnitDescription;
import org.kie.api.Unit;
import org.kie.api.UnitInstance;
import org.kie.api.runtime.Globals;

public class RuleUnitInstance implements UnitInstance {

    interface Signal extends UnitInstance.Signal { }

    private State state;
    private final StatefulKnowledgeSessionImpl session;
    private final Agenda agenda;
    private final EntryPoint entryPoint;
    private RuleUnit ruleUnit;
    private RuleUnitDescription ruleUnitDescr;
    private final Set<UnitInstance> references = new HashSet<>();

    public RuleUnitInstance(
            RuleUnit unit,
            StatefulKnowledgeSessionImpl session,
            EntryPoint entryPoint) {
        this.ruleUnit = unit;
        this.session = session;
        this.agenda = new Agenda(session);
        this.entryPoint = entryPoint;

        this.state = State.Created;
        unit.onCreate();
    }

    @Override
    public Unit unit() {
        return ruleUnit;
    }

    @Override
    public void start() {
        bindRuleUnit();
        try {
            state = State.Running;
            ruleUnit.onStart();
            session.fireAllRules();
        } finally {
            unbindRuleUnit();
        }
    }

    @Override
    public void halt() {
        unbindRuleUnit();
    }

    @Override
    public void suspend() {
        if (state != State.Suspended) {
            state = State.Suspended;
            ruleUnit.onSuspend();
        }
    }

    @Override
    public void resume() {
        if (state == State.Suspended) {
            state = State.Resuming;
            ruleUnit.onResume();
            state = State.ReEntering;
            ruleUnit.onReEnter();
        }
    }

    public void fireUntilHalt() {
        bindRuleUnit();
        try {
            session.fireUntilHalt();
        } finally {
            unbindRuleUnit();
        }
    }

    public void bindRuleUnit() {
        state = State.Entering;
        ruleUnit.onEnter();

        entryPoint.bind(ruleUnit);

        InternalKnowledgeBase kieBase = (InternalKnowledgeBase) session.getKieBase();
        this.ruleUnitDescr = kieBase.getRuleUnitDescriptionRegistry().getDescription(ruleUnit);
        getGlobalResolver().setDelegate(new RuleUnitGlobals(ruleUnitDescr, ruleUnit));
        ruleUnitDescr.bindDataSources(session, ruleUnit);

        agenda.focus(ruleUnit);
    }

    private void unbindRuleUnit() {
        if (state == State.Suspended) {
            return;
        }
        ruleUnitDescr.unbindDataSources(session, ruleUnit);
        getGlobalResolver().setDelegate(null);
        state = State.Exiting;
        ruleUnit.onExit();
        state = State.Completed;
        ruleUnit.onEnd();
    }

    @Override
    public void yield(UnitInstance unit) {
        state = State.Suspended;
        this.ruleUnit.onSuspend();
        this.ruleUnit.onYield(unit.unit());
        session.getPropagationList().flush();
        agenda.unfocus(this.ruleUnit);
    }

    //    @Override
//    public void dispose() {
//
//    }
//
//    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public Collection<UnitInstance> references() {
        return references;
    }

    //
//    @Override
//    public void suspend() {
//        session.onSuspend();
//    }
//
//    @Override
//    public void resume() {
//        session.onResume();
//    }
//
//    @Override
//    public Collection<?> getSessionObjects(ObjectFilter filter) {
//        return session.getObjects(filter);
//    }
//
    @Override
    public void signal(UnitInstance.Signal signal) {
        if (signal instanceof Signal) {
            signal.exec(this);
        }
    }

    @Override
    public State state() {
        return isActive() ? State.Running : State.Suspended;
    }

    private Globals getGlobalResolver() {
        return (Globals) session.getGlobalResolver();
    }

    private static class RuleUnitGlobals implements Globals {

        private final RuleUnitDescription ruDescr;
        private final RuleUnit ruleUnit;

        private RuleUnitGlobals(RuleUnitDescription ruDescr, RuleUnit ruleUnit) {
            this.ruDescr = ruDescr;
            this.ruleUnit = ruleUnit;
        }

        @Override
        public Object get(String identifier) {
            return ruDescr.getValue(ruleUnit, identifier);
        }

        @Override
        public void set(String identifier, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDelegate(Globals delegate) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<String> getGlobalKeys() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString() {
        return "S:" + unit().getClass().getSimpleName();
    }
}
