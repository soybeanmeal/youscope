/**
 * 
 */
package ch.ethz.csb.youscope.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import ch.ethz.csb.youscope.server.microscopeaccess.AvailableDeviceDriverInternal;
import ch.ethz.csb.youscope.server.microscopeaccess.DeviceLoaderInternal;
import ch.ethz.csb.youscope.shared.microscope.AvailableDeviceDriver;
import ch.ethz.csb.youscope.shared.microscope.DeviceLoader;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeDriverException;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeLockedException;

/**
 * @author langmo
 */
class DeviceLoaderRMI extends UnicastRemoteObject implements DeviceLoader
{

	/**
	 * Serial Version UID.
	 */
	private static final long		serialVersionUID	= -975901235633383293L;

	protected DeviceLoaderInternal	deviceManager;

	protected int					accessID;

	/**
	 * Constructor.
	 * 
	 * @throws RemoteException
	 */
	DeviceLoaderRMI(DeviceLoaderInternal deviceManager, int accessID) throws RemoteException
	{
		super();
		this.accessID = accessID;
		this.deviceManager = deviceManager;
	}

	@Override
	public AvailableDeviceDriver[] getAvailableDeviceDrivers() throws RemoteException, MicroscopeDriverException
	{
		AvailableDeviceDriverInternal[] driversInternal = deviceManager.getAvailableDeviceDrivers();
		AvailableDeviceDriver[] drivers = new AvailableDeviceDriver[driversInternal.length];
		for(int i = 0; i < driversInternal.length; i++)
		{
			drivers[i] = new AvailableDeviceDriverRMI(driversInternal[i], accessID);
		}
		return drivers;
	}

	/* @Override public void addDevice(String name, String library, String identifier,
	 * DeviceSettingDTO[] preInitSettings) throws MicroscopeDriverException,
	 * MicroscopeLockedException { deviceManager.addDevice(name, library, identifier,
	 * preInitSettings, accessID); } */

	@Override
	public void removeDevice(String name) throws MicroscopeDriverException, MicroscopeLockedException
	{
		deviceManager.removeDevice(name, accessID);
	}
}
