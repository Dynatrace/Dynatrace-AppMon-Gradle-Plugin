package com.dynatrace.diagnostics.automation.gradle;

import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

public class DtEnableProfile extends DtServerProfileBase {

	private boolean enable;

	@TaskAction
	public void executeTask() throws BuildException {
		getEndpoint().enableProfile(getProfileName(), isEnable());
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isEnable() {
		return enable;
	}
}
