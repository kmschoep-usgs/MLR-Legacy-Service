package gov.usgs.wma.mlrlegacy.db;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.wma.mlrlegacy.Controller;
import gov.usgs.wma.mlrlegacy.model.MonitoringLocation;
import gov.usgs.wma.mlrlegacy.dao.MonitoringLocationDao;

/**
 * DAO integration tests for Create and Update operations
 */
@ActiveProfiles("it")
public class MonitoringLocationDaoCUIT extends BaseDaoIT {

	@Autowired
	private MonitoringLocationDao dao;

	@Test
	@DatabaseSetup("classpath:/testData/emptyDatabase/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_MINUTE,
			value="classpath:/testResult/oneSparseResultDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void createSparce() {
		id = String.valueOf(dao.create(buildASparseMonitoringLocation()));
		agency = UPDATED_AGENCY_CODE;
		siteNbr = UPDATED_SITE_NUMBER;
		createdDate = nowMinutes;
		createdBy = UPDATED_CREATED_BY;
		updatedDate = nowMinutes;
		updatedBy = UPDATED_MODIFIED_BY;
	}

	@Test
	@DatabaseSetup("classpath:/testData/emptyDatabase/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_MINUTE,
			value="classpath:/testResult/oneResultDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void createFull() {
		id = String.valueOf(dao.create(buildAMonitoringLocation()));
		createdDate = nowMinutes;
		createdBy = UPDATED_CREATED_BY;
		updatedDate = nowMinutes;
		updatedBy = UPDATED_MODIFIED_BY;
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_MINUTE,
			value="classpath:/testResult/oneSparseResultDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void updateSparse() {
		MonitoringLocation ml = buildASparseMonitoringLocation();
		ml.setId(ONE_MILLION);
		dao.update(ml);
		id = String.valueOf(ONE_MILLION);
		agency = UPDATED_AGENCY_CODE;
		siteNbr = UPDATED_SITE_NUMBER;
		createdDate = DEFAULT_CREATED_DATE_M;
		createdBy = DEFAULT_CREATED_BY;
		updatedDate = nowMinutes;
		updatedBy = UPDATED_MODIFIED_BY;
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_MINUTE,
			value="classpath:/testResult/oneResultDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void updateFull() {
		MonitoringLocation ml = buildAMonitoringLocation();
		ml.setId(ONE_MILLION);
		dao.update(ml);
		id = String.valueOf(ONE_MILLION);
		createdDate = DEFAULT_CREATED_DATE_M;
		createdBy = DEFAULT_CREATED_BY;
		updatedDate = nowMinutes;
		updatedBy = UPDATED_MODIFIED_BY;
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_MINUTE,
			value="classpath:/testResult/oneSparsePatchDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void patchSparse() {
		Map<String, Object> ml = new HashMap<>();
		ml.put(Controller.AGENCY_CODE, DEFAULT_AGENCY_CODE);
		ml.put(Controller.SITE_NUMBER, DEFAULT_SITE_NUMBER);
		ml.put(Controller.UPDATED_BY, UPDATED_MODIFIED_BY);
		dao.patch(ml);
		id = String.valueOf(ONE_MILLION);
		createdDate = DEFAULT_CREATED_DATE_M;
		createdBy = DEFAULT_CREATED_BY;
		updatedDate = nowMinutes;
		updatedBy = UPDATED_MODIFIED_BY;
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_MINUTE,
			value="classpath:/testResult/oneFullPatchDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void patchFull() {
		Map<String, Object> ml = buildAFullMonitoringLocationMap();
		ml.put(Controller.AGENCY_CODE, DEFAULT_AGENCY_CODE);
		ml.put(Controller.SITE_NUMBER, DEFAULT_SITE_NUMBER);
		dao.patch(ml);
		id = String.valueOf(ONE_MILLION);
		String nowMinutes = LocalDateTime.now(ZoneId.of("UTC")).toString().replace("T", " ").substring(0, 16);
		createdDate = DEFAULT_CREATED_DATE_M;
		createdBy = DEFAULT_CREATED_BY;
		updatedDate = nowMinutes;
		updatedBy = UPDATED_MODIFIED_BY;
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_MINUTE,
			value="classpath:/testResult/onePatchBlanksResultDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void patchFullBlanks() {
		Map<String, Object> ml = buildAFullBlankMonitoringLocationMap();
		ml.put(Controller.AGENCY_CODE, DEFAULT_AGENCY_CODE);
		ml.put(Controller.SITE_NUMBER, DEFAULT_SITE_NUMBER);
		dao.patch(ml);
		id = String.valueOf(ONE_MILLION);
		createdDate = DEFAULT_CREATED_DATE_M;
		createdBy = DEFAULT_CREATED_BY;
		updatedDate = nowMinutes;
		updatedBy = UPDATED_MODIFIED_BY;
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_MINUTE,
			value="classpath:/testResult/oneSparseResultDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void patchFullNull() {
		Map<String, Object> ml = buildAFullNullMonitoringLocationMap();
		ml.put(Controller.AGENCY_CODE, DEFAULT_AGENCY_CODE);
		ml.put(Controller.SITE_NUMBER, DEFAULT_SITE_NUMBER);
		dao.patch(ml);
		id = String.valueOf(ONE_MILLION);
		createdDate = DEFAULT_CREATED_DATE_M;
		createdBy = DEFAULT_CREATED_BY;
		updatedDate = nowMinutes;
		updatedBy = UPDATED_MODIFIED_BY;
	}

	public static MonitoringLocation buildASparseMonitoringLocation() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setAgencyCode(UPDATED_AGENCY_CODE);
		ml.setSiteNumber(UPDATED_SITE_NUMBER);
		ml.setCreatedBy(UPDATED_CREATED_BY);
		ml.setCreated("2017-08-26 07:33:43");
		ml.setUpdatedBy(UPDATED_MODIFIED_BY);
		ml.setUpdated("2017-08-27 23:59:59");
		return ml;
	}

	public static MonitoringLocation buildAMonitoringLocation() {
		MonitoringLocation ml = buildASparseMonitoringLocation();
		ml.setStationName("station_nmx");
		ml.setStationIx("STATIONIXX");
		ml.setLatitude("lat_va    x");
		ml.setLongitude("long_va    x");
		ml.setDecimalLatitude("43.38360141");
		ml.setDecimalLongitude("-88.97733141");
		ml.setCoordinateMethodCode("z");
		ml.setCoordinateAccuracyCode("y");
		ml.setCoordinateDatumCode("coord_datx");
		ml.setDistrictCode("dix");
		ml.setLandNet("land_net_dx");
		ml.setMapName("map_nx");
		ml.setCountryCode("UX");
		ml.setStateFipsCode("56");
		ml.setCountyCode("016");
		ml.setMapScale("map_scx");
		ml.setAltitude("alt_va x");
		ml.setAltitudeMethodCode("x");
		ml.setAltitudeAccuracyValue("aax");
		ml.setAltitudeDatumCode("alt_datumx");
		ml.setHydrologicUnitCode("07010101010101");
		ml.setAgencyUseCode("w");
		ml.setBasinCode("ex");
		ml.setSiteTypeCode("site_tx");
		ml.setTopographicCode("v");
		ml.setDataTypesCode("data_types_cd                x");
		ml.setInstrumentsCode("instruments_cd               x");
		ml.setRemarks("site_rmks_tv");
		ml.setSiteEstablishmentDate("inventox");
		ml.setDrainageArea("drain_ax");
		ml.setContributingDrainageArea("contribx");
		ml.setTimeZoneCode("tz_cx");
		ml.setDaylightSavingsTimeFlag("u");
		ml.setGwFileCode("gw_file_cd         x");
		ml.setFirstConstructionDate("construx");
		ml.setDataReliabilityCode("t");
		ml.setAquiferCode("aqfr_cdx");
		ml.setNationalAquiferCode("nat_aqfr_x");
		ml.setPrimaryUseOfSite("s");
		ml.setSecondaryUseOfSite("r");
		ml.setTertiaryUseOfSiteCode("q");
		ml.setPrimaryUseOfWaterCode("p");
		ml.setSecondaryUseOfWaterCode("o");
		ml.setTertiaryUseOfWaterCode("m");
		ml.setNationalWaterUseCode("nx");
		ml.setAquiferTypeCode("l");
		ml.setWellDepth("well_dex");
		ml.setHoleDepth("hole_dex");
		ml.setSourceOfDepthCode("k");
		ml.setProjectNumber("project_no x");
		ml.setSiteWebReadyCode("j");
		ml.setMinorCivilDivisionCode("mcd_x");
		return ml;
	}

	public static Map<String, Object> buildAFullMonitoringLocationMap() {
		Map<String, Object> ml = new HashMap<>();
		ml.put("stationName", "station_nmx");
		ml.put("stationIx", "STATIONIXX");
		ml.put("latitude", "lat_va    x");
		ml.put("longitude", "long_va    x");
		ml.put("decimalLatitude", "43.38360141");
		ml.put("decimalLongitude", "-88.97733141");
		ml.put("coordinateMethodCode", "z");
		ml.put("coordinateAccuracyCode", "y");
		ml.put("coordinateDatumCode", "coord_datx");
		ml.put("districtCode", "dix");
		ml.put("landNet", "land_net_dx");
		ml.put("mapName", "map_nx");
		ml.put("countryCode", "UX");
		ml.put("stateFipsCode", "56");
		ml.put("countyCode", "016");
		ml.put("mapScale", "map_scx");
		ml.put("altitude", "alt_va x");
		ml.put("altitudeMethodCode", "x");
		ml.put("altitudeAccuracyValue", "aax");
		ml.put("altitudeDatumCode", "alt_datumx");
		ml.put("hydrologicUnitCode", "07010101010101");
		ml.put("agencyUseCode", "w");
		ml.put("basinCode", "ex");
		ml.put("siteTypeCode", "site_tx");
		ml.put("topographicCode", "v");
		ml.put("dataTypesCode", "data_types_cd                x");
		ml.put("instrumentsCode", "instruments_cd               x");
		ml.put("remarks", "site_rmks_tv");
		ml.put("siteEstablishmentDate", "inventox");
		ml.put("drainageArea", "drain_ax");
		ml.put("contributingDrainageArea", "contribx");
		ml.put("timeZoneCode", "tz_cx");
		ml.put("daylightSavingsTimeFlag", "u");
		ml.put("gwFileCode", "gw_file_cd         x");
		ml.put("firstConstructionDate", "construx");
		ml.put("dataReliabilityCode", "t");
		ml.put("aquiferCode", "aqfr_cdx");
		ml.put("nationalAquiferCode", "nat_aqfr_x");
		ml.put("primaryUseOfSite", "s");
		ml.put("secondaryUseOfSite", "r");
		ml.put("tertiaryUseOfSiteCode", "q");
		ml.put("primaryUseOfWaterCode", "p");
		ml.put("secondaryUseOfWaterCode", "o");
		ml.put("tertiaryUseOfWaterCode", "m");
		ml.put("nationalWaterUseCode", "nx");
		ml.put("aquiferTypeCode", "l");
		ml.put("wellDepth", "well_dex");
		ml.put("holeDepth", "hole_dex");
		ml.put("sourceOfDepthCode", "k");
		ml.put("projectNumber", "project_no x");
		ml.put("siteWebReadyCode", "j");
		ml.put("minorCivilDivisionCode", "mcd_x");
		ml.put(Controller.UPDATED_BY, UPDATED_MODIFIED_BY);
		return ml;
	}

	public static Map<String, Object> buildAFullBlankMonitoringLocationMap() {
		Map<String, Object> ml = new HashMap<>();
		for (String key : buildAFullMonitoringLocationMap().keySet()) {
			ml.put(key, " ");
		}
		//These three are nullable, so it would be possible to 
		ml.put("decimalLatitude", null);
		ml.put("decimalLongitude", null);
		ml.put("minorCivilDivisionCode", "");
		ml.put(Controller.UPDATED_BY, UPDATED_MODIFIED_BY);
		return ml;
	}

	public static Map<String, Object> buildAFullNullMonitoringLocationMap() {
		Map<String, Object> ml = new HashMap<>();
		for (String key : buildAFullMonitoringLocationMap().keySet()) {
			ml.put(key, null);
		}
		ml.put(Controller.UPDATED_BY, UPDATED_MODIFIED_BY);
		return ml;

	}

}
