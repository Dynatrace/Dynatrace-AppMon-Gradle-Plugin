package com.dynatrace.diagnostics.automation.gradle;

import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

public class DtMemoryDump extends DtAgentBase {
	private String dumpType;
	private boolean sessionLocked;

	private int waitForDumpTimeout = 60000;
	private int waitForDumpPollingInterval = 5000;
	private boolean doGc;
	private boolean autoPostProcess;
	private boolean capturePrimitives;
	private boolean captureStrings;

	//properties
	private String dumpName = null;
	private boolean dumpFinished = false;

	@TaskAction
	public void executeTask() throws BuildException {
		System.out.println("Creating Memory Dump for " + getProfileName() + "-" + getAgentName() + "-" + getHostName() + "-" + getProcessId()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		String memoryDump = getEndpoint().memoryDump(getProfileName(), getAgentName(), getHostName(), getProcessId(), getDumpType(), isSessionLocked(), getCaptureStrings(), getCapturePrimitives(), getAutoPostProcess(), getDoGc());
		this.setDumpName(memoryDump);

		if (memoryDump == null || memoryDump.length() == 0) {
			throw new BuildException("Memory Dump wasn't taken", new Exception()); //$NON-NLS-1$
		}
		
		int timeout = waitForDumpTimeout;
		boolean dumpFinished = getEndpoint().memoryDumpStatus(getProfileName(), memoryDump).isResultValueTrue();
		while(!dumpFinished && (timeout > 0)) {
			try {
				java.lang.Thread.sleep(waitForDumpPollingInterval);
				timeout -= waitForDumpPollingInterval;
			} catch (InterruptedException e) {
			}
			
			dumpFinished = getEndpoint().memoryDumpStatus(getProfileName(), memoryDump).isResultValueTrue();
		}

		this.setDumpFinished(dumpFinished);
	}

	private boolean getCaptureStrings() {
		return captureStrings;
	}

	private boolean getCapturePrimitives() {
		return capturePrimitives;
	}

	private boolean getAutoPostProcess() {
		return autoPostProcess;
	}

	private boolean getDoGc() {
		return doGc;
	}

	public void setDoGc(boolean doGc) {
		this.doGc = doGc;
	}

	public void setAutoPostProcess(boolean autoPostProcess) {
		this.autoPostProcess = autoPostProcess;
	}

	public void setCapturePrimitives(boolean capturePrimitives) {
		this.capturePrimitives = capturePrimitives;
	}

	public void setCaptureStrings(boolean captureStrings) {
		this.captureStrings = captureStrings;
	}

	public void setDumpType(String dumpType) {
		this.dumpType = dumpType;
	}

	public String getDumpType() {
		return dumpType;
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

	public void setDumpFinished(boolean dumpFinished) {
		this.dumpFinished = dumpFinished;
	}
}
