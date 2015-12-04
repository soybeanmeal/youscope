/**
 * 
 */
package org.youscope.addon.dropletmicrofluidics;

import java.rmi.RemoteException;

import org.youscope.common.measurement.ExecutionInformation;
import org.youscope.common.measurement.MeasurementContext;
import org.youscope.common.measurement.resource.Resource;
import org.youscope.common.measurement.resource.ResourceException;

/**
 * Abstract class all controllers for droplet based microfluidics should implement. 
 * @author Moritz Lang
 *
 */
public interface DropletControllerResource  extends Resource 
{
	/**
	 * Runs the controller and returns the flow rates of the flow units (i.e. the controller's output).
	 * @param executionInformation Current execution information.
	 * @param meanDropletOffset Mean height offset of the droplet (as obtained by observer).
	 * @param measurementContext The Measurement context.
	 * @return Flow rate of the flow units.
	 * @throws ResourceException
	 * @throws RemoteException
	 */
	public DropletControllerResult runController(ExecutionInformation executionInformation, MeasurementContext measurementContext, double meanDropletOffset) throws ResourceException, RemoteException;	
}