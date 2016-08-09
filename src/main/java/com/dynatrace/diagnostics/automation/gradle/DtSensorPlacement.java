package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.diagnostics.automation.rest.sdk.RESTEndpoint;
import com.dynatrace.sdk.server.agentsandcollectors.AgentsAndCollectors;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;


public class DtSensorPlacement extends DtServerBase{
	private int agentId;

	@TaskAction
	public void executeTask() throws BuildException {
		AgentsAndCollectors agentsAndCollectors = new AgentsAndCollectors(this.getDynatraceClient());

		try {
			agentsAndCollectors.placeHotSensor(agentId);
		} catch (ServerConnectionException | ServerResponseException e) {
			throw new BuildException(e.getMessage(), e);
		}
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
}
