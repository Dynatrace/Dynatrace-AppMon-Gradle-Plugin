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

import com.dynatrace.diagnostics.automation.util.DtUtil;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.memorydumps.MemoryDumps;
import com.dynatrace.sdk.server.memorydumps.models.AgentPattern;
import com.dynatrace.sdk.server.memorydumps.models.JobState;
import com.dynatrace.sdk.server.memorydumps.models.MemoryDumpJob;
import com.dynatrace.sdk.server.memorydumps.models.StoredSessionType;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Gradle task for taking memory dump
 */
public class DtMemoryDump extends DtAgentBase {
    public static final String NAME = "DtMemoryDump";

    private String dumpType = "memdump_simple";
    private boolean sessionLocked = false;

    private int waitForDumpTimeout = 60000;
    private int waitForDumpPollingInterval = 5000;
    private boolean doGc = false;
    private boolean autoPostProcess = false;
    private boolean capturePrimitives = false;
    private boolean captureStrings = false;

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
        this.getLogger().log(LogLevel.INFO, String.format("Creating Memory Dump for %s-%s-%s-%d", this.getProfileName(), this.getAgentName(), this.getHostName(), this.getProcessId()));

        MemoryDumps memoryDumps = new MemoryDumps(this.getDynatraceClient());

        MemoryDumpJob memoryDumpJob = new MemoryDumpJob();
        memoryDumpJob.setAgentPattern(new AgentPattern(this.getAgentName(), this.getHostName(), this.getProcessId()));
        memoryDumpJob.setSessionLocked(this.isSessionLocked());
        memoryDumpJob.setCaptureStrings(this.getCaptureStrings());
        memoryDumpJob.setCapturePrimitives(this.getCapturePrimitives());
        memoryDumpJob.setPostProcessed(this.getAutoPostProcess());
        memoryDumpJob.setDogc(this.getDoGc());

        if (this.getDumpType() != null) {
            memoryDumpJob.setStoredSessionType(StoredSessionType.fromInternal(this.getDumpType()));
        }

        try {
            String memoryDumpLocation = memoryDumps.createMemoryDumpJob(this.getProfileName(), memoryDumpJob);
            this.dumpName = this.extractMemoryDumpNameFromUrl(memoryDumpLocation);

            if (DtUtil.isEmpty(this.dumpName)) {
                throw new BuildException("Memory Dump wasn't taken", new Exception());
            }

            JobState memoryDumpJobState = memoryDumps.getMemoryDumpJob(this.getProfileName(), this.dumpName).getState();
            this.dumpFinished = memoryDumpJobState.equals(JobState.FINISHED) || memoryDumpJobState.equals(JobState.FAILED);

            int timeout = this.waitForDumpTimeout;

            while (!dumpFinished && (timeout > 0)) {
                try {
                    Thread.sleep(this.waitForDumpPollingInterval);
                    timeout -= this.waitForDumpPollingInterval;
                } catch (InterruptedException e) {
                    /* don't break execution */
                }

                memoryDumpJobState = memoryDumps.getMemoryDumpJob(this.getProfileName(), this.dumpName).getState();
                this.dumpFinished = memoryDumpJobState.equals(JobState.FINISHED) || memoryDumpJobState.equals(JobState.FAILED);
            }
        } catch (ServerResponseException e) {
            this.getLogger().log(LogLevel.ERROR, String.format("Cannot take memory dump: %s", e.getMessage()));
        } catch (ServerConnectionException | IllegalArgumentException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    /**
     * Extracts memory dump name from the dump url provided by {@link MemoryDumps#createMemoryDumpJob} method
     *
     * @param url - url of the memory dump
     * @return name of the dump required by {@link MemoryDumps#getMemoryDumpJob}
     * @throws IllegalArgumentException whenever given url isn't valid
     */
    private String extractMemoryDumpNameFromUrl(String url) throws IllegalArgumentException {
        try {
            URI location = new URI(url);

            String[] pathArray = location.getPath().split("/");

            if (pathArray.length <= 0) {
                throw new IllegalArgumentException("Missing memory dump name", new Exception());
            }

            return pathArray[pathArray.length - 1];
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Malformed memory dump name", new Exception());
        }
    }

    private boolean getCaptureStrings() {
        return captureStrings;
    }

    public void setCaptureStrings(boolean captureStrings) {
        this.captureStrings = captureStrings;
    }

    private boolean getCapturePrimitives() {
        return capturePrimitives;
    }

    public void setCapturePrimitives(boolean capturePrimitives) {
        this.capturePrimitives = capturePrimitives;
    }

    private boolean getAutoPostProcess() {
        return autoPostProcess;
    }

    public void setAutoPostProcess(boolean autoPostProcess) {
        this.autoPostProcess = autoPostProcess;
    }

    private boolean getDoGc() {
        return doGc;
    }

    public void setDoGc(boolean doGc) {
        this.doGc = doGc;
    }

    public String getDumpType() {
        return dumpType;
    }

    public void setDumpType(String dumpType) {
        this.dumpType = dumpType;
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

    public boolean isDumpFinished() {
        return dumpFinished;
    }
}
