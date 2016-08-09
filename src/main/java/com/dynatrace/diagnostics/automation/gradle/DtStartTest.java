/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: DtStartTest.java
 * @date: 15.01.2013
 * @author: cwat-ruttenth
 * @date: 06.11.2014
 * @author: cwpl-knecel
 */
package com.dynatrace.diagnostics.automation.gradle;

import com.dynatrace.diagnostics.automation.common.DtStartTestCommon;
import com.dynatrace.sdk.server.testautomation.TestAutomation;
import com.dynatrace.sdk.server.testautomation.models.CreateTestRunRequest;
import com.dynatrace.sdk.server.testautomation.models.TestCategory;
import com.dynatrace.sdk.server.testautomation.models.TestMetaData;
import com.dynatrace.sdk.server.testautomation.models.TestRun;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Gradle task to start a testrun.
 *
 * @author cwpl-knecel
 * @author cwat-ruttenth
 */
public class DtStartTest extends DtServerProfileBase {

	private String versionMajor;
	private String versionMinor;
	private String versionRevision;
	private String versionMilestone;
	private String versionBuild;
	private String marker;
	private String category;
	private String platform;
	private String loadTestName; //TODO - remove?
	private final List<CustomProperty> properties = new ArrayList<CustomProperty>();


	/** Flag to print debug information. Default: false */
	// DO NOT REMOVE for backward compatibility!
	private boolean debug = false;

	/** Flag to make this task fail on error. Default: true */
	private boolean failOnError = true;

	/**
	 * Used to add a custom property to the test meta data
	 *
	 * @param property
	 */
	public void addCustomProperty(final CustomProperty property) {
		properties.add(property);
	}

	public void addCustomProperty(String key, String value) {
		CustomProperty customProperty = new CustomProperty();
		customProperty.setKey(key);
		customProperty.setValue(value);

		properties.add(customProperty);
	}

	//properties
	private String testRunId = null;

	@TaskAction
	public void executeTask() throws BuildException {
		try {
			checkParameters();
			checkVersionInformation();
			final HashMap<String, String> additionalInformation = new HashMap<String, String>();
			for (CustomProperty property : properties) {
				additionalInformation.put(property.getKey(), property.getValue());
			}
			log(DtStartTestCommon.generateInfoMessage(getProfileName(), versionMajor, versionMinor, versionRevision,
					versionBuild, versionMilestone, marker, category, loadTestName, platform, additionalInformation),
					LogLevel.INFO);

			TestAutomation testAutomation = new TestAutomation(this.getDynatraceClient());

			CreateTestRunRequest request = new CreateTestRunRequest();
			request.setSystemProfile(this.getProfileName());
			request.setVersionMajor(this.versionMajor);
			request.setVersionMinor(this.versionMinor);
			request.setVersionRevision(this.versionRevision);
			request.setVersionBuild(this.versionBuild);
			request.setVersionMilestone(this.versionMilestone);
			request.setMarker(this.marker);
			request.setCategory(TestCategory.fromInternal(this.category));
			request.setPlatform(this.platform);
			request.setAdditionalMetaData(new TestMetaData(additionalInformation));

			/* FIXME? TODO? loadTestName is not used anymore! */
			TestRun testRun = testAutomation.createTestRun(request);

			String testrunUUID = testRun.getId();

			log(MessageFormat.format(DtStartTestCommon.TESTRUN_ID_PROPERTY_MESSAGE, testrunUUID));

			//this.getProjectProperties().setProperty(DtStartTestCommon.TESTRUN_ID_PROPERTY_NAME, testrunUUID);
			this.setTestRunId(testrunUUID); //should check `DtStartTestCommon.TESTRUN_ID_PROPERTY_NAME`?
		} catch (Exception e) {
			if (failOnError) {
				if (e instanceof BuildException) {
					throw (BuildException) e;
				}
				throw new BuildException(e.getMessage(), e);
			}
			log("Exception when executing task: " + e.getMessage(), e, LogLevel.ERROR); //$NON-NLS-1$
		}
	}

	//TODO new Exception is good?
	private void checkParameters() {
		if (versionBuild == null) {
			throw new BuildException(DtStartTestCommon.MISSING_BUILD_MESSAGE, new Exception());
		}
		if (category == null) {
			throw new BuildException(DtStartTestCommon.MISSING_CATEGORY_MESSAGE, new Exception());
		}
		if (!DtStartTestCommon.TEST_CATEGORIES.contains(category)) {
			throw new BuildException(MessageFormat.format(DtStartTestCommon.INVALID_CATEGORY_MESSAGE, category), new Exception());
		}
		if (category != null && DtStartTestCommon.TEST_CATEGORY_LOAD.equalsIgnoreCase(category) && loadTestName == null) {
			throw new BuildException(DtStartTestCommon.MISSING_LOAD_TEST_NAME_MESSAGE, new Exception());
		}
	}

	private void checkVersionInformation() {
		if (versionBuild != null && !DtStartTestCommon.isBuildNumberValid(versionBuild)) {
			throw new BuildException(DtStartTestCommon.INVALID_BUILD_NUMBER_MESSAGE, new Exception());
		}
	}

	public final String getVersionMajor() {
		return versionMajor;
	}

	public final void setVersionMajor(String versionMajor) {
		this.versionMajor = versionMajor;
	}

	public final String getVersionMinor() {
		return versionMinor;
	}

	public final void setVersionMinor(String versionMinor) {
		this.versionMinor = versionMinor;
	}

	public final String getVersionRevision() {
		return versionRevision;
	}

	public final void setVersionRevision(String versionRevision) {
		this.versionRevision = versionRevision;
	}

	/**
	 * Not supported since dT 6.2
	 *
	 * @return {@code null}
	 */
	@Deprecated
	public final String getAgentGroup() {
		return null;
	}

	/** Not supported since dT 6.2 */
	@Deprecated
	public final void setAgentGroup(String agentGroup) {
	}

	public final String getVersionMilestone() {
		return versionMilestone;
	}

	public final void setVersionMilestone(String versionMilestone) {
		this.versionMilestone = versionMilestone;
	}

	public final String getVersionBuild() {
		return versionBuild;
	}

	public final void setVersionBuild(String versionBuild) {
		this.versionBuild = versionBuild;
	}

	public final String getMarker() {
		return marker;
	}

	public final void setMarker(String marker) {
		this.marker = marker;
	}

	public final String getCategory() {
		return category;
	}

	public final void setCategory(String category) {
		this.category = category;
	}

	public final String getPlatform() {
		return platform;
	}

	public final void setPlatform(String platform) {
		this.platform = platform;
	}

	public final String getLoadTestName() {
		return loadTestName;
	}

	/**
	 * Method supporting setting load test name with old parameter 'testrunname'
	 * It's left only for compatibiliy with existing Gradle scripts.
	 * Use {@link #setLoadTestName(String)} instead.
	 *
	 * @param testrunname
	 */
	@Deprecated
	public final void setTestrunname(String testrunname) {
		this.loadTestName = testrunname;
	}

	public final void setLoadTestName(String loadTestName) {
		this.loadTestName = loadTestName;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	//properties
	public String getTestRunId() {
		return testRunId;
	}

	private void setTestRunId(String testRunId) {
		this.testRunId = testRunId;
	}
}
