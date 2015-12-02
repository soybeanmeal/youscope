/**
 * 
 */
package ch.ethz.csb.youscope.shared.addon.celldetection;

import ch.ethz.csb.youscope.shared.measurement.resource.ResourceException;

/**
 * Exception thrown by cell detection algorithms.
 * @author Moritz Lang
 *
 */
public class CellDetectionException extends ResourceException
{

	/**
	 * Serial Version UID.
	 */
	private static final long	serialVersionUID	= 1433710243352359988L;

	/**
	 * @param message
	 * @param cause
	 */
	public CellDetectionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CellDetectionException(String message)
	{
		super(message);
	}
}
