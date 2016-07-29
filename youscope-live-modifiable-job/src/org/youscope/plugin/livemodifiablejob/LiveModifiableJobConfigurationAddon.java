/**
 * 
 */
package org.youscope.plugin.livemodifiablejob;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.youscope.addon.AddonException;
import org.youscope.addon.component.ComponentAddonUIAdapter;
import org.youscope.addon.component.ComponentMetadataAdapter;
import org.youscope.clientinterfaces.YouScopeClient;
import org.youscope.serverinterfaces.YouScopeServer;
import org.youscope.uielements.DynamicPanel;
import org.youscope.uielements.JobsDefinitionPanel;

/**
 * @author Moritz Lang
 */
class LiveModifiableJobConfigurationAddon  extends ComponentAddonUIAdapter<LiveModifiableJobConfiguration>
{
    private JCheckBox enabledAtStartField = new JCheckBox("Enabled at startup", true);

    private JobsDefinitionPanel jobPanel;

    /**
	 * Constructor.
	 * @param client Interface to the client.
	 * @param server Interface to the server.
	 * @throws AddonException 
	 */
	public LiveModifiableJobConfigurationAddon(YouScopeClient client, YouScopeServer server) throws AddonException
	{
		super(getMetadata(),  client, server);
	}
    
	static ComponentMetadataAdapter<LiveModifiableJobConfiguration> getMetadata()
	{
		return new ComponentMetadataAdapter<LiveModifiableJobConfiguration>(LiveModifiableJobConfiguration.TYPE_IDENTIFIER, 
				LiveModifiableJobConfiguration.class, 
				LiveModifiableJob.class, 
				"Live Modifiable Job", 
				new String[]{"misc"},
				"icons/edit-diff.png");
	}

	@Override
	protected Component createUI(LiveModifiableJobConfiguration configuration) throws AddonException
    {
        setTitle("Live Modifiable Job");
        setResizable(true);
        setMaximizable(false);

        DynamicPanel mainPanel = new DynamicPanel();
        mainPanel.add(new JLabel("Executed jobs when enabled:"));
        jobPanel = new JobsDefinitionPanel(getClient(), getServer(), getContainingFrame());
        jobPanel.setJobs(configuration.getJobs());
        mainPanel.addFill(jobPanel);
        enabledAtStartField.setSelected(configuration.isEnabledAtStartup());
        mainPanel.add(enabledAtStartField);

        return mainPanel;
    }

	@Override
	protected void commitChanges(LiveModifiableJobConfiguration configuration) {
		configuration.setJobs(jobPanel.getJobs());
		configuration.setEnabledAtStartup(enabledAtStartField.isSelected());
		
	}

	@Override
	protected void initializeDefaultConfiguration(LiveModifiableJobConfiguration configuration) throws AddonException {
		// do nothing.
	}
}