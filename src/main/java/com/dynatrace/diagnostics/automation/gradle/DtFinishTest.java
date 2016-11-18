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

import com.dynatrace.sdk.server.testautomation.TestAutomation;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.java.archives.Attributes;
import org.gradle.tooling.BuildException;

import java.text.MessageFormat;

/**
 * Gradle task to start a test run
 */
public class DtFinishTest extends DtServerProfileBase {
    public static final String NAME = "DtFinishTest";

    /* properties */
    @Input
    private String testRunId;

    /**
     * Flag to make this task fail on error. Default: true
     */
    @Input
    @Optional
    private boolean failOnError = true;

    /**
     * Executes gradle task
     *
     * @throws BuildException whenever connecting to the server, parsing a response or execution fails
     */
    @TaskAction
    public void executeTask() throws BuildException {
        try {

            TestAutomation testAutomation = new TestAutomation(this.getDynatraceClient());
            testAutomation.finishTestRun(this.getProfileName(),testRunId);

            this.getLogger().log(LogLevel.INFO, MessageFormat.format("Finish testRun profile %s with testRun ID=%s", this.getProfileName(),testRunId));
        } catch (Exception e) {
            if (this.failOnError) {
                if (e instanceof BuildException) {
                    throw (BuildException) e;
                }
                throw new BuildException(e.getMessage(), e);
            }
            this.getLogger().log(LogLevel.ERROR, String.format("Exception when finishing testRun profile %s with testRun ID=%s", this.getProfileName(),testRunId), e);
        }
    }

    public final String getTestRunId() { return  testRunId; }

    public final void setTestRunId(String testRunId) {
        this.testRunId = testRunId;
    }
}
