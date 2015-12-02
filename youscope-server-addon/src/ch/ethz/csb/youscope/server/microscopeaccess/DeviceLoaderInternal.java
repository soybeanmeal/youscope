/**
 * 
 */
package ch.ethz.csb.youscope.server.microscopeaccess;


import ch.ethz.csb.youscope.shared.microscope.MicroscopeDriverException;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeLockedException;

/**
 * @author langmo
 *
 */
public interface DeviceLoaderInternal
{
	/**
	 * Returns a list of all available device drivers.
	 * @return List of all available device drivers.
	 * @throws MicroscopeDriverException 
	 */
	public AvailableDeviceDriverInternal[] getAvailableDeviceDrivers() throws MicroscopeDriverException;
	
	/**
	 * Returns the device driver with the given library and driver ID, or null, if driver could not be found.
	 * @param libraryID The ID of the library the driver belongs to.
	 * @param driverID The ID of the device driver.
	 * @return the device driver with the given IDs.
	 * @throws MicroscopeDriverException Thrown if error occurred while trying to load drivers.
	 */
	public AvailableDeviceDriverInternal getAvailableDeviceDriver(String libraryID, String driverID) throws MicroscopeDriverException;
	
	/**
	 * Removes a previously added device.
	 * @param accessID The access ID of the current microscope object. If the microscope is locked with a different accessID, a MicroscopeLockedException is thrown.
	 * @param name Name of the device.
	 * @throws MicroscopeDriverException
	 * @throws MicroscopeLockedException 
	 */
	public void removeDevice(String name, int accessID) throws MicroscopeDriverException, MicroscopeLockedException;	
}
