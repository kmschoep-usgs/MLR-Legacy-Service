package gov.usgs.wma.mlrlegacy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import gov.usgs.wma.mlrlegacy.dao.MonitoringLocationDao;
import gov.usgs.wma.mlrlegacy.db.BaseIT;
import gov.usgs.wma.mlrlegacy.model.MonitoringLocation;
import gov.usgs.wma.mlrlegacy.validation.UniqueNormalizedStationNameValidator;
import gov.usgs.wma.mlrlegacy.validation.UniqueSiteNumberAndAgencyCodeValidator;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class ControllerTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@MockBean
	private MonitoringLocationDao dao;

	@MockBean
	private UniqueNormalizedStationNameValidator uniqueNormalizedStationNameValidator;
	
	@MockBean
	private UniqueSiteNumberAndAgencyCodeValidator uniqueSiteIdAndAgencyCodeValidator;
	
	@MockBean
	private Authentication authentication;

	@MockBean
	private SecurityContext securityContext;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity()) 
			.build();
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenReturnData_whenGetByAgencyCodeAndSiteNumber_thenReturnMonitoringLocation() throws Exception {
		MonitoringLocation mlOne = new MonitoringLocation();

		mlOne.setId(BigInteger.ONE);
		mlOne.setAgencyCode(BaseIT.DEFAULT_AGENCY_CODE);
		mlOne.setSiteNumber("987654321");

		Map<String, Object> params = new HashMap<>();
		params.put(Controller.AGENCY_CODE, BaseIT.DEFAULT_AGENCY_CODE);
		params.put(Controller.SITE_NUMBER, "987654321");

		MultiValueMap<String, String> cruParams = new LinkedMultiValueMap<>();
		cruParams.set(Controller.AGENCY_CODE, BaseIT.DEFAULT_AGENCY_CODE);
		cruParams.set(Controller.SITE_NUMBER, "987654321");
		
		given(dao.getByAK(params)).willReturn(mlOne);

		mvc.perform(get("/monitoringLocations").params(cruParams))
			.andExpect(status().isOk())
			.andExpect(jsonPath("id", is(equalTo(1))));
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenReturnNoData_whenGetByAgencyCodeAndSiteNumber_thenReturn404() throws Exception {
		MultiValueMap<String, String> cruParams = new LinkedMultiValueMap<>();
		cruParams.set(Controller.AGENCY_CODE, BaseIT.DEFAULT_AGENCY_CODE);
		cruParams.set(Controller.SITE_NUMBER, "987654321");
		
		given(dao.getByNormalizedName(any())).willReturn(null);

		mvc.perform(get("/monitoringLocations").params(cruParams))
			.andExpect(status().isNotFound());
	}

	
	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenReturnData_whenGetByDistrictCodeandDateRange_thenReturnMonitoringLocation() throws Exception {
		MonitoringLocation mlOne = new MonitoringLocation();
		MonitoringLocation mlTwo = new MonitoringLocation();

		mlOne.setId(BigInteger.ONE);
		mlOne.setAgencyCode(BaseIT.DEFAULT_AGENCY_CODE);
		mlOne.setSiteNumber("987654321");
		mlOne.setDistrictCode(BaseIT.DEFAULT_DISTRICT_CODE);
		mlOne.setCreated(BaseIT.DEFAULT_CREATED_DATE_M);
		mlOne.setUpdated(BaseIT.DEFAULT_UPDATED_DATE_S);
		
		mlTwo.setId(BigInteger.TEN);
		mlTwo.setAgencyCode(BaseIT.DEFAULT_AGENCY_CODE);
		mlTwo.setSiteNumber("987354321");
		mlTwo.setDistrictCode(BaseIT.DEFAULT_DISTRICT_CODE);
		mlTwo.setCreated("2014-08-24 09:15");
		mlTwo.setUpdated("2015-01-24 06:55");
		
		List<MonitoringLocation> monitoringLocations = Arrays.asList(mlOne, mlTwo);

		MultiValueMap<String, String> cruParams = new LinkedMultiValueMap<>();
		cruParams.set(Controller.DISTRICT_CODE, BaseIT.DEFAULT_DISTRICT_CODE);
		cruParams.set(Controller.START_DATE, "2014-01-24");
		cruParams.set(Controller.END_DATE, "2016-01-24");
		
		given(dao.getByDistrictCodeDateRange(anyMap())).willReturn(monitoringLocations);
		
		List<Integer> expectedIds = Arrays.asList(1, 10);
		mvc.perform(get("/monitoringLocations").params(cruParams))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$..['id']", is(equalTo(expectedIds))));
	}
	
	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenReturnNull_whenGetByDistrictCodeandDateRange_thenReturn404() throws Exception {
		MultiValueMap<String, String> cruParams = new LinkedMultiValueMap<>();
		cruParams.set(Controller.DISTRICT_CODE, BaseIT.DEFAULT_DISTRICT_CODE);
		cruParams.set(Controller.START_DATE, "2014-01-24");
		cruParams.set(Controller.END_DATE, "2016-01-24");

		given(dao.getByNormalizedName(anyMap())).willReturn(null);

		mvc.perform(get("/monitoringLocations").params(cruParams))
			.andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenReturnData_whenGetByStationName_thenReturnMonitoringLocation() throws Exception {
		final String MY_NORMALIZED_STATION_NAME = "THELOCALWATERINGHOLE";
		MonitoringLocation mlOne = new MonitoringLocation();

		mlOne.setId(BigInteger.ZERO);
		mlOne.setAgencyCode(BaseIT.DEFAULT_AGENCY_CODE);
		mlOne.setSiteNumber("987654321");
		mlOne.setStationName(MY_NORMALIZED_STATION_NAME);

		MonitoringLocation mlTwo = new MonitoringLocation();
		mlTwo.setId(BigInteger.ONE);
		mlTwo.setAgencyCode("Some other agency");
		mlTwo.setSiteNumber("123456");
		mlTwo.setStationName(MY_NORMALIZED_STATION_NAME);

		List<MonitoringLocation> monitoringLocations = Arrays.asList(mlOne, mlTwo);

		Map<String, Object> params = new HashMap<>();
		params.put(Controller.NORMALIZED_STATION_NAME, MY_NORMALIZED_STATION_NAME);

		MultiValueMap<String, String> cruParams = new LinkedMultiValueMap<>();
		cruParams.set(Controller.NORMALIZED_STATION_NAME, MY_NORMALIZED_STATION_NAME);

		given(dao.getByNormalizedName(params)).willReturn(monitoringLocations);

		List<Integer> expectedIds = Arrays.asList(0, 1);
		mvc.perform(get("/monitoringLocations").params(cruParams))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$..['id']", is(equalTo(expectedIds))));
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenReturnNull_whenGetByStationName_thenReturn404() throws Exception {
		MultiValueMap<String, String> cruParams = new LinkedMultiValueMap<>();
		cruParams.set(Controller.NORMALIZED_STATION_NAME, "STATIONY");

		given(dao.getByNormalizedName(any())).willReturn(null);

		mvc.perform(get("/monitoringLocations").params(cruParams))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenReturnEmptyList_whenGetByStationName_thenReturn404() throws Exception {
		MultiValueMap<String, String> cruParams = new LinkedMultiValueMap<>();
		cruParams.set(Controller.NORMALIZED_STATION_NAME, "STATIONY");
		List<MonitoringLocation> emptyList = new ArrayList<>();
		given(dao.getByNormalizedName(any())).willReturn(emptyList);

		mvc.perform(get("/monitoringLocations").params(cruParams))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenML_whenGetById_thenReturnML() throws Exception {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setId(BigInteger.ONE);
		ml.setAgencyCode(BaseIT.DEFAULT_AGENCY_CODE);
		ml.setSiteNumber("987654321");

		given(dao.getById(BigInteger.ONE)).willReturn(ml);

		mvc.perform(get("/monitoringLocations/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("id", is(equalTo(1))))
			.andExpect(jsonPath(Controller.AGENCY_CODE, is(equalTo(ml.getAgencyCode()))))
			.andExpect(jsonPath(Controller.SITE_NUMBER, is(equalTo(ml.getSiteNumber()))))
		;
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenNoML_whenGetById_thenReturnNotFound() throws Exception {
		given(dao.getById(BigInteger.ONE)).willReturn(null);

		mvc.perform(get("/monitoringLocations/1"))
				.andExpect(status().isNotFound())
				.andExpect(content().string(""));
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenML_whenCreate_thenReturnMLWithId() throws Exception {
		final String SITE_NUMBER = "12345678";
		MonitoringLocation newMl = new MonitoringLocation();
		newMl.setAgencyCode(BaseIT.DEFAULT_AGENCY_CODE);
		newMl.setSiteNumber(SITE_NUMBER);
		newMl.setId(BigInteger.ONE);

		String requestBody = "{\"agencyCode\": \"" + BaseIT.DEFAULT_AGENCY_CODE+ "\", \"siteNumber\": \"" + SITE_NUMBER +"\", \"stationIx\":\"ABC\"}";
		given(uniqueNormalizedStationNameValidator.isValid(any(), any())).willReturn(true);
		given(uniqueSiteIdAndAgencyCodeValidator.isValid(any(), any())).willReturn(true);
		
		given(dao.create(any(MonitoringLocation.class))).willReturn(BigInteger.ONE);
		given(dao.getById(BigInteger.ONE)).willReturn(newMl);

		mvc.perform(post("/monitoringLocations").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("id", is(equalTo(1))))
				.andExpect(jsonPath(Controller.AGENCY_CODE, is(equalTo(BaseIT.DEFAULT_AGENCY_CODE))))
				.andExpect(jsonPath(Controller.SITE_NUMBER, is(equalTo("12345678"))));
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenML_whenUpdate_thenReturnUpdatedML() throws Exception {
		final String SITE_NUMBER = "12345678";
		String requestBody = "{\"agencyCode\": \"" + BaseIT.DEFAULT_AGENCY_CODE + "\", \"siteNumber\": \"" + SITE_NUMBER +"\"}";
		MonitoringLocation ml = new MonitoringLocation();

		ml.setId(BigInteger.ONE);
		ml.setAgencyCode(BaseIT.DEFAULT_AGENCY_CODE);
		ml.setSiteNumber(SITE_NUMBER);

		Mockito.doNothing().when(dao).update(any(MonitoringLocation.class));
		given(dao.getById(BigInteger.ONE)).willReturn(ml);
		given(uniqueNormalizedStationNameValidator.isValid(any(), any())).willReturn(true);
		given(uniqueSiteIdAndAgencyCodeValidator.isValid(any(), any())).willReturn(true);

		mvc.perform(put("/monitoringLocations/1").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id", is(equalTo(1))))
				.andExpect(jsonPath(Controller.AGENCY_CODE, is(equalTo(BaseIT.DEFAULT_AGENCY_CODE))))
				.andExpect(jsonPath(Controller.SITE_NUMBER, is(equalTo("12345678"))));
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenNewML_whenUpdate_thenStatusNotFound() throws Exception {
		String requestBody = "{\"agencyCode\": \"USGS\", \"siteNumber\": \"12345678\"}";
		
		given(dao.getById(BigInteger.ONE)).willReturn(null);
		mvc.perform(put("/monitoringLocations/1").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenBadML_whenUpdate_thenStatusBadRequest() throws Exception {
		final String SITE_NUMBER = "12345678";
		String requestBody = "{\"agencyCode\": \"" + BaseIT.DEFAULT_AGENCY_CODE + "\", \"siteNumber\": \"" + SITE_NUMBER +"\"}";
		MonitoringLocation ml = new MonitoringLocation();

		ml.setId(BigInteger.ONE);
		ml.setAgencyCode(BaseIT.DEFAULT_AGENCY_CODE);
		ml.setSiteNumber(SITE_NUMBER);

		Mockito.doNothing().when(dao).update(any(MonitoringLocation.class));
		given(dao.getById(BigInteger.ONE)).willReturn(ml);
		given(uniqueNormalizedStationNameValidator.isValid(any(), any())).willReturn(false);
		given(uniqueSiteIdAndAgencyCodeValidator.isValid(any(), any())).willReturn(false);

		MvcResult result = mvc.perform(put("/monitoringLocations/1").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable())
				.andReturn();

		assertEquals("Invalid data submitted to CRU.", result.getResponse().getErrorMessage());
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenML_whenPatch_thenReturnUpdatedML() throws Exception {
		String requestBody = "{\"agencyCode\": \"USGS\", \"siteNumber\": \"12345678\"}";
		MonitoringLocation ml = new MonitoringLocation();
		ml.setId(BigInteger.ONE);
		ml.setAgencyCode(BaseIT.DEFAULT_AGENCY_CODE);
		ml.setSiteNumber("12345678");

		Mockito.doNothing().when(dao).patch(anyMap());
		given(dao.getByAK(anyMap())).willReturn(ml);

		mvc.perform(patch("/monitoringLocations").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id", is(equalTo(1))))
				.andExpect(jsonPath(Controller.AGENCY_CODE, is(equalTo(BaseIT.DEFAULT_AGENCY_CODE))))
				.andExpect(jsonPath(Controller.SITE_NUMBER, is(equalTo("12345678"))));
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenML_whenValidateGood_thenReturnStatus() throws Exception {
		final String SITE_NUMBER = "12345678";
		String requestBody = "{\"agencyCode\": \"" + BaseIT.DEFAULT_AGENCY_CODE+ "\", \"siteNumber\": \"" + SITE_NUMBER +"\", \"stationIx\":\"ABC\"}";
		given(uniqueNormalizedStationNameValidator.isValid(any(), any())).willReturn(true);
		given(uniqueSiteIdAndAgencyCodeValidator.isValid(any(), any())).willReturn(true);

		mvc.perform(post("/monitoringLocations/validate").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	public void givenML_whenValidateBadName_thenReturnStatus() throws Exception {
		final String SITE_NUMBER = "12345678";
		String requestBody = "{\"agencyCode\": \"" + BaseIT.DEFAULT_AGENCY_CODE+ "\", \"siteNumber\": \"" + SITE_NUMBER +"\", \"stationIx\":\"ABC\"}";
		given(uniqueNormalizedStationNameValidator.isValid(any(), any())).willReturn(false);
		given(uniqueSiteIdAndAgencyCodeValidator.isValid(any(), any())).willReturn(true);

		mvc.perform(post("/monitoringLocations/validate").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(authorities="unknown")
	public void givenML_whenValidateBadUnique_thenReturnStatus() throws Exception {
		final String SITE_NUMBER = "12345678";
		String requestBody = "{\"agencyCode\": \"" + BaseIT.DEFAULT_AGENCY_CODE+ "\", \"siteNumber\": \"" + SITE_NUMBER +"\", \"stationIx\":\"ABC\"}";
		given(uniqueNormalizedStationNameValidator.isValid(any(), any())).willReturn(true);
		given(uniqueSiteIdAndAgencyCodeValidator.isValid(any(), any())).willReturn(false);

		mvc.perform(post("/monitoringLocations/validate").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	
		MvcResult result =mvc.perform(post("/monitoringLocations/validate").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
	
		assertTrue(result.getResponse().getContentAsString().contains("\"validation_errors\":"));
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenML_whenValidateBadBoth_thenReturnStatus() throws Exception {
		final String SITE_NUMBER = "12345678";
		String requestBody = "{\"agencyCode\": \"" + BaseIT.DEFAULT_AGENCY_CODE+ "\", \"siteNumber\": \"" + SITE_NUMBER +"\", \"stationIx\":\"ABC\"}";
		given(uniqueNormalizedStationNameValidator.isValid(any(), any())).willReturn(false);
		given(uniqueSiteIdAndAgencyCodeValidator.isValid(any(), any())).willReturn(false);

		MvcResult result =mvc.perform(post("/monitoringLocations/validate").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
	
		assertTrue(result.getResponse().getContentAsString().contains("\"validation_errors\":"));
	}

	@Test
	@WithMockUser(authorities="test_allowed")
	public void givenNewML_whenPatch_thenStatusNotFound() throws Exception {
		String requestBody = "{\"agencyCode\": \"USGS\", \"siteNumber\": \"12345678\"}";

		Mockito.doNothing().when(dao).patch(anyMap());
		given(dao.getByAK(anyMap())).willReturn(null);
		mvc.perform(patch("/monitoringLocations").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithAnonymousUser
	public void givenAnonUser_whenGet_thenStatusNotAllowed() throws Exception {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setId(BigInteger.ONE);
		ml.setAgencyCode(BaseIT.DEFAULT_AGENCY_CODE);
		ml.setSiteNumber("987654321");

		given(dao.getById(BigInteger.ONE)).willReturn(ml);

		mvc.perform(get("/monitoringLocations/1"))
			.andExpect(status().is4xxClientError());
		;
	}

	@Test
	@WithMockUser
	public void givenNoRoleUser_whenGet_thenStatusOk() throws Exception {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setId(BigInteger.ONE);
		ml.setAgencyCode(BaseIT.DEFAULT_AGENCY_CODE);
		ml.setSiteNumber("987654321");

		given(dao.getById(BigInteger.ONE)).willReturn(ml);

		mvc.perform(get("/monitoringLocations/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("id", is(equalTo(1))))
			.andExpect(jsonPath(Controller.AGENCY_CODE, is(equalTo(ml.getAgencyCode()))))
			.andExpect(jsonPath(Controller.SITE_NUMBER, is(equalTo(ml.getSiteNumber()))))
		;
	}

	@Test
	@WithAnonymousUser
	public void givenAnonUser_whenCreate_thenStatusNotAllowed() throws Exception {
		final String SITE_NUMBER = "12345678";

		String requestBody = "{\"agencyCode\": \"" + BaseIT.DEFAULT_AGENCY_CODE+ "\", \"siteNumber\": \"" + SITE_NUMBER +"\", \"stationIx\":\"ABC\"}";

		mvc.perform(post("/monitoringLocations").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockUser
	public void givenNoRoleUser_whenCreate_thenStatusNotAllowed() throws Exception {
		final String SITE_NUMBER = "12345678";

		String requestBody = "{\"agencyCode\": \"" + BaseIT.DEFAULT_AGENCY_CODE+ "\", \"siteNumber\": \"" + SITE_NUMBER +"\", \"stationIx\":\"ABC\"}";

		mvc.perform(post("/monitoringLocations").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@WithAnonymousUser
	public void givenAnonUser_whenUpdate_thenStatusNotAllowed() throws Exception {
		String requestBody = "{\"agencyCode\": \"USGS\", \"siteNumber\": \"12345678\"}";
		
		mvc.perform(put("/monitoringLocations/1").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockUser
	public void givenNoRoleUserUser_whenUpdate_thenStatusNotAllowed() throws Exception {
		String requestBody = "{\"agencyCode\": \"USGS\", \"siteNumber\": \"12345678\"}";
		
		mvc.perform(put("/monitoringLocations/1").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@WithAnonymousUser
	public void givenAnonUser_whenPatch_thenStatusNotAllowed() throws Exception {
		String requestBody = "{\"agencyCode\": \"USGS\", \"siteNumber\": \"12345678\"}";
		
		mvc.perform(patch("/monitoringLocations").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockUser
	public void givenNoRoleUser_whenPatch_thenStatusNotAllowed() throws Exception {
		String requestBody = "{\"agencyCode\": \"USGS\", \"siteNumber\": \"12345678\"}";
		
		mvc.perform(patch("/monitoringLocations").content(requestBody).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void givenNoSecurityContext_thenUsernameUnknown() {
		Controller controller = new Controller();
		assertEquals(Controller.UNKNOWN_USERNAME, controller.getUsername());
	}

	@Test
	@WithAnonymousUser
	public void givenAnonymousUser_thenUsernameUnkown() {
		Controller controller = new Controller();
		assertEquals(Controller.UNKNOWN_USERNAME, controller.getUsername());
	}

	@Test
	@WithMockUser(username="Known")
	public void givenRealUser_thenUsernameKnown() {
		Controller controller = new Controller();
		assertEquals("Known", controller.getUsername());
	}

}
