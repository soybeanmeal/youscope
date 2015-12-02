/**
 * 
 */
package ch.ethz.csb.youscope.server.microscopeaccess;

import ch.ethz.csb.youscope.shared.microscope.MicroscopeException;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeLockedException;

/**
 * @author langmo
 * 
 */
public interface FocusDeviceInternal extends DeviceInternal
{
	/**
	 * Returns the position of the focus device.
	 * 
	 * @return Position of the focus device.
	 * @throws MicroscopeException
	 * @throws InterruptedException
	 */
	double getFocusPosition() throws MicroscopeException, InterruptedException;

	/**
	 * Sets the position of the current focus device.
	 * 
	 * @param position The new position.
	 * @param accessID The access ID of the current microscope object. If the microscope is locked with a different accessID, a MicroscopeLockedException is thrown.
	 * @throws MicroscopeLockedException
	 * @throws MicroscopeException
	 * @throws InterruptedException
	 */
	void setFocusPosition(double position, int accessID) throws MicroscopeLockedException, MicroscopeException, InterruptedException;

	/**
	 * Sets the position of the current focus device relative to the current
	 * focus.
	 * 
	 * @param offset The offset.
	 * @param accessID The access ID of the current microscope object. If the microscope is locked with a different accessID, a MicroscopeLockedException is thrown.
	 * @throws MicroscopeLockedException
	 * @throws MicroscopeException
	 * @throws InterruptedException
	 */
	void setRelativeFocusPosition(double offset, int accessID) throws MicroscopeLockedException, MicroscopeException, InterruptedException;
}
