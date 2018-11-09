package org.drools.units.example;

import org.drools.units.RuleUnitSubsystem;
import org.junit.Test;
import org.kie.api.KieUnitExecutor;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.api.UnitInstance.State.Completed;
import static org.kie.api.UnitInstance.State.Created;
import static org.kie.api.UnitInstance.State.Entering;
import static org.kie.api.UnitInstance.State.Exiting;
import static org.kie.api.UnitInstance.State.Running;

public class RuleUnitTest {

    @Test
    public void lifecycle() throws Exception {

        HelloUnit u = new HelloUnit("Ed");

        KieUnitExecutor kieUnitExecutor =
                KieUnitExecutor.create(RuleUnitSubsystem::new);

        kieUnitExecutor.run(u);

        assertEquals(asList(Created, Entering, Running, Exiting, Completed),
                     u.stateSequence);
    }
}
