package org.drools.units.example;

import org.drools.units.RuleUnit;
import org.kie.api.runtime.rule.DataSource;

public class HelloUnit implements RuleUnit {
    private String name;

    public HelloUnit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
