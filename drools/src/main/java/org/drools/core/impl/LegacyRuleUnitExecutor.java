package org.drools.core.impl;

import java.util.Collection;

import org.drools.core.datasources.InternalDataSource;
import org.drools.core.spi.Activation;
import org.drools.units.internal.RuleUnitSignal;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.Unit;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitSchedulerSignal;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;

public class LegacyRuleUnitExecutor implements InternalRuleUnitExecutor {

    private final UnitExecutor unitExecutor;
    private final RuleUnitInstance.Factory ruleUnitSessionFactory;

    public LegacyRuleUnitExecutor(UnitExecutor unitExecutor, RuleUnitInstance.Factory ruleUnitSessionFactory) {
        this.unitExecutor = unitExecutor;
        this.ruleUnitSessionFactory = ruleUnitSessionFactory;
    }

    @Override
    public int run(Class<? extends RuleUnit> ruleUnitClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int run(RuleUnit ruleUnit) {
        unitExecutor.run((Unit) ruleUnit);
        return 1;
    }

    @Override
    public RuleUnitExecutor bind(KieBase kiebase) {
//        unitExecutor.bind(kiebase);
        return this;
    }

    @Override
    public KieSession getKieSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void runUntilHalt(Class<? extends RuleUnit> ruleUnitClass) {
//        unitExecutor.runUntilHalt(ruleUnitClass);
    }

    @Override
    public void runUntilHalt(RuleUnit ruleUnit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void halt() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> DataSource<T> newDataSource(String name, T... items) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RuleUnitExecutor bindVariable(String name, Object value) {
//        return this;
        throw new UnsupportedOperationException();
    }

    @Override
    public void dispose() {
        //unitExecutor.dispose();
    }

    @Override
    public void onSuspend() {
        UnitSchedulerSignal signal = RuleUnitSignal.suspend();
//        unitExecutor.scheduler.signal(signal);
    }

    @Override
    public void onResume() {
        UnitSchedulerSignal signal = RuleUnitSignal.resume();
//        unitExecutor.scheduler.signal(signal);
    }

    @Override
    public void switchToRuleUnit(Class<? extends RuleUnit> ruleUnitClass, Activation activation) {
//        RuleUnit ruleUnit = unitExecutor.ruleUnitFactory.getOrCreateRuleUnit(this, ruleUnitClass);
//        switchToRuleUnit(ruleUnit, activation);
    }

    @Override
    public void switchToRuleUnit(RuleUnit ruleUnit, Activation activation) {
//        RuleUnitSignal.YieldSignal yieldSignal =
//                RuleUnitSignal.yield(
//                        ruleUnitSessionFactory.create(ruleUnit),
//                        activation.getRule().getRuleUnitClassName());
//        unitExecutor.scheduler.signal(yieldSignal);
    }

    @Override
    public void guardRuleUnit(Class<? extends RuleUnit> ruleUnitClass, Activation activation) {
//        RuleUnit ruleUnit = unitExecutor.ruleUnitFactory.getOrCreateRuleUnit(this, ruleUnitClass);
//        guardRuleUnit(ruleUnit, activation);
    }

    @Override
    public void guardRuleUnit(RuleUnit ruleUnit, Activation activation) {
//        unitExecutor.ruleUnitFactory.registerUnit(this, ruleUnit);
//        GuardedRuleUnitSession guard =
//                ruleUnitSessionFactory.createGuard(ruleUnit);
//        UnitSchedulerSignal signal =
//                RuleUnitSignal.registerGuard(guard, activation);
//        unitExecutor.scheduler.signal(signal);
    }

    @Override
    public void cancelActivation(Activation activation) {
//        RuleUnitSignal.UnregisterGuardSignal signal =
//                RuleUnitSignal.unregisterGuard(activation);
//        unitExecutor.scheduler.signal(signal);
    }

    @Override
    public RuleUnit getCurrentRuleUnit() {
//        UnitSession current = unitExecutor.scheduler.current();
//        return current == null ? null : current.unit();
        throw new UnsupportedOperationException();
    }

    @Override
    public KieRuntimeLogger addConsoleLogger() {
        throw new UnsupportedOperationException();
//        return KieServices.Factory.get().getLoggers().newConsoleLogger(unitExecutor.getKieSession());
    }

    @Override
    public KieRuntimeLogger addFileLogger(String fileName) {
//        return KieServices.Factory.get().getLoggers().newFileLogger(unitExecutor.getKieSession(), fileName);
        throw new UnsupportedOperationException();
    }

    @Override
    public KieRuntimeLogger addFileLogger(String fileName, int maxEventsInMemory) {
        throw new UnsupportedOperationException();//return KieServices.Factory.get().getLoggers().newFileLogger(unitExecutor.getKieSession(), fileName, maxEventsInMemory);
    }

    @Override
    public KieRuntimeLogger addThreadedFileLogger(String fileName, int interval) {
//        return KieServices.Factory.get().getLoggers().newThreadedFileLogger(unitExecutor.getKieSession(), fileName, interval);
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<?> getSessionObjects() {
        throw new UnsupportedOperationException();
        //return unitExecutor.scheduler.current().getSessionObjects(null);
    }

    @Override
    public Collection<?> getSessionObjects(ObjectFilter filter) {
        throw new UnsupportedOperationException();// return unitExecutor.scheduler.current().getSessionObjects(filter);
    }

    @Override
    public void bindDataSource(InternalDataSource dataSource) {
        throw new UnsupportedOperationException();//unitExecutor.bindDataSource(dataSource);
    }
}
