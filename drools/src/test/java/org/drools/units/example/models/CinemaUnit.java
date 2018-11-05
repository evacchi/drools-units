package org.drools.units.example.models;

import org.drools.units.RuleUnit;
import org.kie.api.runtime.rule.DataSource;

public class CinemaUnit implements RuleUnit {

    private DataSource<Cinema> cinema;

    public DataSource<Cinema> getCinema() {
        return cinema;
    }
}
