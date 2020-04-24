package gov.usgs.wma.mlrlegacy.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoggedTransaction {
    public static final String DISTRICT_CODE_COLUMN = "district_cd";

    public class ValueChange {
        public ValueChange(String column, String oldValue, String newValue) {
            this.column = column;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
        public String column;
        public String oldValue;
        public String newValue;
    }

    private Map<String, String> oldFields;
    private Map<String, String> changedFields;
    private Instant actionTime;
    private String action;
    private String agencyCode;
    private String siteNumber;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(String siteNumber) {
        this.siteNumber = siteNumber;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Instant getActionTime() {
        return actionTime;
    }

    public void setActionTime(Instant actionTime) {
        this.actionTime = actionTime;
    }

    public void setOldFields(Map<String,String> oldFields) {
        this.oldFields = oldFields;
    }

    public void setChangedFields(Map<String,String> changedFields) {
        this.changedFields = changedFields;
    }

    public List<ValueChange> getChanges() {
        List<ValueChange> result = new ArrayList<>();
        
        if(changedFields != null && !changedFields.isEmpty()) {
            for(String key : changedFields.keySet()) {
                result.add(new ValueChange(key, oldFields.get(key), changedFields.get(key)));
            }
        }

        return result;        
    }
    
    public Set<String> getAffectedDistricts() {
        Set<String> result = new HashSet<>();

        if(oldFields.get(DISTRICT_CODE_COLUMN) != null) {
            result.add(oldFields.get(DISTRICT_CODE_COLUMN).trim());
        }
        
        if(changedFields != null && changedFields.get(DISTRICT_CODE_COLUMN) != null) {
            result.add(changedFields.get(DISTRICT_CODE_COLUMN).trim());
        }

        return result;
    }
}