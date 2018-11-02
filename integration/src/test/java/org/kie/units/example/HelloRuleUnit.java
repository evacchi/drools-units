package org.kie.units.example;

import org.drools.units.RuleUnit;

public class HelloRuleUnit implements RuleUnit {
    private String name;

    public HelloRuleUnit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
