package com.dynatrace.diagnostics.automation.gradle;

public abstract class DtServerProfileBase extends DtServerBase {

	private String profileName;

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getProfileName() {
		if(profileName == null) {
			String dtProfile = this.getProjectProperties().getProfile(); //$NON-NLS-1$
			if(dtProfile != null && dtProfile.length() > 0)
				profileName = dtProfile;
		}
		return profileName;
	}

}
