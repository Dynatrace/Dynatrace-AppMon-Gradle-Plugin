package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.sdk.server.agentsandcollectors.AgentsAndCollectors;
import com.dynatrace.sdk.server.agentsandcollectors.models.AgentInformation;
import com.dynatrace.sdk.server.agentsandcollectors.models.Agents;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

import java.util.List;

public class DtGetAgentInfo extends DtServerBase {
	private int infoForAgentByIndex = -1;
	private String infoForAgentByName;

	//properties TODO - add more items?
	private String agentName = null;
	private String agentHost = null;
	private int agentProcessId;
	private int agentCount;

	@TaskAction
	public void executeTask() throws BuildException {
		System.out.println("Execute with " + getUsername() + " " + getPassword() + " " + getServerUrl()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        try {
            AgentsAndCollectors agentsAndCollectors = new AgentsAndCollectors(this.getDynatraceClient());
            Agents agentsContainer = agentsAndCollectors.fetchAgents();
            List<AgentInformation> agents = agentsContainer.getAgents();

            System.out.println("Set AgentCount to " + String.valueOf(agents.size())); //$NON-NLS-1$
            this.setAgentCount(agents.size());

            AgentInformation agentForInfo = null;
            if (infoForAgentByIndex >= 0 && infoForAgentByIndex < agents.size()) {
                agentForInfo = agents.get(infoForAgentByIndex);
            }
            if (infoForAgentByName != null) {
                for (AgentInformation agent : agents) {
                    if (agent.getName().equalsIgnoreCase(infoForAgentByName))
                        agentForInfo = agent;
                }
            }

            if (agentForInfo != null) {
                this.setAgentName(agentForInfo.getName());
                this.setAgentHost(agentForInfo.getHost());
                this.setAgentProcessId(agentForInfo.getProcessId());
            }

        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }


	public void setInfoForAgentByIndex(int infoForAgentByIndex) {
		this.infoForAgentByIndex = infoForAgentByIndex;
	}

	public int getInfoForAgentByIndex() {
		return infoForAgentByIndex;
	}

	public void setInfoForAgentByName(String infoForAgentByName) {
		this.infoForAgentByName = infoForAgentByName;
	}

	public String getInfoForAgentByName() {
		return infoForAgentByName;
	}

	//properties
	public String getAgentName() {
		return agentName;
	}

	private void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgentHost() {
		return agentHost;
	}

	private void setAgentHost(String agentHost) {
		this.agentHost = agentHost;
	}

	public int getAgentProcessId() {
		return agentProcessId;
	}

	private void setAgentProcessId(int agentProcessId) {
		this.agentProcessId = agentProcessId;
	}

	public int getAgentCount() {
		return agentCount;
	}

	private void setAgentCount(int agentCount) {
		this.agentCount = agentCount;
	}
}
