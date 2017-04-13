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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

/**
 * "Startup class" for Gradle plugin, defines tasks and properties (via extension)
 */
public class GradleEntryPoint implements Plugin<Project> {

    /**
     * Defines available tasks
     *
     * @param project - gradle project
     */
    @Override
    public void apply(Project project) {
        /* properties registration */
        project.getExtensions().create(GradleExtension.NAME, GradleExtension.class);

        /* tasks registration */
        TaskContainer tasks = project.getTasks();

        tasks.create(DtActivateConfiguration.NAME, DtActivateConfiguration.class);
        tasks.create(DtEnableProfile.NAME, DtEnableProfile.class);
        tasks.create(DtRestartServer.NAME, DtRestartServer.class);
        tasks.create(DtStartRecording.NAME, DtStartRecording.class);
        tasks.create(DtStartTest.NAME, DtStartTest.class);
        tasks.create(DtStopRecording.NAME, DtStopRecording.class);
        tasks.create(DtStorePurePaths.NAME, DtStorePurePaths.class);
        tasks.create(DtFinishTest.NAME, DtFinishTest.class);
    }
}