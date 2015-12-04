/**
 * 
 */
package org.youscope.serverinterfaces;

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface providing information of the configuration and installed addons of the YouScope server.
 * @author Moritz Lang
 */
public interface YouScopeServerConfiguration extends Remote
{
	/**
	 * Returns a list of supported image formats.
	 * 
	 * @return List of image formats.
	 * @throws RemoteException
	 */
	String[] getSupportedImageFormats() throws RemoteException;

	/**
	 * Returns the IP address of the server.
	 * 
	 * @return IP address of server.
	 * @throws UnknownHostException
	 * @throws RemoteException
	 */
	byte[] getIP() throws UnknownHostException, RemoteException;

	/**
	 * Returns a list of all supported script engines of the server.
	 * 
	 * @return List of script engines.
	 * @throws RemoteException
	 */
	String[] getSupportedScriptEngines() throws RemoteException;

	/**
	 * Returns a remote addon provider implementing the given interface.
	 * This is a general purpose function designed to allow for specialized addon types. The interface of
	 * the addon has to be known by the client.
	 * If more than one addon implements the given interface, the first one found is returned. If no addon
	 * implements the interface, null is returned.
	 * See the description of the interface GeneralPurposeAddon for more details.
	 * @param <T> The interface of the addon.
	 * @param addonInterface The interface of the addon.
	 * @return An addon implementing the given interface, or null, if no addon could be found.
	 * @throws RemoteException
	 */
	public <T extends GeneralPurposeAddon> T getGeneralAddon(Class<T> addonInterface) throws RemoteException;

	/**
	 * Returns all remote addon providers implementing the given interface.
	 * This is a general purpose function designed to allow for specialized addon types. The interface of
	 * the addon has to be known by the client.
	 * If no addon
	 * implements the interface, an empty array is returned.
	 * See the description of the interface GeneralPurposeAddon for more details.
	 * @param <T> The interface of the addons.
	 * @param addonInterface The interface of the addons.
	 * @return An array of addons implementing the given interface, or an empty array, if no addon could be found.
	 * @throws RemoteException
	 */
	public <T extends GeneralPurposeAddon> T[] getGeneralAddons(Class<T> addonInterface) throws RemoteException;

	/**
	 * Returns an array of all general purpose addons known by the server.
	 * @return Array of general purpose addons, or an empty list, if no addon is known.
	 * @throws RemoteException
	 */
	public GeneralPurposeAddon[] getGeneralAddons() throws RemoteException;
}