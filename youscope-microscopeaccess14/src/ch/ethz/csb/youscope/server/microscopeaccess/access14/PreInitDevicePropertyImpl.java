/**
 * 
 */
package ch.ethz.csb.youscope.server.microscopeaccess.access14;

import java.util.Arrays;

import ch.ethz.csb.youscope.server.microscopeaccess.DeviceInternal;
import ch.ethz.csb.youscope.server.microscopeaccess.PreInitDevicePropertyInternal;
import ch.ethz.csb.youscope.shared.microscope.PropertyType;
import ch.ethz.csb.youscope.shared.microscope.DeviceType;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeDriverException;

/**
 * @author langmo
 *
 */
class PreInitDevicePropertyImpl implements PreInitDevicePropertyInternal
{
	private final String propertyID;
	private final PropertyType type;
	private final String[] allowedValues;
	private final String  defaultValue;
	private final MicroscopeImpl microscope;
	PreInitDevicePropertyImpl(MicroscopeImpl microscope, String name, PropertyType type, String[] allowedValues, String defaultValue)
	{
		this.microscope = microscope;
		this.propertyID = name;
		this.type = type;
		this.allowedValues = allowedValues;
		this.defaultValue = defaultValue;
	}
	@Override
	public String getPropertyID()
	{
		return propertyID;
	}
	
	@Override
	public String getDefaultValue()
	{
		return defaultValue;
	}

	@Override
	public PropertyType getType()
	{
		return type;
	}
	
	
	private String[] getSerialPortNames() throws MicroscopeDriverException
	{
		DeviceInternal[] devices = microscope.getDevices(DeviceType.SerialDevice);
		String[] deviceNames = new String[devices.length];
		for(int i = 0; i < deviceNames.length; i++)
		{
			deviceNames[i] = devices[i].getDeviceID();
		}
		return deviceNames;
	}
	@Override
	public String[] getAllowedPropertyValues() throws MicroscopeDriverException
	{
		if(getPropertyID().equals("Port"))
		{
			// microManager does not automatically set the available ports property.
			return getSerialPortNames();
		}
		else if(allowedValues == null)
			return null;
		else
			return Arrays.copyOf(allowedValues, allowedValues.length);
	}
}
