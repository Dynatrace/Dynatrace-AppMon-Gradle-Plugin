package com.dynatrace.diagnostics.automation.gradle;

public class GradleExtension {
    private String baseDir;
    private String username;
    private String password;
    private String serverUrl;
    private String profile;
    private String sessionName;
    private Boolean ignoreSSLErrors;

    /* getters and setters for "dynaTrace" configuration properties */
    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) { this.serverUrl = serverUrl; }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Boolean getIgnoreSSLErrors() {
        return ignoreSSLErrors;
    }

    public void setIgnoreSSLErrors(Boolean ignoreSSLErrors) {
        this.ignoreSSLErrors = ignoreSSLErrors;
    }

}
