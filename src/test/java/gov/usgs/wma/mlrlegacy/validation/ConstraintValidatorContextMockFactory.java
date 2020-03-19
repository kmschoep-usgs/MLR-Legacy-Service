package gov.usgs.wma.mlrlegacy.validation;

import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Supports testing ConstraintValidators.
 * Usages of ConstraintValidatorContext require more sophisticated mocks.
 */
public class ConstraintValidatorContextMockFactory {
	public static ConstraintValidatorContext get () {
		PathImpl path = PathImpl.createRootPath();
		path.addBeanNode();
		ClockProvider timeProvider = mock(ClockProvider.class);
		ConstraintValidatorContextImpl context = new ConstraintValidatorContextImpl(
			null, timeProvider, path, null, null
		);
		context.disableDefaultConstraintViolation();
		return spy(context);
	}
}
