package com.dynatrace.diagnostics.automation.gradle;

import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

public class DtReanalyzeSession extends DtServerBase {

	private String sessionName;
	private int reanalyzeSessionTimeout = 60000;
	private int reanalyzeSessionPollingInterval = 5000;

	//properties
	private boolean reanalyzeFinished = false;

	@TaskAction
	public void executeTask() throws BuildException {
		boolean reanalyzeFinished = false;

		if (getEndpoint().reanalyzeSession(getSessionName())) {
			int timeout = reanalyzeSessionTimeout;
			reanalyzeFinished = getEndpoint().reanalyzeSessionStatus(getSessionName());
			while(!reanalyzeFinished && (timeout > 0)) {
				try {
					java.lang.Thread.sleep(getReanalyzeSessionPollingInterval());
					timeout -= getReanalyzeSessionPollingInterval();
				} catch (InterruptedException e) {
				}

				reanalyzeFinished = getEndpoint().reanalyzeSessionStatus(getSessionName());
			}
		}

		this.setReanalyzeFinished(reanalyzeFinished);
	}

	public void setReanalyzeSessionTimeout(int reanalyzeSessionTimeout) {
		this.reanalyzeSessionTimeout = reanalyzeSessionTimeout;
	}

	public int getReanalyzeSessionTimeout() {
		return reanalyzeSessionTimeout;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setReanalyzeSessionPollingInterval(
			int reanalyzeSessionPollingInterval) {
		this.reanalyzeSessionPollingInterval = reanalyzeSessionPollingInterval;
	}

	public int getReanalyzeSessionPollingInterval() {
		return reanalyzeSessionPollingInterval;
	}

	//properties
	public boolean isReanalyzeFinished() {
		return reanalyzeFinished;
	}

	public void setReanalyzeFinished(boolean reanalyzeFinished) {
		this.reanalyzeFinished = reanalyzeFinished;
	}
}
