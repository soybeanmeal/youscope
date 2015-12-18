package org.youscope.common.saving;

import org.youscope.common.resource.ResourceConfiguration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Subclasses of this abstract class represent the configuration of a folder structure into which measurement data should be stored into.
 * @author Moritz Lang
 *
 */
@XStreamAlias("save-settings")
public abstract class SaveSettingsConfiguration extends ResourceConfiguration 
{

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -1725372967132338811L;
}
