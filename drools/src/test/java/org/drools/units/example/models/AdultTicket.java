package org.drools.units.example.models;

public class AdultTicket {

    private final Person person;

    public AdultTicket(final Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}
