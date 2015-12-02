/**
 * 
 */
package ch.ethz.csb.youscope.shared.microscope;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;


/**
 * Listener which gets notified if a device of the microscope was removed or its configuration modified.
 * @author Moritz Lang
 *
 */
public interface MicroscopeConfigurationListener extends EventListener, Remote
{
	
	/**
	 * Called when the microscope is uninitialized. All stored values about the microscope configuration should be unloaded.
	 * @throws RemoteException 
	 */
	public void microscopeUninitialized() throws RemoteException;
	/**
	 * Called when a previously defined device was removed.
	 * @param deviceID The ID of the removed device.
	 * @throws RemoteException 
	 */
	public void deviceRemoved(String deviceID) throws RemoteException;
	/**
	 * Called when a label of a state device changed its name.
	 * @param oldLabel Setting containing the old label.
	 * @param newLabel Setting containing the new label.
	 * @throws RemoteException 
	 */
	public void labelChanged(DeviceSettingDTO oldLabel, DeviceSettingDTO newLabel) throws RemoteException;
}
