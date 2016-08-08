package com.dynatrace.diagnostics.automation.gradle;

import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

public class DtStorePurePaths extends DtServerProfileBase{
	private String recordingOption;
	private boolean sessionLocked;
	private boolean appendTimestamp;

	@TaskAction
	public void executeTask() throws BuildException {
		getEndpoint().storePurePaths(getProfileName(), getRecordingOption(), isSessionLocked(), isAppendTimestamp());
	}

	public String getRecordingOption() {
		return recordingOption;
	}

	public void setRecordingOption(String recordingOption) {
		this.recordingOption = recordingOption;
	}

	public boolean isSessionLocked() {
		return sessionLocked;
	}

	public void setSessionLocked(boolean sessionLocked) {
		this.sessionLocked = sessionLocked;
	}

	public boolean isAppendTimestamp() {
		return appendTimestamp;
	}

	public void setAppendTimestamp(boolean appendTimestamp) {
		this.appendTimestamp = appendTimestamp;
	}
}
