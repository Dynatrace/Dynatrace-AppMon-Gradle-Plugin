package com.dynatrace.diagnostics.automation.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.LogLevel;

/**
 * Created by Lukasz.Hamerszmidt on 2016-07-07.
 */
public class GradleTask extends DefaultTask {
    protected GradleExtension getProjectProperties() {
        GradleExtension extension = getProject().getExtensions().findByType(GradleExtension.class);

        if (extension != null) {
            return extension;
        }

        return new GradleExtension();
    }

    //modify destination methods to match this "Wrappers"
    protected void log(String s) {
        this.log(s, LogLevel.INFO);
    }

    protected void log(String message, LogLevel level) {
        this.getLogger().log(level, message);
    }

    protected void log(String message, Throwable throwable, LogLevel level) {
        this.getLogger().log(level, message, throwable);
    }
}
