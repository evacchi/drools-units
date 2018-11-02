package org.drools.core.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.core.SessionConfigurationImpl;
import org.drools.core.common.AgendaGroupQueueImpl;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.ruleunit.RuleUnitDescription;
import org.drools.core.spi.AgendaGroup;
import org.drools.units.RuleUnit;
import org.drools.units.internal.LegacyRuleUnitFactory;
import org.drools.units.internal.RuleUnitSessionSignal;
import org.kie.api.KieBase;
import org.kie.api.Unit;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSessionSignal;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.rule.FactHandle;

import static org.drools.core.ruleunit.RuleUnitUtil.RULE_UNIT_ENTRY_POINT;

public class RuleUnitInstance implements UnitInstance {

    private boolean yielded = false;

    @Override
    public Unit unit() {
        return ruleUnit;
    }

    @Override
    public void start() {
        fireAllRules();
    }

    @Override
    public void halt() {
        unbindRuleUnit();
    }

    public void fireUntilHalt() {
        bindRuleUnit();
        try {
            session.fireUntilHalt();
        } finally {
            unbindRuleUnit();
        }
    }

    private final StatefulKnowledgeSessionImpl session;
    private final Agenda agenda;
    private final EntryPoint entryPoint;
    private RuleUnit ruleUnit;
    private RuleUnitDescription ruleUnitDescr;
    private final Set<UnitInstance> references = new HashSet<>();

    private AtomicBoolean suspended = new AtomicBoolean(false);

    public RuleUnitInstance(
            RuleUnit unit,
            StatefulKnowledgeSessionImpl session,
            EntryPoint entryPoint) {
        this.ruleUnit = unit;
        this.session = session;
        this.agenda = new Agenda(session);
        this.entryPoint = entryPoint;
    }

    public int fireAllRules() {
        bindRuleUnit();
        try {
            return session.fireAllRules();
        } finally {
            unbindRuleUnit();
        }
    }

    public void bindRuleUnit() {
        suspended.set(false);
        ruleUnit.onStart();

        entryPoint.bind(ruleUnit);

        this.ruleUnitDescr = session.kBase.getRuleUnitDescriptionRegistry().getDescriptionForUnit(ruleUnit);
        getGlobalResolver().setDelegate(new RuleUnitGlobals(ruleUnitDescr, ruleUnit));
        ruleUnitDescr.bindDataSources(session, ruleUnit);

        agenda.focus(ruleUnit);
    }

    private void unbindRuleUnit() {
        if (yielded) {
            yielded = false;
            return;
        }
        ruleUnitDescr.unbindDataSources(session, ruleUnit);
        (getGlobalResolver()).setDelegate(null);
        ruleUnit.onEnd();
        suspended.set(true);
    }

//    @Override
    public void yield(UnitInstance unit) {
        yielded = true;
        ruleUnit.onYield(unit.unit());
        session.getPropagationList().flush();
        agenda.unfocus(ruleUnit);
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
    public void signal(UnitSessionSignal signal) {
        if (signal instanceof RuleUnitSessionSignal) {
            signal.exec(this);
        }
    }

    @Override
    public State state() {
        return isActive() ? State.Active : State.Suspended;
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

    /**
     * Maintains the binding between the currently active RuleUnit (by class)
     * and the unit entry point
     */
    static class EntryPoint {

        private final StatefulKnowledgeSessionImpl session;
        private final Map<Class<?>, FactHandle> registry = new HashMap<>();

        public EntryPoint(StatefulKnowledgeSessionImpl session) {
            this.session = session;
        }

        public void bind(RuleUnit unit) {
            registry.computeIfAbsent(unit.getClass(),
                                     c -> session.getEntryPoint(RULE_UNIT_ENTRY_POINT).insert(unit));
        }
    }

    /**
     * Maintains a mapping between a RuleUnit (via RuleUnit.Identity)
     * and the corresponding UnitSession (if any)
     */
    private static class Registry {

        private final Map<org.kie.api.runtime.rule.RuleUnit.Identity, UnitInstance> registry = new HashMap<>();

        <T extends UnitInstance> T register(T session) {
            if (registry.containsKey(session.unit().getUnitIdentity())) {
                return (T) registry.get(session.unit().getUnitIdentity());
            } else {
                registry.put(session.unit().getUnitIdentity(), session);
                return session;
            }
        }

        public UnitInstance get(RuleUnit unit) {
            return registry.get(unit.getUnitIdentity());
        }
    }

    public static class Factory  {

        private final StatefulKnowledgeSessionImpl session;
        private final EntryPoint entryPoint;
        private final Registry registry;
        private InternalKnowledgeBase kiebase;
        public final LegacyRuleUnitFactory ruleUnitFactory;


        public Factory(StatefulKnowledgeSessionImpl session) {
            this.session = session;
            this.kiebase = (InternalKnowledgeBase) session.getKieBase();
            this.entryPoint = new EntryPoint(session);
            this.registry = new Registry();
            this.ruleUnitFactory = new LegacyRuleUnitFactory();

            this.session.init(
                    new SessionConfigurationImpl(),
                    EnvironmentFactory.newEnvironment());


            this.session.agendaEventSupport = new AgendaEventSupport();
            this.session.ruleRuntimeEventSupport = new RuleRuntimeEventSupport();
            this.session.ruleEventListenerSupport = new RuleEventListenerSupport();
        }

        public static Factory of(StatefulKnowledgeSessionImpl session) {
            return new Factory(session);
        }

        public Factory setLegacyExecutor(LegacyRuleUnitExecutor legacyExecutor) {
            this.session.ruleUnitExecutor = legacyExecutor;
            return this;
        }

        /**
         * Instantiates a session for the given unit
         */
        public RuleUnitInstance create(RuleUnit ruleUnit) {
            RuleUnit unit = ruleUnitFactory.injectUnitVariables(
                    session.ruleUnitExecutor, ruleUnit);

            return registry.register(new RuleUnitInstance(
                    unit,
                    session,
                    entryPoint));
        }

        /**
         * Instantiates a guarded session for the given unit
         */
        public GuardedRuleUnitSession createGuard(RuleUnit unit) {
            return registry.register(new GuardedRuleUnitSession(
                    unit,
                    session,
                    entryPoint));
        }

        public void bind(KieBase kiebase) {
            Objects.requireNonNull(kiebase);
            this.kiebase = (InternalKnowledgeBase) kiebase;
            this.session.handleFactory = this.kiebase.newFactHandleFactory();
            this.session.bindRuleBase(this.kiebase, null, false);
        }
    }

    private static class Agenda {

        private static final String EMPTY_AGENDA = "$$$EMPTY_AGENDA$$";
        final StatefulKnowledgeSessionImpl session;

        Agenda(StatefulKnowledgeSessionImpl session) {
            this.session = session;
        }

        void focus(RuleUnit unit) {
            InternalAgenda agenda = session.getAgenda();
            InternalAgendaGroup unitGroup = (InternalAgendaGroup) agenda.getAgendaGroup(unit.getClass().getName());
            unitGroup.setAutoDeactivate(false);
            unitGroup.setFocus();
        }

        void unfocus(RuleUnit unit) {
            InternalAgenda agenda = session.getAgenda();
            AgendaGroup agendaGroup = agenda.getAgendaGroup(unit.getClass().getName());
            agenda.getStackList().remove(agendaGroup);

            AgendaGroupQueueImpl unitGroup =
                    (AgendaGroupQueueImpl) session.getAgenda().getAgendaGroup(EMPTY_AGENDA);
            unitGroup.setAutoDeactivate(false);
//            unitGroup.setKeepWhenEmpty(true);
            unitGroup.setFocus();
        }
    }
}
