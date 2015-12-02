/**
 * 
 */
package ch.ethz.csb.youscope.addon.usercontrolmeasurement;

import java.rmi.RemoteException;

import ch.ethz.csb.youscope.shared.ImageEvent;
import ch.ethz.csb.youscope.shared.measurement.callback.Callback;

/**
 * Simple measurement callback to show a message and wait for the user to acknowledge it.
 * @author Moritz Lang
 *
 */
public interface UserControlMeasurementCallback extends Callback
{
	/**
	 * Type identifier of callback.
	 */
	public static final String TYPE_IDENTIFIER = "CSB::UserControlMeasurement::Callback";
	/**
	 * Adds a listener to the user control callback which gets informed upon user interaction.
	 * @param listener Listener to add.
	 * @throws RemoteException 
	 */
	public void addCallbackListener(UserControlMeasurementCallbackListener listener) throws RemoteException;
	
	/**
	 * Removes a previously added listener.
	 * @param listener Listener to remove.
	 * @throws RemoteException
	 */
	public void removeCallbackListener(UserControlMeasurementCallbackListener listener) throws RemoteException;
	
	/**
	 * Returns the currently selected channel, or null.
	 * @return Currently selected channel.
	 * @throws RemoteException
	 */
	public String getCurrentChannel() throws RemoteException;
	
	/**
	 * Returns the currently selected channel group, or null.
	 * @return Currently selected channel group.
	 * @throws RemoteException
	 */
	public String getCurrentChannelGroup() throws RemoteException;
	
	/**
	 * Returns the currently selected exposure in ms.
	 * @return Exposure in ms.
	 * @throws RemoteException
	 */
	public double getCurrentExposure() throws RemoteException;
	
	/**
	 * Notifies the callback that a new image was made. The image should be displayed to the user.
	 * @param e The new image.
	 * @throws RemoteException
	 */
	public void newImage(ImageEvent e) throws RemoteException;
	
	/**
	 * Notifies the callback that the last image was saved to disk. The callback should give some feedback to the user.
	 * @throws RemoteException
	 */
	public void snappedImage() throws RemoteException;
}
