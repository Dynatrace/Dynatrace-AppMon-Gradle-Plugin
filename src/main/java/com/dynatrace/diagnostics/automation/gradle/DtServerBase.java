/*
 * Dynatrace Gradle Plugin
 * Copyright (c) 2008-2016, DYNATRACE LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  Neither the name of the dynaTrace software nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.diagnostics.automation.util.DtUtil;
import com.dynatrace.sdk.org.apache.http.client.utils.URIBuilder;
import com.dynatrace.sdk.org.apache.http.impl.client.CloseableHttpClient;
import com.dynatrace.sdk.server.BasicServerConfiguration;
import com.dynatrace.sdk.server.DynatraceClient;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.tooling.BuildException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Base for Gradle tasks which are using server connection
 */
abstract class DtServerBase extends GradleTask {
    private static final String PROTOCOL_WITHOUT_SSL = "http";
    private static final String PROTOCOL_WITH_SSL = "https";

    /**
     * Use unlimited connection timeout
     */
    private static final int CONNECTION_TIMEOUT = 0;

    @Input
    private String username = null;

    @Input
    private String password = null;

    @Input
    private String serverUrl = null;

    @Input
    private Boolean ignoreSSLErrors = null;

    /**
     * contains Dynatrace client
     */
    @Internal
    private DynatraceClient dynatraceClient;

    /**
     * Builds configuration required for {@link DynatraceClient}
     *
     * @return {@link BasicServerConfiguration} containing configuration based on parameters provided in properties
     * @throws BuildException whenever connecting to the server, parsing a response or execution fails
     */
    private BasicServerConfiguration buildServerConfiguration() throws BuildException {
        try {
            URIBuilder uriBuilder = new URIBuilder(this.getServerUrl());
            URI uri = uriBuilder.build();

            String protocol = uri.getScheme();
            String host = uri.getHost();
            int port = uri.getPort();
            boolean ssl = this.isProtocolCompatibleWithSsl(protocol);

            return new BasicServerConfiguration(this.getUsername(), this.getPassword(), ssl, host, port, !this.getIgnoreSSLErrors(), CONNECTION_TIMEOUT);
        } catch (URISyntaxException | IllegalArgumentException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    /**
     * Checks whether given protocol is http (without SSL) or https (with SSL)
     *
     * @param protocol - protocol name extracted from url
     * @return boolean that describes that the given protocol has SSL
     * @throws IllegalArgumentException whenever given protocol name isn't valid (isn't http or https)
     */
    private boolean isProtocolCompatibleWithSsl(String protocol) throws IllegalArgumentException {
        if (!DtUtil.isEmpty(protocol) && (protocol.equals(PROTOCOL_WITH_SSL) || protocol.equals(PROTOCOL_WITHOUT_SSL))) {
            return protocol.equals(PROTOCOL_WITH_SSL);
        }

        throw new IllegalArgumentException(String.format("Invalid protocol name: %s", protocol), new Exception());
    }

    /**
     * Returns {@link DynatraceClient} required for Server SDK classes
     *
     * @return {@link DynatraceClient} with parameters provided in properties
     * @throws BuildException whenever execution fails
     */
    public DynatraceClient getDynatraceClient() throws BuildException {
        if (this.dynatraceClient == null) {
            this.getLogger().log(LogLevel.INFO, String.format("Connection to dynaTrace Server via %s with username %s, ignoring SSL errors: %b", this.getServerUrl(), this.getUsername(), this.getIgnoreSSLErrors()));
            this.dynatraceClient = new DynatraceClient(this.buildServerConfiguration());
        }

        return this.dynatraceClient;
    }

    /**
     * Returns {@link DynatraceClient} required for Server SDK classes
     * <p>
     * Used only for testing purposes
     *
     * @param client - user-defined {@link CloseableHttpClient}
     * @return {@link DynatraceClient} with parameters provided in properties
     * @throws BuildException whenever execution fails
     */
    public void setDynatraceClientWithCustomHttpClient(CloseableHttpClient client) throws BuildException {
        this.getLogger().log(LogLevel.INFO, String.format("Connection to dynaTrace Server via %s with username %s, ignoring SSL errors: %b", this.getServerUrl(), this.getUsername(), this.getIgnoreSSLErrors()));
        this.dynatraceClient = new DynatraceClient(this.buildServerConfiguration(), client);
    }

    public String getUsername() {
        if (this.username == null) {
            String usernameFromProjectProperties = this.getProjectProperties().getUsername();

            if (!DtUtil.isEmpty(usernameFromProjectProperties)) {
                this.username = usernameFromProjectProperties;
            }
        }

        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        if (this.password == null) {
            String passwordFromProjectProperties = this.getProjectProperties().getPassword();

            if (!DtUtil.isEmpty(passwordFromProjectProperties)) {
                this.password = passwordFromProjectProperties;
            }
        }

        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerUrl() {
        if (this.serverUrl == null) {
            String serverUrlFromProjectProperties = this.getProjectProperties().getServerUrl();

            if (!DtUtil.isEmpty(serverUrlFromProjectProperties)) {
                this.serverUrl = serverUrlFromProjectProperties;
            }
        }

        return this.serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public Boolean getIgnoreSSLErrors() {
        if (this.ignoreSSLErrors == null) {
            Boolean ignoreSSLErrorsFromProjectProperties = this.getProjectProperties().getIgnoreSSLErrors();

            if (ignoreSSLErrorsFromProjectProperties != null) {
                this.ignoreSSLErrors = ignoreSSLErrorsFromProjectProperties.booleanValue();
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
