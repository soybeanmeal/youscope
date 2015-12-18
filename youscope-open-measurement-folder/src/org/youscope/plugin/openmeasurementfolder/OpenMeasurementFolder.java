/**
 * 
 */
package org.youscope.plugin.openmeasurementfolder;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.youscope.addon.AddonException;
import org.youscope.addon.ConfigurationManagement;
import org.youscope.addon.tool.ToolAddonUIAdapter;
import org.youscope.addon.tool.ToolMetadata;
import org.youscope.addon.tool.ToolMetadataAdapter;
import org.youscope.clientinterfaces.YouScopeClient;
import org.youscope.common.measurement.MeasurementConfiguration;
import org.youscope.serverinterfaces.YouScopeServer;
import org.youscope.uielements.StandardFormats;

/**
 * @author Moritz Lang
 *
 */
class OpenMeasurementFolder extends ToolAddonUIAdapter
{
	public static final String TYPE_IDENTIFIER = "YouScope.OpenMeasurementFolder";
	
	private final JTextField measurementIDField = new JTextField();
	private final JTextField measurementFolderField = new JTextField();
	
	private JButton openMeasurementButton = new JButton("Open Folder");
	
	private final static GridBagConstraints newLineCnstr = StandardFormats.getNewLineConstraint();

	private final String measurementFolder;
	
	static ToolMetadata getMetadata()
	{
		return new ToolMetadataAdapter(TYPE_IDENTIFIER, "Open Folder", new String[0]);
	}
	
	/**
	 * Constructor.
	 * @param client
	 * @param server
	 * @param measurementFolder
	 * @throws AddonException 
	 */
	OpenMeasurementFolder(YouScopeClient client, YouScopeServer server, String measurementFolder) throws AddonException
	{
		super(getMetadata(), client, server);
		this.measurementFolder = measurementFolder;
	}
	@Override
	public java.awt.Component createUI() throws AddonException
	{
		// Initialize fields.
		String measurementName;
		boolean localServer;
		measurementFolderField.setText(measurementFolder);
		try
		{
			MeasurementConfiguration configuration = (MeasurementConfiguration) ConfigurationManagement.loadConfiguration(measurementFolder + File.separator + "configuration.csb");
			measurementName = configuration.getName();
			measurementIDField.setText(measurementName);
			localServer = getClient().isLocalServer();
		}
		catch(Throwable e)
		{
			throw new AddonException("Could not get measurement information. Leaving respective fields empty.",e);
		}
		
		// Measurement identification
		final GridBagLayout elementsLayout = new GridBagLayout();
		JPanel elementsPanel = new JPanel(elementsLayout);
		if(!localServer)
		{
			JEditorPane descriptionPane = new JEditorPane();
			descriptionPane.setEditable(false);
			descriptionPane.setContentType("text/html");
			descriptionPane.setText("<html><p style=\"font-size:small;margin-top:0px;\"><b>Measurement folder located at YouScope server.</b></p>" +
					"<p style=\"font-size:small;margin-top:4px;margin-bottom:0px\">The requested measurement "+measurementName+" is located at the YouScope server.<br />Please open the respective folder directly on the server side,<br />or using a remote desktop/shell.</p>" +
					"</html>");
			JScrollPane descriptionScrollPane = new JScrollPane(descriptionPane);
			StandardFormats.addGridBagElement(descriptionScrollPane, elementsLayout, newLineCnstr, elementsPanel);
		}
		else
		{
			JEditorPane descriptionPane = new JEditorPane();
			descriptionPane.setEditable(false);
			descriptionPane.setContentType("text/html");
			descriptionPane.setText("<html><p style=\"font-size:small;margin-top:0px;\"><b>Folder containin measurement data is opening.</b></p>" +
					"<p style=\"font-size:small;margin-top:4px;margin-bottom:0px\">The requested folder should open automatically.<br />If it is not opening, press the button again,<br />or manually surf to the respective path.<br />You can close this window.</p>" +
					"</html>");
			JScrollPane descriptionScrollPane = new JScrollPane(descriptionPane);
			StandardFormats.addGridBagElement(descriptionScrollPane, elementsLayout, newLineCnstr, elementsPanel);
		}
		StandardFormats.addGridBagElement(new JLabel("Measurement Name:"), elementsLayout, newLineCnstr, elementsPanel);
		measurementIDField.setEditable(false);
		StandardFormats.addGridBagElement(measurementIDField, elementsLayout, newLineCnstr, elementsPanel);
		StandardFormats.addGridBagElement(new JLabel("Measurement Folder:"), elementsLayout, newLineCnstr, elementsPanel);
		measurementFolderField.setEditable(false);
		StandardFormats.addGridBagElement(measurementFolderField, elementsLayout, newLineCnstr, elementsPanel);
		
			
		openMeasurementButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				openFolder();
			}
		});
		
		// Set frame properties
		if(localServer)
			setTitle("Folder is opening");
		else
			setTitle("Cannot open folder");
		setResizable(false);
		setMaximizable(false);
		
		// Create content pane
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(elementsPanel, BorderLayout.CENTER);
		if(localServer)
			contentPane.add(openMeasurementButton, BorderLayout.SOUTH);
		
		(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// open folder with a minimal delay.
				try
				{
					Thread.sleep(100);
				}
				catch(@SuppressWarnings("unused") InterruptedException e)
				{
					// do nothing.
				}
				openFolder();
			}
		})).start();
		
		return contentPane;
	}
	
	private void openFolder()
	{
		try
		{
			Desktop.getDesktop().open(new File(measurementFolder));
			closeAddon();
		}
		catch(IOException e1)
		{
			sendErrorMessage("Could not open folder " + measurementFolder + ".", e1);
		}
	}
}
