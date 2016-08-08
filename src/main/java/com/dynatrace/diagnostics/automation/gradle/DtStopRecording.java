package com.dynatrace.diagnostics.automation.gradle;

import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

public class DtStopRecording extends DtServerProfileBase {

	private boolean doReanalyzeSession = false;
	
	private int reanalyzeSessionTimeout = 60000;
	private int reanalyzeSessionPollingInterval = 5000;
	private int stopDelay=0;
	private boolean failOnError=true;

	//properties
	private boolean reanalyzeFinished = false;
	private String recordedSessionName = null;

	@TaskAction
	public void executeTask() throws BuildException {
		try {
			Thread.sleep(stopDelay);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		String sessionName=null;
		try {
			sessionName = getEndpoint().stopRecording(getProfileName());

			log(String.format("Stopped recording on %1$s with SessionName %2$s", getProfileName(), sessionName)); //$NON-NLS-1$
			this.setRecordedSessionName(sessionName); //local-scope
			this.getProjectProperties().setSessionName(sessionName); //global-scope

			if (doReanalyzeSession) {
				boolean reanalyzeFinished = getEndpoint().reanalyzeSessionStatus(sessionName);
				if (getEndpoint().reanalyzeSession(sessionName)) {
					int timeout = reanalyzeSessionTimeout;
					while (!reanalyzeFinished && (timeout > 0)) {
						try {
							java.lang.Thread.sleep(getReanalyzeSessionPollingInterval());
							timeout -= getReanalyzeSessionPollingInterval();
						} catch (InterruptedException e) {
						}

						reanalyzeFinished = getEndpoint().reanalyzeSessionStatus(sessionName);
					}
				}

				this.setReanalyzeFinished(reanalyzeFinished);
			}
		} catch (RuntimeException e) {
			if (isFailOnError()) {
				throw e;
			}
			log(String.format(
					"Caught exception while Stopping session recording of session %1$s on profile %2$s. Since failOnError==true ignoring this exception.\n\tException message: %3$s", sessionName,getProfileName(),e.getMessage()), e, LogLevel.WARN); //$NON-NLS-1$
		}
	}


	public void setDoReanalyzeSession(boolean doReanalyzeSession) {
		this.doReanalyzeSession = doReanalyzeSession;
	}

	public boolean isDoReanalyzeSession() {
		return doReanalyzeSession;
	}

	public void setReanalyzeSessionTimeout(int reanalyzeSessionTimeout) {
		this.reanalyzeSessionTimeout = reanalyzeSessionTimeout;
	}

	public int getReanalyzeSessionTimeout() {
		return reanalyzeSessionTimeout;
	}

	public void setReanalyzeSessionPollingInterval(
			int reanalyzeSessionPollingInterval) {
		this.reanalyzeSessionPollingInterval = reanalyzeSessionPollingInterval;
	}

	public int getReanalyzeSessionPollingInterval() {
		return reanalyzeSessionPollingInterval;
	}

	public int getStopDelay() {
		return stopDelay;
	}

	public void setStopDelay(int stopDelay) {
		this.stopDelay = stopDelay;
	}

	public boolean isFailOnError() {
		return failOnError;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	//properties

	public boolean isReanalyzeFinished() {
		return reanalyzeFinished;
	}

	private void setReanalyzeFinished(boolean reanalyzeFinished) {
		this.reanalyzeFinished = reanalyzeFinished;
	}

	public String getRecordedSessionName() {
		return recordedSessionName;
	}

	private void setRecordedSessionName(String recordedSessionName) {
		this.recordedSessionName = recordedSessionName;
	}
}
