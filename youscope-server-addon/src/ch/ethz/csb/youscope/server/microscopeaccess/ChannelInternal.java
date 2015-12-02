/**
 * 
 */
package ch.ethz.csb.youscope.server.microscopeaccess;

import ch.ethz.csb.youscope.shared.microscope.DeviceException;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeException;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeLockedException;
import ch.ethz.csb.youscope.shared.microscope.SettingException;

/**
 * @author Moritz Lang
 *
 */
public interface ChannelInternal
{	
	/**
	 * Activates the channel, i.e. sets all device settings associated to the channel.
	 * Should be called before taking the first image in the channel.
	 * @param accessID The access ID of the current microscope object. If the microscope is locked with a different accessID, a MicroscopeLockedException is thrown.
	 * @throws MicroscopeLockedException
	 * @throws MicroscopeException
	 * @throws InterruptedException
	 * @throws SettingException
	 */
	void activateChannel(int accessID) throws MicroscopeLockedException, MicroscopeException, InterruptedException, SettingException;
	/**
	 * Deactivates the channel, i.e. sets all device settings associated to stopping imaging in the respective channel.
	 * Should be called after taking the last image in the channel.
	 * @param accessID The access ID of the current microscope object. If the microscope is locked with a different accessID, a MicroscopeLockedException is thrown.
	 * @throws MicroscopeLockedException
	 * @throws MicroscopeException
	 * @throws InterruptedException
	 * @throws SettingException
	 */
	void deactivateChannel(int accessID) throws MicroscopeLockedException, MicroscopeException, InterruptedException, SettingException;
	/**
	 * Opens the shutter associated to the channel.
	 * Should be called directly before taking an image in the channel.
	 * Might be called more than once if taking more than one image, but with a certain delay between images.
	 * The shutter should be closed as soon as possible to minimize bleaching and similar.
	 * If no shutter is defined for this channel, does nothing.
	 * @param accessID The access ID of the current microscope object. If the microscope is locked with a different accessID, a MicroscopeLockedException is thrown.
	 * @throws MicroscopeLockedException
	 * @throws MicroscopeException
	 * @throws InterruptedException
	 * @throws SettingException
	 * @throws DeviceException 
	 */
	void openShutter(int accessID) throws MicroscopeLockedException, MicroscopeException, InterruptedException, SettingException, DeviceException;
	/**
	 * Closes the shutter associated to the channel.
	 * Should be called directly after taking one or many images in the channel.
	 * Might be called more than once if taking more than one image, but with a certain delay between images.
	 * The shutter should be closed as soon as possible to minimize bleaching and similar.
	 * If no shutter is defined for this channel, does nothing.
	 * @param accessID The access ID of the current microscope object. If the microscope is locked with a different accessID, a MicroscopeLockedException is thrown.
	 * @throws MicroscopeLockedException
	 * @throws MicroscopeException
	 * @throws InterruptedException
	 * @throws SettingException
	 * @throws DeviceException 
	 */
	void closeShutter(int accessID) throws MicroscopeLockedException, MicroscopeException, InterruptedException, SettingException, DeviceException;
	
	
	/**
	 * Returns the ID of the channel group where this channel belongs to.
	 * @return ID of channel group.
	 */
	String getChannelGroupID();
	
	/**
	 * Returns the ID of this channel. Together with the channel group ID, this ID identifies this channel.
	 * @return ID of channel.
	 */
	String getChannelID();
}
