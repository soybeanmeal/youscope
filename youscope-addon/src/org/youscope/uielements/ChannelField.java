package org.youscope.uielements;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.youscope.clientinterfaces.YouScopeClient;
import org.youscope.clientinterfaces.YouScopeProperties;
import org.youscope.common.configuration.ChannelConfiguration;
import org.youscope.common.microscope.Channel;
import org.youscope.serverinterfaces.YouScopeServer;


/**
 * UI element to select a channel and channel group.
 * @author Moritz Lang
 *
 */
public class ChannelField extends JPanel
{

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -3742711668440764856L;

	private final JComboBox<String>								channelGroupField		= new JComboBox<String>();

	private final JComboBox<String>								channelField			= new JComboBox<String>();
    private final YouScopeClient client;
    private final YouScopeServer server;
    
    /**
     * Constructor.
     * @param client Interface to the client.
     * @param server Interface to the server.
     */
    public ChannelField(YouScopeClient client, YouScopeServer server)
    {
    	this(client.getProperties().getProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL_GROUP, (String)null), client.getProperties().getProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL, (String)null), client, server);
    }
    
    /**
     * Constructor.
     * @param channelConfiguration Selected channel,or null.
     * @param client Interface to the client.
     * @param server Interface to the server.
     */
    public ChannelField(ChannelConfiguration channelConfiguration, YouScopeClient client, YouScopeServer server)
    {
    	this(channelConfiguration == null ? client.getProperties().getProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL_GROUP, (String)null) : channelConfiguration.getChannelGroup(),
    			channelConfiguration == null ? client.getProperties().getProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL, (String)null) : channelConfiguration.getChannel(),
    					client, server);
    }
    
    
    /**
     * Constructor.
     * @param channelGroup Selected channel group.
     * @param channel Selected channel.
     * @param client Interface to the client.
     * @param server Interface to the server.
     */
    public ChannelField(String channelGroup, String channel, YouScopeClient client, YouScopeServer server)
    {
    	this.client = client;
    	this.server = server;
    
    	channelGroupField.setOpaque(false);
    	channelField.setOpaque(false);
    	
    	loadChannelGroups();
    	if(channelGroup == null)
    		channelGroup = getLastChannelGroup();
    	if(channelGroup != null)
    	{
    		channelGroupField.setSelectedItem(channelGroup);
    	}
    	loadChannels();
    	if(channel == null)
    		channel = getLastChannel();
    	if(channel != null)
    	{
    		channelField.setSelectedItem(channel);
    	}
    	channelGroupField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				loadChannels();
				setLastChannelGroup(getChannelGroup());
				
			}
		});
    	channelField.addActionListener(new ActionListener()
    			{
					@Override
					public void actionPerformed(ActionEvent e) {
						setLastChannel(getChannel());
					}
    			});
    	if(channelGroupField.getItemCount() > 1)
    	{
    		setLayout(new GridLayout(1,2,5,0));
	    	add(channelGroupField);
	    	add(channelField);
    	}
    	else
    	{
    		setLayout(new BorderLayout());
    		add(channelField, BorderLayout.CENTER);
    	}
    	setOpaque(false);
    	
    	setLastChannelGroup(getChannelGroup());
    	setLastChannel(getChannel());
    }
    
    private String getLastChannelGroup()
    {
    	return client.getProperties().getProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL_GROUP, (String)null);
    }
    
    private String getLastChannel()
    {
    	return client.getProperties().getProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL, (String)null);
    }
    
    private void setLastChannel(String channel)
    {
    	if(channel != null)
    		client.getProperties().setProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL, channel);
    }
    
    private void setLastChannelGroup(String channelGroup)
    {
    	if(channelGroup != null)
    		client.getProperties().setProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL_GROUP, channelGroup);
    }
    
    /**
     * Returns the currently selected channel configuration.
     * @return Channel configuration.
     */
    public ChannelConfiguration getChannelConfiguration()
    {
    	ChannelConfiguration configuration = new ChannelConfiguration();
    	Object channel = channelField.getSelectedItem();
    	configuration.setChannel(channel==null?null:channel.toString());
    	Object channelGroup = channelGroupField.getSelectedItem();
    	configuration.setChannelGroup(channelGroup==null?null:channelGroup.toString());
    	return configuration;
    }
    
    /**
     * Returns the currently selected channel, or null if no channel can be selected since there are no channels.
     * @return Channel name or null.
     */
    public String getChannel()
    {
    	Object channel = channelField.getSelectedItem();
    	if(channel == null)
    		return null;
    	return channel.toString();
    }
    
    /**
     * Adds an action listener which gets notified when the channel selection changed.
     * @param listener Listener to add.
     */
    public void addActionListener(ActionListener listener)
    {
    	channelField.addActionListener(listener);
    }
    
    /**
     * Removes a previously added Action listener.
     * @param listener Listener to remove.
     */
    public void removeActionListener(ActionListener listener)
    {
    	channelField.removeActionListener(listener);
    }
    
    /**
     * Listener which gets notified one or two times when selected channel changes.
     * @param listener Listener to add.
     */
    public void addItemListener(ItemListener listener)
    {
    	channelField.addItemListener(listener);
    }
    
    /**
     * Removes a previously added listener.
     * @param listener Listener to remove.
     */
    public void removeItemListener(ItemListener listener)
    {
    	channelField.removeItemListener(listener);
    }
    
    /**
     * Sets the currently chosen channel
     * @param channelGroup The channel group.
     * @param channel The channel.
     */
    public void setChannel(String channelGroup, String channel)
    {
    	if(channelGroup != null)
    	{
    		channelGroupField.setSelectedItem(channelGroup);
    	}
    	if(channel != null)
    	{
    		channelField.setSelectedItem(channel);
    	}
    }
    
    /**
     * Sets the currently selected channel configuration.
     * @param channelConfiguration Channel configuration (channel group + channel).
     */
    public void setChannel(ChannelConfiguration channelConfiguration)
    {
    	if(channelConfiguration == null)
    		return;
    	setChannel(channelConfiguration.getChannelGroup(), channelConfiguration.getChannel());
    }
    
    /**
     * Returns the currently selected channel group, or null if no channel group can be selected since there are no channels.
     * @return Channel group name or null.
     */
    public String getChannelGroup()
    {
    	Object channelGroup = channelGroupField.getSelectedItem();
    	if(channelGroup == null)
    		return null;
    	return channelGroup.toString();
    }
    
    private void loadChannelGroups()
	{
		String[] channelGroupNames = null;
		try
		{
			channelGroupNames = server.getMicroscope().getChannelManager().getChannelGroupIDs();
		}
		catch(Exception e)
		{
			client.sendError("Could not obtain config group names.", e);
		}

		if(channelGroupNames == null || channelGroupNames.length <= 0)
		{
			channelGroupNames = new String[] {""};
		}

		channelGroupField.removeAllItems();
		for(String configGroupName : channelGroupNames)
		{
			channelGroupField.addItem(configGroupName);
		}
	}

	private void loadChannels()
	{
		String[] channelNames = null;

		Object selectedGroup = channelGroupField.getSelectedItem();
		if(selectedGroup != null && selectedGroup.toString().length() > 0)
		{
			try
			{
				Channel[] channels = server.getMicroscope().getChannelManager().getChannels(selectedGroup.toString()); 
				channelNames = new String[channels.length];
				for(int i=0; i<channels.length; i++)
				{
					channelNames[i] = channels[i].getChannelID();
				}
			}
			catch(Exception e)
			{
				client.sendError("Could not obtain channel names of microscope.", e);
			}
		}

		if(channelNames == null)
		{
			channelNames = new String[0];
		}

		channelField.removeAllItems();
		for(String channelName : channelNames)
		{
			channelField.addItem(channelName);
		}
	}
}