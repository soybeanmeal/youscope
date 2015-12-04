/**
 * 
 */
package org.youscope.plugin.lifecelldetection;

import java.awt.Component;

import javax.swing.JTabbedPane;

import org.youscope.addon.AddonException;
import org.youscope.addon.component.ComponentAddonUIAdapter;
import org.youscope.addon.component.ComponentMetadataAdapter;
import org.youscope.clientinterfaces.YouScopeClient;
import org.youscope.serverinterfaces.YouScopeServer;

/**
 * @author langmo
 */
public class CellDetectionJobConfigurationAddon extends ComponentAddonUIAdapter<CellDetectionJobConfiguration>
{	
	private InputImageConfigurationPanel inputImageConfigurationPanel;
	private DetectionAlgorithmConfigurationPanel detectionAlgorithmConfigurationPanel;
	private VisualizationAlgorithmConfigurationPanel visualizationAlgorithmConfigurationPanel;
	private QuantificationPanel quantificationPanel;
	private MiscPanel miscPanel;
	
	/**
	 * Constructor.
	 * @param client Interface to the client.
	 * @param server Interface to the server.
	 * @throws AddonException 
	 */
	public CellDetectionJobConfigurationAddon(YouScopeClient client, YouScopeServer server) throws AddonException
	{
		super(getMetadata(), client, server);
	}
	
	static ComponentMetadataAdapter<CellDetectionJobConfiguration> getMetadata()
	{
		return new ComponentMetadataAdapter<CellDetectionJobConfiguration>(CellDetectionJobConfiguration.TYPE_IDENTIFIER, 
				CellDetectionJobConfiguration.class, 
				CellDetectionJob.class, "Cell-Detection", new String[]{"feedback"}, "icons/smiley-mr-green.png");
	}
    
	@Override
	protected Component createUI(CellDetectionJobConfiguration configuration) throws AddonException 
	{
		setTitle("Life Cell Detection");
		setResizable(true);
		setMaximizable(false);
		
		inputImageConfigurationPanel = new InputImageConfigurationPanel(configuration, getClient(), getContainingFrame());
		detectionAlgorithmConfigurationPanel = new DetectionAlgorithmConfigurationPanel(configuration, getClient(), getContainingFrame());
		visualizationAlgorithmConfigurationPanel = new VisualizationAlgorithmConfigurationPanel(configuration, getClient(), getContainingFrame());
		quantificationPanel = new QuantificationPanel(configuration, getClient(), getServer(), getContainingFrame());
		miscPanel = new MiscPanel(configuration, getContainingFrame());
		
		JTabbedPane contentPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.addTab("Detection Image", inputImageConfigurationPanel);
        contentPane.addTab("Quantification Images", quantificationPanel);
        contentPane.addTab("Detection Algorithm", detectionAlgorithmConfigurationPanel);
        contentPane.addTab("Visualization Algorithm", visualizationAlgorithmConfigurationPanel);
        contentPane.addTab("Misc", miscPanel);
        
		return contentPane;
    }

	@Override
	protected void commitChanges(CellDetectionJobConfiguration configuration) {
		
		inputImageConfigurationPanel.commitChanges(configuration);
		detectionAlgorithmConfigurationPanel.commitChanges(configuration);
		visualizationAlgorithmConfigurationPanel.commitChanges(configuration);
		quantificationPanel.commitChanges(configuration);
		miscPanel.commitChanges(configuration);
	}
}