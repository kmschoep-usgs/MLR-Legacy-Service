package gov.usgs.wma.mlrlegacy.model;

public class LoggedAction {

	private String agencyCode;
	private String siteNumber;
	private String logChangeTimestamp;
	private String changeAction;
	private String changedField;
	private String oldValue;
	private String newValue;
	
	
	public String getAgencyCode() {
		return agencyCode;
	}
	public void setAgencyCode(String agencyCode) {
		this.agencyCode = agencyCode;
	}
	public String getSiteNumber() {
		return siteNumber;
	}
	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}
	public String getLogChangeTimestamp() {
		return logChangeTimestamp;
	}
	public void setLogChangeTimestamp(String logChangeTimestamp) {
		this.logChangeTimestamp = logChangeTimestamp;
	}
	public String getChangeAction() {
		return changeAction;
	}
	public void setChangeAction(String changeAction) {
		this.changeAction = changeAction;
	}
	public String getChangedField() {
		return changedField;
	}
	public void setChangedField(String changedField) {
		this.changedField = changedField;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}



}
