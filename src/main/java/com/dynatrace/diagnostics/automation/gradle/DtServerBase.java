package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.diagnostics.automation.rest.sdk.RESTEndpoint;

public abstract class DtServerBase extends GradleTask {

	private String username = null;
	private String password = null;
	private String serverUrl = null;
	private RESTEndpoint endpoint = null;

	public RESTEndpoint getEndpoint() {
		if(endpoint == null)
			endpoint = new RESTEndpoint(getUsername(), getPassword(), getServerUrl());
		return endpoint;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		if(username == null) {
			String dtUsername = this.getProjectProperties().getUsername(); //$NON-NLS-1$

			if(dtUsername != null && dtUsername.length() > 0)
				username = dtUsername;
		}
		return username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		if(password == null) {
			String dtPassword = this.getProjectProperties().getPassword(); //$NON-NLS-1$
			if(dtPassword != null && dtPassword.length() > 0)
				password = dtPassword;
		}
		return password;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	public String getServerUrl() {
		if(serverUrl == null) {
			String dtServerUrl = this.getProjectProperties().getServerUrl(); //$NON-NLS-1$
			if(dtServerUrl != null && dtServerUrl.length() > 0)
				serverUrl = dtServerUrl;
		}
		return serverUrl;
	}
}
