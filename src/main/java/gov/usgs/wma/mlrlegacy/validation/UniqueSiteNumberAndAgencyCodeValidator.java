package gov.usgs.wma.mlrlegacy.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.usgs.wma.mlrlegacy.Controller;
import gov.usgs.wma.mlrlegacy.dao.MonitoringLocationDao;
import gov.usgs.wma.mlrlegacy.model.MonitoringLocation;
/**
 * No two monitoring locations should share the same Agency Code and Site Number.
 * 
 */
@Component
public class UniqueSiteNumberAndAgencyCodeValidator extends BaseUniqueMonitoringLocationValidator{

	
	public UniqueSiteNumberAndAgencyCodeValidator(@Autowired MonitoringLocationDao dao) {
		super(dao);
	}
	
	/**
	 * 
	 * No two monitoring locations should share the same Agency Code and Site Number.
	 * 
	 * Side effects: adds constraint violations to `context`
	 * @param newOrUpdatedMonitoringLocation
	 * @param context
	 * @return true if valid, false if invalid
	 */
	@Override
	public boolean isValid(MonitoringLocation newOrUpdatedMonitoringLocation, ConstraintValidatorContext context) {
		boolean valid = true;
		String msg = "Duplicate Agency Code and Site Number found in MLR.";
		if (null != newOrUpdatedMonitoringLocation.getAgencyCode() && null != newOrUpdatedMonitoringLocation.getSiteNumber()) {
			Map<String, Object> filters = new HashMap<>();
			filters.put(Controller.AGENCY_CODE, newOrUpdatedMonitoringLocation.getAgencyCode());
			filters.put(Controller.SITE_NUMBER, newOrUpdatedMonitoringLocation.getSiteNumber());
			MonitoringLocation existingMonitoringLocation = dao.getByAK(filters);
			if (existingMonitoringLocation != null) {
				if(isPatch(newOrUpdatedMonitoringLocation)) {
					valid = true;
				} else if (isCreate(newOrUpdatedMonitoringLocation) || isPKUpdate(newOrUpdatedMonitoringLocation)){
					valid = false;
				} else if (isUpdate(newOrUpdatedMonitoringLocation)){
					if(isThisUpdateValid(newOrUpdatedMonitoringLocation, existingMonitoringLocation)){
						valid = true;
					} else {
						valid = false;
					}
				} else {
					valid = false;
					msg = "Unable to classify Monitoring Location as a Create, Update, Primary Key Update or Patch.";
				}
			}
		}
		if(!valid) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(msg)
				.addPropertyNode(Controller.DUPLICATE_SITE)
				.addConstraintViolation();
		}
		return valid;
	}

	/**
	 * 
	 * @param updatedMonitoringLocation the caller is responsible for 
	 * ensuring that this is an update rather than a create or a patch.
	 * @param existingMl
	 * @return true if this is a valid update to the existing MonitoringLocation
	 */
	protected boolean isThisUpdateValid(MonitoringLocation updatedMonitoringLocation, MonitoringLocation existingMl) {
		boolean validUpdate = Objects.equals(updatedMonitoringLocation.getId(), existingMl.getId());
		return validUpdate;
	}

}
