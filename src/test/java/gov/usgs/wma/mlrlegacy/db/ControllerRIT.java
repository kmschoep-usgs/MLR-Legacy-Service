package gov.usgs.wma.mlrlegacy.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Controller integration tests for Read operations
 */
@ActiveProfiles("it")
@DatabaseSetup("classpath:/testData/setupOne/")
public class ControllerRIT extends BaseControllerIT {

	@Test
	public void getMonitoringLocationsNoAgencyCodeNoSiteNumberInvalidRequest() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>("", getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations", HttpMethod.GET, entity, String.class);
		assertEquals(400, responseEntity.getStatusCodeValue());
	}

	@Test
	public void getMonitoringLocationsByAgencyCodeAndSiteNumberFoundUpperCase() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>("", getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations?agencyCode=USGS&siteNumber=123456789012345", HttpMethod.GET, entity, String.class);
		assertEquals(200, responseEntity.getStatusCodeValue());
		JSONAssert.assertEquals(getExpectedReadJson("oneMillion.json"), responseEntity.getBody(), JSONCompareMode.STRICT);
	}

	@Test
	public void getMonitoringLocationsByAgencyCodeAndSiteNumberFoundLowerCase() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>("", getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations?agencyCode=usgs&siteNumber=123456789012345", HttpMethod.GET, entity, String.class);
		assertEquals(200, responseEntity.getStatusCodeValue());
		JSONAssert.assertEquals(getExpectedReadJson("oneMillion.json"), responseEntity.getBody(), JSONCompareMode.STRICT);
	}

	@Test
	public void getMonitoringLocationsByAgencyCodeAndSiteNumberFoundMixedCase() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>("", getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations?agencyCode=UsgS&siteNumber=123456789012345", HttpMethod.GET, entity, String.class);
		assertEquals(200, responseEntity.getStatusCodeValue());
		JSONAssert.assertEquals(getExpectedReadJson("oneMillion.json"), responseEntity.getBody(), JSONCompareMode.STRICT);
	}

	@Test
	public void getMonitoringLocationsByAgencyCodeAndSiteNumberNotFound() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>("", getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations?agencyCode=USGS&siteNumber=123456789012349", HttpMethod.GET, entity, String.class);
		assertEquals(404, responseEntity.getStatusCodeValue());
		JSONAssert.assertEquals(null, responseEntity.getBody(), JSONCompareMode.STRICT);
	}

	@Test
	public void getNoToken() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>("", getHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations?agencyCode=USGS&siteNumber=123456789012345", HttpMethod.GET, entity, String.class);
		assertEquals(401, responseEntity.getStatusCodeValue());
	}

	@Test
	public void getMonitoringLocationFound() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>("", getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations/1000000", HttpMethod.GET, entity, String.class);
		assertEquals(200, responseEntity.getStatusCodeValue());
		JSONAssert.assertEquals(getExpectedReadJson("oneMillion.json"), responseEntity.getBody(), JSONCompareMode.STRICT);
	}

	@Test
	public void getMonitoringLocationNotFound() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>("", getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations/1000001", HttpMethod.GET, entity, String.class);
		assertEquals(404, responseEntity.getStatusCodeValue());
		assertNull(responseEntity.getBody());
	}
	
	@Test
	public void getMonitoringLocationByNormalizedNameFound() throws Exception {
		HttpEntity<String> entity;
		entity = new HttpEntity<String>("", getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations?normalizedStationName=STATIONIX", HttpMethod.GET, entity, String.class);
		assertEquals(200, responseEntity.getStatusCodeValue());
		JSONAssert.assertEquals("[" + getExpectedReadJson("oneMillion.json") + "]", responseEntity.getBody(), JSONCompareMode.STRICT);
	}

	@Test
	public void getMonitoringLocationByNormalizedNameNotFound() throws Exception {
		HttpEntity<String> entity = new HttpEntity<>("", getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations?normalizedStationName=DOESNOTEXIST", HttpMethod.GET, entity, String.class);
		assertEquals(404, responseEntity.getStatusCodeValue());
		JSONAssert.assertEquals("[]", responseEntity.getBody(), JSONCompareMode.STRICT);
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupThree/")
	public void getMonitoringLocationByDistrictCodeAndDateRangeDayFound() throws Exception {
		HttpEntity<String> entity;
		entity = new HttpEntity<String>("", getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations?districtCode=dis&startDate=2015-08-24&endDate=2015-08-24", HttpMethod.GET, entity, String.class);
		assertEquals(200, responseEntity.getStatusCodeValue());
		JSONAssert.assertEquals("[" + getExpectedReadJson("oneDay.json") + "]", responseEntity.getBody(), JSONCompareMode.STRICT);
	}
	
	@Test
	@DatabaseSetup("classpath:/testData/setupThreeDistrictCodes/")
	public void getMonitoringLocationByDistrictCodeAndDateRangeMultiDisCodeFound() throws Exception {
		HttpEntity<String> entity;
		entity = new HttpEntity<String>("", getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations?districtCode=2&districtCode=4&startDate=2010-08-24&endDate=2019-08-24", HttpMethod.GET, entity, String.class);
		assertEquals(200, responseEntity.getStatusCodeValue());
		JSONAssert.assertEquals("[" + getExpectedReadJson("twoDistrictCodes.json") + "]", responseEntity.getBody(), JSONCompareMode.STRICT);
	}
	
	@Test
	@DatabaseSetup("classpath:/testData/setupThreeDistrictCodes/")
	public void getMonitoringLocationByDateRangeFound() throws Exception {
		HttpEntity<String> entity;
		entity = new HttpEntity<String>("", getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations?&startDate=2016-01-24&endDate=2019-08-24", HttpMethod.GET, entity, String.class);
		assertEquals(200, responseEntity.getStatusCodeValue());
		JSONAssert.assertEquals("[" + getExpectedReadJson("twoDistrictCodes.json") + "]", responseEntity.getBody(), JSONCompareMode.STRICT);
	}
	
	@Test
	public void getMonitoringLocationByDistrictCodeAndDateRangeNotFound() throws Exception {
		HttpEntity<String> entity = new HttpEntity<>("", getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations?districtCode=xxx&startDate=1999-01-01&endDate=1999-08-24", HttpMethod.GET, entity, String.class);
		assertEquals(404, responseEntity.getStatusCodeValue());
		JSONAssert.assertEquals("[]", responseEntity.getBody(), JSONCompareMode.STRICT);
	}
		
	@Test
	public void getByIdNoToken() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>("", getHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations/1000000", HttpMethod.GET, entity, String.class);
		assertEquals(401, responseEntity.getStatusCodeValue());
	}

}