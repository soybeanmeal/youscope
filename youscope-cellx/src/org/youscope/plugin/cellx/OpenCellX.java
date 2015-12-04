/**
 * 
 */
package org.youscope.plugin.cellx;

import java.awt.BorderLayout;
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

import org.youscope.addon.postprocessing.PostProcessorAddon;
import org.youscope.clientinterfaces.YouScopeClient;
import org.youscope.clientinterfaces.YouScopeFrame;
import org.youscope.common.configuration.MeasurementConfiguration;
import org.youscope.common.tools.ConfigurationManagement;
import org.youscope.serverinterfaces.YouScopeServer;
import org.youscope.uielements.StandardFormats;

/**
 * @author Moritz Lang
 *
 */
class OpenCellX implements PostProcessorAddon
{
	public static final String ADDON_ID = "CSB::OpenCellX::1.0";
	
	private final YouScopeClient client;
	
	private final JTextField measurementIDField = new JTextField();
	private final JTextField measurementFolderField = new JTextField();
	
	private JButton openCellXButton = new JButton("Open CellX");
	
	private final static GridBagConstraints newLineCnstr = StandardFormats.getNewLineConstraint();

	private final String measurementFolder;
	
	/**
	 * Constructor.
	 * @param client
	 * @param server
	 * @param measurementFolder
	 */
	OpenCellX(YouScopeClient client, YouScopeServer server, String measurementFolder)
	{
		this.measurementFolder = measurementFolder;
		this.client = client;
	}
	@Override
	public void createUI(YouScopeFrame frame)
	{
		// Initialize fields.
		String measurementName;
		boolean localServer;
		measurementFolderField.setText(measurementFolder);
		try
		{
			MeasurementConfiguration configuration = ConfigurationManagement.loadConfiguration(measurementFolder + File.separator + "configuration.csb");
			measurementName = configuration.getName();
			measurementIDField.setText(measurementName);
			localServer = client.isLocalServer();
		}
		catch(Exception e)
		{
			frame.setToErrorState("Could not get measurement information. Leaving respective fields empty.",e);
			frame.pack();
			return;
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
			descriptionPane.setText("<html><p style=\"font-size:small;margin-top:0px;\"><b>CellX Gui is starting.</b></p>" +
					"<p style=\"font-size:small;margin-top:4px;margin-bottom:0px\">CellX is starting automatically.<br />This may take several seconds. Thank you for waiting!<br />You can close this window.</p>" +
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
		
			
		openCellXButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				openCellX();
			}
		});
		
		// Set frame properties
		if(localServer)
			frame.setTitle("CellX is starting");
		else
			frame.setTitle("Cannot start CellX");
		frame.setResizable(false);
		frame.setClosable(true);
		frame.setMaximizable(false);
		
		// Create content pane
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(elementsPanel, BorderLayout.CENTER);
		if(localServer)
			contentPane.add(openCellXButton, BorderLayout.SOUTH);
		frame.setContentPane(contentPane);
		frame.pack();
		
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
				openCellX();
			}
		})).start();
	}
	
	private void openCellX()
	{
		// Check if all necessary files exist
		File cellxDirectory = new File("cellx");
		if(!cellxDirectory.exists() || !cellxDirectory.isDirectory())
		{
			client.sendError("Directory " + cellxDirectory.getAbsolutePath() + " does not exist. Check the CellX addon installation for consistency.");
			return;
		}
		
		File cellxFile = new File(cellxDirectory, "CellXGui.jar");
		if(!cellxFile.exists() || !cellxFile.isFile())
		{
			client.sendError("CellX Gui " + cellxFile.getAbsolutePath() + " does not exist. Check the CellX addon installation for consistency.");
			return;
		}
    	try
		{
			Runtime.getRuntime().exec(new String[]{"javaw", "-jar", "CellXGui.jar"}, null, cellxDirectory);
		}
		catch(IOException e)
		{
			client.sendError("Error while starting CellX Gui (" + cellxFile.getAbsolutePath() + ").", e);
			return;
		}
	}
}