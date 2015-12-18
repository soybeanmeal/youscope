/**
 * 
 */
package org.youscope.plugin.standardsavesettings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.youscope.addon.AddonException;
import org.youscope.addon.component.ComponentAddonUIAdapter;
import org.youscope.addon.component.ComponentMetadataAdapter;
import org.youscope.clientinterfaces.StandardProperty;
import org.youscope.clientinterfaces.YouScopeClient;
import org.youscope.serverinterfaces.YouScopeServer;
import org.youscope.uielements.DynamicPanel;
import org.youscope.common.saving.SaveSettings;

/**
 * @author Moritz Lang
 */
class StandardSaveSettingsUI extends ComponentAddonUIAdapter<StandardSaveSettingsConfiguration>
{
	private JTextField folderField = new JTextField();

	private JComboBox<String> imageTypeField;

	private JComboBox<FolderStructureType> imageFolderTypeField = new JComboBox<FolderStructureType>(FolderStructureType.values());

	private FileNameComboBox imageFileField = new FileNameComboBox(FileNameComboBox.Type.FILE_NAME);
	
    /**
	 * Constructor.
	 * @param client Interface to the client.
	 * @param server Interface to the server.
	 * @throws AddonException 
	 */
	public StandardSaveSettingsUI(YouScopeClient client, YouScopeServer server) throws AddonException
	{
		super(getMetadata(), client, server);
	}
	
	static ComponentMetadataAdapter<StandardSaveSettingsConfiguration> getMetadata()
	{
		return new ComponentMetadataAdapter<StandardSaveSettingsConfiguration>(StandardSaveSettingsConfiguration.TYPE_IDENTIFIER, 
				StandardSaveSettingsConfiguration.class, 
				SaveSettings.class, "Standard Save Settings", new String[]{"save settings"});
	}
	
	@Override
	protected Component createUI(StandardSaveSettingsConfiguration configuration) throws AddonException
	{
		setTitle("Standard save settings");
		setResizable(false);
		setMaximizable(false);
		
		// Get supported image types
		String[] imageTypes;
		try
		{
			imageTypes = getServer().getProperties().getSupportedImageFormats();
		}
		catch(RemoteException e1)
		{
			sendErrorMessage("Could not obtain supported image file types from server.", e1);
			imageTypes = new String[0];
		}
		imageTypeField = new JComboBox<String>(imageTypes);
		
		folderField.setText(configuration.getBaseFolder());
		imageFolderTypeField.setSelectedItem(configuration.getFolderStructureType());
		imageFileField.setSelectedItem(configuration.getImageFileName());
		imageTypeField.setSelectedItem(configuration.getImageFileType());
		
		DynamicPanel contentPane = new DynamicPanel();
		contentPane.add(new JLabel("Output Directory:"));
		JPanel folderPanel = new JPanel(new BorderLayout(5, 0));
		folderPanel.add(folderField, BorderLayout.CENTER);

		if(getClient().isLocalServer())
		{
			JButton openFolderChooser = new JButton("Edit");
			openFolderChooser.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					JFileChooser fileChooser = new JFileChooser(folderField.getText());
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int returnVal = fileChooser.showDialog(null, "Open");
					if(returnVal == JFileChooser.APPROVE_OPTION)
					{
						folderField.setText(fileChooser.getSelectedFile().getAbsolutePath());
					}
				}
			});
			folderPanel.add(openFolderChooser, BorderLayout.EAST);
		}
		contentPane.add(folderPanel);

		contentPane.add(new JLabel("Folder structure:"));
		contentPane.add(imageFolderTypeField);

		contentPane.add(new JLabel("Image filenames:"));
		contentPane.add(imageFileField);

		// Panel to choose image file type
		contentPane.add(new JLabel("Image File Type:"));
		contentPane.add(imageTypeField);
		
		
		return contentPane;
    }

    @Override
	protected void commitChanges(StandardSaveSettingsConfiguration configuration)
    {   
    	configuration.setBaseFolder(folderField.getText());
    	configuration.setImageFileType((String) imageTypeField.getSelectedItem());
    	configuration.setFolderStructureType((FolderStructureType) imageFolderTypeField.getSelectedItem());
    	configuration.setImageFileName(imageFileField.getSelectedItem().toString());
    	
    	getClient().getProperties().setProperty(StandardProperty.PROPERTY_LAST_MEASUREMENT_SAVE_FOLDER, configuration.getBaseFolder());
    }

	@Override
	protected void initializeDefaultConfiguration(StandardSaveSettingsConfiguration configuration) throws AddonException 
	{
		String lastFolder = (String) getClient().getProperties().getProperty(StandardProperty.PROPERTY_LAST_MEASUREMENT_SAVE_FOLDER);
		configuration.setBaseFolder(lastFolder == null ? "" : lastFolder);
		configuration.setImageFileName(FileNameComboBox.PRE_DEFINED_FILE_NAMES[0][0]);
		configuration.setFolderStructureType(FolderStructureType.ALL_IN_ONE_FOLDER);
		configuration.setImageFileType("tif");
	}
}
