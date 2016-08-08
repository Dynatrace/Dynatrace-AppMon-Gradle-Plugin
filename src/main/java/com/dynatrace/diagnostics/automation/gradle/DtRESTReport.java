package com.dynatrace.diagnostics.automation.gradle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

/**
 * This class implements an Gradle task to query the dynaTrace REST Reporting interface (see here:
 * https://community.compuwareapm.com/community/pages/viewpage.action?pageId=114033234
 * for details)
 *   
 */
public class DtRESTReport extends DtServerBase {
	
	private File reportDir;
	private boolean withTimestamp = true;
	
	private String dashboardName;
	private String type;
	private String format;
	private String filter;
	private String source;
	private String compare;

	@TaskAction
	public void executeTask() throws BuildException {
		if(reportDir != null && !reportDir.exists())
			reportDir.mkdir();
				
		ReadableByteChannel channel = null;
		FileOutputStream fos = null;
		try {
			channel = getEndpoint().getReportChannel(dashboardName, type, format, filter, source, compare);
			if(channel != null){
				fos = new FileOutputStream(reportDir.getPath() +  File.separator + (withTimestamp?getTimestampPrefix():"") + dashboardName + "." + type); //$NON-NLS-1$ //$NON-NLS-2$
				fos.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
				channel.close();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getTimestampPrefix() {
		return System.currentTimeMillis() + "_"; //$NON-NLS-1$
	}	
	
	public File getReportDir() {
		return reportDir;
	}
	public void setReportDir(File reportDir) {
		this.reportDir = reportDir;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getCompare() {
		return compare;
	}
	public void setCompare(String compare) {
		this.compare = compare;
	}

	public String getDashboardName() {
		return dashboardName;
	}

	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}

	public boolean isWithTimestamp() {
		return withTimestamp;
	}

	public void setWithTimestamp(boolean withTimestamp) {
		this.withTimestamp = withTimestamp;
	}
}
