package org.kie.units.example;

import java.util.Optional;

import org.drools.units.RuleUnit;
import org.drools.units.RuleUnitInstanceSignal;
import org.drools.units.RuleUnitSubsystem;
import org.jbpm.units.ProcessUnit;
import org.jbpm.units.ProcessUnitInstanceSignal;
import org.jbpm.units.ProcessUnitSubsystem;
import org.kie.api.UnitExecutor;
import org.kie.api.UnitInstance;
import org.kie.api.UnitInstanceSignal;
import org.kie.api.UnitSubsystem;

// this could be code-gen'd by a processor
public class GeneratedUnitSupport implements UnitSubsystem {

    private final UnitSubsystem process;
    private final UnitSubsystem rule;

    public GeneratedUnitSupport(UnitExecutor executor) {
        this.process = new ProcessUnitSubsystem(executor);
        this.rule = new RuleUnitSubsystem(executor);
    }

    @Override
    public void signal(UnitInstanceSignal signal) {
        if (signal instanceof ProcessUnitInstanceSignal) {
            process.signal(signal);
        } else if (signal instanceof RuleUnitInstanceSignal) {
            rule.signal(signal);
        } else {
            process.signal(signal);
            rule.signal(signal);
        }
    }

    @Override
    public Optional<UnitInstance> createInstance(UnitInstance.Proto proto) {
        if (proto.unit() instanceof ProcessUnit) {
            return process.createInstance(proto);
        }
        if (proto.unit() instanceof RuleUnit) {
            return rule.createInstance(proto);
        }
        return Optional.empty();
    }
}
