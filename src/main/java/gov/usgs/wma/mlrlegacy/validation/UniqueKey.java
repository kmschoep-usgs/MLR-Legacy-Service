package gov.usgs.wma.mlrlegacy.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy={
	UniqueKeyValidatorForMonitoringLocation.class
})
@Documented
public @interface UniqueKey {

	String message() default "Duplicates found";
	Class<?>[] groups() default {};
	public abstract Class<?>[] payload() default {};

	String[] propertyName() default {};
}

