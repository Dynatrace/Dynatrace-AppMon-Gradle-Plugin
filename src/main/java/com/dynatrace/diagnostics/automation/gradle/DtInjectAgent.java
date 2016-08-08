package com.dynatrace.diagnostics.automation.gradle;

import org.gradle.api.Task;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

import java.util.ArrayList;

public class DtInjectAgent extends GradleTask {
	private final String JVM_ARGS_ARGUMENT_NAME = "-agentpath:";
	private final String JVM_ARGS_AGENT_NAME = "name";
	private final String JVM_ARGS_SERVER_NAME = "server";
	private final String JVM_ARGS_WAIT_NAME = "wait";
	private final String JVM_ARGS_TESTRUNID_NAME = "optionTestRunIdJava";

	private final String JVM_ARGS_KEY_VALUE_DELIMITER = "=";
	private final String JVM_ARGS_DELIMITER = ",";

	private final String JVM_ARGS_PROPERTY_NAME = "jvmArgs";

	private String agentPath = null;
	private String agentName = null;
	private String testRunId = null;
	private String server = "localhost"; //default
	private int wait = 5;

	private boolean injectionEnabled = false;
	private String injectionTaskName = "test";

	@TaskAction
	public void executeTask() throws BuildException {
		if (this.isInjectionEnabled()) {
			if (this.getInjectionTaskName() != null) {
				try {
					Task taskToInject = this.getProject().getTasks().getByName(this.getInjectionTaskName());

					if (taskToInject != null) {
						ArrayList<String> arrayList = new ArrayList<>();
						arrayList.add(this.getJvmArgs());

						taskToInject.setProperty(JVM_ARGS_PROPERTY_NAME, arrayList);
					}
				} catch (Exception ex) {
					throw new BuildException(ex.getMessage(), ex);
				}
			}
		}
	}

	private void checkParameters() throws IllegalArgumentException {
		if (this.getAgentPath() == null) {
			throw new IllegalArgumentException("Missing attribute for DtInjectAgent: agentPath");
		}

		if (this.getAgentName() == null) {
			throw new IllegalArgumentException("Missing attribute for DtInjectAgent: agentName");
		}

		if (this.getServer() == null) {
			throw new IllegalArgumentException("Missing attribute for DtInjectAgent: server");
		}
	}

	private String getJvmArgs() throws IllegalArgumentException {
		this.checkParameters();

		StringBuilder stringBuilder = new StringBuilder();

		// -agentpath:C:\\Program Files\\dynaTrace\\dynaTrace 6.3\\agent\\lib64\\dtagent.dll=
		stringBuilder.append(JVM_ARGS_ARGUMENT_NAME);
		stringBuilder.append(this.getAgentPath());
		stringBuilder.append(JVM_ARGS_KEY_VALUE_DELIMITER);

		// name=$NAME,
		stringBuilder.append(JVM_ARGS_AGENT_NAME);
		stringBuilder.append(JVM_ARGS_KEY_VALUE_DELIMITER);
		stringBuilder.append(this.getAgentName());
		stringBuilder.append(JVM_ARGS_DELIMITER);

		// server=$SERVER,
		stringBuilder.append(JVM_ARGS_SERVER_NAME);
		stringBuilder.append(JVM_ARGS_KEY_VALUE_DELIMITER);
		stringBuilder.append(this.getServer());
		stringBuilder.append(JVM_ARGS_DELIMITER);

		// wait=$WAIT,
		stringBuilder.append(JVM_ARGS_WAIT_NAME);
		stringBuilder.append(JVM_ARGS_KEY_VALUE_DELIMITER);
		stringBuilder.append(this.getWait());
		stringBuilder.append(JVM_ARGS_DELIMITER);

		//,optionTestRunIdJava=
		if (this.getTestRunId() != null) {
			stringBuilder.append(JVM_ARGS_TESTRUNID_NAME);
			stringBuilder.append(JVM_ARGS_KEY_VALUE_DELIMITER);
			stringBuilder.append(this.getTestRunId());
		}

		return stringBuilder.toString();
	}

	//getters and setters
	public String getAgentPath() {
		return agentPath;
	}

	public void setAgentPath(String agentPath) {
		this.agentPath = agentPath;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getTestRunId() {
		if (testRunId == null) {
			Task task = this.getProject().getTasks().getByName(GradleEntryPoint.DT_START_TEST);

			if (task != null) {
				if (task instanceof DtStartTest) {
					DtStartTest dtStartTest = (DtStartTest) task;

					return dtStartTest.getTestRunId();
				}
			}
		}

		return testRunId;
	}

	public void setTestRunId(String testRunId) { this.testRunId = testRunId; }

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getWait() {
		return wait;
	}

	public void setWait(int wait) {
		this.wait = wait;
	}

	public String getInjectionTaskName() {
		return injectionTaskName;
	}

	public void setInjectionTaskName(String injectionTaskName) {
		this.injectionTaskName = injectionTaskName;
	}

	public boolean isInjectionEnabled() {
		return injectionEnabled;
	}

	public void setInjectionEnabled(boolean injectionEnabled) {
		this.injectionEnabled = injectionEnabled;
	}
}
