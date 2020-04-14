package gov.usgs.wma.mlrlegacy.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.wma.mlrlegacy.Controller;
import gov.usgs.wma.mlrlegacy.model.LoggedAction;
import gov.usgs.wma.mlrlegacy.dao.LoggedActionsDao;

/**
 * DAO integration tests for Read operations
 */

@ActiveProfiles("it")
@DatabaseSetup("classpath:/testData/emptyDatabase/")
@DatabaseSetup("classpath:/testData/setupOne/")
public class LoggedActionsDaoRIT extends BaseDaoIT {

	@Autowired
	private LoggedActionsDao dao;
	
	@Test
	public void getByAgencyCode() {
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.AGENCY_CODE, DEFAULT_AGENCY_CODE_TRIMMED);
		List<LoggedAction> loggedActions = dao.find(params);
		assertEquals(DEFAULT_AGENCY_CODE, loggedActions.get(0).getAgencyCode());
		assertEquals(DEFAULT_SITE_NUMBER, loggedActions.get(0).getSiteNumber());
	}
	
	@Test
	public void getBySiteNumber() {
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.SITE_NUMBER, DEFAULT_SITE_NUMBER);
		List<LoggedAction> loggedActions = dao.find(params);
		assertEquals(DEFAULT_AGENCY_CODE, loggedActions.get(0).getAgencyCode());
		assertEquals(DEFAULT_SITE_NUMBER, loggedActions.get(0).getSiteNumber());
	}
	
	@Test
	public void getByAgencyCodeAndSiteNumber() {
		Map<String, Object> params = new HashMap<>();
		params.put(Controller.AGENCY_CODE, DEFAULT_AGENCY_CODE_TRIMMED);
		params.put(Controller.SITE_NUMBER, DEFAULT_SITE_NUMBER);
		List<LoggedAction> loggedActions = dao.find(params);
		assertEquals(DEFAULT_AGENCY_CODE, loggedActions.get(0).getAgencyCode());
		assertEquals(DEFAULT_SITE_NUMBER, loggedActions.get(0).getSiteNumber());
	}
	
}
