package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.servermanagement.ServerManagement;
import com.dynatrace.sdk.server.sessions.Sessions;
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

		Sessions sessions = new Sessions(this.getDynatraceClient());

		try {
			if (sessions.reanalyze(this.getSessionName())) {
				int timeout = reanalyzeSessionTimeout;
				reanalyzeFinished = sessions.getReanalysisStatus(this.getSessionName());
				while (!reanalyzeFinished && (timeout > 0)) {
					try {
						java.lang.Thread.sleep(getReanalyzeSessionPollingInterval());
						timeout -= getReanalyzeSessionPollingInterval();
					} catch (InterruptedException e) {
					}

					reanalyzeFinished = sessions.getReanalysisStatus(this.getSessionName());
				}
			}
		} catch (ServerConnectionException | ServerResponseException e) {
			throw new BuildException(e.getMessage(), e);
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
