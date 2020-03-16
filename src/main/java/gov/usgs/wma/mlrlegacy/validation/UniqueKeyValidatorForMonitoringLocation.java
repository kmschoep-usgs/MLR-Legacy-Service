package gov.usgs.wma.mlrlegacy.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.usgs.wma.mlrlegacy.model.MonitoringLocation;

@Component
public class UniqueKeyValidatorForMonitoringLocation implements ConstraintValidator<UniqueKey, MonitoringLocation> {	
	private final UniqueSiteNumberAndAgencyCodeValidator uniqueSiteIdAndAgencyCodeValidator;
	
	private final UniqueNormalizedStationNameValidator uniqueNormalizedStationNameValidator;
	
	@Autowired
	public UniqueKeyValidatorForMonitoringLocation(
		UniqueSiteNumberAndAgencyCodeValidator uniqueSiteIdAndAgencyCodeValidator,
		UniqueNormalizedStationNameValidator uniqueNormalizedStationNameValidator
	) {
		this.uniqueSiteIdAndAgencyCodeValidator = uniqueSiteIdAndAgencyCodeValidator;
		this.uniqueNormalizedStationNameValidator = uniqueNormalizedStationNameValidator;
	}
	
	@Override
	public void initialize(UniqueKey constraintAnnotation) {
		// Nothing for us to do here at this time.
	}

	@Override
	public boolean isValid(MonitoringLocation newOrUpdatedMonitoringLocation, ConstraintValidatorContext context) {
		boolean valid = true;
		if (null != newOrUpdatedMonitoringLocation && null != context) {
			valid = this.uniqueSiteIdAndAgencyCodeValidator.isValid(newOrUpdatedMonitoringLocation, context);
			valid &= this.uniqueNormalizedStationNameValidator.isValid(newOrUpdatedMonitoringLocation, context);
		}
		return valid;
	}

}