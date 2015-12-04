/**
 * 
 */
package org.youscope.plugin.composedimaging;

import java.awt.Dimension;
import java.rmi.RemoteException;

import org.youscope.addon.AddonException;
import org.youscope.addon.component.ComponentAddonFactoryAdapter;
import org.youscope.addon.component.ConstructionContext;
import org.youscope.addon.component.CustomAddonCreator;
import org.youscope.common.configuration.ConfigurationException;
import org.youscope.common.configuration.JobConfiguration;
import org.youscope.common.measurement.ComponentCreationException;
import org.youscope.common.measurement.MeasurementRunningException;
import org.youscope.common.measurement.PositionInformation;
import org.youscope.common.measurement.job.Job;
import org.youscope.common.measurement.job.basicjobs.CompositeJob;

/**
 * @author Moritz Lang
 */
public class StaggeringJobAddonFactory extends ComponentAddonFactoryAdapter 
{
	/**
	 * Constructor.
	 */
	public StaggeringJobAddonFactory()
	{
		super(StaggeringJobConfigurationAddon.class, CREATOR, StaggeringJobConfigurationAddon.getMetadata());
	}
	
	private static final CustomAddonCreator<StaggeringJobConfiguration, StaggeringJob> CREATOR = new CustomAddonCreator<StaggeringJobConfiguration,StaggeringJob>()
	{
		@Override
		public StaggeringJob createCustom(PositionInformation positionInformation, StaggeringJobConfiguration configuration,
				ConstructionContext constructionContext) throws ConfigurationException, AddonException 
		{
			StaggeringJobImpl job;
			try
			{
				job = new StaggeringJobImpl(positionInformation);
				job.setDeltaX(configuration.getDeltaX());
				job.setDeltaY(configuration.getDeltaY());
				job.setNumTiles(new Dimension(configuration.getNumTilesX(), configuration.getNumTilesY()));
				job.setNumIterationsBreak(configuration.getNumIterationsBreak());
				job.setNumTilesPerIteration(configuration.getNumTilesPerIteration());
				
				for(int x = 0; x < configuration.getNumTilesX(); x++)
				{
					PositionInformation xPositionInformation = new PositionInformation(positionInformation, PositionInformation.POSITION_TYPE_XTILE, x);
					for(int y = 0; y < configuration.getNumTilesY(); y++)
					{
						PositionInformation yPositionInformation = new PositionInformation(xPositionInformation, PositionInformation.POSITION_TYPE_YTILE, y);
						
						CompositeJob jobContainer;
						try {
							jobContainer = constructionContext.getComponentProvider().createJob(yPositionInformation, CompositeJob.DEFAULT_TYPE_IDENTIFIER, CompositeJob.class);
						} catch (ComponentCreationException e) {
							throw new AddonException("Plate scanning jobs need the composite job plugin.", e);
						}
							
						
						// Add all child jobs
						for(JobConfiguration childJobConfig : configuration.getJobs())
						{
							Job childJob;
							try {
								childJob = constructionContext.getComponentProvider().createJob(yPositionInformation, childJobConfig);
							} catch (ComponentCreationException e) {
								throw new AddonException("Could not create child jobs.", e);
							}
							jobContainer.addJob(childJob);
						}
						
						job.addJob(jobContainer);
					}
				}
				
				
			}
			catch(MeasurementRunningException e)
			{
				throw new AddonException("Could not create job, since newly created job is already running.", e);
			}
			catch(RemoteException e)
			{
				throw new AddonException("Could not create job due to remote error.", e);
			}
			return job;
		}

		@Override
		public Class<StaggeringJob> getComponentInterface() {
			return StaggeringJob.class;
		}
	};
}