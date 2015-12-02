/**
 * 
 */
package ch.ethz.csb.youscope.addon.waitforuser;

import java.rmi.RemoteException;

import ch.ethz.csb.youscope.shared.measurement.ExecutionInformation;
import ch.ethz.csb.youscope.shared.measurement.MeasurementContext;
import ch.ethz.csb.youscope.shared.measurement.MeasurementRunningException;
import ch.ethz.csb.youscope.shared.measurement.PositionInformation;
import ch.ethz.csb.youscope.shared.measurement.callback.CallbackException;
import ch.ethz.csb.youscope.shared.measurement.job.JobAdapter;
import ch.ethz.csb.youscope.shared.measurement.job.JobException;
import ch.ethz.csb.youscope.shared.microscope.Microscope;

/**
 * @author langmo 
 */
class WaitForUserJobImpl  extends JobAdapter implements WaitForUserJob
{
	/**
	 * SerializableVersion UID.
	 */
	private static final long	serialVersionUID	= 8128119758338178084L;

	private String message = "No message.";
	private WaitForUserCallback callback;

	public WaitForUserJobImpl(PositionInformation positionInformation) throws RemoteException
	{
		super(positionInformation);
	}

	

	@Override
	public void runJob(ExecutionInformation executionInformation,  Microscope microscope, MeasurementContext measurementContext) throws JobException, InterruptedException, RemoteException
	{
		callback.waitForUser(message);
	}

	@Override
	public String getDefaultName()
	{
		return "Wait for user";
	}

	@Override
	public String getMessage() throws RemoteException
	{
		return message;
	}

	@Override
	public void setMessage(String message) throws RemoteException, MeasurementRunningException
	{
		assertRunning();
		if(message != null)
			this.message = message;
	}

	@Override
	public void setMeasurementCallback(WaitForUserCallback callback) throws RemoteException, MeasurementRunningException
	{
		assertRunning();
		this.callback = callback;
	}

	@Override
	public void initializeJob(Microscope microscope, MeasurementContext measurementContext) throws JobException, InterruptedException, RemoteException
	{
		super.initializeJob(microscope, measurementContext);
		// Check if callback defined.
		if(callback == null)
			throw new JobException("Measurement callback is null, thus cannot wait for user.");
		// Initialize callback.
		try
		{
			callback.initializeCallback();
		}
		catch(CallbackException e)
		{
			throw new JobException("Measurement callback did throw an error while initialization.", e);
		}
	}



	@Override
	public void uninitializeJob(Microscope microscope, MeasurementContext measurementContext) throws JobException, InterruptedException, RemoteException
	{
		super.uninitializeJob(microscope, measurementContext);
		
		if(callback != null)
		{
			try
			{
				callback.uninitializeCallback();
			}
			catch(CallbackException e)
			{
				throw new JobException("Measurement callback did throw an error while uninitialization.", e);
			}
		}	
	}
}
