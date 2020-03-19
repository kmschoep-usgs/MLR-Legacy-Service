package gov.usgs.wma.mlrlegacy.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

public class MonitoringLocationTest {
	private ObjectMapper objectMapper;
	
	@BeforeEach
	public void setUp() {
		objectMapper = new ObjectMapper();
	}
	
	/**
	 * @see http://www.davismol.net/2015/03/21/jackson-using-jsonignore-and-jsonproperty-annotations-to-exclude-a-property-only-from-json-deserialization/
	 * @throws JsonProcessingException 
	 */
	@Test
	public void testSerializationExcludesTransactionType () throws JsonProcessingException {
		final MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("A");
		final String json = objectMapper.writeValueAsString(ml);
		assertFalse(json.contains("transactionType"));
	}
	
	/**
	 * @see http://www.davismol.net/2015/03/21/jackson-using-jsonignore-and-jsonproperty-annotations-to-exclude-a-property-only-from-json-deserialization/
	 * @throws IOException 
	 */
	@Test
	public void testDeserializationIncludesTransactionType() throws IOException {
		final String input = "{\"transactionType\":\"M\"}";
		final MonitoringLocation ml = objectMapper.readValue(input, MonitoringLocation.class);
		final String actualTransactionType = ml.getTransactionType();
		assertEquals("M", actualTransactionType);
	}
}
