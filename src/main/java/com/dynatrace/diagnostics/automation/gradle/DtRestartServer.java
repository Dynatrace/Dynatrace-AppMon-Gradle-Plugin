package com.dynatrace.diagnostics.automation.gradle;

import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

public class DtRestartServer extends DtServerBase {
	private boolean restart = true;

	@TaskAction
	public void executeTask() throws BuildException {
		if(restart)
			getEndpoint().restartServer();
		else
			getEndpoint().shutdownServer();
	}

	public void setRestart(boolean restart) {
		this.restart = restart;
	}
}
