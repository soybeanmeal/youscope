/**
 * 
 */
package ch.ethz.csb.youscope.addon.matlabfocusscores;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface to temporary save the focus score.
 * @author Moritz Lang
 *
 */
public interface FocusSink extends Remote
{
	/**
	 * Sets the focus score.
	 * @param score
	 * @throws RemoteException
	 */
	public void setScore(double score) throws RemoteException;
	
	/**
	 * Returns the last focus score.
	 * @return Last focus score, or -1, if yet not set.
	 * @throws RemoteException
	 */
	public double getLastScore() throws RemoteException;
}
