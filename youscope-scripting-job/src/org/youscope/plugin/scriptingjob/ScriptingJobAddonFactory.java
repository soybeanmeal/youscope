/**
 * 
 */
package org.youscope.plugin.scriptingjob;

import java.rmi.RemoteException;

import org.youscope.addon.AddonException;
import org.youscope.addon.component.ComponentAddonFactoryAdapter;
import org.youscope.addon.component.ComponentCreationException;
import org.youscope.addon.component.CustomAddonCreator;
import org.youscope.common.PositionInformation;
import org.youscope.common.callback.CallbackCreationException;
import org.youscope.common.configuration.ConfigurationException;
import org.youscope.common.job.Job;
import org.youscope.common.job.JobConfiguration;
import org.youscope.common.job.basicjobs.ScriptingJob;
import org.youscope.common.measurement.MeasurementRunningException;
import org.youscope.common.scripting.RemoteScriptEngine;
import org.youscope.serverinterfaces.ConstructionContext;

/**
 * @author Moritz Lang
 */
public class ScriptingJobAddonFactory extends ComponentAddonFactoryAdapter 
{
	/**
	 * Constructor.
	 */
	public ScriptingJobAddonFactory()
	{
		super(ScriptingJobConfigurationAddon.class, CREATOR, ScriptingJobConfigurationAddon.getMetadata());
	}
	
	private static final CustomAddonCreator<ScriptingJobConfiguration, ScriptingJob> CREATOR = new CustomAddonCreator<ScriptingJobConfiguration,ScriptingJob>()
	{
		@Override
		public ScriptingJob createCustom(PositionInformation positionInformation, ScriptingJobConfiguration configuration,
				ConstructionContext constructionContext) throws ConfigurationException, AddonException 
		{
			if(configuration.getScriptEngine() == null)
				throw new ConfigurationException("No script engine defined.");
			ScriptingJob job;
			try
			{
				job = new ScriptingJobImpl(positionInformation);
				job.setScriptFile(configuration.getScriptFile());		
				job.addMessageListener(constructionContext.getLogger());
				
				if(configuration.isUseClientScriptEngine())
				{
					RemoteScriptEngine engine;
					try {
						engine = constructionContext.getCallbackProvider().createCallback(configuration.getScriptEngine(), RemoteScriptEngine.class);
					} catch (CallbackCreationException e) {
						throw new AddonException("Could not create remote script engine with name " + configuration.getScriptEngine()+".", e);
					}
					job.setRemoteScriptEngine(engine);
				}
				else
				{
					job.setScriptEngine(configuration.getScriptEngine());
				}
				for(JobConfiguration childJobConfig : configuration.getJobs())
				{
					Job childJob;
					try {
						childJob = constructionContext.getComponentProvider().createJob(positionInformation, childJobConfig);
					} catch (ComponentCreationException e) {
						throw new AddonException("Could not create child job.", e);
					}
					job.addJob(childJob);
				}
			}
			catch(MeasurementRunningException e)
			{
				throw new AddonException("Could not create scripting job since newly created job already running.", e);
			} catch (RemoteException e) {
				throw new AddonException("Could not create scripting job due to remote exception.", e);
			}
			return job;
		}

		@Override
		public Class<ScriptingJob> getComponentInterface() {
			return ScriptingJob.class;
		}
	};
}
