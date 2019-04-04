package gov.usgs.wma.mlrlegacy.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.wma.mlrlegacy.Controller;
import gov.usgs.wma.mlrlegacy.MonitoringLocation;
import gov.usgs.wma.mlrlegacy.MonitoringLocationDao;

/**
 * DAO integration tests for Read operations
 */
@DatabaseSetup("classpath:/testData/setupOne/")
public class MonitoringLocationDaoRIT extends BaseDaoIT {

	@Autowired
	private MonitoringLocationDao dao;

	@Test
	public void getAll() {
		MonitoringLocation location = dao.getByAK(null);
		assertNull(location);
	}
	
	@Test
	public void getByAgencyCode() {
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.AGENCY_CODE, DEFAULT_AGENCY_CODE);
		MonitoringLocation location = dao.getByAK(params);
		assertNull(location);
	}
	
	@Test
	public void getBySiteNumber() {
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.SITE_NUMBER, DEFAULT_SITE_NUMBER);
		MonitoringLocation location = dao.getByAK(params);
		assertNull(location);
	}
	
	@Test
	public void getByAgencyCodeAndSiteNumber() {
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.AGENCY_CODE, DEFAULT_AGENCY_CODE);
		params.put(Controller.SITE_NUMBER, DEFAULT_SITE_NUMBER);
		MonitoringLocation location = dao.getByAK(params);
		assertOneMillion(location);
	}
	
	@Test
	public void getByAgencyCodeAndSiteNumberNotFound() {
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.AGENCY_CODE, "USEPA");
		params.put(Controller.SITE_NUMBER, DEFAULT_SITE_NUMBER);
		MonitoringLocation location = dao.getByAK(params);
		assertNull(location);
	}
	
	@Test
	public void getByNormalizedStationName() {
		final String MY_NORMALIZED_STATION_NAME = "STATIONIX";
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.NORMALIZED_STATION_NAME, MY_NORMALIZED_STATION_NAME);
		List<MonitoringLocation> locations = dao.getByNormalizedName(params);
		assertNotNull(locations);
		assertEquals(1, locations.size());
		MonitoringLocation location = locations.get(0);
		assertOneMillion(location);
	}
	
	/**
	 * We want to ensure that the DAO returns multiple matching results,
	 * while excluding non-matching results. Accordingly, we set up a db
	 * with two matching results and one non-matching result.
	 */
	@DatabaseSetup("classpath:/testData/setupThree/")
	@Test
	public void getByStationNameMultipleResults() {
		final String MY_NORMALIZED_STATION_NAME = "STATIONNM";
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.NORMALIZED_STATION_NAME, MY_NORMALIZED_STATION_NAME);
		List<MonitoringLocation> locations = dao.getByNormalizedName(params);
		assertNotNull(locations);

		assertEquals(2, locations.size());
		MonitoringLocation location0 = locations.get(0);
		MonitoringLocation location1 = locations.get(1);

		assertEquals(MY_NORMALIZED_STATION_NAME, location0.getStationIx());
		assertEquals(MY_NORMALIZED_STATION_NAME, location1.getStationIx());
		assertNotEquals(location0.getId(), location1.getId());
	}

	@Test
	public void getByStationNameNotFound() {
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.NORMALIZED_STATION_NAME, "DOESNOTEXIST");
		List<MonitoringLocation> locations = dao.getByNormalizedName(params);
		assertEquals(0, locations.size());
	}
	
	@Test
	public void getById() {
		MonitoringLocation location = dao.getById(ONE_MILLION);
		assertOneMillion(location);
	}

	public static void assertOneMillion(MonitoringLocation location) {
		assertEquals(ONE_MILLION, location.getId());
		assertEquals(DEFAULT_AGENCY_CODE, location.getAgencyCode());
		assertEquals(DEFAULT_SITE_NUMBER, location.getSiteNumber());
		assertEquals("station_nm", location.getStationName());
		assertEquals("STATIONIX", location.getStationIx());
		assertEquals("lat_va     ", location.getLatitude());
		assertEquals("long_va     ", location.getLongitude());
		assertEquals("43.3836014", location.getDecimalLatitude());
		assertEquals("-88.9773314", location.getDecimalLongitude());
		assertEquals("a", location.getCoordinateMethodCode());
		assertEquals("b", location.getCoordinateAccuracyCode());
		assertEquals("coord_datu", location.getCoordinateDatumCode());
		assertEquals("dis", location.getDistrictCode());
		assertEquals("land_net_ds", location.getLandNet());
		assertEquals("map_nm", location.getMapName());
		assertEquals("US", location.getCountryCode());
		assertEquals("55", location.getStateFipsCode());
		assertEquals("013", location.getCountyCode());
		assertEquals("map_sca", location.getMapScale());
		assertEquals("alt_va  ", location.getAltitude());
		assertEquals("c", location.getAltitudeMethodCode());
		assertEquals("aav", location.getAltitudeAccuracyValue());
		assertEquals("alt_datum_", location.getAltitudeDatumCode());
		assertEquals("070101010101", location.getHydrologicUnitCode());
		assertEquals("d", location.getAgencyUseCode());
		assertEquals("ee", location.getBasinCode());
		assertEquals("site_tp", location.getSiteTypeCode());
		assertEquals("f", location.getTopographicCode());
		assertEquals("data_types_cd                 ", location.getDataTypesCode());
		assertEquals("instruments_cd                ", location.getInstrumentsCode());
		assertEquals("site_rmks_tx", location.getRemarks());
		assertEquals("inventor", location.getSiteEstablishmentDate());
		assertEquals("drain_ar", location.getDrainageArea());
		assertEquals("contrib_", location.getContributingDrainageArea());
		assertEquals("tz_cd ", location.getTimeZoneCode());
		assertEquals("g", location.getDaylightSavingsTimeFlag());
		assertEquals("gw_file_cd          ", location.getGwFileCode());
		assertEquals("construc", location.getFirstConstructionDate());
		assertEquals("h", location.getDataReliabilityCode());
		assertEquals("aqfr_cd ", location.getAquiferCode());
		assertEquals("nat_aqfr_c", location.getNationalAquiferCode());
		assertEquals("i", location.getPrimaryUseOfSite());
		assertEquals("j", location.getSecondaryUseOfSite());
		assertEquals("k", location.getTertiaryUseOfSiteCode());
		assertEquals("l", location.getPrimaryUseOfWaterCode());
		assertEquals("m", location.getSecondaryUseOfWaterCode());
		assertEquals("n", location.getTertiaryUseOfWaterCode());
		assertEquals("na", location.getNationalWaterUseCode());
		assertEquals("o", location.getAquiferTypeCode());
		assertEquals("well_dep", location.getWellDepth());
		assertEquals("hole_dep", location.getHoleDepth());
		assertEquals("p", location.getSourceOfDepthCode());
		assertEquals("project_no  ", location.getProjectNumber());
		assertEquals("q", location.getSiteWebReadyCode());
		assertEquals(DEFAULT_CREATED_BY, location.getCreatedBy());
		assertEquals(DEFAULT_CREATED_DATE_S, location.getCreated());
		assertEquals(DEFAULT_UPDATED_BY, location.getUpdatedBy());
		assertEquals(DEFAULT_UPDATED_DATE_S, location.getUpdated());
		assertEquals("mcd_c", location.getMinorCivilDivisionCode());
	}

}
