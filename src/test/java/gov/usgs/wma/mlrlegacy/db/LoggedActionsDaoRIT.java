package gov.usgs.wma.mlrlegacy.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.wma.mlrlegacy.Controller;
import gov.usgs.wma.mlrlegacy.model.LoggedAction;
import gov.usgs.wma.mlrlegacy.model.LoggedTransactionSummary;
import gov.usgs.wma.mlrlegacy.model.MonitoringLocation;
import gov.usgs.wma.mlrlegacy.dao.LoggedActionsDao;
import gov.usgs.wma.mlrlegacy.dao.MonitoringLocationDao;

/**
 * DAO integration tests for Read operations
 */

@ActiveProfiles("it")
@DatabaseSetup("classpath:/testData/emptyDatabase/")
public class LoggedActionsDaoRIT extends BaseDaoIT {

	@Autowired
	private LoggedActionsDao dao;

	@Autowired
	private MonitoringLocationDao mlDao;
	
	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	public void getActionsByAgencyCodeTest() {
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.AGENCY_CODE, DEFAULT_AGENCY_CODE_TRIMMED);
		List<LoggedAction> loggedActions = dao.findActions(params);
		assertTrue(loggedActions.size() > 0);
		assertEquals(DEFAULT_AGENCY_CODE, loggedActions.get(0).getAgencyCode());
		// Not guaranteed a specific site number so just check that we got any number
		assertFalse(loggedActions.get(0).getSiteNumber().isEmpty());
	}
	
	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	public void getActionsBySiteNumberTest() {
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.SITE_NUMBER, DEFAULT_SITE_NUMBER);
		List<LoggedAction> loggedActions = dao.findActions(params);
		assertTrue(loggedActions.size() > 0);
		assertEquals(DEFAULT_AGENCY_CODE, loggedActions.get(0).getAgencyCode());
		assertEquals(DEFAULT_SITE_NUMBER, loggedActions.get(0).getSiteNumber());
	}
	
	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	public void getActionsByAgencyCodeAndSiteNumberTest() {
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.AGENCY_CODE, DEFAULT_AGENCY_CODE_TRIMMED);
		params.put(Controller.SITE_NUMBER, DEFAULT_SITE_NUMBER);
		List<LoggedAction> loggedActions = dao.findActions(params);
		assertTrue(loggedActions.size() > 0);
		assertEquals(DEFAULT_AGENCY_CODE, loggedActions.get(0).getAgencyCode());
		assertEquals(DEFAULT_SITE_NUMBER, loggedActions.get(0).getSiteNumber());
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	public void getTransactionSummaryByDCSingleTest() {
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.DISTRICT_CODE, "dis");
		List<LoggedTransactionSummary> summaries = dao.transactionSummaryByDC(params);
		assertEquals(1, summaries.size());
		assertEquals("dis", summaries.get(0).getDistrictCode());
		// Can't guarantee an exact number here since logs aren't purged between tests
		assertTrue(summaries.get(0).getInsertCount() > 0);
		assertTrue(summaries.get(0).getUpdateCount() != null);
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupThreeDistrictCodes/")
	public void getTransactionSummaryByDCMulti() {
		Map<String, Object> params = new HashMap<>();
		List<LoggedTransactionSummary> summaries = dao.transactionSummaryByDC(params);

		// Can't guarantee an exact number here since logs aren't purged between tests
		assertTrue(summaries.size() >= 3);
	}

	@Test
	public void getTransactionSummaryByDCMultiInsTest() {
		MonitoringLocation newLoc1 = new MonitoringLocation();
		newLoc1.setAgencyCode("USGS");
		newLoc1.setSiteNumber("12345671");
		newLoc1.setStationName("loc1");
		newLoc1.setStationIx("loc1");
		newLoc1.setDistrictCode("zz1");
		newLoc1.setCreated("2017-08-24 09:15:23");
		newLoc1.setUpdated("2017-08-24 09:15:23");
		newLoc1.setCreatedBy("site_cn");
		newLoc1.setUpdatedBy("site_mn");

		MonitoringLocation newLoc2 = new MonitoringLocation();
		newLoc2.setAgencyCode("USGS");
		newLoc2.setSiteNumber("12345672");
		newLoc2.setStationName("loc2");
		newLoc2.setStationIx("loc2");
		newLoc2.setDistrictCode("zz1");
		newLoc2.setCreated("2017-08-24 09:15:23");
		newLoc2.setUpdated("2017-08-24 09:15:23");
		newLoc2.setCreatedBy("site_cn");
		newLoc2.setUpdatedBy("site_mn");

		MonitoringLocation newLoc3 = new MonitoringLocation();
		newLoc3.setAgencyCode("USGS");
		newLoc3.setSiteNumber("12345673");
		newLoc3.setStationName("loc2");
		newLoc3.setStationIx("loc2");
		newLoc3.setDistrictCode("zz2");
		newLoc3.setCreated("2017-08-24 09:15:23");
		newLoc3.setUpdated("2017-08-24 09:15:23");
		newLoc3.setCreatedBy("site_cn");
		newLoc3.setUpdatedBy("site_mn");

		mlDao.create(newLoc1);
		mlDao.create(newLoc2);
		mlDao.create(newLoc3);

		List<LoggedTransactionSummary> summaries = dao.transactionSummaryByDC(new HashMap<>());
		assertTrue(summaries.size() >= 2);

		LoggedTransactionSummary zz1Sum = summaries.stream().filter(s -> s.getDistrictCode().equals("zz1")).collect(Collectors.toList()).get(0);
		LoggedTransactionSummary zz2Sum = summaries.stream().filter(s -> s.getDistrictCode().equals("zz2")).collect(Collectors.toList()).get(0);

		assertNotNull(zz1Sum);
		assertNotNull(zz2Sum);
		assertEquals(2, zz1Sum.getInsertCount());
		assertEquals(0, zz1Sum.getUpdateCount());
		assertEquals(1, zz2Sum.getInsertCount());
		assertEquals(0, zz2Sum.getUpdateCount());
	}

	@Test
	public void getTransactionSummaryByDCMultiModsTest() {
		MonitoringLocation newLoc1 = new MonitoringLocation();
		newLoc1.setAgencyCode("USGS");
		newLoc1.setSiteNumber("12345671");
		newLoc1.setStationName("loc1");
		newLoc1.setStationIx("loc1");
		newLoc1.setDistrictCode("zz1");
		newLoc1.setCreated("2017-08-24 09:15:23");
		newLoc1.setUpdated("2017-08-24 09:15:23");
		newLoc1.setCreatedBy("site_cn");
		newLoc1.setUpdatedBy("site_mn");

		MonitoringLocation newLoc2 = new MonitoringLocation();
		newLoc2.setAgencyCode("USGS");
		newLoc2.setSiteNumber("12345672");
		newLoc2.setStationName("loc2");
		newLoc2.setStationIx("loc2");
		newLoc2.setDistrictCode("zz1");
		newLoc2.setCreated("2017-08-24 09:15:23");
		newLoc2.setUpdated("2017-08-24 09:15:23");
		newLoc2.setCreatedBy("site_cn");
		newLoc2.setUpdatedBy("site_mn");

		MonitoringLocation newLoc3 = new MonitoringLocation();
		newLoc3.setAgencyCode("USGS");
		newLoc3.setSiteNumber("12345673");
		newLoc3.setStationName("loc2");
		newLoc3.setStationIx("loc2");
		newLoc3.setDistrictCode("zz2");
		newLoc3.setCreated("2017-08-24 09:15:23");
		newLoc3.setUpdated("2017-08-24 09:15:23");
		newLoc3.setCreatedBy("site_cn");
		newLoc3.setUpdatedBy("site_mn");

		mlDao.create(newLoc1);
		mlDao.create(newLoc2);
		mlDao.create(newLoc3);
		newLoc1.setDistrictCode("zz3");
		newLoc2.setDistrictCode("zz2");
		mlDao.update(newLoc1);
		mlDao.update(newLoc2);

		List<LoggedTransactionSummary> summaries = dao.transactionSummaryByDC(new HashMap<>());
		assertTrue(summaries.size() >= 3);

		LoggedTransactionSummary zz1Sum = summaries.stream().filter(s -> s.getDistrictCode().equals("zz1")).collect(Collectors.toList()).get(0);
		LoggedTransactionSummary zz2Sum = summaries.stream().filter(s -> s.getDistrictCode().equals("zz2")).collect(Collectors.toList()).get(0);
		LoggedTransactionSummary zz3Sum = summaries.stream().filter(s -> s.getDistrictCode().equals("zz3")).collect(Collectors.toList()).get(0);

		assertNotNull(zz1Sum);
		assertNotNull(zz2Sum);
		assertNotNull(zz3Sum);
		assertEquals(2, zz1Sum.getInsertCount());
		assertEquals(2, zz1Sum.getUpdateCount());
		assertEquals(1, zz2Sum.getInsertCount());
		assertEquals(1, zz2Sum.getUpdateCount());
		assertEquals(0, zz3Sum.getInsertCount());
		assertEquals(1, zz3Sum.getUpdateCount());
	}

	/*
	@Test
	public void findTransactionsSingleTest() {

	}

	@Test
	public void findTransactionsMultiTest() {
		
	}

	@Test
	public void findTransactionsUserTest() {
		
	}

	@Test
	public void findTransactionsNoResultTest() {
		
	}
	*/
}
