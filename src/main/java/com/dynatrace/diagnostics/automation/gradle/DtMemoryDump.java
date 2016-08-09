package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.memorydumps.MemoryDumps;
import com.dynatrace.sdk.server.memorydumps.models.AgentPattern;
import com.dynatrace.sdk.server.memorydumps.models.JobState;
import com.dynatrace.sdk.server.memorydumps.models.MemoryDumpJob;
import com.dynatrace.sdk.server.memorydumps.models.StoredSessionType;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

import java.net.URI;
import java.net.URISyntaxException;

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

		MemoryDumps memoryDumps = new MemoryDumps(this.getDynatraceClient());

		MemoryDumpJob memoryDumpJob = new MemoryDumpJob();
		memoryDumpJob.setAgentPattern(new AgentPattern(this.getAgentName(), this.getHostName(), this.getProcessId()));
		memoryDumpJob.setStoredSessionType(StoredSessionType.fromInternal(this.getDumpType())); /* TODO FIXME - dump type is wrong? use new values with prefixes! */
		memoryDumpJob.setSessionLocked(this.isSessionLocked());
		memoryDumpJob.setCaptureStrings(this.getCaptureStrings());
		memoryDumpJob.setCapturePrimitives(this.getCapturePrimitives());
		memoryDumpJob.setPostProcessed(this.getAutoPostProcess());
		memoryDumpJob.setDogc(this.getDoGc());

		try {
			String memoryDumpLocation = memoryDumps.createMemoryDumpJob(this.getProfileName(), memoryDumpJob);

			URI uri = new URI(memoryDumpLocation);
			String[] uriPathArray = uri.getPath().split("/");

			String memoryDump = null;

			try {
				memoryDump = uriPathArray[uriPathArray.length - 1];
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new BuildException("Malformed memory dump response", new Exception()); //$NON-NLS-1$
			}

			this.setDumpName(memoryDump);

			if (memoryDump == null || memoryDump.length() == 0) {
				throw new BuildException("Memory Dump wasn't taken", new Exception()); //$NON-NLS-1$
			}

			int timeout = waitForDumpTimeout;

			JobState memoryDumpJobState = memoryDumps.getMemoryDumpJob(this.getProfileName(), memoryDump).getState();
			boolean dumpFinished = memoryDumpJobState.equals(JobState.FINISHED) || memoryDumpJobState.equals(JobState.FAILED);

			while (!dumpFinished && (timeout > 0)) {
				try {
					java.lang.Thread.sleep(waitForDumpPollingInterval);
					timeout -= waitForDumpPollingInterval;
				} catch (InterruptedException e) {
				}

				memoryDumpJobState = memoryDumps.getMemoryDumpJob(this.getProfileName(), memoryDump).getState();
				dumpFinished = memoryDumpJobState.equals(JobState.FINISHED) || memoryDumpJobState.equals(JobState.FAILED);
			}

			this.setDumpFinished(dumpFinished);
		} catch (ServerConnectionException | ServerResponseException e) {
			throw new BuildException(e.getMessage(), e);
		} catch (URISyntaxException e) {
			throw new BuildException(e.getMessage(), e);
		}
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
