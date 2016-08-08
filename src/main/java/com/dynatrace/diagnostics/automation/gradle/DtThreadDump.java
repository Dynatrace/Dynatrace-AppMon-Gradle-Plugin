package com.dynatrace.diagnostics.automation.gradle;

import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

public class DtThreadDump extends DtAgentBase {

	private boolean sessionLocked;
	
	private int waitForDumpTimeout = 60000;
	private int waitForDumpPollingInterval = 5000;

	//properties
	private String dumpName = null;
	private boolean dumpFinished = false;
	
	@TaskAction
	public void executeTask() throws BuildException {
		System.out.println("Creating Thread Dump for " + getProfileName() + "-" + getAgentName() + "-" + getHostName() + "-" + getProcessId()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		String threadDump = getEndpoint().threadDump(getProfileName(), getAgentName(), getHostName(), getProcessId(), isSessionLocked());
		this.setDumpName(threadDump);
		
		int timeout = waitForDumpTimeout;
		boolean dumpFinished = getEndpoint().threadDumpStatus(getProfileName(), threadDump).isResultValueTrue();
		while(!dumpFinished && (timeout > 0)) {
			try {
				java.lang.Thread.sleep(waitForDumpPollingInterval);
				timeout -= waitForDumpPollingInterval;
			} catch (InterruptedException e) {
			}
			
			dumpFinished = getEndpoint().threadDumpStatus(getProfileName(), threadDump).isResultValueTrue();
		}

		this.setDumpFinished(dumpFinished);
	}

	public void setSessionLocked(boolean sessionLocked) {
		this.sessionLocked = sessionLocked;
	}

	public boolean isSessionLocked() {
		return sessionLocked;
	}

	public void setWaitForDumpTimeout(int waitForDumpTimeout) {
		this.waitForDumpTimeout = waitForDumpTimeout;
	}

	public int getWaitForDumpTimeout() {
		return waitForDumpTimeout;
	}

	public void setWaitForDumpPollingInterval(int waitForDumpPollingInterval) {
		this.waitForDumpPollingInterval = waitForDumpPollingInterval;
	}

	public int getWaitForDumpPollingInterval() {
		return waitForDumpPollingInterval;
	}

	//properties
	public String getDumpName() {
		return dumpName;
	}

	private void setDumpName(String dumpName) {
		this.dumpName = dumpName;
	}

	public boolean isDumpFinished() {
		return dumpFinished;
	}

	private void setDumpFinished(boolean dumpFinished) {
		this.dumpFinished = dumpFinished;
	}
}
