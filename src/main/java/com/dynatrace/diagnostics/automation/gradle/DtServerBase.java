package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.sdk.org.apache.http.client.utils.URIBuilder;
import com.dynatrace.sdk.server.BasicServerConfiguration;
import com.dynatrace.sdk.server.DynatraceClient;
import org.gradle.api.InvalidUserDataException;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class DtServerBase extends GradleTask {
    private static final String PROTOCOL_WITHOUT_SSL = "http";
    private static final String PROTOCOL_WITH_SSL = "https";

    /**
     * Use unlimited connection timeout
     */
    private static final int CONNECTION_TIMEOUT = 0;

    private String username = null;
    private String password = null;
    private String serverUrl = null;

    /**
     * Ignore SSL errors
     */
    private Boolean ignoreSSLErrors = null;

    private DynatraceClient dynatraceClient;

    private BasicServerConfiguration buildServerConfiguration() {
        try {
            URIBuilder uriBuilder = new URIBuilder(this.getServerUrl());
            URI uri = uriBuilder.build();

            String protocol = uri.getScheme();
            String host = uri.getHost();
            int port = uri.getPort();
            boolean ssl = BasicServerConfiguration.DEFAULT_SSL;

            if (protocol != null && (protocol.equals(PROTOCOL_WITH_SSL) || protocol.equals(PROTOCOL_WITHOUT_SSL))) {
                ssl = protocol.equals(PROTOCOL_WITH_SSL);
            } else {
                throw new URISyntaxException(protocol, "Invalid protocol name in serverUrl");
            }

            return new BasicServerConfiguration(this.getUsername(), this.getPassword(), ssl, host, port, !this.getIgnoreSSLErrors(), CONNECTION_TIMEOUT);
        } catch (URISyntaxException e) {
            throw new InvalidUserDataException(e.getMessage(), e);
        }
    }

    public DynatraceClient getDynatraceClient() throws InvalidUserDataException {
        if (this.dynatraceClient == null) {
            this.dynatraceClient = new DynatraceClient(this.buildServerConfiguration());
        }

        return this.dynatraceClient;
    }

    public String getUsername() {
        if (username == null) {
            String dtUsername = this.getProjectProperties().getUsername(); //$NON-NLS-1$

            if (dtUsername != null && dtUsername.length() > 0)
                username = dtUsername;
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        if (password == null) {
            String dtPassword = this.getProjectProperties().getPassword(); //$NON-NLS-1$
            if (dtPassword != null && dtPassword.length() > 0)
                password = dtPassword;
        }
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerUrl() {
        if (serverUrl == null) {
            String dtServerUrl = this.getProjectProperties().getServerUrl(); //$NON-NLS-1$
            if (dtServerUrl != null && dtServerUrl.length() > 0)
                serverUrl = dtServerUrl;
        }
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public Boolean getIgnoreSSLErrors() {
        if (this.ignoreSSLErrors == null) {
            Boolean dtIgnoreSSLErrorsProperty = this.getProjectProperties().getIgnoreSSLErrors();

            if (dtIgnoreSSLErrorsProperty != null) {
                this.ignoreSSLErrors = dtIgnoreSSLErrorsProperty.booleanValue();
            } else {
                // malformed property value, assign default value
                this.ignoreSSLErrors = Boolean.TRUE;
            }
        }

        return this.ignoreSSLErrors;
    }

    public void setIgnoreSSLErrors(boolean ignoreSslErrors) {
        this.ignoreSSLErrors = ignoreSslErrors;
    }
}
