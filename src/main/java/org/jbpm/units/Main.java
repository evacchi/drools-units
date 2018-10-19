package org.jbpm.units;

import org.kie.api.KieBase;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

public class Main {

    public static void main(String[] args) {
        new Main().main();
    }
    void main() {

        KieHelper kieHelper = new KieHelper();
        KieBase kieBase = kieHelper
                .addResource(ResourceFactory.newClassPathResource("sample.bpmn2"))
                .build();

        HelloUnit u = new HelloUnit();

        ProcessUnitExecutor.create()
                .bind(kieBase)
                .run(u);
    }
}
