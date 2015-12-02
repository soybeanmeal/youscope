/**
 * 
 */
package ch.ethz.csb.youscope.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import ch.ethz.csb.youscope.server.microscopeaccess.MicroscopeConfigurationInternal;
import ch.ethz.csb.youscope.shared.microscope.DeviceSettingDTO;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeConfiguration;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeLockedException;
import ch.ethz.csb.youscope.shared.microscope.SettingException;

/**
 * Class to change the general microscope configuration, i.e. everything which is not a device.
 * @author Moritz Lang
 */
class MicroscopeConfigurationImpl extends UnicastRemoteObject implements MicroscopeConfiguration
{

	/**
	 * Serial Version UID.
	 */
	private static final long						serialVersionUID	= -3933537842182223623L;

	private final MicroscopeConfigurationInternal	microscopeConfiguration;

	private final int								accessID;

	/**
	 * Constructor.
	 * @throws RemoteException
	 */
	protected MicroscopeConfigurationImpl(MicroscopeConfigurationInternal microscopeConfiguration, int accessID) throws RemoteException
	{
		super();
		this.microscopeConfiguration = microscopeConfiguration;
		this.accessID = accessID;
	}

	@Override
	public void setImageSynchronizationDevices(String[] devices) throws SettingException, RemoteException, MicroscopeLockedException
	{
		microscopeConfiguration.setImageSynchronizationDevices(devices, accessID);
	}

	@Override
	public void addImageSynchronizationDevice(String device) throws SettingException, RemoteException, MicroscopeLockedException
	{
		microscopeConfiguration.addImageSynchronizationDevice(device, accessID);
	}

	@Override
	public void removeImageSynchronizationDevice(String device) throws SettingException, RemoteException, MicroscopeLockedException
	{
		microscopeConfiguration.removeImageSynchronizationDevice(device, accessID);
	}

	@Override
	public String[] getImageSynchronizationDevices() throws RemoteException
	{
		return microscopeConfiguration.getImageSynchronizationDevices();
	}

	@Override
	public DeviceSettingDTO[] getSystemStartupSettings() throws RemoteException
	{
		return microscopeConfiguration.getSystemStartupSettings();
	}

	@Override
	public void setSystemStartupSettings(DeviceSettingDTO[] settings) throws RemoteException, SettingException, MicroscopeLockedException
	{
		microscopeConfiguration.setSystemStartupSettings(settings, accessID);
	}

	@Override
	public void addSystemStartupSetting(DeviceSettingDTO setting) throws RemoteException, SettingException, MicroscopeLockedException
	{
		microscopeConfiguration.addSystemStartupSetting(setting, accessID);
	}

	@Override
	public int getCommunicationTimeout() throws RemoteException
	{
		return microscopeConfiguration.getCommunicationTimeout();
	}

	@Override
	public void setCommunicationTimeout(int timeout) throws RemoteException, SettingException, MicroscopeLockedException
	{
		microscopeConfiguration.setCommunicationTimeout(timeout, accessID);
	}

	@Override
	public DeviceSettingDTO[] getSystemShutdownSettings()
	{
		return microscopeConfiguration.getSystemShutdownSettings();
	}

	@Override
	public void setSystemShutdownSettings(DeviceSettingDTO[] settings) throws SettingException, MicroscopeLockedException
	{
		microscopeConfiguration.setSystemShutdownSettings(settings, accessID);
	}

	@Override
	public void addSystemShutdownSetting(DeviceSettingDTO setting) throws SettingException, MicroscopeLockedException
	{
		microscopeConfiguration.addSystemShutdownSetting(setting, accessID);
	}

	@Override
	public int getCommunicationPingPeriod()
	{
		return microscopeConfiguration.getCommunicationPingPeriod();
	}

	@Override
	public void setCommunicationPingPeriod(int pingPeriod) throws SettingException, MicroscopeLockedException
	{
		microscopeConfiguration.setCommunicationPingPeriod(pingPeriod, accessID);
	}

	@Override
	public void setImageBufferSize(int sizeMB) throws RemoteException, SettingException, MicroscopeLockedException, UnsupportedOperationException
	{
		microscopeConfiguration.setImageBufferSize(sizeMB, accessID);
	}

	@Override
	public int getImageBufferSize() throws UnsupportedOperationException, RemoteException
	{
		return microscopeConfiguration.getImageBufferSize();
	}
}
