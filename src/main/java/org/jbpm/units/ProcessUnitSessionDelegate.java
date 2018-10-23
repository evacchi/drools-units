package org.jbpm.units;

import org.kie.api.runtime.KieSession;

public class ProcessUnitSessionDelegate implements ProcessUnitSession {
    private final KieSession session;
    private final ProcessUnit processUnit;

    public ProcessUnitSessionDelegate(
            ProcessUnit processUnit,
            KieSession session) {
        this.processUnit = processUnit;
        this.session = session;
    }

    public ProcessUnit unit() { return processUnit; }
    public void start() {
        processUnit.onStart();
        session.startProcess(processUnit.processId());
    }

    @Override
    public void halt() {

    }

    @Override
    public <T> void signal(T signal) {

    }
}
