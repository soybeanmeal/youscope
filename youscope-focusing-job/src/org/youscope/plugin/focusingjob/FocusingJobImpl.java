/**
 * 
 */
package org.youscope.plugin.focusingjob;

import java.rmi.RemoteException;

import org.youscope.common.measurement.ExecutionInformation;
import org.youscope.common.measurement.MeasurementContext;
import org.youscope.common.measurement.MeasurementRunningException;
import org.youscope.common.measurement.PositionInformation;
import org.youscope.common.measurement.job.JobAdapter;
import org.youscope.common.measurement.job.JobException;
import org.youscope.common.measurement.job.basicjobs.FocusingJob;
import org.youscope.common.microscope.Microscope;

/**
 * @author langmo
 */
class FocusingJobImpl extends JobAdapter implements FocusingJob
{

	/**
	 * Serial Version UID.
	 */
	private static final long	serialVersionUID	= -936997010448014858L;

	private double			position			= 0;

	private int				adjustmentTime		= 0;

	private boolean			relative			= true;

	private String			focusDevice			= null;

	public FocusingJobImpl(PositionInformation positionInformation) throws RemoteException
	{
		super(positionInformation);
	}

	@Override
	public double getPosition()
	{
		return position;
	}

	@Override
	public void setPosition(double position, boolean relative) throws MeasurementRunningException
	{
		assertRunning();
		FocusingJobImpl.this.position = position;
		FocusingJobImpl.this.relative = relative;
	}

	@Override
	public boolean isRelative()
	{
		return relative;
	}

	@Override
	public String getDefaultName()
	{
		String text = "";
		if(focusDevice == null)
			text += "defaultFocus";
		else
			text += focusDevice;
		text += ".focus";
		if(relative)
			text += "+=";
		else
			text += "=";
		text += Double.toString(position) + "um";
		return text;
	}

	@Override
	public void runJob(ExecutionInformation executionInformation, Microscope microscope, MeasurementContext measurementContext) throws JobException, InterruptedException, RemoteException
	{
		try
		{
			if(focusDevice == null)
			{
				if(relative)
					microscope.getFocusDevice().setRelativeFocusPosition(position);
				else
					microscope.getFocusDevice().setFocusPosition(position);
			}
			else
			{
				if(relative)
					microscope.getFocusDevice(focusDevice).setRelativeFocusPosition(position);
				else
					microscope.getFocusDevice(focusDevice).setFocusPosition(position);
			}
		}
		catch(Exception e)
		{
			throw new JobException("Could not set focus position.", e);
		}
		if(Thread.interrupted())
			throw new InterruptedException();
		Thread.sleep(adjustmentTime);
	}

	@Override
	public String getFocusDevice()
	{
		return focusDevice;
	}

	@Override
	public void setFocusDevice(String focusDevice) throws MeasurementRunningException
	{
		assertRunning();
		this.focusDevice = focusDevice;
	}

	@Override
	public int getFocusAdjustmentTime()
	{
		return adjustmentTime;
	}

	@Override
	public void setFocusAdjustmentTime(int adjustmentTime) throws MeasurementRunningException
	{
		assertRunning();
		this.adjustmentTime = adjustmentTime;
	}
}