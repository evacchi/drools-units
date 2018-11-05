package org.drools.units.example.models;

import org.drools.units.RuleUnit;
import org.kie.api.runtime.rule.DataSource;

public class BoxOfficeUnit implements RuleUnit {

        private DataSource<BoxOffice> boxOffices;

        public DataSource<BoxOffice> getBoxOffices() {
            return boxOffices;
        }
    }
