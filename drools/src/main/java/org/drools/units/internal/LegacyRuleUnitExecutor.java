package org.drools.units.internal;

import java.util.Collection;
import java.util.HashMap;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.datasources.CursoredDataSource;
import org.drools.core.datasources.InternalDataSource;
import org.drools.core.impl.InternalRuleUnitExecutor;
import org.drools.core.spi.Activation;
import org.drools.units.GuardedUnitInstance;
import org.kie.api.KieBase;
import org.kie.api.Unit;
import org.kie.api.UnitBinding;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.internal.LegacySessionWrapper;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;

public class LegacyRuleUnitExecutor implements InternalRuleUnitExecutor {

    private final UnitExecutor unitExecutor;
    private HashMap<String, UnitBinding> bindings = new HashMap<>();

    public LegacyRuleUnitExecutor(UnitExecutor unitExecutor) {
        this.unitExecutor = unitExecutor;
    }

    @Override
    public int run(Class<? extends RuleUnit> ruleUnitClass) {
        Unit unit = createUnit(ruleUnitClass);
        unitExecutor.run(new UnitInstance.Proto(unit, bindings()));
        return 1;
    }

    private Unit createUnit(Class<? extends RuleUnit> ruleUnitClass) {
        try {
            return (Unit) ruleUnitClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private UnitBinding[] bindings() {
        return bindings.values().toArray(new UnitBinding[0]);
    }

    @Override
    public int run(RuleUnit ruleUnit) {
        unitExecutor.run(new UnitInstance.Proto((Unit) ruleUnit, bindings()));
        return 1;
    }

    @Override
    public RuleUnitExecutor bind(KieBase kiebase) {
        return this;
    }

    @Override
    public KieSession getKieSession() {
        return ((LegacySessionWrapper) unitExecutor).getSession();
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
        CursoredDataSource<T> dataSource = new CursoredDataSource<>((InternalWorkingMemory) getKieSession());
        for (T item : items) {
            dataSource.insert(item);
        }
        bindVariable(name, dataSource);
        return dataSource;
    }

    @Override
    public RuleUnitExecutor bindVariable(String name, Object value) {
        this.bindings.put(name, new UnitBinding(name, value));
        if (value instanceof InternalDataSource) {
            bindDataSource((InternalDataSource) value);
        }
        return this;
    }

    @Override
    public void dispose() {
        //unitExecutor.dispose();
    }

    @Override
    public void onSuspend() {
        UnitInstance current = unitExecutor.current();
        if (current != null) {
            current.suspend();
        }
    }

    @Override
    public void onResume() {
        UnitInstance current = unitExecutor.current();
        if (current != null) {
            current.resume();
        }
    }

    @Override
    public void switchToRuleUnit(Class<? extends RuleUnit> ruleUnitClass, Activation activation) {
        switchToRuleUnit(createUnit(ruleUnitClass), activation);
    }

    @Override
    public void switchToRuleUnit(RuleUnit ruleUnit, Activation activation) {
        UnitInstance.Proto proto = new UnitInstance.Proto((Unit) ruleUnit, bindings());
        UnitInstance current = unitExecutor.current();
        UnitInstance unitInstance = unitExecutor.create(proto);
        if (current != null) {
            current.yield(unitInstance);
        }
    }

    @Override
    public void guardRuleUnit(Class<? extends RuleUnit> ruleUnitClass, Activation activation) {
        guardRuleUnit(createUnit(ruleUnitClass), activation);
    }

    @Override
    public void guardRuleUnit(RuleUnit ruleUnit, Activation activation) {
        UnitInstance instance = unitExecutor.create(new UnitInstance.Proto((Unit) ruleUnit));
        // fixme we should probably keep track of guard instances (1:1 to ruleUnit)
        GuardedUnitInstance guarded = new GuardedUnitInstance(instance);
        // fixme maybe this is a signal:
        unitExecutor.current().references().add(guarded);
        guarded.addActivation(activation);
    }

    @Override
    public void cancelActivation(Activation activation) {
        unitExecutor.current().references()
                .stream()
                .filter(u -> u instanceof GuardedUnitInstance)
                .forEach(u -> ((GuardedUnitInstance) u).removeActivation(activation));
    }

    @Override
    public RuleUnit getCurrentRuleUnit() {
        UnitInstance current = unitExecutor.current();
        return current == null ? null : current.unit();
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
        dataSource.setWorkingMemory((InternalWorkingMemory) getKieSession());
    }
}
