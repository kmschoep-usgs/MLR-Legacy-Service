package gov.usgs.wma.mlrlegacy.model;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;

public class LoggedTransactionQueryParams {
	public static final String AGENCY_CODE = "agencyCode";
	public static final String SITE_NUMBER = "siteNumber";
	public static final String USERNAME = "username";
	public static final String ACTION = "action";
	public static final String DISTRICT_CODE = "districtCode";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String PAGE_SIZE = "pageSize";
	public static final String PAGE_NUM = "pageNum";
	public static final String SORT_BY = "sortBy";
	public static final String SORT_DIR = "sortDir";

	@Pattern(regexp = "\\d\\d\\d\\d-\\d\\d-\\d\\d")
	@Nullable
	private String startDate;

	@Pattern(regexp = "\\d\\d\\d\\d-\\d\\d-\\d\\d")
	@Nullable
	private String endDate;

	@Nullable
	private String agencyCode;

	@Nullable
	private String siteNumber;

	@Nullable
	private String username;

	@Nullable
	private String action;

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

	public Map<String, Object> getAsQueryParams(Integer pageSize, Integer pageNum, String sortBy, String sortDir) {
		Map<String, Object> params = getAsQueryParams();
		params.put(PAGE_SIZE, pageSize);
		params.put(PAGE_NUM, pageNum);
		params.put(SORT_BY, sortBy);
		params.put(SORT_DIR, sortDir);
		return params;
	}

	public Map<String, Object> getAsQueryParams() {
		Map<String, Object> params = new HashMap<>();
		params.put(START_DATE, startDate);
		params.put(END_DATE, endDate);
		params.put(AGENCY_CODE, agencyCode != null && !agencyCode.trim().isEmpty() ? agencyCode.trim() : null);
		params.put(SITE_NUMBER, siteNumber != null && !siteNumber.trim().isEmpty() ? siteNumber.trim() : null);
		params.put(USERNAME, username != null && !username.trim().isEmpty() ? username.trim() : null);
		params.put(ACTION, action != null && !action.trim().isEmpty() ? action.trim().toUpperCase() : null);
		params.put(DISTRICT_CODE, districtCode != null && !districtCode.trim().isEmpty() ? districtCode.trim() : null);
		return params;
	}
}