package gov.usgs.wma.mlrlegacy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;

import javax.validation.constraints.Digits;

import org.hibernate.validator.constraints.Length;

import gov.usgs.wma.mlrlegacy.validation.UniqueKey;
import gov.usgs.wma.mlrlegacy.validation.UniqueMonitoringLocation;

@UniqueKey(groups = {
	javax.validation.groups.Default.class,
	UniqueMonitoringLocation.class
})
public class MonitoringLocation {
	private BigInteger id;
	
	/**
	 * This field is ignored during serialization, but included during
	 * deserialization.
	 * http://www.davismol.net/2015/03/21/jackson-using-jsonignore-and-jsonproperty-annotations-to-exclude-a-property-only-from-json-deserialization/
	 */
	@JsonIgnore
	private String transactionType;
	
	@Length(min=0, max=5)
	private String agencyCode;
	
	@Length(min=8, max=15)
	private String siteNumber;
	
	@Length(min=0, max=50)
	private String stationName;
	
	@Length(min=0, max=50)
	private String stationIx;
	
	@Length(min=0, max=7)
	private String siteTypeCode;
	
	@Digits(integer=10,fraction=20)
	private String decimalLatitude;
	
	@Digits(integer=10,fraction=20)
	private String decimalLongitude;
	
	@Length(min=0, max=11)
	private String latitude;
	
	@Length(min=0, max=12)
	private String longitude;
	
	@Length(min=0, max=1)
	private String coordinateAccuracyCode;
	
	@Length(min=0, max=10)
	private String coordinateDatumCode;
	
	@Length(min=0, max=1)
	private String coordinateMethodCode;
	
	@Length(min=0, max=8)
	private String altitude;
	
	@Length(min=0, max=10)
	private String altitudeDatumCode;
	
	@Length(min=0, max=1)
	private String altitudeMethodCode;
	
	@Length(min=0, max=3)
	private String altitudeAccuracyValue;
	
	@Length(min=0, max=3)
	private String districtCode;
	
	@Length(min=0, max=2)
	private String countryCode;
	
	@Length(min=0, max=2)
	private String stateFipsCode;
	
	@Length(min=0, max=3)
	private String countyCode;
	
	@Length(min=0, max=5)
	private String minorCivilDivisionCode;
	
	@Length(min=0, max=16)
	private String hydrologicUnitCode;
	
	@Length(min=0, max=2)
	private String basinCode;
	
	@Length(min=0, max=10)
	private String nationalAquiferCode;
	
	@Length(min=0, max=8)
	private String aquiferCode;
	
	@Length(min=0, max=1)
	private String aquiferTypeCode;
	
	@Length(min=0, max=1)
	private String agencyUseCode;
	
	@Length(min=0, max=1)
	private String dataReliabilityCode;
	
	@Length(min=0, max=23)
	private String landNet;
	
	@Length(min=0, max=20)
	private String mapName;
	
	@Length(min=0, max=7)
	private String mapScale;
	
	@Length(min=0, max=2)
	private String nationalWaterUseCode;
	
	@Length(min=0, max=1)
	private String primaryUseOfSite;
	
	@Length(min=0, max=1)
	private String secondaryUseOfSite;
	
	@Length(min=0, max=1)
	private String tertiaryUseOfSiteCode;
	
	@Length(min=0, max=1)
	private String primaryUseOfWaterCode;
	
	@Length(min=0, max=1)
	private String secondaryUseOfWaterCode;
	
	@Length(min=0, max=1)
	private String tertiaryUseOfWaterCode;
	
	@Length(min=0, max=1)
	private String topographicCode;
	
	@Length(min=0, max=30)
	private String dataTypesCode;
	
	@Length(min=0, max=30)
	private String instrumentsCode;
	
	@Length(min=0, max=8)
	private String contributingDrainageArea;
	
	@Length(min=0, max=8)
	private String drainageArea;
	
	@Length(min=0, max=8)
	private String firstConstructionDate;
	
	@Length(min=0, max=8)
	private String siteEstablishmentDate;
	
	@Length(min=0, max=8)
	private String holeDepth;
	
	@Length(min=0, max=8)
	private String wellDepth;
	
	@Length(min=0, max=1)
	private String sourceOfDepthCode;
	
	@Length(min=0, max=12)
	private String projectNumber;
	
	@Length(min=0, max=6)
	private String timeZoneCode;
	
	@Length(min=0, max=1)
	private String daylightSavingsTimeFlag;
	
	@Length(min=0, max=50)
	private String remarks;
	
	@Length(min=0, max=1)
	private String siteWebReadyCode;
	
	@Length(min=0, max=20)
	private String gwFileCode;
	
	private String created;
	
	@Length(min=0, max=8)
	private String createdBy;
	
	private String updated;
	
	@Length(min=0, max=8)
	private String updatedBy;

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}
	
	/**
	 * This field is never persisted in the database, but it is present on
	 * incoming values posted to the web service. We @JsonIgnore this so that
	 * no "transactionType" keys are included in the CRU services' responses
	 * http://www.davismol.net/2015/03/21/jackson-using-jsonignore-and-jsonproperty-annotations-to-exclude-a-property-only-from-json-deserialization/
	 */
	@JsonIgnore
	public String getTransactionType() {
		return transactionType;
	}

	/**
	 * We don't @JsonIgnore `setTransactionType()`, instead we @JsonProperty
	 * it because we still want the CRU services to read this field from 
	 * requests they receive.
	 * http://www.davismol.net/2015/03/21/jackson-using-jsonignore-and-jsonproperty-annotations-to-exclude-a-property-only-from-json-deserialization/
	 * 
	 * @param transactionType 
	 */
	@JsonProperty
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
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

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getStationIx() {
		return stationIx;
	}

	public void setStationIx(String stationIx) {
		this.stationIx = stationIx;
	}
	
	public String getSiteTypeCode() {
		return siteTypeCode;
	}

	public void setSiteTypeCode(String siteTypeCode) {
		this.siteTypeCode = siteTypeCode;
	}

	public String getDecimalLatitude() {
		return decimalLatitude;
	}

	public void setDecimalLatitude(String decimalLatitude) {
		this.decimalLatitude = decimalLatitude;
	}

	public String getDecimalLongitude() {
		return decimalLongitude;
	}

	public void setDecimalLongitude(String decimalLongitude) {
		this.decimalLongitude = decimalLongitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getCoordinateAccuracyCode() {
		return coordinateAccuracyCode;
	}

	public void setCoordinateAccuracyCode(String coordinateAccuracyCode) {
		this.coordinateAccuracyCode = coordinateAccuracyCode;
	}

	public String getCoordinateDatumCode() {
		return coordinateDatumCode;
	}

	public void setCoordinateDatumCode(String coordinateDatumCode) {
		this.coordinateDatumCode = coordinateDatumCode;
	}

	public String getCoordinateMethodCode() {
		return coordinateMethodCode;
	}

	public void setCoordinateMethodCode(String coordinateMethodCode) {
		this.coordinateMethodCode = coordinateMethodCode;
	}

	public String getAltitude() {
		return altitude;
	}

	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}

	public String getAltitudeDatumCode() {
		return altitudeDatumCode;
	}

	public void setAltitudeDatumCode(String altitudeDatumCode) {
		this.altitudeDatumCode = altitudeDatumCode;
	}

	public String getAltitudeMethodCode() {
		return altitudeMethodCode;
	}

	public void setAltitudeMethodCode(String altitudeMethodCode) {
		this.altitudeMethodCode = altitudeMethodCode;
	}

	public String getAltitudeAccuracyValue() {
		return altitudeAccuracyValue;
	}

	public void setAltitudeAccuracyValue(String altitudeAccuracyValue) {
		this.altitudeAccuracyValue = altitudeAccuracyValue;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getStateFipsCode() {
		return stateFipsCode;
	}

	public void setStateFipsCode(String stateFipsCode) {
		this.stateFipsCode = stateFipsCode;
	}

	public String getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	public String getMinorCivilDivisionCode() {
		return minorCivilDivisionCode;
	}

	public void setMinorCivilDivisionCode(String minorCivilDivisionCode) {
		this.minorCivilDivisionCode = minorCivilDivisionCode;
	}

	public String getHydrologicUnitCode() {
		return hydrologicUnitCode;
	}

	public void setHydrologicUnitCode(String hydrologicUnitCode) {
		this.hydrologicUnitCode = hydrologicUnitCode;
	}

	public String getBasinCode() {
		return basinCode;
	}

	public void setBasinCode(String basinCode) {
		this.basinCode = basinCode;
	}

	public String getNationalAquiferCode() {
		return nationalAquiferCode;
	}

	public void setNationalAquiferCode(String nationalAquiferCode) {
		this.nationalAquiferCode = nationalAquiferCode;
	}

	public String getAquiferCode() {
		return aquiferCode;
	}

	public void setAquiferCode(String aquiferCode) {
		this.aquiferCode = aquiferCode;
	}

	public String getAquiferTypeCode() {
		return aquiferTypeCode;
	}

	public void setAquiferTypeCode(String aquiferTypeCode) {
		this.aquiferTypeCode = aquiferTypeCode;
	}

	public String getAgencyUseCode() {
		return agencyUseCode;
	}

	public void setAgencyUseCode(String agencyUseCode) {
		this.agencyUseCode = agencyUseCode;
	}

	public String getDataReliabilityCode() {
		return dataReliabilityCode;
	}

	public void setDataReliabilityCode(String dataReliabilityCode) {
		this.dataReliabilityCode = dataReliabilityCode;
	}

	public String getLandNet() {
		return landNet;
	}

	public void setLandNet(String landNet) {
		this.landNet = landNet;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getMapScale() {
		return mapScale;
	}

	public void setMapScale(String mapScale) {
		this.mapScale = mapScale;
	}

	public String getNationalWaterUseCode() {
		return nationalWaterUseCode;
	}

	public void setNationalWaterUseCode(String nationalWaterUseCode) {
		this.nationalWaterUseCode = nationalWaterUseCode;
	}

	public String getPrimaryUseOfSite() {
		return primaryUseOfSite;
	}

	public void setPrimaryUseOfSite(String primaryUseOfSite) {
		this.primaryUseOfSite = primaryUseOfSite;
	}

	public String getSecondaryUseOfSite() {
		return secondaryUseOfSite;
	}

	public void setSecondaryUseOfSite(String secondaryUseOfSite) {
		this.secondaryUseOfSite = secondaryUseOfSite;
	}

	public String getTertiaryUseOfSiteCode() {
		return tertiaryUseOfSiteCode;
	}

	public void setTertiaryUseOfSiteCode(String tertiaryUseOfSiteCode) {
		this.tertiaryUseOfSiteCode = tertiaryUseOfSiteCode;
	}

	public String getPrimaryUseOfWaterCode() {
		return primaryUseOfWaterCode;
	}

	public void setPrimaryUseOfWaterCode(String primaryUseOfWaterCode) {
		this.primaryUseOfWaterCode = primaryUseOfWaterCode;
	}

	public String getSecondaryUseOfWaterCode() {
		return secondaryUseOfWaterCode;
	}

	public void setSecondaryUseOfWaterCode(String secondaryUseOfWaterCode) {
		this.secondaryUseOfWaterCode = secondaryUseOfWaterCode;
	}

	public String getTertiaryUseOfWaterCode() {
		return tertiaryUseOfWaterCode;
	}

	public void setTertiaryUseOfWaterCode(String tertiaryUseOfWaterCode) {
		this.tertiaryUseOfWaterCode = tertiaryUseOfWaterCode;
	}

	public String getTopographicCode() {
		return topographicCode;
	}

	public void setTopographicCode(String topographicCode) {
		this.topographicCode = topographicCode;
	}

	public String getDataTypesCode() {
		return dataTypesCode;
	}

	public void setDataTypesCode(String dataTypesCode) {
		this.dataTypesCode = dataTypesCode;
	}

	public String getInstrumentsCode() {
		return instrumentsCode;
	}

	public void setInstrumentsCode(String instrumentsCode) {
		this.instrumentsCode = instrumentsCode;
	}

	public String getContributingDrainageArea() {
		return contributingDrainageArea;
	}

	public void setContributingDrainageArea(String contributingDrainageArea) {
		this.contributingDrainageArea = contributingDrainageArea;
	}

	public String getDrainageArea() {
		return drainageArea;
	}

	public void setDrainageArea(String drainageArea) {
		this.drainageArea = drainageArea;
	}

	public String getFirstConstructionDate() {
		return firstConstructionDate;
	}

	public void setFirstConstructionDate(String firstConstructionDate) {
		this.firstConstructionDate = firstConstructionDate;
	}

	public String getSiteEstablishmentDate() {
		return siteEstablishmentDate;
	}

	public void setSiteEstablishmentDate(String siteEstablishmentDate) {
		this.siteEstablishmentDate = siteEstablishmentDate;
	}

	public String getHoleDepth() {
		return holeDepth;
	}

	public void setHoleDepth(String holeDepth) {
		this.holeDepth = holeDepth;
	}

	public String getWellDepth() {
		return wellDepth;
	}

	public void setWellDepth(String wellDepth) {
		this.wellDepth = wellDepth;
	}

	public String getSourceOfDepthCode() {
		return sourceOfDepthCode;
	}

	public void setSourceOfDepthCode(String sourceOfDepthCode) {
		this.sourceOfDepthCode = sourceOfDepthCode;
	}

	public String getProjectNumber() {
		return projectNumber;
	}

	public void setProjectNumber(String projectNumber) {
		this.projectNumber = projectNumber;
	}

	public String getTimeZoneCode() {
		return timeZoneCode;
	}

	public void setTimeZoneCode(String timeZoneCode) {
		this.timeZoneCode = timeZoneCode;
	}

	public String getDaylightSavingsTimeFlag() {
		return daylightSavingsTimeFlag;
	}

	public void setDaylightSavingsTimeFlag(String daylightSavingsTimeFlag) {
		this.daylightSavingsTimeFlag = daylightSavingsTimeFlag;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSiteWebReadyCode() {
		return siteWebReadyCode;
	}

	public void setSiteWebReadyCode(String siteWebReadyCode) {
		this.siteWebReadyCode = siteWebReadyCode;
	}

	public String getGwFileCode() {
		return gwFileCode;
	}

	public void setGwFileCode(String gwFileCode) {
		this.gwFileCode = gwFileCode;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

}
