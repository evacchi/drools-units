package org.jbpm.units;

public class HelloUnit implements ProcessUnit {

    @Override
    public String processId() {
        return "org.jbpm.HelloWorld";
    }
}
