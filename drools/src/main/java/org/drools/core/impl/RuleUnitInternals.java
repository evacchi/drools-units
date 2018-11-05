package org.drools.core.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.drools.core.SessionConfigurationImpl;
import org.drools.core.common.AgendaGroupQueueImpl;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.datasources.InternalDataSource;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.spi.AgendaGroup;
import org.drools.units.GuardedRuleUnitSession;
import org.drools.units.RuleUnit;
import org.drools.units.RuleUnitInstance;
import org.drools.units.internal.LegacyRuleUnitExecutor;
import org.kie.api.UnitBinding;
import org.kie.api.KieBase;
import org.kie.api.UnitInstance;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.FactHandle;

import static org.drools.core.ruleunit.RuleUnitUtil.RULE_UNIT_ENTRY_POINT;
import static org.drools.core.util.ClassUtils.isAssignable;

public class RuleUnitInternals {

    /**
     * Maintains the binding between the currently active RuleUnit (by class)
     * and the unit entry point
     */
    public static class EntryPoint {

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

    public static class Factory {

        private final StatefulKnowledgeSessionImpl session;
        private final EntryPoint entryPoint;
        private final Registry registry;
        private InternalKnowledgeBase kiebase;
//        public final LegacyRuleUnitFactory ruleUnitFactory;

        public Factory(StatefulKnowledgeSessionImpl session) {
            this.session = session;
            this.kiebase = (InternalKnowledgeBase) session.getKieBase();
            this.entryPoint = new EntryPoint(session);
            this.registry = new Registry();
//            this.ruleUnitFactory = new LegacyRuleUnitFactory();

            this.session.init(
                    new SessionConfigurationImpl(),
                    EnvironmentFactory.newEnvironment());

            this.session.agendaEventSupport = new AgendaEventSupport();
            this.session.ruleRuntimeEventSupport = new RuleRuntimeEventSupport();
            this.session.ruleEventListenerSupport = new RuleEventListenerSupport();
        }

        public Factory setLegacyExecutor(LegacyRuleUnitExecutor legacyExecutor) {
            this.session.ruleUnitExecutor = legacyExecutor;
            return this;
        }

        /**
         * Instantiates a session for the given unit
         */
        public RuleUnitInstance create(RuleUnit ruleUnit, UnitBinding... binding) {
//            RuleUnit unit = ruleUnitFactory.injectUnitVariables(
//                    session.ruleUnitExecutor, ruleUnit);

            return registry.register(new RuleUnitInstance(
                    ruleUnit,
                    session,
                    entryPoint));
        }

        /**
         * Instantiates a guarded session for the given unit
         */
        public org.drools.units.GuardedRuleUnitSession createGuard(RuleUnit unit) {
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

    public static class Agenda {

        private static final String EMPTY_AGENDA = "$$$EMPTY_AGENDA$$";
        final StatefulKnowledgeSessionImpl session;

        public Agenda(StatefulKnowledgeSessionImpl session) {
            this.session = session;
        }

        public void focus(RuleUnit unit) {
            InternalAgenda agenda = session.getAgenda();
            InternalAgendaGroup unitGroup = (InternalAgendaGroup) agenda.getAgendaGroup(unit.getClass().getName());
            unitGroup.setAutoDeactivate(false);
            unitGroup.setFocus();
        }

        public void unfocus(RuleUnit unit) {
            InternalAgenda agenda = session.getAgenda();
            AgendaGroup agendaGroup = agenda.getAgendaGroup(unit.getClass().getName());
            agenda.getStackList().remove(agendaGroup);

            AgendaGroupQueueImpl unitGroup =
                    (AgendaGroupQueueImpl) session.getAgenda().getAgendaGroup(EMPTY_AGENDA);
            unitGroup.setAutoDeactivate(false);
            unitGroup.setKeepWhenEmpty(true);
            unitGroup.setFocus();
        }
    }
}
