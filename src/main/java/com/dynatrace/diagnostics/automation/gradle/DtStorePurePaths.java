package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.sessions.Sessions;
import com.dynatrace.sdk.server.sessions.models.RecordingOption;
import com.dynatrace.sdk.server.sessions.models.StoreSessionRequest;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

public class DtStorePurePaths extends DtServerProfileBase{
	private String recordingOption;
	private boolean sessionLocked;
	private boolean appendTimestamp;

	@TaskAction
	public void executeTask() throws BuildException {
		Sessions sessions = new Sessions(this.getDynatraceClient());

		StoreSessionRequest storeSessionRequest = new StoreSessionRequest(this.getProfileName());
		storeSessionRequest.setRecordingOption(RecordingOption.fromInternal(this.getRecordingOption()));
		storeSessionRequest.setSessionLocked(this.isSessionLocked());
		storeSessionRequest.setAppendTimestamp(this.isAppendTimestamp());

		try {
			sessions.store(storeSessionRequest);
		} catch (ServerConnectionException | ServerResponseException e) {
			throw new BuildException(e.getMessage(), e);
		}
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
