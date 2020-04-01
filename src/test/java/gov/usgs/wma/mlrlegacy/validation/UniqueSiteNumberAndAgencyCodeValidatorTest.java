package gov.usgs.wma.mlrlegacy.validation;

import java.math.BigInteger;
import javax.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.usgs.wma.mlrlegacy.dao.MonitoringLocationDao;
import gov.usgs.wma.mlrlegacy.model.MonitoringLocation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UniqueSiteNumberAndAgencyCodeValidatorTest {
	
	MonitoringLocationDao dao;
	ConstraintValidatorContext context;
	UniqueSiteNumberAndAgencyCodeValidator instance;
	
	@BeforeEach
	public void setUp() {
		dao = mock(MonitoringLocationDao.class);
		context = ConstraintValidatorContextMockFactory.get();
		instance = new UniqueSiteNumberAndAgencyCodeValidator(dao);
	}
	
	@Test
	public void testNullAgencyCode() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setAgencyCode(null);
		ml.setSiteNumber("1");
		boolean result = instance.isValid(ml, context);
		assertTrue(result);
	}
	
	@Test
	public void testNullSiteNumber() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setAgencyCode("USGS");
		ml.setSiteNumber(null);
		boolean result = instance.isValid(ml, context);
		assertTrue(result);
	}
	
	@Test
	public void testDuplicateCreate() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setAgencyCode("USGS");
		ml.setSiteNumber("1");
		ml.setTransactionType("A");
		
		when(dao.getByAK(any())).thenReturn(ml);
		boolean result = instance.isValid(ml, context);
		//it's bad to create an existing ML
		assertFalse(result);
	}
	
	@Test
	public void testDuplicatePKCreate() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setAgencyCode("USGS");
		ml.setSiteNumber("1");
		ml.setTransactionType("PK");
		
		when(dao.getByAK(any())).thenReturn(ml);
		boolean result = instance.isValid(ml, context);
		//it's bad to create an existing ML
		assertFalse(result);
	}
	
	@Test
	public void testNoDuplicateCreate() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setAgencyCode("USGS");
		ml.setSiteNumber("1");
		ml.setTransactionType("A");
		
		when(dao.getByAK(any())).thenReturn(null);
		boolean result = instance.isValid(ml, context);
		assertTrue(result);
	}
	
	@Test
	public void testNoDuplicatePKCreate() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setAgencyCode("USGS");
		ml.setSiteNumber("1");
		ml.setTransactionType("PK");
		
		when(dao.getByAK(any())).thenReturn(null);
		boolean result = instance.isValid(ml, context);
		assertTrue(result);
	}
	
	@Test
	public void testDuplicatePatch() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setAgencyCode("USGS");
		ml.setSiteNumber("1");
		ml.setTransactionType("M");
		
		when(dao.getByAK(any())).thenReturn(ml);
		boolean result = instance.isValid(ml, context);
		//it's OK to update an existing ML
		assertTrue(result);
	}
	
	@Test
	public void testNoDuplicatePatch() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setAgencyCode("USGS");
		ml.setSiteNumber("1");
		ml.setTransactionType("M");
		
		when(dao.getByAK(any())).thenReturn(null);
		boolean result = instance.isValid(ml, context);
		assertTrue(result);
	}
	
	@Test
	public void testUpdateWithMismatchedIds() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setAgencyCode("USGS");
		ml.setSiteNumber("1");
		ml.setTransactionType("M");
		ml.setId(BigInteger.ONE);
		
		MonitoringLocation existingMl = new MonitoringLocation();
		existingMl.setAgencyCode("USGS");
		existingMl.setSiteNumber("1");
		existingMl.setId(BigInteger.TEN);
		
		when(dao.getByAK(any())).thenReturn(existingMl);
		boolean result = instance.isValid(ml, context);
		assertFalse(result);
	}
	
	@Test
	public void testUpdateWithMatchingIds() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setAgencyCode("USGS");
		ml.setSiteNumber("1");
		ml.setTransactionType("M");
		ml.setId(BigInteger.ONE);
		
		MonitoringLocation existingMl = new MonitoringLocation();
		existingMl.setAgencyCode("USGS");
		existingMl.setSiteNumber("1");
		existingMl.setId(BigInteger.ONE);
		
		when(dao.getByAK(any())).thenReturn(existingMl);
		boolean result = instance.isValid(ml, context);
		assertTrue(result);
	}
	
	@Test
	public void testUnidentifiedTransactionType() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setAgencyCode("USGS");
		ml.setSiteNumber("1");
		ml.setTransactionType("Z");
		ml.setId(BigInteger.ONE);
		
		MonitoringLocation existingMl = new MonitoringLocation();
		existingMl.setAgencyCode("USGS");
		existingMl.setSiteNumber("1");
		existingMl.setId(BigInteger.ONE);
		
		when(dao.getByAK(any())).thenReturn(existingMl);
		boolean result = instance.isValid(ml, context);
		assertFalse(result);
	}
}
