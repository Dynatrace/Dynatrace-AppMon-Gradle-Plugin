package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.sessions.Sessions;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

public class DtClearSession extends DtServerProfileBase {

	@TaskAction
	public void executeTask() throws BuildException {
		Sessions sessions = new Sessions(this.getDynatraceClient());
		try {
			sessions.clear(this.getProfileName());
		} catch (ServerResponseException | ServerConnectionException e) {
			throw new BuildException(e.getMessage(), e);
		}
	}	
}
