/**
 * 
 */
package org.youscope.plugin.devicejob;

import java.rmi.RemoteException;

import org.youscope.common.measurement.ExecutionInformation;
import org.youscope.common.measurement.MeasurementContext;
import org.youscope.common.measurement.MeasurementRunningException;
import org.youscope.common.measurement.PositionInformation;
import org.youscope.common.measurement.job.JobAdapter;
import org.youscope.common.measurement.job.JobException;
import org.youscope.common.measurement.job.basicjobs.DeviceSettingJob;
import org.youscope.common.microscope.DeviceSettingDTO;
import org.youscope.common.microscope.Microscope;
import org.youscope.common.microscope.MicroscopeException;
import org.youscope.common.microscope.MicroscopeLockedException;
import org.youscope.common.microscope.SettingException;
import org.youscope.common.table.Table;
import org.youscope.common.table.TableDefinition;
import org.youscope.common.table.TableException;

/**
 * @author Moritz Lang
 */
class DeviceJobImpl extends JobAdapter implements DeviceSettingJob
{
	/**
	 * Serial Version UID.
	 */
	private static final long	serialVersionUID	= -115321069638111069L;

	private DeviceSettingDTO[]	settings			= new DeviceSettingDTO[0];

	private volatile Table tableToEvaluate = null;
	
	public DeviceJobImpl(PositionInformation positionInformation) throws RemoteException
	{
		super(positionInformation);
	}

	@Override
	public synchronized void setDeviceSettings(DeviceSettingDTO[] settings) throws MeasurementRunningException
	{
		assertRunning();
		if(settings == null)
		{
			this.settings = new DeviceSettingDTO[0];
			return;
		}

		this.settings = new DeviceSettingDTO[settings.length];
		for(int i = 0; i < settings.length; i++)
		{
			this.settings[i] = settings[i].clone();
		}

	}

	@Override
	public DeviceSettingDTO[] getDeviceSettings()
	{
		return settings;
	}

	@Override
	public void clearDeviceSettings() throws MeasurementRunningException
	{
		DeviceJobImpl.this.setDeviceSettings(null);
	}

	@Override
	public void addDeviceSetting(String device, String property, String value) throws MeasurementRunningException
	{
		assertRunning();
		DeviceSettingDTO[] newSettings = new DeviceSettingDTO[settings.length + 1];
		System.arraycopy(settings, 0, newSettings, 0, settings.length);
		DeviceSettingDTO newSetting = new DeviceSettingDTO();
		newSetting.setDeviceProperty(device, property);
		newSetting.setValue(value);
		newSettings[newSettings.length - 1] = newSetting;
		setDeviceSettings(newSettings);
	}

	
	@Override
	public void initializeJob(Microscope microscope, MeasurementContext measurementContext) throws JobException, InterruptedException, RemoteException
	{
		super.initializeJob(microscope, measurementContext);
		tableToEvaluate = null;
	}
	
	@Override
	public void uninitializeJob(Microscope microscope, MeasurementContext measurementContext) throws JobException, InterruptedException, RemoteException
	{
		super.uninitializeJob(microscope, measurementContext);
		tableToEvaluate = null;
	}
	
	@Override
	public void runJob(ExecutionInformation executionInformation, Microscope microscope, MeasurementContext measurementContext) throws JobException, InterruptedException, RemoteException
	{
		if(settings != null)
		{
			try
			{
				microscope.applyDeviceSettings(settings);
			}
			catch(SettingException e)
			{
				throw new JobException("Device settings invalid.", e);
			}
			catch(MicroscopeLockedException e)
			{
				throw new JobException("Device settings could not be set.", e);
			}
			catch(MicroscopeException e)
			{
				throw new JobException("Device settings caused error in device driver.", e);
			}
		}
		if(Thread.interrupted())
			throw new InterruptedException();
		Table tableToEvaluate;
		synchronized(this)
		{
			tableToEvaluate = this.tableToEvaluate;
			 this.tableToEvaluate = null;
		}
		if(tableToEvaluate != null)
		{
			DeviceSettingDTO[] settings = new DeviceSettingDTO[tableToEvaluate.getNumRows()];
			for(int row = 0;row<tableToEvaluate.getNumRows(); row++)
			{
				try {
					settings[row] = new DeviceSettingDTO(
							tableToEvaluate.getValue(row, DeviceTable.COLUMN_DEVICE.getColumnName(),String.class),
							tableToEvaluate.getValue(row, DeviceTable.COLUMN_PROPERTY.getColumnName(),String.class),
							tableToEvaluate.getValue(row, DeviceTable.COLUMN_VALUE.getColumnName(),String.class));
				} catch (Exception e) {
					throw new JobException("Could not interpret values in consumed table in row "+ Integer.toString(row)+".", e);
				}
			}
			try
			{
				microscope.applyDeviceSettings(settings);
			}
			catch(SettingException e)
			{
				throw new JobException("Device settings invalid.", e);
			}
			catch(MicroscopeLockedException e)
			{
				throw new JobException("Device settings could not be set.", e);
			}
			catch(MicroscopeException e)
			{
				throw new JobException("Device settings caused error in device driver.", e);
			}
		}
	}

	@Override
	public synchronized String getDefaultName()
	{
		String text = "Device Settings(";
		for(int i = 0; i < settings.length; i++)
		{
			if(i > 0)
				text += ", ";
			text += settings[i].getDevice() + "." + settings[i].getProperty() + "=" + settings[i].getStringValue();
		}
		return text;
	}

	@Override
	public synchronized void consumeTable(Table table) throws RemoteException, TableException
	{
		tableToEvaluate = table.toTable(getConsumedTableDefinition());
		
	}

	@Override
	public TableDefinition getConsumedTableDefinition() throws RemoteException {
		return DeviceTable.getTableDefinition();
	}
}