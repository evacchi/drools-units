package org.kie.units.example;

import org.jbpm.units.ProcessUnit;

public class HelloProcessUnit implements ProcessUnit {
    private String name;

    public HelloProcessUnit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
