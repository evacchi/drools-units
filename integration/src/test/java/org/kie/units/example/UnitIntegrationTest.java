package org.kie.units.example;

import java.util.Optional;

import org.drools.units.RuleUnit;
import org.drools.units.RuleUnitSupport;
import org.jbpm.units.ProcessUnit;
import org.jbpm.units.ProcessUnitSupport;
import org.junit.Test;
import org.kie.api.KieUnitExecutor;
import org.kie.api.RuntimeUnitSupport;
import org.kie.api.Unit;
import org.kie.api.UnitInstance;
import org.kie.api.UnitSupport;

public class UnitIntegrationTest {

    @Test
    public void runtimeUnitSupport() {
        KieUnitExecutor kieUnitExecutor = KieUnitExecutor.create(
                RuntimeUnitSupport.register(
                        ProcessUnitSupport.get(),
                        RuleUnitSupport.get()));

        ProcessUnit pu = new HelloProcessUnit("Ed");
        RuleUnit ru = new HelloRuleUnit("Ed");
        kieUnitExecutor.run(pu);
        kieUnitExecutor.run(ru);
    }

    @Test
    public void staticUnitSupport() {
        KieUnitExecutor kieUnitExecutor = KieUnitExecutor.create(sess -> new UnitSupport() {
            UnitSupport process = ProcessUnitSupport.get().get(sess);
            UnitSupport rule = RuleUnitSupport.get().get(sess);
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
        });

        ProcessUnit pu = new HelloProcessUnit("Ed");
        RuleUnit ru = new HelloRuleUnit("Ed");
        kieUnitExecutor.run(pu);
        kieUnitExecutor.run(ru);
    }


}
