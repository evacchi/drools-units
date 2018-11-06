package org.drools.units.example.models;

public class BoxOffice {

    private boolean open;

    public BoxOffice(final boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(final boolean open) {
        this.open = open;
    }
}
