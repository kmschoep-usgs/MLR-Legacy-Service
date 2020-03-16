package gov.usgs.wma.mlrlegacy.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.wma.mlrlegacy.dao.MonitoringLocationDao;
import gov.usgs.wma.mlrlegacy.model.MonitoringLocation;

/**
 * Common functionality for validators verifying MonitoringLocation uniqueness
 * 
 */
public abstract class BaseUniqueMonitoringLocationValidator {

	private static transient final Logger LOG = LoggerFactory.getLogger(BaseUniqueMonitoringLocationValidator.class);
	protected static final String TRANSACTION_TYPE = "transactionType";
	public static final String SERIALIZATION_ERROR_MESSAGE = "{\"msg\":\"error serializing monitoring location\"}";
	protected final MonitoringLocationDao dao;
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public BaseUniqueMonitoringLocationValidator(MonitoringLocationDao dao) {
		this.dao = dao;
	}
	
	public abstract boolean isValid(MonitoringLocation newOrUpdatedMonitoringLocation, ConstraintValidatorContext context);

	/**
	 * 
	 * @param ml
	 * @return true if it's a create (as opposed to an update or patch)
	 */
	protected boolean isCreate(MonitoringLocation ml) {
		boolean isCreate = "A".equals(ml.getTransactionType());
		return isCreate;
	}

	/**
	 * 
	 * @param ml
	 * @return true if it's an update (as opposed to a create or a patch)
	 */	
	protected boolean isUpdate(MonitoringLocation ml) {
		boolean isUpdate = 
			"M".equals(ml.getTransactionType())
			&&
			null != ml.getId();
		
		return isUpdate;
	}

	/**
	 * 
	 * @param ml
	 * @return true if it's a patch (as opposed to a create or an update)
	 */
	protected boolean isPatch(MonitoringLocation ml) {
		boolean isPatch =
			"M".equals((ml.getTransactionType()))
			&&
			null == ml.getId();
		return isPatch;
	}

	/**
	 * 
	 * @param ml1
	 * @param ml2
	 * @return true if they have the same agency code and site number,
	 * false otherwise
	 */
	protected boolean same(MonitoringLocation ml1, MonitoringLocation ml2) {
		boolean same = Objects.equal(ml1.getAgencyCode(), ml2.getAgencyCode())
			&& Objects.equal(ml1.getSiteNumber(), ml2.getSiteNumber());
		return same;
	}
	
	/**
	 * Serializes each MonitoringLocation as JSON. If a MonitoringLocation
	 * can't be serialized, the String 
	 * `BaseUniqueMonitoringLocationValidator.SERIALIZATION_ERROR_MESSAGE` 
	 * is used instead
	 * @param mls
	 * @return a String with a JSON Array of MonitoringLocations
	 */
	protected String serializeMls(Collection<MonitoringLocation> mls) {
		List<String> mlMsgs = new ArrayList<>();
		for (MonitoringLocation ml : mls) {
			String existingMlMsg;
			try {
				existingMlMsg = getObjectMapper().writeValueAsString(ml);
			} catch (JsonProcessingException ex) {
				existingMlMsg = SERIALIZATION_ERROR_MESSAGE;
				LOG.warn("Problem serializing monitoring location", ex);
			}
			mlMsgs.add(existingMlMsg);
		}
		String msg = "[" + String.join(",", mlMsgs) + "]";
		return msg;
	}
	
	/**
	 * @return the objectMapper
	 */
	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
}
