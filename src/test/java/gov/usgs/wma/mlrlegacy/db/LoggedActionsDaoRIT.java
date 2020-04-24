package gov.usgs.wma.mlrlegacy.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
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
import gov.usgs.wma.mlrlegacy.model.LoggedTransaction;
import gov.usgs.wma.mlrlegacy.model.LoggedTransactionQueryParams;
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

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	public void findTransactionsByAgencyCodeSiteNumberTest() {
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.AGENCY_CODE, DEFAULT_AGENCY_CODE_TRIMMED);
		params.put(Controller.SITE_NUMBER, DEFAULT_SITE_NUMBER);
		params.put(LoggedTransactionQueryParams.ACTION, "I");
		List<LoggedTransaction> transactions = dao.findTransactions(params);

		// Can't guarantee an exact number here since logs aren't purged between tests
		assertTrue(transactions.size() >= 1);
		assertNotNull(transactions.get(0).getChanges());
		assertTrue(transactions.get(0).getChanges().isEmpty());
		assertEquals(1, transactions.get(0).getAffectedDistricts().size());
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupThreeDistrictCodes/")
	public void findTransactionsMultiTest() {
		Map<String, Object> params = new HashMap<>();
		List<LoggedTransaction> transactions = dao.findTransactions(params);

		// Can't guarantee an exact number here since logs aren't purged between tests
		assertTrue(transactions.size() >= 3);
		assertTrue(transactions.stream().map(t -> t.getSiteNumber()).collect(Collectors.toSet()).size() >= 3);
		assertTrue(transactions.stream().map(t -> t.getSiteNumber()).collect(Collectors.toSet()).containsAll(
			Arrays.asList("123456789012345", "987654321098765", "876543210987654")
		));
	}

	@Test
	public void findTransactionsInsTest() {
		MonitoringLocation newLoc1 = new MonitoringLocation();
		newLoc1.setAgencyCode("USGS");
		newLoc1.setSiteNumber("88888887");
		newLoc1.setStationName("loc1");
		newLoc1.setStationIx("loc1");
		newLoc1.setDistrictCode("yy1");
		newLoc1.setCreated("2017-08-24 09:15:23");
		newLoc1.setUpdated("2017-08-24 09:15:23");
		newLoc1.setCreatedBy("acins_u");
		newLoc1.setUpdatedBy("acins_u");

		mlDao.create(newLoc1);

		Map<String, Object> params = new HashMap<>();
		params.put(Controller.AGENCY_CODE, "USGS");
		params.put(Controller.SITE_NUMBER,"88888887");
		params.put(LoggedTransactionQueryParams.USERNAME,"acins_u");
		List<LoggedTransaction> transactions = dao.findTransactions(params);

		assertEquals(1, transactions.size());
		assertEquals("I", transactions.get(0).getAction());
		assertEquals("USGS", transactions.get(0).getAgencyCode());
		assertEquals("88888887", transactions.get(0).getSiteNumber());
		assertEquals("acins_u", transactions.get(0).getUsername());
		assertEquals(1, transactions.get(0).getAffectedDistricts().size());
		assertTrue(transactions.get(0).getAffectedDistricts().contains("yy1"));
	}

	@Test
	public void findTransactionsModsTest() {
		MonitoringLocation newLoc1 = new MonitoringLocation();
		newLoc1.setAgencyCode("USGS");
		newLoc1.setSiteNumber("88888888");
		newLoc1.setStationName("loc1");
		newLoc1.setStationIx("loc1");
		newLoc1.setDistrictCode("yy1");
		newLoc1.setCreated("2017-08-24 09:15:23");
		newLoc1.setUpdated("2017-08-24 09:15:23");
		newLoc1.setCreatedBy("acins_u");
		newLoc1.setUpdatedBy("acins_u");

		mlDao.create(newLoc1);

		newLoc1.setDistrictCode("yy2");

		mlDao.update(newLoc1);

		newLoc1.setStationName("loc1_mod");
		newLoc1.setUpdatedBy("acmod_u");

		mlDao.update(newLoc1);

		Map<String, Object> params = new HashMap<>();
		params.put(Controller.AGENCY_CODE, "USGS");
		params.put(Controller.SITE_NUMBER,"88888888");
		params.put(LoggedTransactionQueryParams.DISTRICT_CODE, "yy1");

		List<LoggedTransaction> transactions = dao.findTransactions(params);
		assertEquals(2, transactions.size());

		params.put(LoggedTransactionQueryParams.ACTION, "I");
		transactions = dao.findTransactions(params);
		assertEquals(1, transactions.size());
		assertEquals("I", transactions.get(0).getAction());
		assertEquals("USGS", transactions.get(0).getAgencyCode());
		assertEquals("88888888", transactions.get(0).getSiteNumber());
		assertEquals("acins_u", transactions.get(0).getUsername());
		assertTrue(transactions.get(0).getChanges().isEmpty());
		assertEquals(1, transactions.get(0).getAffectedDistricts().size());
		assertTrue(transactions.get(0).getAffectedDistricts().contains("yy1"));

		params.put(LoggedTransactionQueryParams.ACTION, "U");
		transactions = dao.findTransactions(params);
		assertEquals(1, transactions.size());
		assertEquals("U", transactions.get(0).getAction());
		assertEquals("USGS", transactions.get(0).getAgencyCode());
		assertEquals("88888888", transactions.get(0).getSiteNumber());
		assertEquals("acins_u", transactions.get(0).getUsername());
		assertEquals(1, transactions.get(0).getChanges().size());
		assertEquals("district_cd", transactions.get(0).getChanges().get(0).column);
		assertEquals("yy1", transactions.get(0).getChanges().get(0).oldValue);
		assertEquals("yy2", transactions.get(0).getChanges().get(0).newValue);
		assertEquals(2, transactions.get(0).getAffectedDistricts().size());
		assertTrue(transactions.get(0).getAffectedDistricts().containsAll(Arrays.asList("yy1", "yy2")));

		params.put(LoggedTransactionQueryParams.DISTRICT_CODE, "yy2");
		transactions = dao.findTransactions(params);
		assertEquals(2, transactions.size());

		params.put(LoggedTransactionQueryParams.USERNAME, "acmod_u");
		transactions = dao.findTransactions(params);
		assertEquals(1, transactions.size());
		assertEquals("U", transactions.get(0).getAction());
		assertEquals("USGS", transactions.get(0).getAgencyCode());
		assertEquals("88888888", transactions.get(0).getSiteNumber());
		assertEquals("acmod_u", transactions.get(0).getUsername());
		assertEquals(1, transactions.get(0).getAffectedDistricts().size());
		assertTrue(transactions.get(0).getAffectedDistricts().containsAll(Arrays.asList("yy2")));
		assertEquals(2, transactions.get(0).getChanges().size());
		LoggedTransaction.ValueChange nmChange = transactions.get(0).getChanges().stream().filter(c -> c.column.equals("station_nm")).collect(Collectors.toList()).get(0);
		LoggedTransaction.ValueChange mnChange = transactions.get(0).getChanges().stream().filter(c -> c.column.equals("site_mn")).collect(Collectors.toList()).get(0);
		assertNotNull(nmChange);
		assertEquals("loc1", nmChange.oldValue.trim());
		assertEquals("loc1_mod", nmChange.newValue.trim());
		assertNotNull(mnChange);
		assertEquals("acins_u", mnChange.oldValue.trim());
		assertEquals("acmod_u", mnChange.newValue.trim());

		params.put(LoggedTransactionQueryParams.ACTION, "I");
		transactions = dao.findTransactions(params);
		assertEquals(0, transactions.size());
	}

	@Test
	public void findTransactionsNoResultTest() {
		
	}

}
