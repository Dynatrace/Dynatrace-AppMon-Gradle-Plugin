package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.systemprofiles.SystemProfiles;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

import java.net.URISyntaxException;

public class DtActivateConfiguration extends DtServerProfileBase {
	private String configuration;

	@TaskAction
	public void executeTask() throws BuildException {
		try {
			SystemProfiles systemProfiles = new SystemProfiles(this.getDynatraceClient());
			systemProfiles.activateProfileConfiguration(this.getProfileName(), this.getConfiguration());
		} catch (ServerConnectionException | ServerResponseException e) {
			throw new BuildException(e.getMessage(), e);
		}
		//getEndpoint().activateConfiguration(getProfileName(), getConfiguration());
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getConfiguration() {
		return configuration;
	}
}
