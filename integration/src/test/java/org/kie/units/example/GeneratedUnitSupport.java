package org.kie.units.example;

import java.util.Optional;

import org.drools.units.RuleUnit;
import org.drools.units.RuleUnitSupport;
import org.jbpm.units.ProcessUnit;
import org.jbpm.units.ProcessUnitSupport;
import org.kie.api.Unit;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSupport;

// this could be code-gen'd by a processor
public class GeneratedUnitSupport implements UnitSupport {

    private final UnitSupport process;
    private final UnitSupport rule;

    public GeneratedUnitSupport(UnitExecutor executor) {
        this.process = new ProcessUnitSupport(executor);
        this.rule = new RuleUnitSupport(executor);
    }

    @Override
    public Optional<UnitInstance> createInstance(Unit unit) {
        if (unit instanceof ProcessUnit) {
            return process.createInstance(unit);
        }
        if (unit instanceof RuleUnit) {
            return rule.createInstance(unit);
        }
        return Optional.empty();
    }
}
