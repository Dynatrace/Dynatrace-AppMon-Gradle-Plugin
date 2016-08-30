/*
 * Dynatrace Gradle Plugin
 * Copyright (c) 2008-2016, DYNATRACE LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  Neither the name of the dynaTrace software nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.sdk.server.agentsandcollectors.AgentsAndCollectors;
import com.dynatrace.sdk.server.agentsandcollectors.models.AgentInformation;
import com.dynatrace.sdk.server.agentsandcollectors.models.Agents;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

import java.util.List;

/**
 * Gradle task for getting information about agent
 */
public class DtGetAgentInfo extends DtServerBase {

    public static final String NAME = "DtGetAgentInfo";

    private int infoForAgentByIndex = -1;
    private String infoForAgentByName;

    private String agentName;
    private String agentHost;
    private int agentProcessId;
    private int agentCount;

    /**
     * Executes Gradle task
     *
     * @throws BuildException whenever connecting to the server, parsing a response or execution fails
     */
    @TaskAction
    public void executeTask() throws BuildException {
        this.getLogger().log(LogLevel.INFO, String.format("Execute with %s %s %s", this.getUsername(), this.getPassword(), this.getServerUrl()));

        AgentsAndCollectors agentsAndCollectors = new AgentsAndCollectors(this.getDynatraceClient());

        try {
            Agents agentsContainer = agentsAndCollectors.fetchAgents();
            List<AgentInformation> agentsList = agentsContainer.getAgents();

            this.getLogger().log(LogLevel.INFO, String.format("Set agentCount to %s", String.valueOf(agentsList.size())));
            this.agentCount = agentsList.size();

            AgentInformation agent = this.getAgentInformationByNameOrAtIndex(agentsList, this.infoForAgentByName, this.infoForAgentByIndex);

            if (agent != null) {
                this.getLogger().log(LogLevel.INFO, String.format("Return agent info: %s/%s/%s", agent.getName(), agent.getHost(), agent.getProcessId()));

                this.agentName = agent.getName();
                this.agentHost = agent.getHost();
                this.agentProcessId = agent.getProcessId();
            }

        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    /**
     * Returns agent information by given name and index found in list
     * <p>
     * If agent with given name was found, returns it, otherwise looks for agent at given index
     *
     * @param agents - list containing {@link AgentInformation}
     * @param name   - agent name to find in given list
     * @param index  - agent index to find in given list
     * @return {@link AgentInformation} that matches given index or name if it's found, otherwise returns {@code null}
     */
    private AgentInformation getAgentInformationByNameOrAtIndex(List<AgentInformation> agents, String name, int index) {
        AgentInformation agentInformation = this.getAgentInformationByName(agents, name);

        if (agentInformation != null) {
            return agentInformation;
        }

        return this.getAgentInformationAtIndex(agents, index);
    }

    /**
     * Returns agent information by index found in given list
     *
     * @param agents - list containing {@link AgentInformation}
     * @param index  - agent index to find in given list
     * @return {@link AgentInformation} that matches given index if it's found, otherwise returns {@code null}
     */
    private AgentInformation getAgentInformationAtIndex(List<AgentInformation> agents, int index) {
        if (index >= 0 && index < agents.size()) {
            return agents.get(index);
        }

        return null;
    }

    /**
     * Returns agent information by name found in given list
     *
     * @param agents - list containing {@link AgentInformation}
     * @param name   - agent name to find in given list
     * @return {@link AgentInformation} that matches given name if it's found, otherwise returns {@code null}
     */
    private AgentInformation getAgentInformationByName(List<AgentInformation> agents, String name) {
        if (name != null) {
            for (AgentInformation agent : agents) {
                if (agent.getName().equalsIgnoreCase(name)) {
                    return agent;
                }
            }
        }

        return null;
    }

    public int getInfoForAgentByIndex() {
        return infoForAgentByIndex;
    }

    public void setInfoForAgentByIndex(int infoForAgentByIndex) {
        this.infoForAgentByIndex = infoForAgentByIndex;
    }

    public String getInfoForAgentByName() {
        return infoForAgentByName;
    }

    public void setInfoForAgentByName(String infoForAgentByName) {
        this.infoForAgentByName = infoForAgentByName;
    }

    //properties
    public String getAgentName() {
        return agentName;
    }

    public String getAgentHost() {
        return agentHost;
    }

    public int getAgentProcessId() {
        return agentProcessId;
    }

    public int getAgentCount() {
        return agentCount;
    }
}
