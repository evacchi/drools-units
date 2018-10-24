package org.jbpm.units;

import java.util.Collections;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

public class ProcessUnitSessionDelegate implements ProcessUnitSession {

    private final KieSession session;
    private final ProcessUnit processUnit;
    private ProcessInstance processInstance;

    public ProcessUnitSessionDelegate(
            ProcessUnit processUnit,
            KieSession session) {
        String processId = processUnit.getUnitIdentity().toString();
        this.processUnit = processUnit;
        this.session = session;
        this.processInstance = session.createProcessInstance(
                processId, Collections.emptyMap());
    }

    public ProcessUnit unit() {
        return processUnit;
    }

    public void start() {
        processUnit.onStart();
        session.startProcessInstance(processInstance.getId());
    }

    @Override
    public void halt() {
        session.abortProcessInstance(id());
    }

    @Override
    public long id() {
        return processInstance.getId();
    }

    @Override
    public void signal(String type, Object value) {
        processInstance.signalEvent(type, value);
    }
}
