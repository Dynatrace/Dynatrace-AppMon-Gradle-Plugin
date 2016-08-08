package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.diagnostics.automation.rest.sdk.RESTEndpoint;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;


public class DtSensorPlacement extends DtServerBase{
	private int agentId;

	@TaskAction
	public void executeTask() throws BuildException {
		RESTEndpoint endpoint=getEndpoint();
		endpoint.hotSensorPlacement(agentId);
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
}
