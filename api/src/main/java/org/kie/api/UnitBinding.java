package org.kie.api;

public class UnitBinding {

    private final String name;
    private final Object value;

    public UnitBinding(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public Object value() {
        return value;
    }

    @Override
    public String toString() {
        return "UnitBinding{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
