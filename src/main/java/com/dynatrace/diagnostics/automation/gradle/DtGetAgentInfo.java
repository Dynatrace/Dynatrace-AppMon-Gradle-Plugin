package com.dynatrace.diagnostics.automation.gradle;

import java.util.ArrayList;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.Agent;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

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
		
		ArrayList<Agent> agents = getEndpoint().getAgents();
		System.out.println("Set AgentCount to " + String.valueOf(agents.size())); //$NON-NLS-1$
		this.setAgentCount(agents.size());

		Agent agentForInfo = null;
		if (infoForAgentByIndex >= 0 && infoForAgentByIndex < agents.size()) {
			agentForInfo = agents.get(infoForAgentByIndex);
		}
		if (infoForAgentByName != null) {
			for(Agent agent : agents) {
				if(agent.getName().equalsIgnoreCase(infoForAgentByName))
					agentForInfo = agent;				
			}
		}

		if (agentForInfo != null) {
			this.setAgentName(agentForInfo.getName());
			this.setAgentHost(agentForInfo.getHost());
			this.setAgentProcessId(agentForInfo.getProcessId());
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
