/**
 * 
 */
package org.youscope.plugin.composedimaging;

import java.rmi.RemoteException;

import org.youscope.addon.AddonException;
import org.youscope.addon.component.ConstructionContext;
import org.youscope.addon.measurement.CustomMeasurementInitializer;
import org.youscope.common.configuration.ConfigurationException;
import org.youscope.common.configuration.RegularPeriod;
import org.youscope.common.configuration.VaryingPeriodDTO;
import org.youscope.common.measurement.Measurement;
import org.youscope.common.measurement.MeasurementRunningException;
import org.youscope.common.measurement.PositionInformation;
import org.youscope.common.measurement.job.Job;
import org.youscope.common.measurement.task.MeasurementTask;

/**
 * @author langmo
 * 
 */
public class ComposedImagingMeasurementInitializer implements CustomMeasurementInitializer<ComposedImagingMeasurementConfiguration>
{

	@Override
	public void initializeMeasurement(Measurement measurement, ComposedImagingMeasurementConfiguration configuration, ConstructionContext jobInitializer) throws ConfigurationException, AddonException
	{
		MeasurementTask task;
		if(configuration.getPeriod() instanceof RegularPeriod)
		{
			RegularPeriod period = (RegularPeriod)configuration.getPeriod();
			try
			{
				task = measurement.addTask(period.getPeriod(), period.isFixedTimes(), period.getStartTime(), period.getNumExecutions());
			}
			catch(MeasurementRunningException e)
			{
				throw new AddonException("Could not create measurement since it is already running.", e);
			}
			catch (RemoteException e)
			{
				throw new AddonException("Could not create measurement due to remote exception.", e);
			}
		}
		else if(configuration.getPeriod() instanceof VaryingPeriodDTO)
		{
			VaryingPeriodDTO period = (VaryingPeriodDTO)configuration.getPeriod();
			try
			{
				task = measurement.addMultiplePeriodTask(period.getPeriods(), period.getBreakTime(), period.getStartTime(), period.getNumExecutions());
			}
			catch(MeasurementRunningException e)
			{
				throw new AddonException("Could not create measurement since it is already running.", e);
			}
			catch (RemoteException e)
			{
				throw new AddonException("Could not create measurement due to remote exception.", e);
			}
		}
		else
		{
			throw new ConfigurationException("Period type is not supported.");
		}
		
		ComposedImagingJobConfiguration jobConfiguration = new ComposedImagingJobConfiguration();
		jobConfiguration.setChannel(configuration.getChannelGroup(), configuration.getChannel());
		jobConfiguration.setExposure(configuration.getExposure());
		jobConfiguration.setImageSaveName(configuration.getImageSaveName());
		jobConfiguration.setNumPixels(configuration.getNumPixels());
		jobConfiguration.setNx(configuration.getNx());
		jobConfiguration.setNy(configuration.getNy());
		jobConfiguration.setOverlap(configuration.getOverlap());
		jobConfiguration.setPixelSize(configuration.getPixelSize());
		jobConfiguration.setSaveImages(configuration.isSaveImages());
		
		try
		{
			Job job = jobInitializer.getComponentProvider().createJob(new PositionInformation(), jobConfiguration);
			task.addJob(job);
		}
		catch(Exception e)
		{
			throw new AddonException("Could not create measurement.", e);
		}
	}
}