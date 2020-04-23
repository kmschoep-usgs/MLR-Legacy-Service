package gov.usgs.wma.mlrlegacy.model;

public class LoggedTransactionSummary {
    private String districtCode;
    private Integer insertCount;
    private Integer updateCount;

    public Integer getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(Integer updateCount) {
        this.updateCount = updateCount;
    }

    public Integer getInsertCount() {
        return insertCount;
    }

    public void setInsertCount(Integer insertCount) {
        this.insertCount = insertCount;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }
}