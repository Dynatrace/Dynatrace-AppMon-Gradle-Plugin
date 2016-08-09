package com.dynatrace.diagnostics.automation.gradle;

import org.codehaus.groovy.runtime.MethodClosure;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

/**
 * This class implements "startup class" for Gradle plugin, defines tasks and properties (via extension)
 *
 * @author Lukasz Hamerszmidt
 */
public class GradleEntryPoint implements Plugin<Project> {
    public static final String DT_EXTENSION = "dynaTrace";

    public static final String DT_ACTIVATE_CONFIGURATION = "DtActivateConfiguration";
    public static final String DT_CLEAR_SESSION = "DtClearSession";
    public static final String DT_ENABLE_PROFILE = "DtEnableProfile";
    public static final String DT_GET_AGENT_INFO = "DtGetAgentInfo";
    public static final String DT_MEMORY_DUMP = "DtMemoryDump";
    public static final String DT_REANALYZE_SESSION = "DtReanalyzeSession";
    //public static final String DT_REPORT = "DtReport";
    public static final String DT_RESTART_COLLECTOR = "DtRestartCollector";
    public static final String DT_RESTART_SERVER = "DtRestartServer";
    //public static final String DT_REST_REPORT = "DtRESTReport";
    public static final String DT_SENSOR_PLACEMENT = "DtSensorPlacement";
    public static final String DT_START_RECORDING = "DtStartRecording";
    public static final String DT_START_TEST = "DtStartTest";
    public static final String DT_STOP_RECORDING = "DtStopRecording";
    public static final String DT_STORE_PURE_PATHS = "DtStorePurePaths";
    public static final String DT_THREAD_DUMP = "DtThreadDump";

    public static final String DT_INJECT_AGENT = "DtInjectAgent";

    @Override
    public void apply(Project project) {
        //properties registration
        project.getExtensions().create(DT_EXTENSION, GradleExtension.class);

        //tasks registration
        project.getTasks().create(DT_ACTIVATE_CONFIGURATION, DtActivateConfiguration.class);
        project.getTasks().create(DT_CLEAR_SESSION, DtClearSession.class);
        project.getTasks().create(DT_ENABLE_PROFILE, DtEnableProfile.class);
        project.getTasks().create(DT_GET_AGENT_INFO, DtGetAgentInfo.class);
        project.getTasks().create(DT_MEMORY_DUMP, DtMemoryDump.class);
        project.getTasks().create(DT_REANALYZE_SESSION, DtReanalyzeSession.class);
        //project.getTasks().create(DT_REPORT, DtReport.class);
        project.getTasks().create(DT_RESTART_COLLECTOR, DtRestartCollector.class);
        project.getTasks().create(DT_RESTART_SERVER, DtRestartServer.class);
       // project.getTasks().create(DT_REST_REPORT, DtRESTReport.class);
        project.getTasks().create(DT_SENSOR_PLACEMENT, DtSensorPlacement.class);
        project.getTasks().create(DT_START_RECORDING, DtStartRecording.class);
        project.getTasks().create(DT_START_TEST, DtStartTest.class);
        project.getTasks().create(DT_STOP_RECORDING, DtStopRecording.class);
        project.getTasks().create(DT_STORE_PURE_PATHS, DtStorePurePaths.class);
        project.getTasks().create(DT_THREAD_DUMP, DtThreadDump.class);
        project.getTasks().create(DT_INJECT_AGENT, DtInjectAgent.class);

        //inject additional dependencies
        project.afterEvaluate(new MethodClosure(this, "pluginConfiguration"));
    }

    private void pluginConfiguration(Project proj) {
        Task injectAgentAsSimpleTask = proj.getTasks().getByName(DT_INJECT_AGENT);

        //inject task dependency
        if (injectAgentAsSimpleTask != null) {
            if (injectAgentAsSimpleTask instanceof DtInjectAgent) {
                DtInjectAgent dtInjectAgent = (DtInjectAgent) injectAgentAsSimpleTask;

                //DtStartTest as dependency
                Task dtStartTest = proj.getTasks().getByName(DT_START_TEST);

                if (dtStartTest != null) {
                    dtInjectAgent.dependsOn(dtStartTest);
                }

                //configure injection
                String injectionTaskName = dtInjectAgent.getInjectionTaskName();
                boolean isInjectionEnabled = dtInjectAgent.isInjectionEnabled();

                if (isInjectionEnabled) {
                    if (injectionTaskName != null) {
                        Task taskToInjectTo = proj.getTasks().getByName(injectionTaskName);

                        if (taskToInjectTo != null) {
                            taskToInjectTo.dependsOn(injectAgentAsSimpleTask);
                        }
                    }
                }

            }
        }
    }
}