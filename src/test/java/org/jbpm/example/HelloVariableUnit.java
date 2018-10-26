package org.jbpm.example;

import org.jbpm.units.ProcessUnit;

public class HelloVariableUnit implements ProcessUnit {
    private int count = 0;

    public HelloVariableUnit(int count) {
        this.count = count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
