package gov.usgs.wma.mlrlegacy.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import javax.validation.ConstraintValidatorContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import gov.usgs.wma.mlrlegacy.dao.MonitoringLocationDao;
import gov.usgs.wma.mlrlegacy.model.MonitoringLocation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class BaseUniqueMonitoringLocationValidatorTest {

	private BaseUniqueMonitoringLocationValidator instance;
	
	@Mock
	private MonitoringLocationDao dao;
	
	@BeforeEach
	public void setUp() {
		instance = new BaseUniqueMonitoringLocationValidator(dao){
			@Override
			public boolean isValid(MonitoringLocation newOrUpdatedMonitoringLocation, ConstraintValidatorContext context) {
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}
			
		};
	}
	
	@Test
	public void testIsCreateWithNullTransactionType() {
		MonitoringLocation ml = new MonitoringLocation();
		boolean result = instance.isCreate(ml);
		assertFalse(result);
	}

	@Test
	public void testIsCreateWithModifyTransactionType() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("M");
		boolean result = instance.isCreate(ml);
		assertFalse(result);
	}
	
	@Test
	public void testIsCreateWithCreateTransactionType() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("A");
		boolean result = instance.isCreate(ml);
		assertTrue(result);
	}
	
	@Test
	public void testIsUpdateWithNullTransactionType() {
		MonitoringLocation ml = new MonitoringLocation();
		boolean result = instance.isUpdate(ml);
		assertFalse(result);
	}

	@Test
	public void testIsUpdateWithModifyTransactionType() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("M");
		ml.setId(BigInteger.ONE);
		boolean result = instance.isUpdate(ml);
		assertTrue(result);
	}
	
	@Test
	public void testIsUpdateWithModifyTransactionTypeAndNoId() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("M");
		ml.setId(null);
		boolean result = instance.isUpdate(ml);
		assertFalse(result);
	}
	
	@Test
	public void testIsModifyWithCreateTransactionType() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("A");
		boolean result = instance.isUpdate(ml);
		assertFalse(result);
	}
	
	@Test
	public void testIsPatchWithNullTransactionType() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType(null);
		boolean result = instance.isPatch(ml);
		assertFalse(result);
	}

	@Test
	public void testIsPatchWithPatch() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("M");
		ml.setId(null);
		boolean result = instance.isPatch(ml);
		assertTrue(result);
	}
	
	@Test
	public void testIsPatchWithPatchAndAnId() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("M");
		ml.setId(BigInteger.ONE);
		boolean result = instance.isPatch(ml);
		assertFalse(result);
	}
	
	@Test
	public void testIsPatchWithCreateTransactionType() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("A");
		boolean result = instance.isUpdate(ml);
		assertFalse(result);
	}
	
	@Test
	public void testSerialization() throws IOException {
		MonitoringLocation ml = new MonitoringLocation();
		String expected = (new ObjectMapper()).writeValueAsString(Arrays.asList(ml));
		String result = instance.serializeMls(Arrays.asList(ml));
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testSerializationError() throws JsonProcessingException {
		ObjectMapper objectMapper = mock(ObjectMapper.class);
		when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
		instance.setObjectMapper(objectMapper);
		
		MonitoringLocation ml = new MonitoringLocation();
		String result = instance.serializeMls(Arrays.asList(ml));
		String expected = "[" + BaseUniqueMonitoringLocationValidator.SERIALIZATION_ERROR_MESSAGE + "]";
		assertEquals(expected, result);
	}

	@Test
	public void testSameNullIDWithNullAttributes() {
		boolean result = instance.same(new MonitoringLocation(), new MonitoringLocation());
		assertTrue(result);
	}

	@Test
	public void testSameNullIDWithSameAttributes() {
		MonitoringLocation ml1 = new MonitoringLocation();
		ml1.setAgencyCode("USGS");
		ml1.setSiteNumber("1");
		MonitoringLocation ml2 = new MonitoringLocation();
		ml2.setAgencyCode("USGS");
		ml2.setSiteNumber("1");
		boolean result = instance.same(ml1, ml2);
		assertTrue(result);
	}

	@Test
	public void testSameNullIDWithDifferentAgency() {
		MonitoringLocation ml1 = new MonitoringLocation();
		ml1.setAgencyCode("USGS");
		ml1.setSiteNumber("1");
		MonitoringLocation ml2 = new MonitoringLocation();
		ml2.setAgencyCode("EPA ");
		ml2.setSiteNumber("1");
		boolean result = instance.same(ml1, ml2);
		assertFalse(result);
	}

	@Test
	public void testSameNullIDWithDifferentSiteNumber() {
		MonitoringLocation ml1 = new MonitoringLocation();
		ml1.setAgencyCode("USGS");
		ml1.setSiteNumber("2");
		MonitoringLocation ml2 = new MonitoringLocation();
		ml2.setAgencyCode("USGS");
		ml2.setSiteNumber("1");
		boolean result = instance.same(ml1, ml2);
		assertFalse(result);
	}
	
	@Test
	public void testSameIdWithDifferentAgencyCodes() {
		MonitoringLocation ml1 = new MonitoringLocation();
		ml1.setAgencyCode("USGS");
		ml1.setSiteNumber("1");
		ml1.setId(BigInteger.valueOf(1));
		MonitoringLocation ml2 = new MonitoringLocation();
		ml2.setAgencyCode("EPA ");
		ml2.setSiteNumber("2");
		ml2.setId(BigInteger.valueOf(1));
		boolean result = instance.same(ml1, ml2);
		assertTrue(result);
	}
	
	@Test
	public void testSameIdWithDifferentSiteNumbers() {
		MonitoringLocation ml1 = new MonitoringLocation();
		ml1.setAgencyCode("USGS");
		ml1.setSiteNumber("1");
		ml1.setId(BigInteger.valueOf(1));
		MonitoringLocation ml2 = new MonitoringLocation();
		ml2.setAgencyCode("USGS");
		ml2.setSiteNumber("2");
		ml2.setId(BigInteger.valueOf(1));
		boolean result = instance.same(ml1, ml2);
		assertTrue(result);
	}

	@Test
	public void testDifferentIdWithSameAttributes() {
		MonitoringLocation ml1 = new MonitoringLocation();
		ml1.setAgencyCode("USGS");
		ml1.setSiteNumber("1");
		ml1.setId(BigInteger.valueOf(1));
		MonitoringLocation ml2 = new MonitoringLocation();
		ml2.setAgencyCode("USGS");
		ml2.setSiteNumber("1");
		ml2.setId(BigInteger.valueOf(2));
		boolean result = instance.same(ml1, ml2);
		assertFalse(result);
	}

	@Test
	public void testDifferentIdWithDifferentAttributes() {
		MonitoringLocation ml1 = new MonitoringLocation();
		ml1.setAgencyCode("EPA ");
		ml1.setSiteNumber("1");
		ml1.setId(BigInteger.valueOf(1));
		MonitoringLocation ml2 = new MonitoringLocation();
		ml2.setAgencyCode("USGS");
		ml2.setSiteNumber("1");
		ml2.setId(BigInteger.valueOf(2));
		boolean result = instance.same(ml1, ml2);
		assertFalse(result);
	}
}
