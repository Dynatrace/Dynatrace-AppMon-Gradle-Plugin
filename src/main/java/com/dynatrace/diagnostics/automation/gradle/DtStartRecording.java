package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.sessions.Sessions;
import com.dynatrace.sdk.server.sessions.models.RecordingOption;
import com.dynatrace.sdk.server.sessions.models.StartRecordingRequest;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

/**
 * Gradle task to start session recording via the server REST-interface
 *
 * @author andreas.grabner
 * @author cwat-ruttenth
 */
public class DtStartRecording extends DtServerProfileBase {

	private String sessionName;
	private String sessionDescription;
	private String recordingOption;
	private boolean sessionLocked;
	private boolean appendTimestamp;

	//properties
	private String recordedSessionName = null;

	@TaskAction
	public void executeTask() throws BuildException {
		Sessions sessions = new Sessions(this.getDynatraceClient());

		StartRecordingRequest startRecordingRequest = new StartRecordingRequest(this.getProfileName());
		startRecordingRequest.setPresentableName(this.getSessionName());
		startRecordingRequest.setDescription(this.getSessionDescription());
		startRecordingRequest.setSessionLocked(this.isSessionLocked());
		startRecordingRequest.setTimestampAllowed(this.isAppendTimestamp());

		if (this.getRecordingOption() != null) {
			startRecordingRequest.setRecordingOption(RecordingOption.fromInternal(this.getRecordingOption()));
		}

		try {
			String sessionName = sessions.startRecording(startRecordingRequest);

			log("Started recording on " + getProfileName() + " with SessionName " + sessionName); //$NON-NLS-1$ //$NON-NLS-2$

			this.setRecordedSessionName(sessionName); //local-scope
			this.getProjectProperties().setSessionName(sessionName); //global-scope
		} catch (ServerConnectionException | ServerResponseException e) {
			throw new BuildException(e.getMessage(), e);
		}

	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionDescription(String sessionDescription) {
		this.sessionDescription = sessionDescription;
	}

	public String getSessionDescription() {
		return sessionDescription;
	}

	public void setRecordingOption(String recordingOption) {
		this.recordingOption = recordingOption;
	}

	public String getRecordingOption() {
		return recordingOption;
	}

	public void setSessionLocked(boolean sessionLocked) {
		this.sessionLocked = sessionLocked;
	}

	public boolean isSessionLocked() {
		return sessionLocked;
	}

	public void setAppendTimestamp(boolean appendTimestamp) {
		this.appendTimestamp = appendTimestamp;
	}

	public boolean isAppendTimestamp() {
		return appendTimestamp;
	}

	/**
	 *
	 * @return the name of the session the recording is started
	 */
	//properties
	public String getRecordedSessionName() {
		return recordedSessionName;
	}

	private void setRecordedSessionName(String recordedSessionName) {
		this.recordedSessionName = recordedSessionName;
	}
}
