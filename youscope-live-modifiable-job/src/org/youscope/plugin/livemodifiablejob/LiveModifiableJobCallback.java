/**
 * 
 */
package org.youscope.plugin.livemodifiablejob;

import java.rmi.RemoteException;

import org.youscope.common.measurement.callback.Callback;
import org.youscope.common.measurement.callback.CallbackException;

/**
 * Callback for registering jobs which can be live modified.
 * 
 * @author Moritz Lang
 */
public interface LiveModifiableJobCallback extends Callback
{
	
	/**
	 * type identifier of callback.
	 */
	public static final String TYPE_IDENTIFIER = "CSB::LiveModifiableJob::Callback";
    /**
     * Registers a job to be modifiable by the UI.
     * 
     * @param job job to be registered
     * @throws RemoteException
     * @throws CallbackException 
     */
    public void registerJob(LiveModifiableJob job) throws RemoteException, CallbackException;
    
    /**
     * Registers a job to be modifiable by the UI.
     * 
     * @param job job to be unregistered
     * @throws RemoteException
     * @throws CallbackException 
     */
    public void unregisterJob(LiveModifiableJob job) throws RemoteException, CallbackException;
}