/**
 * 
 */
package org.youscope.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.youscope.addon.microscopeaccess.AvailableDeviceDriverInternal;
import org.youscope.addon.microscopeaccess.PreInitDevicePropertyInternal;
import org.youscope.common.microscope.AvailableDeviceDriver;
import org.youscope.common.microscope.DeviceSettingDTO;
import org.youscope.common.microscope.DeviceType;
import org.youscope.common.microscope.MicroscopeDriverException;
import org.youscope.common.microscope.MicroscopeLockedException;
import org.youscope.common.microscope.PreInitDeviceProperty;

/**
 * @author langmo
 */
public class AvailableDeviceDriverRMI extends UnicastRemoteObject implements AvailableDeviceDriver
{
	/**
	 * Serial Version UID.
	 */
	private static final long				serialVersionUID	= 4944415630156050190L;

	private AvailableDeviceDriverInternal	deviceDriver;

	protected int							accessID;

	/**
	 * @throws RemoteException
	 */
	protected AvailableDeviceDriverRMI(AvailableDeviceDriverInternal deviceDriver, int accessID) throws RemoteException
	{
		super();
		this.deviceDriver = deviceDriver;
		this.accessID = accessID;
	}

	@Override
	public String getDriverID() throws MicroscopeDriverException
	{
		return deviceDriver.getDriverID();
	}

	@Override
	public DeviceType getType() throws MicroscopeDriverException
	{
		return deviceDriver.getType();
	}

	@Override
	public String getDescription() throws MicroscopeDriverException
	{
		return deviceDriver.getDescription();
	}

	@Override
	public String getLibraryID() throws MicroscopeDriverException
	{
		return deviceDriver.getLibraryID();
	}

	/* @Override public PreInitDeviceProperty[] getPreInitDeviceProperties() throws RemoteException,
	 * MicroscopeDriverException, MicroscopeLockedException { PreInitDevicePropertyInternal[]
	 * propertiesOrg = deviceDriver.getPreInitDeviceProperties(accessID); PreInitDeviceProperty[]
	 * properties = new PreInitDeviceProperty[propertiesOrg.length]; for(int i = 0; i <
	 * propertiesOrg.length; i++) { properties[i] = new PreInitDevicePropertyRMI(propertiesOrg[i],
	 * accessID); } return properties; } */

	@Override
	public boolean isSerialPortDriver() throws RemoteException, MicroscopeLockedException, MicroscopeDriverException
	{
		return deviceDriver.isSerialPortDriver(accessID);
	}

	@Override
	public PreInitDeviceProperty[] loadDevice(String deviceID) throws MicroscopeDriverException, MicroscopeLockedException, RemoteException
	{
		PreInitDevicePropertyInternal[] propertiesOrg = deviceDriver.loadDevice(deviceID, accessID);
		PreInitDeviceProperty[] properties = new PreInitDeviceProperty[propertiesOrg.length];
		for(int i = 0; i < propertiesOrg.length; i++)
		{
			properties[i] = new PreInitDevicePropertyRMI(propertiesOrg[i], accessID);
		}
		return properties;
	}

	@Override
	public void initializeDevice(DeviceSettingDTO[] preInitSettings) throws MicroscopeDriverException, MicroscopeLockedException
	{
		deviceDriver.initializeDevice(preInitSettings, accessID);
	}

	@Override
	public void unloadDevice() throws MicroscopeLockedException, MicroscopeDriverException
	{
		deviceDriver.unloadDevice(accessID);
	}
}