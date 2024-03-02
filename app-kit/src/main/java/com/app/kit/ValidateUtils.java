package com.app.kit;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * hibernate-validator校验工具类
 * <a href="http://docs.jboss.org/hibernate/validator/5.4/reference/en-US/html_single/">参考文档</a>
 */
public class ValidateUtils {

	private static final Validator validator;

	static {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	/**
	 * 校验对象
	 * @param object 待校验对象
	 * @param groups 待校验的组
	 */
	public static void validateEntity(final Object object, final Class<?>... groups) {
		final Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
		if (!constraintViolations.isEmpty()) {
			for (final ConstraintViolation<Object> constraint : constraintViolations) {
				throw new IllegalStateException(constraint.getMessage());
			}
		}
	}

}
