package org.jbpm.units;

import org.drools.core.impl.StatefulKnowledgeSessionImpl;

public class ProcessUnitSessionDelegate implements ProcessUnitSession {
    private final StatefulKnowledgeSessionImpl session;
    private final ProcessUnit processUnit;

    public ProcessUnitSessionDelegate(
            ProcessUnit processUnit,
            StatefulKnowledgeSessionImpl session) {
        this.processUnit = processUnit;
        this.session = session;
    }

    public ProcessUnit unit() { return processUnit; }
    public void start() {
        processUnit.onStart();
        session.startProcess(processUnit.processId());
    }
}
