package com.dynatrace.diagnostics.automation.gradle;

import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

public class DtRestartCollector extends DtServerBase {
	private boolean restart = true;
	private String collector;

	@TaskAction
	public void executeTask() throws BuildException {
		if(restart)
			getEndpoint().restartCollector(getCollector());
		else
			getEndpoint().shutdownCollector(getCollector());
	}

	public void setRestart(boolean restart) {
		this.restart = restart;
	}

	public void setCollector(String collector) {
		this.collector = collector;
	}

	public String getCollector() {
		return collector;
	}
}
