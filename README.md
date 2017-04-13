# Dynatrace Gradle Plugin [![Build Status](https://travis-ci.org/Dynatrace/Dynatrace-Gradle-Plugin.svg?branch=master)](https://travis-ci.org/Dynatrace/Dynatrace-Gradle-Plugin)

The automation plugin enables FULL Automation of Dynatrace by leveraging the REST interfaces of the Dynatrace AppMon Server. The automation plugin includes Gradle tasks to execute the following actions on the Dynatrace AppMon Server:
* Activate Configuration: Activates a configuration within a system profile
* Enable/Disable Profile
* Stop/Restart Server
* Start/Stop Session Recording: Returns the actual recorded session URI
* Start/Stop Test: Start returns testrun id, allowing to inject it into Dynatrace agent parameters and use to finish test
* Store pure paths


#### Table of Contents

* [Installation](#installation)
 * [Prerequisites](#prerequisites)
 * [Manual Installation](#manual_installation)
* [Configuration](#configuration)
 * [Global Settings](#global)
 * [Local Settings](#local)
* [Available Gradle tasks](#tasks)
* [Additional Resources](#resources)

## <a name="installation"></a>Installation

### <a name="prerequisites"></a>Prerequisites

* Dynatrace Application Monitoring version: 7+
* Gradle 2.14+

### <a name="manual_installation"></a>Manual Installation

* Download the [latest plugin](https://github.com/Dynatrace/Dynatrace-Gradle-Plugin/releases) and extract it into the `lib` folder in your project
* Add the following to your build.gradle

   ```groovy
    buildscript {
        repositories {
            mavenLocal()
            mavenCentral()
        }
        dependencies {
            classpath 'com.dynatrace.diagnostics.automation:dynatrace-gradle-plugin:7.0.0'
        }
    }

    apply plugin: 'dynatrace-gradle-plugin'
   ```
* Define properties for the Dynatrace tasks shown in build script from the example package
* Invoke your gradle tasks, e.g.: `gradlew DtEnableProfile`

## Building

In order to build the plugin, Gradle environment is needed to be configured in your system. Then you should be able to build package by executing `gradlew build`.
Jar file should be available in `build/libs` folder

## <a name="configuration"></a>Configuration

### <a name="global"></a>Global Settings

To define plugin-scope properties, use `dynaTrace` closure:
```groovy

dynaTrace {
	username = 'admin'
	password = 'admin'
	serverUrl = 'https://localhost:8021'
	profile = 'systemProfileName'
	ignoreSSLErrors = true
}
```

### <a name="local"></a>Local Settings
In order to override given properties mentioned above, use task-scope properties.
This example shows that functionality in `DtStartTest` task:
```groovy

DtStartTest {
	username = 'user'
	password = 'password'
	serverUrl = 'https://localhost:8021'
	profileName = 'otherProfile'

	versionBuild = 1
	versionMajor = 2
	versionMinor = 3
	versionRevision = 4
	versionMilestone = 5
	platform = 'Linux'
	marker = 'marker'
	category = 'unit'

	addCustomProperty 'testset', 'unit-tests'
}
```

## <a name="tasks"></a>Available Gradle tasks
Description of Available Gradle Tasks

#### Server Management
* DtEnableProfile - Enables or disables a System Profile
* DtActivateConfiguration - Activates a Configuration of a System Profile
* DtRestartServer - Restarts a dynaTrace Server
* DtStorePurePaths - Store current Live Session

#### Session Management
* DtStartRecording - Starts session recording for a specified system profile
* DtStopRecording - Stops session recording for a specified system profile

#### Test Management
* DtStartTest - Sets meta data information for the Test Automation Feature and provides the DtStartTest.testRunId necessary to support parallel builds. The DtStartTest.testRunId value needs to be passed to the agent instrumenting the JVM that's executing the tests.
* DtFinishTest - Sets test status of test run to finished

## <a name="resources"></a>Additional Resources

- [Continuous Delivery & Test Automation](https://community.dynatrace.com/community/pages/viewpage.action?pageId=215161284)
- [Capture Performance Data from Tests](https://community.dynatrace.com/community/display/DOCDT63/Capture+Performance+Data+from+Tests)
- [Integrate Dynatrace in Continous Integration Builds](https://community.dynatrace.com/community/display/DOCDT63/Integrate+Dynatrace+in+Continuous+Integration+Builds)

