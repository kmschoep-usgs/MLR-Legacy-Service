package gov.usgs.wma.mlrlegacy.db;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.jayway.jsonpath.JsonPath;

/**
 * Controller integration tests for Create and Update operations
  */
@ActiveProfiles("it")
public class ControllerCUIT extends BaseControllerIT {

	@Test
	@DatabaseSetup("classpath:/testData/emptyDatabase/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_SECOND,
			value="classpath:/testResult/oneSparseResultDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void createSparseMonitoringLocation() throws Exception {
		agency = UPDATED_AGENCY_CODE;
		siteNbr = UPDATED_SITE_NUMBER;
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("createSparseMonitoringLocation.json"), getAuthorizedHeaders());

		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/monitoringLocations", entity, String.class);

		assertEquals(201, responseEntity.getStatusCodeValue());

		String responseBody = responseEntity.getBody();
		id = JsonPath.read(responseBody, "$.id").toString();
		String expectedBody = getExpectedCreateJson(responseBody, "sparseMonitoringLocation.json", KNOWN_USER);

		JSONAssert.assertEquals(expectedBody, responseBody, JSONCompareMode.STRICT);
	}

	@Test
	@DatabaseSetup("classpath:/testData/emptyDatabase/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_SECOND,
			value="classpath:/testResult/oneResultDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void createFullMonitoringLocation() throws Exception {
		agency = UPDATED_AGENCY_CODE;
		siteNbr = UPDATED_SITE_NUMBER;
		createdBy = DEFAULT_CREATED_BY;
		updatedBy = DEFAULT_UPDATED_BY;
		
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("createFullMonitoringLocation.json"), getAuthorizedHeaders());

		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/monitoringLocations", entity, String.class);

		assertEquals(201, responseEntity.getStatusCodeValue());

		String responseBody = responseEntity.getBody();
		id = JsonPath.read(responseBody, "$.id").toString();
		String expectedBody = getExpectedCreateJson(responseBody, "fullMonitoringLocation.json", KNOWN_USER);

		JSONAssert.assertEquals(expectedBody, responseBody, JSONCompareMode.STRICT);
	}

	@Test
	public void createNoToken() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("createSparseMonitoringLocation.json"), getHeaders());
		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/monitoringLocations", entity, String.class);
		assertEquals(401, responseEntity.getStatusCodeValue());
	}

	@Test
	public void createUnauthorized() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("createSparseMonitoringLocation.json"), getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/monitoringLocations", entity, String.class);
		assertEquals(403, responseEntity.getStatusCodeValue());
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_SECOND,
			value="classpath:/testResult/oneSparseResultDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void updateSparseMonitoringLocation() throws Exception {
		id = String.valueOf(ONE_MILLION);
		agency = UPDATED_AGENCY_CODE;
		siteNbr = UPDATED_SITE_NUMBER;
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("updateSparseMonitoringLocation.json"), getAuthorizedHeaders());

		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations/" + id, HttpMethod.PUT, entity, String.class);

		assertEquals(200, responseEntity.getStatusCodeValue());

		String responseBody = responseEntity.getBody();
		String expectedBody = getExpectedUpdateJson(responseBody, "sparseMonitoringLocation.json", KNOWN_USER);

		JSONAssert.assertEquals(expectedBody, responseBody, JSONCompareMode.STRICT);
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_SECOND,
			value="classpath:/testResult/oneResultDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void updateFullMonitoringLocation() throws Exception {
		id = String.valueOf(ONE_MILLION);
		agency = UPDATED_AGENCY_CODE;
		siteNbr = UPDATED_SITE_NUMBER;
		createdBy = UPDATED_CREATED_BY;
		updatedBy = UPDATED_MODIFIED_BY;
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("updateFullMonitoringLocation.json"), getAuthorizedHeaders());

		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations/" + id, HttpMethod.PUT, entity, String.class);

		assertEquals(200, responseEntity.getStatusCodeValue());

		String responseBody = responseEntity.getBody();
		String expectedBody = getExpectedUpdateJson(responseBody, "fullMonitoringLocation.json", KNOWN_USER);

		JSONAssert.assertEquals(expectedBody, responseBody, JSONCompareMode.STRICT);
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_SECOND,
			value="classpath:/testResult/oneSparsePatchDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class, UpdatedModifier.class}
			)
	public void updateMonitoringLocation_notFound() throws Exception {
		id = String.valueOf(ONE_MILLION);
		agency = UPDATED_AGENCY_CODE;
		siteNbr = UPDATED_SITE_NUMBER;
		createdBy = UPDATED_CREATED_BY;
		updatedBy = UPDATED_MODIFIED_BY;
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("updateFullMonitoringLocation.json"), getAuthorizedHeaders());

		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations/1", HttpMethod.PUT, entity, String.class);

		assertEquals(404, responseEntity.getStatusCodeValue());
		assertNull(responseEntity.getBody());
	}

	@Test
	public void updateNoToken() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("updateSparseMonitoringLocation.json"), getHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations/" + id, HttpMethod.PUT, entity, String.class);
		assertEquals(401, responseEntity.getStatusCodeValue());
	}

	@Test
	public void updateUnauthorized() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("updateSparseMonitoringLocation.json"), getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations/" + id, HttpMethod.PUT, entity, String.class);
		assertEquals(403, responseEntity.getStatusCodeValue());
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_SECOND,
			value="classpath:/testResult/oneSparsePatchDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void patchSparseMonitoringLocation() throws Exception {
		id = String.valueOf(ONE_MILLION);
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("updateSparseMonitoringLocation.json"), getAuthorizedHeaders());

		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations", HttpMethod.PATCH, entity, String.class);

		assertEquals(200, responseEntity.getStatusCodeValue());

		String responseBody = responseEntity.getBody();
		String expectedBody = getExpectedUpdateJson(responseBody, "oneMillion.json", KNOWN_USER);

		JSONAssert.assertEquals(expectedBody, responseBody, JSONCompareMode.STRICT);
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_SECOND,
			value="classpath:/testResult/oneFullPatchDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void patchFullMonitoringLocation() throws Exception {
		id = String.valueOf(ONE_MILLION);
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("updateFullMonitoringLocation.json"), getAuthorizedHeaders());

		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations", HttpMethod.PATCH, entity, String.class);

		assertEquals(200, responseEntity.getStatusCodeValue());

		String responseBody = responseEntity.getBody();
		String expectedBody = getExpectedUpdateJson(responseBody, "fullMonitoringLocation.json", KNOWN_USER);

		JSONAssert.assertEquals(expectedBody, responseBody, JSONCompareMode.STRICT);
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_SECOND,
			value="classpath:/testResult/onePatchBlanksResultDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void patchFullBlanksMonitoringLocation() throws Exception {
		id = String.valueOf(ONE_MILLION);
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("patchFullBlankMonitoringLocation.json"), getAuthorizedHeaders());

		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations", HttpMethod.PATCH, entity, String.class);

		assertEquals(200, responseEntity.getStatusCodeValue());

		String responseBody = responseEntity.getBody();
		String expectedBody = getExpectedUpdateJson(responseBody, "patchBlanksMonitoringLocation.json", KNOWN_USER);

		JSONAssert.assertEquals(expectedBody, responseBody, JSONCompareMode.STRICT);
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_SECOND,
			value="classpath:/testResult/oneSparseResultDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void patchFullNullMonitoringLocation() throws Exception {
		id = String.valueOf(ONE_MILLION);
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("patchFullNullMonitoringLocation.json"), getAuthorizedHeaders());

		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations", HttpMethod.PATCH, entity, String.class);

		assertEquals(200, responseEntity.getStatusCodeValue());

		String responseBody = responseEntity.getBody();
		String expectedBody = getExpectedUpdateJson(responseBody, "sparseMonitoringLocation.json", KNOWN_USER);

		JSONAssert.assertEquals(expectedBody, responseBody, JSONCompareMode.STRICT);
	}

	@Test
	@DatabaseSetup("classpath:/testData/setupOne/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_SECOND,
			value="classpath:/testResult/oneSparsePatchDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void patchMonitoringLocation_notFound() throws Exception {
		id = String.valueOf(ONE_MILLION);
		HttpEntity<String> entity = new HttpEntity<String>("{\"agencyCode\": \"abc\", \"siteNumber\": \"123\"}", getAuthorizedHeaders());

		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations", HttpMethod.PATCH, entity, String.class);

		assertEquals(404, responseEntity.getStatusCodeValue());
		assertNull(responseEntity.getBody());
	}

	@Test
	public void patchNoToken() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("updateSparseMonitoringLocation.json"), getHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations", HttpMethod.PATCH, entity, String.class);
		assertEquals(401, responseEntity.getStatusCodeValue());
	}

	@Test
	public void patchUnauthorized() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("updateSparseMonitoringLocation.json"), getUnauthorizedHeaders());
		ResponseEntity<String> responseEntity = restTemplate.exchange("/monitoringLocations", HttpMethod.PATCH, entity, String.class);
		assertEquals(403, responseEntity.getStatusCodeValue());
	}
	
	@Test
	@DatabaseSetup("classpath:/testData/setupOneSparse/")
	@ExpectedDatabase(
			table="legacy_location",
			query=QUERY_ALL_TO_SECOND,
			value="classpath:/testResult/oneSparsePatchDb/legacy_location.csv",assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			modifiers={KeyModifier.class,CreatedModifier.class,UpdatedModifier.class}
			)
	public void addDuplicateMonitoringLocation() throws Exception {
		id = String.valueOf(ONE_MILLION);
		agency = DEFAULT_AGENCY_CODE;
		siteNbr = DEFAULT_SITE_NUMBER;
		HttpEntity<String> entity = new HttpEntity<String>(getInputJson("createSparseMonitoringLocation.json"), getAuthorizedHeaders());

		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/monitoringLocations", entity, String.class);
		assertEquals(406, responseEntity.getStatusCodeValue());
	}

}
