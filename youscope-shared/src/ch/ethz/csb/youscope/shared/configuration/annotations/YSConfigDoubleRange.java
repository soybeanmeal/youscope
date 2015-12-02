package ch.ethz.csb.youscope.shared.configuration.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allowed range of a douvle property.
 * @author Moritz Lang
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface YSConfigDoubleRange {
	/**
	 * Minimal value allowed for a double property.
	 * 
	 * @return minimal allowed value.
	 */
	public double minValue() default Double.MIN_VALUE;

	/**
	 * Maximal value allowed for a double property.
	 * 
	 * @return maximal allowed value.
	 */
	public double maxValue() default Double.MAX_VALUE;

	/**
	 * Null String used to indicate that no min/max value function is given.
	 */
	public static final String NULL = "";

	/**
	 * Name of a function taking no arguments and returning a double. This
	 * function is used during runtime to determine the minimal allowed value.
	 * If set, overwrites anything set in minValue.
	 * 
	 * @return Name of the function to determine minimal value.
	 */
	public String minValueFunction() default NULL;

	/**
	 * Name of a function taking no arguments and returning a double. This
	 * function is used during runtime to determine the maximal allowed value.
	 * If set, overwrites anything set in maxValue.
	 * 
	 * @return Name of the function to determine maximal value.
	 */
	public String maxValueFunction() default NULL;
}
