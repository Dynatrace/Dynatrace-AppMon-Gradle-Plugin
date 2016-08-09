package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.resourcedumps.ResourceDumps;
import com.dynatrace.sdk.server.resourcedumps.models.CreateThreadDumpRequest;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.impldep.org.apache.maven.model.Build;
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

		ResourceDumps resourceDumps = new ResourceDumps(this.getDynatraceClient());
		CreateThreadDumpRequest createThreadDumpRequest = new CreateThreadDumpRequest(this.getProfileName(), this.getAgentName(), this.getHostName(), this.getProcessId());
		createThreadDumpRequest.setSessionLocked(this.isSessionLocked());

		try {
			String threadDump = resourceDumps.createThreadDump(createThreadDumpRequest);
			this.setDumpName(threadDump);

			int timeout = waitForDumpTimeout;

			boolean dumpFinished = resourceDumps.getThreadDumpStatus(this.getProfileName(), threadDump).isSuccessful();
			while (!dumpFinished && (timeout > 0)) {
				try {
					java.lang.Thread.sleep(waitForDumpPollingInterval);
					timeout -= waitForDumpPollingInterval;
				} catch (InterruptedException e) {
				}

				dumpFinished = resourceDumps.getThreadDumpStatus(this.getProfileName(), threadDump).isSuccessful();
			}

			this.setDumpFinished(dumpFinished);
		} catch (ServerConnectionException | ServerResponseException e) {
			throw new BuildException(e.getMessage(), e);
		}
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
