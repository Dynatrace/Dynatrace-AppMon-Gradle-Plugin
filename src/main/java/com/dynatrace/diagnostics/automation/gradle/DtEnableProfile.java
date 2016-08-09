package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.systemprofiles.SystemProfiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

public class DtEnableProfile extends DtServerProfileBase {

	private boolean enable;

	@TaskAction
	public void executeTask() throws BuildException {
		try {
			SystemProfiles systemProfiles = new SystemProfiles(this.getDynatraceClient());
			if (this.enable) {
				systemProfiles.enableProfile(this.getProfileName());
			} else {
				systemProfiles.disableProfile(this.getProfileName());
			}
		} catch (ServerConnectionException | ServerResponseException e) {
			throw new BuildException(e.getMessage(), e);
		}
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isEnable() {
		return enable;
	}
}
