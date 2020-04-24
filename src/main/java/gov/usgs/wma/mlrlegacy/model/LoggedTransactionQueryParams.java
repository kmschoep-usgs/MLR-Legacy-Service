package gov.usgs.wma.mlrlegacy.model;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class LoggedTransactionQueryParams {
    public static final String AGENCY_CODE = "agencyCode";
	public static final String SITE_NUMBER = "siteNumber";
	public static final String USERNAME = "username";
	public static final String ACTION = "action";
	public static final String DISTRICT_CODE = "districtCode";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";

    @Pattern(regexp="\\d\\d\\d\\d-\\d\\d-\\d\\d")
    @Nullable
    private String startDate;

    @Pattern(regexp="\\d\\d\\d\\d-\\d\\d-\\d\\d")
    @Nullable
    private String endDate;

    @Size(min=0, max=5)
    @Nullable
    private String agencyCode;

    @Size(min=8, max=15)
    @Nullable
    private String siteNumber;

    @Size(min=5, max=8)
    @Nullable
    private String username;

    @Size(min=1, max=1)
    @Nullable
    private String action;

    @Size(min=0, max=3)
    @Nullable
    private String districtCode;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public Map<String, Object> getAsQueryParams() {
        Map<String, Object> params = new HashMap<>();

        params.put(START_DATE, startDate);
		params.put(END_DATE, endDate);
		params.put(AGENCY_CODE, agencyCode != null ? agencyCode.trim() : null);
		params.put(SITE_NUMBER, siteNumber != null ? siteNumber.trim() : null);
		params.put(USERNAME, username != null ? username.trim() : null);
		params.put(ACTION, action != null ? action.trim().toUpperCase() : null);
        params.put(DISTRICT_CODE, districtCode != null ? districtCode.trim() : null);
        
        return params;
    }
}