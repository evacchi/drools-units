package org.drools.units.example;

import org.drools.units.RuleUnitSupport;
import org.junit.Test;
import org.kie.api.KieUnitExecutor;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.api.UnitInstance.State.Completed;
import static org.kie.api.UnitInstance.State.Created;
import static org.kie.api.UnitInstance.State.Entering;
import static org.kie.api.UnitInstance.State.Resuming;
import static org.kie.api.UnitInstance.State.Running;

public class RuleUnitTest {

    @Test
    public void lifecycle() throws Exception {

        HelloUnit u = new HelloUnit("Ed");

        KieUnitExecutor kieUnitExecutor =
                KieUnitExecutor.create(RuleUnitSupport::new);

        kieUnitExecutor.run(u);

        assertEquals(
                asList(Created, Entering, Resuming, Running, Completed),
                u.stateSequence);
    }
}
