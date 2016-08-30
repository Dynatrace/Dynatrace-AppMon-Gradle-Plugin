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

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.resourcedumps.ResourceDumps;
import com.dynatrace.sdk.server.resourcedumps.models.CreateThreadDumpRequest;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

/**
 * Gradle task for taking thread dump
 */
public class DtThreadDump extends DtAgentBase {
    public static final String NAME = "DtThreadDump";

    private boolean sessionLocked;

    private int waitForDumpTimeout = 60000;
    private int waitForDumpPollingInterval = 5000;

    //properties
    private String dumpName = null;
    private boolean dumpFinished = false;

    /**
     * Executes gradle task
     *
     * @throws BuildException whenever connecting to the server, parsing a response or execution fails
     */
    @TaskAction
    public void executeTask() throws BuildException {
        this.getLogger().log(LogLevel.INFO, String.format("Creating Thread Dump for %s-%s-%s-%d", this.getProfileName(), this.getAgentName(), this.getHostName(), this.getProcessId()));

        ResourceDumps resourceDumps = new ResourceDumps(this.getDynatraceClient());
        CreateThreadDumpRequest createThreadDumpRequest = new CreateThreadDumpRequest(this.getProfileName(), this.getAgentName(), this.getHostName(), this.getProcessId());
        createThreadDumpRequest.setSessionLocked(this.isSessionLocked());

        try {
            this.dumpName = resourceDumps.createThreadDump(createThreadDumpRequest);
            this.dumpFinished = Boolean.TRUE.equals(resourceDumps.getThreadDumpStatus(this.getProfileName(), this.dumpName).getResultValue());

            int timeout = this.waitForDumpTimeout;

            while (!dumpFinished && (timeout > 0)) {
                try {
                    java.lang.Thread.sleep(this.waitForDumpPollingInterval);
                    timeout -= this.waitForDumpPollingInterval;
                } catch (InterruptedException e) {
                    /* don't break execution */
                }

                this.dumpFinished = Boolean.TRUE.equals(resourceDumps.getThreadDumpStatus(this.getProfileName(), this.dumpName).getResultValue());
            }
        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    public boolean isSessionLocked() {
        return sessionLocked;
    }

    public void setSessionLocked(boolean sessionLocked) {
        this.sessionLocked = sessionLocked;
    }

    public int getWaitForDumpTimeout() {
        return waitForDumpTimeout;
    }

    public void setWaitForDumpTimeout(int waitForDumpTimeout) {
        this.waitForDumpTimeout = waitForDumpTimeout;
    }

    public int getWaitForDumpPollingInterval() {
        return waitForDumpPollingInterval;
    }

    public void setWaitForDumpPollingInterval(int waitForDumpPollingInterval) {
        this.waitForDumpPollingInterval = waitForDumpPollingInterval;
    }

    //properties
    public String getDumpName() {
        return dumpName;
    }

    private void setDumpName(String dumpName) {
        this.dumpName = dumpName;
    }

    public boolean isDumpFinished() {
        return dumpFinished;
    }

    private void setDumpFinished(boolean dumpFinished) {
        this.dumpFinished = dumpFinished;
    }
}
