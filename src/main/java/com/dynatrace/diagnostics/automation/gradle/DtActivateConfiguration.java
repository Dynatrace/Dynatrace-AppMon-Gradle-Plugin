package com.dynatrace.diagnostics.automation.gradle;

import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

public class DtActivateConfiguration extends DtServerProfileBase {

	private String configuration;

	@TaskAction
	public void executeTask() throws BuildException {
		getEndpoint().activateConfiguration(getProfileName(), getConfiguration());
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getConfiguration() {
		return configuration;
	}
}
