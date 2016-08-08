package com.dynatrace.diagnostics.automation.gradle;

import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

import java.io.File;

public class DtReport extends DtServerBase {

	private String dashboardName;
	private String source;
	private String comparison;
	private String iteratorDashboard;
	private File xmlToFile;
	private File reportDir;	
	private boolean createHtml = false;

	@TaskAction
	public void executeTask() {
		try {
			com.dynatrace.diagnostics.automation.common.Report report = new com.dynatrace.diagnostics.automation.common.Report(getEndpoint());
			report.setComparison(comparison);
			report.setCreateHtml(isCreateHtml());
			report.setDashboardName(dashboardName);
			report.setIteratorDashboard(iteratorDashboard);
			report.setReportDir(reportDir);
			report.setSource(source);
			report.setXmlToFile(xmlToFile);
			report.execute();
		} catch (Exception ex) {
			throw new BuildException(ex.getMessage(), ex);
		}
	}

	public File getReportDir() {
		return reportDir;
	}

	public void setReportDir(File reportDir) {
		this.reportDir = reportDir;
	}

	public String getIteratorDashboard() {
		return iteratorDashboard;
	}

	public void setIteratorDashboard(String iteratorDashboard) {
		this.iteratorDashboard = iteratorDashboard;
	}

	public void setXmlToFile(File file) {
		xmlToFile = file;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getComparison() {
		return comparison;
	}

	public void setComparison(String comparison) {
		this.comparison = comparison;
	}

	public String getDashboardName() {
		return dashboardName;
	}

	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}

	public void setCreateHtml(boolean createHtml) {
		this.createHtml = createHtml;
	}

	public boolean isCreateHtml() {
		return createHtml;
	}
}
