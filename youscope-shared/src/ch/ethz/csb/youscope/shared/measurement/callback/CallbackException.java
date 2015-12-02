/**
 * 
 */
package ch.ethz.csb.youscope.shared.measurement.callback;

import ch.ethz.csb.youscope.shared.measurement.ComponentException;

/**
 * Exception thrown by measurement callbacks.
 * @author Moritz Lang
 * 
 */
public class CallbackException extends ComponentException
{
	/**
	 * Serial version UID.
	 */
	private static final long	serialVersionUID				= -2814438972668943211L;

	/**
	 * Constructor.
	 * 
	 * @param description Human readable description of the exception.
	 */
	public CallbackException(String description)
	{
		super(description);
	}


	/**
	 * Constructor.
	 * 
	 * @param description Human readable description of the exception.
	 * @param cause the parent exception.
	 */
	public CallbackException(String description, Throwable cause)
	{
		super(description, cause);
	}
}
