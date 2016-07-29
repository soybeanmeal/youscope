/**
 * 
 */
package org.youscope.plugin.shareexecution;

import java.rmi.RemoteException;

import org.youscope.common.job.EditableJobContainer;
import org.youscope.common.job.Job;
import org.youscope.common.measurement.MeasurementRunningException;


/**
 * A job which executes its child job only for a certain share of all positions per execution
 * 
 * @author Moritz Lang
 */
public interface ShareExecutionJob extends Job, EditableJobContainer
{
	/**
	 * Returns the number of times this job gets totally executed per iteration.
	 * @return share of executions per iteration.
	 * @throws RemoteException 
	 */
	public int getNumShare() throws RemoteException;

	/**
	 * Sets the number of times this job gets totally executed per iteration.
	 * @param numShare share of executions per iteration.
	 * @throws RemoteException 
	 * @throws MeasurementRunningException 
	 */
	public void setNumShare(int numShare) throws RemoteException, MeasurementRunningException;


	/**
	 * Returns the ID for this share job. Share jobs with different IDs act independently from one another.
	 * @return share ID of this job.
	 * @throws RemoteException 
	 */
	public int getShareID() throws RemoteException;


	/**
	 * Sets the ID for this share job. Share jobs with different IDs act independently from one another.
	 * @param shareID share ID of this job.
	 * @throws RemoteException 
	 * @throws MeasurementRunningException 
	 */
	public void setShareID(int shareID) throws RemoteException, MeasurementRunningException;
	
	/**
	 * Returns true if for each well the share of the jobs which get executed is determined separately.
	 * @return True if different counting for each well.
	 * @throws RemoteException 
	 */
	public boolean isSeparateForEachWell() throws RemoteException;
	

	/**
	 * If set to true, for each well the share of the jobs which get executed is determined separately.
	 * @param separateForEachWell True if different counting for each well.
	 * @throws RemoteException 
	 * @throws MeasurementRunningException 
	 */
	public void setSeparateForEachWell(boolean separateForEachWell) throws RemoteException, MeasurementRunningException;
	

}
