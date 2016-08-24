/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: Constants.java
 * @date: Mar 20, 2015
 * @author: wiktor.bachnik
 */
package com.dynatrace.diagnostics.automation.common;

import com.dynatrace.sdk.server.testautomation.models.TestCategory;

import java.util.Arrays;
import java.util.Map;

/**
 * Common constants and utility methods for DtStartTest tasks in Ant and Maven plugins.
 *
 * @author wiktor.bachnik
 */
public final class DtStartTestCommon {
	public static final String ATTRIBUTE_TEST_BUILD = "versionBuild"; //$NON-NLS-1$
	public static final String ATTRIBUTE_TEST_CATEGORY = "category"; //$NON-NLS-1$
	/** The Ant property which holds the testrun UUID. */
	public static final String TESTRUN_ID_PROPERTY_NAME = "dtTestrunID"; //$NON-NLS-1$
	/** MUST BE KEPT IN SYNC WITH Java Agents TestIntrospection. */
	public static final String DT_AGENT_TESTRUN_OPTION = "optionTestRunIdJava"; //$NON-NLS-1$
	/** MUST BE KEPT IN SYNC WITH .NET Agents TestIntrospection. */
	public static final String DOTNET_TESTRUN_ENV_VARIABLE = "DT_TESTRUN_ID"; //$NON-NLS-1$

	public static final String INVALID_BUILD_NUMBER_MESSAGE = "Build number cannot be '-' or empty"; //$NON-NLS-1$
	public static final String TESTRUN_ID_PROPERTY_MESSAGE = "Setting property <" + TESTRUN_ID_PROPERTY_NAME + "> to value <{0}>. " + //$NON-NLS-1$//$NON-NLS-2$
			"Remember to pass it to DT agent in <" + DT_AGENT_TESTRUN_OPTION + "> parameter"; //$NON-NLS-1$//$NON-NLS-2$
	public static final String MISSING_BUILD_MESSAGE = "Task requires attribute \"" + ATTRIBUTE_TEST_BUILD + "\""; //$NON-NLS-1$//$NON-NLS-2$
	public static final String MISSING_CATEGORY_MESSAGE = "Task requires attribute \"" + ATTRIBUTE_TEST_CATEGORY + "\". Select one from " + Arrays.toString(TestCategory.values()); //$NON-NLS-1$//$NON-NLS-2$
	public static final String INVALID_CATEGORY_MESSAGE = "\"" + ATTRIBUTE_TEST_CATEGORY + "\" has invalid value \"{0}\"." + //$NON-NLS-1$//$NON-NLS-2$
			"Select one from " + Arrays.toString(TestCategory.values()); //FIXME currently printing keys!  //$NON-NLS-1$

	private DtStartTestCommon() {
	}


	public static boolean isBuildNumberValid(String buildNumber) {
		if (buildNumber == null || "".equals(buildNumber.trim())) {//$NON-NLS-1$
			return false;
		}
		return !"-".equals(buildNumber); //$NON-NLS-1$
	}

	/**
	 * Returns true if given string is null or empty, false otherwise.
	 */
	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}

	/**
	 * Returns a debug message with all the details ragarding provided test run information.
	 */
	public static String generateInfoMessage(String profileName, String versionMajor, String versionMinor,
											 String versionRevision, String versionBuild, String versionMilestone, String marker, String category, String platform, Map<String, String> customProperties) {
		StringBuilder sb = new StringBuilder();
		sb.append("Setting Test Information for system profile: ").append(profileName);   //$NON-NLS-1$
		sb.append("\n\tversion: ").append(versionMajor).append(".").append(versionMinor).append(".").append(versionRevision).append(".").append(versionBuild).append(" milestone: ").append(versionMilestone); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		if (!isEmpty(marker)) {
			sb.append("\n\tmarker: ").append(marker); //$NON-NLS-1$
		}
		if (!isEmpty(category)) {
			sb.append("\n\tcategory: ").append(category); //$NON-NLS-1$
		}
		if (!isEmpty(platform)) {
			sb.append("\n\tplatform: ").append(platform); //$NON-NLS-1$
		}
		if (!customProperties.isEmpty()) {
			sb.append("\n\tcustom properties: "); //$NON-NLS-1$
			for (Map.Entry<String, String> entry : customProperties.entrySet()) {
				sb.append("\n\t\t").append(entry.getKey()).append("=").append(entry.getValue()); //$NON-NLS-1$//$NON-NLS-2$
			}
		}
		return sb.toString();
	}
}
