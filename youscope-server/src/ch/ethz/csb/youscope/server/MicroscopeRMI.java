/**
 * 
 */
package ch.ethz.csb.youscope.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import ch.ethz.csb.youscope.server.microscopeaccess.AutoFocusDeviceInternal;
import ch.ethz.csb.youscope.server.microscopeaccess.CameraDeviceInternal;
import ch.ethz.csb.youscope.server.microscopeaccess.DeviceInternal;
import ch.ethz.csb.youscope.server.microscopeaccess.FocusDeviceInternal;
import ch.ethz.csb.youscope.server.microscopeaccess.MicroscopeInternal;
import ch.ethz.csb.youscope.server.microscopeaccess.SerialDeviceInternal;
import ch.ethz.csb.youscope.server.microscopeaccess.ShutterDeviceInternal;
import ch.ethz.csb.youscope.server.microscopeaccess.StageDeviceInternal;
import ch.ethz.csb.youscope.server.microscopeaccess.StateDeviceInternal;
import ch.ethz.csb.youscope.shared.YouScopeMessageListener;
import ch.ethz.csb.youscope.shared.microscope.AutoFocusDevice;
import ch.ethz.csb.youscope.shared.microscope.CameraDevice;
import ch.ethz.csb.youscope.shared.microscope.ChannelManager;
import ch.ethz.csb.youscope.shared.microscope.Device;
import ch.ethz.csb.youscope.shared.microscope.DeviceException;
import ch.ethz.csb.youscope.shared.microscope.DeviceLoader;
import ch.ethz.csb.youscope.shared.microscope.DeviceSettingDTO;
import ch.ethz.csb.youscope.shared.microscope.DeviceType;
import ch.ethz.csb.youscope.shared.microscope.FocusDevice;
import ch.ethz.csb.youscope.shared.microscope.Microscope;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeConfiguration;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeConfigurationException;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeConfigurationListener;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeException;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeLockedException;
import ch.ethz.csb.youscope.shared.microscope.PixelSizeManager;
import ch.ethz.csb.youscope.shared.microscope.SettingException;
import ch.ethz.csb.youscope.shared.microscope.ShutterDevice;
import ch.ethz.csb.youscope.shared.microscope.StageDevice;
import ch.ethz.csb.youscope.shared.microscope.StateDevice;
import ch.ethz.csb.youscope.shared.tools.RMIReader;
import ch.ethz.csb.youscope.shared.tools.RMIWriter;

/**
 * The purpose of this class is to hide complexity from the user. Internally, when manipulating the state of the microscope,
 * one has to provide a so called access ID whenever one wants to manipulate the microscope. Access to the microscope can be locked,
 * such that it is only accessible by a given access ID. This is typically done by measurements, which, thus, prevent anybody else
 * from varying the microscope's state.
 * This complexity is hidden from the user by coupling each Microscope RMI object to an unique access ID. Thus, when the user locks the microscope,
 * it can be only used by using the same RMI object with which it was locked.
 * Furthermore, this class handles remote method invocations, i.e. can be remotely accessed, in difference to the microscope object.
 * Finally, some of the functionalities in the Microscope interface, like loading and saving configuration files, the channel manager, and similar,
 * are not handled by a plug-in, but by the server using the general purpose interfaces of the plug-in.
 * 
 * @author Moritz Lang
 */
class MicroscopeRMI extends UnicastRemoteObject implements Microscope
{
	/**
	 * Serial Version UID.
	 */
	private static final long			serialVersionUID	= -3408485236746942349L;

	private final MicroscopeInternal	microscope;
	private final ConfigFileManager		configFileManager;
	private final ChannelManagerImpl	channelManager;
	private final PixelSizeManagerImpl	pixelSizeManager;

	private final int					accessID;

	private static int					nextAccessID		= 1;

	/**
	 * @throws RemoteException
	 */
	MicroscopeRMI(MicroscopeInternal microscope, ConfigFileManager configFileManager, ChannelManagerImpl channelManager, PixelSizeManagerImpl pixelSizeManager) throws RemoteException
	{
		super();
		this.microscope = microscope;
		this.configFileManager = configFileManager;
		this.pixelSizeManager = pixelSizeManager;
		this.channelManager = channelManager;
		this.accessID = nextAccessID++;
	}

	@Override
	public Device[] getDevices() throws RemoteException
	{
		DeviceInternal[] orgDevices = microscope.getDevices();
		Device[] newDevices = new Device[orgDevices.length];
		for(int i = 0; i < newDevices.length; i++)
		{
			newDevices[i] = toDevice(orgDevices[i]);
		}
		return newDevices;
	}

	@Override
	public Device[] getDevices(DeviceType type) throws RemoteException
	{
		DeviceInternal[] orgDevices = microscope.getDevices(type);
		Device[] newDevices = new Device[orgDevices.length];
		for(int i = 0; i < newDevices.length; i++)
		{
			newDevices[i] = toDevice(orgDevices[i]);
		}
		return newDevices;
	}

	@Override
	public Device getDevice(String name) throws DeviceException, RemoteException
	{
		return toDevice(microscope.getDevice(name));
	}

	private Device toDevice(DeviceInternal device) throws RemoteException
	{
		if(device instanceof CameraDeviceInternal)
			return new CameraDeviceRMI((CameraDeviceInternal)device, channelManager, accessID);
		else if(device instanceof StageDeviceInternal)
			return new StageDeviceRMI((StageDeviceInternal)device, accessID);
		else if(device instanceof ShutterDeviceInternal)
			return new ShutterDeviceRMI((ShutterDeviceInternal)device, accessID);
		else if(device instanceof FocusDeviceInternal)
			return new FocusDeviceRMI((FocusDeviceInternal)device, accessID);
		else if(device instanceof StateDeviceInternal)
			return new StateDeviceRMI((StateDeviceInternal)device, accessID);
		else if(device instanceof AutoFocusDeviceInternal)
			return new AutoFocusDeviceRMI((AutoFocusDeviceInternal)device, accessID);
		else if(device instanceof SerialDeviceInternal)
			return new SerialDeviceRMI((SerialDeviceInternal)device, accessID);
		else
			return new DeviceRMI(device, accessID);
	}

	@Override
	public FocusDevice getFocusDevice() throws DeviceException, RemoteException
	{
		return new FocusDeviceRMI(microscope.getFocusDevice(), accessID);
	}

	@Override
	public FocusDevice getFocusDevice(String name) throws DeviceException, RemoteException
	{
		return new FocusDeviceRMI(microscope.getFocusDevice(name), accessID);
	}

	@Override
	public StageDevice getStageDevice() throws DeviceException, RemoteException
	{
		return new StageDeviceRMI(microscope.getStageDevice(), accessID);
	}

	@Override
	public StageDevice getStageDevice(String name) throws DeviceException, RemoteException
	{
		return new StageDeviceRMI(microscope.getStageDevice(name), accessID);
	}

	@Override
	public CameraDevice getCameraDevice() throws DeviceException, RemoteException
	{
		return new CameraDeviceRMI(microscope.getCameraDevice(), channelManager, accessID);
	}

	@Override
	public CameraDevice getCameraDevice(String name) throws DeviceException, RemoteException
	{
		return new CameraDeviceRMI(microscope.getCameraDevice(name), channelManager, accessID);
	}

	@Override
	public ChannelManager getChannelManager() throws RemoteException
	{
		return channelManager.getRMIInterface(accessID);
	}

	@Override
	public void applyDeviceSetting(DeviceSettingDTO setting) throws RemoteException, MicroscopeLockedException, MicroscopeException, InterruptedException, SettingException
	{
		microscope.applyDeviceSetting(setting, accessID);
	}

	@Override
	public void applyDeviceSettings(DeviceSettingDTO[] settings) throws RemoteException, MicroscopeLockedException, MicroscopeException, InterruptedException, SettingException
	{
		microscope.applyDeviceSettings(settings, accessID);
	}

	@Override
	public void applyDeviceSettingAsync(DeviceSettingDTO setting) throws SettingException, MicroscopeLockedException, MicroscopeException, InterruptedException, RemoteException
	{
		microscope.applyDeviceSettingAsync(setting, accessID);
	}

	@Override
	public void applyDeviceSettingsAsync(DeviceSettingDTO[] settings) throws SettingException, MicroscopeLockedException, MicroscopeException, InterruptedException, RemoteException
	{
		microscope.applyDeviceSettingsAsync(settings, accessID);
	}

	@Override
	public boolean isEmergencyStopped()
	{
		return microscope.isEmergencyStopped();
	}

	@Override
	public void emergencyStop()
	{
		microscope.emergencyStop();
	}

	@Override
	public void resetEmergencyStop()
	{
		microscope.resetEmergencyStop();
	}

	@Override
	public void lockExclusiveWrite() throws MicroscopeLockedException, RemoteException
	{
		microscope.lockExclusiveWrite(accessID);
	}

	@Override
	public void unlockExclusiveWrite() throws MicroscopeLockedException, RemoteException
	{
		microscope.unlockExclusiveWrite(accessID);
	}

	@Override
	public DeviceLoader getDeviceLoader() throws RemoteException, UnsupportedOperationException
	{
		return new DeviceLoaderRMI(microscope.getDeviceLoader(), accessID);
	}

	@Override
	public FocusDevice[] getFocusDevices() throws RemoteException
	{
		FocusDeviceInternal[] devicesInternal = microscope.getFocusDevices();
		FocusDevice[] devices = new FocusDevice[devicesInternal.length];
		for(int i = 0; i < devicesInternal.length; i++)
		{
			devices[i] = new FocusDeviceRMI(devicesInternal[i], accessID);
		}
		return devices;
	}

	@Override
	public void setFocusDevice(String deviceName) throws RemoteException, DeviceException, MicroscopeLockedException
	{
		microscope.setFocusDevice(deviceName, accessID);
	}

	@Override
	public void setShutterDevice(String deviceName) throws RemoteException, DeviceException, MicroscopeLockedException
	{
		microscope.setShutterDevice(deviceName, accessID);
	}

	@Override
	public ShutterDevice getShutterDevice() throws RemoteException, DeviceException
	{
		return new ShutterDeviceRMI(microscope.getShutterDevice(), accessID);
	}

	@Override
	public ShutterDevice[] getShutterDevices() throws RemoteException
	{
		ShutterDeviceInternal[] devicesInternal = microscope.getShutterDevices();
		ShutterDevice[] devices = new ShutterDevice[devicesInternal.length];
		for(int i = 0; i < devicesInternal.length; i++)
		{
			devices[i] = new ShutterDeviceRMI(devicesInternal[i], accessID);
		}
		return devices;
	}

	@Override
	public ShutterDevice getShutterDevice(String deviceName) throws RemoteException, DeviceException
	{
		return new ShutterDeviceRMI(microscope.getShutterDevice(deviceName), accessID);
	}

	@Override
	public StageDevice[] getStageDevices() throws RemoteException
	{
		StageDeviceInternal[] devicesInternal = microscope.getStageDevices();
		StageDevice[] devices = new StageDevice[devicesInternal.length];
		for(int i = 0; i < devicesInternal.length; i++)
		{
			devices[i] = new StageDeviceRMI(devicesInternal[i], accessID);
		}
		return devices;
	}

	@Override
	public void setStageDevice(String deviceName) throws RemoteException, DeviceException, MicroscopeLockedException
	{
		microscope.setStageDevice(deviceName, accessID);
	}

	@Override
	public void setCameraDevice(String cameraDevice) throws RemoteException, DeviceException, MicroscopeLockedException
	{
		microscope.setCameraDevice(cameraDevice, accessID);
	}

	@Override
	public CameraDevice[] getCameraDevices() throws RemoteException
	{
		CameraDeviceInternal[] devicesInternal = microscope.getCameraDevices();
		CameraDevice[] devices = new CameraDevice[devicesInternal.length];
		for(int i = 0; i < devicesInternal.length; i++)
		{
			devices[i] = new CameraDeviceRMI(devicesInternal[i], channelManager, accessID);
		}
		return devices;
	}

	@Override
	public StateDevice getStateDevice(String name) throws DeviceException, RemoteException
	{
		return new StateDeviceRMI(microscope.getStateDevice(name), accessID);
	}

	@Override
	public StateDevice[] getStateDevices() throws RemoteException
	{
		StateDeviceInternal[] devicesInternal = microscope.getStateDevices();
		StateDevice[] devices = new StateDevice[devicesInternal.length];
		for(int i = 0; i < devicesInternal.length; i++)
		{
			devices[i] = new StateDeviceRMI(devicesInternal[i], accessID);
		}
		return devices;
	}

	@Override
	public String getLastConfigurationWarning()
	{
		return configFileManager.getLastParseResult().getWarningMessage();
	}

	@Override
	public PixelSizeManager getPixelSizeManager() throws RemoteException
	{
		return pixelSizeManager.getRMIInterface(accessID);
	}

	@Override
	public void uninitializeMicroscope() throws MicroscopeException, MicroscopeLockedException, RemoteException, InterruptedException
	{
		microscope.uninitializeMicroscope(accessID);
	}

	@Override
	public void addMessageListener(YouScopeMessageListener listener) throws RemoteException
	{
		microscope.addMessageListener(listener);
	}

	@Override
	public void removeMessageListener(YouScopeMessageListener listener) throws RemoteException
	{
		microscope.removeMessageListener(listener);
	}

	@Override
	public MicroscopeConfiguration getMicroscopeConfiguration() throws RemoteException
	{
		return new MicroscopeConfigurationImpl(microscope.getMicroscopeConfiguration(), accessID);
	}

	@Override
	public String loadConfiguration(RMIReader configurationReader) throws MicroscopeConfigurationException, MicroscopeLockedException, RemoteException, InterruptedException, MicroscopeException
	{
		return configFileManager.loadConfiguration(configurationReader, accessID);
	}

	@Override
	public void saveConfiguration(RMIWriter configurationWriter) throws MicroscopeConfigurationException, RemoteException, MicroscopeLockedException
	{
		configFileManager.saveConfiguration(configurationWriter, accessID);
	}

	@Override
	public AutoFocusDevice getAutoFocusDevice() throws DeviceException, RemoteException
	{
		return new AutoFocusDeviceRMI(microscope.getAutoFocusDevice(), accessID);
	}

	@Override
	public AutoFocusDevice[] getAutoFocusDevices() throws RemoteException
	{
		AutoFocusDeviceInternal[] devicesInternal = microscope.getAutoFocusDevices();
		AutoFocusDevice[] devices = new AutoFocusDevice[devicesInternal.length];
		for(int i = 0; i < devicesInternal.length; i++)
		{
			devices[i] = new AutoFocusDeviceRMI(devicesInternal[i], accessID);
		}
		return devices;
	}

	@Override
	public AutoFocusDevice getAutoFocusDevice(String deviceID) throws DeviceException, RemoteException
	{
		return new AutoFocusDeviceRMI(microscope.getAutoFocusDevice(deviceID), accessID);
	}

	@Override
	public void setAutoFocusDevice(String deviceID) throws DeviceException, MicroscopeLockedException, RemoteException
	{
		microscope.setAutoFocusDevice(deviceID, accessID);
	}

	@Override
	public void addConfigurationListener(MicroscopeConfigurationListener listener)
	{
		microscope.addConfigurationListener(listener);
	}

	@Override
	public void removeConfigurationListener(MicroscopeConfigurationListener listener)
	{
		microscope.removeConfigurationListener(listener);
	}

}
