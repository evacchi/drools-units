package org.drools.units.example.models;

import java.util.List;

import org.drools.units.RuleUnit;
import org.kie.api.runtime.rule.DataSource;

public class TicketIssuerUnit implements RuleUnit {

    private DataSource<Person> persons;
    private DataSource<AdultTicket> tickets;

    private List<String> results;

    public TicketIssuerUnit() {
    }

    public TicketIssuerUnit(final DataSource<Person> persons, final DataSource<AdultTicket> tickets) {
        this.persons = persons;
        this.tickets = tickets;
    }

    public DataSource<Person> getPersons() {
        return persons;
    }

    public DataSource<AdultTicket> getTickets() {
        return tickets;
    }

    public List<String> getResults() {
        return results;
    }
}
