package com.dynatrace.diagnostics.automation.gradle;

import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

public class DtClearSession extends DtServerProfileBase {

	@TaskAction
	public void executeTask() throws BuildException {
		getEndpoint().clearSession(getProfileName());
	}	
}
