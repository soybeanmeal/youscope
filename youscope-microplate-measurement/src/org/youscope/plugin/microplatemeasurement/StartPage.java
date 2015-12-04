/**
 * 
 */
package org.youscope.plugin.microplatemeasurement;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.youscope.addon.measurement.MeasurementAddonUIPage;
import org.youscope.clientinterfaces.YouScopeFrame;
import org.youscope.uielements.ImageLoadingTools;

/**
 * @author langmo
 *
 */
class StartPage extends MeasurementAddonUIPage<MicroplateMeasurementConfigurationDTO>
{

	/**
	 * Serial Version UID.
	 */
	private static final long	serialVersionUID	= -5407788842391715831L;

	@Override
	public void loadData(MicroplateMeasurementConfigurationDTO configuration)
	{
		// Do nothing.
	}

	@Override
	public boolean saveData(MicroplateMeasurementConfigurationDTO configuration)
	{
		// Do nothing.
		return true;
	}

	@Override
	public void setToDefault(MicroplateMeasurementConfigurationDTO configuration)
	{
		// Do nothing.
	}

	@Override
	public String getPageName()
	{
		return "Description";
	}

	@Override
	public void createUI(YouScopeFrame frame)
	{
		setLayout(new BorderLayout(5, 5));
		setOpaque(false);
		
		JEditorPane descriptionPane = new JEditorPane();
		descriptionPane.setEditable(false);
		descriptionPane.setContentType("text/html");
		descriptionPane.setText("<html><p style=\"font-size:small;margin-top:0px;\"><b>Description:</b></p>" +
				"<p style=\"font-size:small;margin-top:4px;margin-bottom:0px\">A microplate measurement helps to perform an identical imaging protocol for several wells and/or positions in a microplate (see Figure 1).</p>" +
				"<p style=\"font-size:small;margin-top:4px;margin-bottom:0px\">To configure such a measurement, the wells to be measured are selected and combined with the imaging protocol consisting of several subelements, called jobs.</p>" +
				"<p style=\"font-size:small;margin-top:4px;margin-bottom:0px\">One job thereby corresponds to a single step of the imaging protocol, like taking a bright-field or a green fluorescence image.</p></html>");
		JScrollPane descriptionScrollPane = new JScrollPane(descriptionPane);
		descriptionScrollPane.setPreferredSize(new Dimension(250, 200));
		add(descriptionScrollPane, BorderLayout.CENTER);
		
		// Descriptive image
		ImageIcon microplateMeasurementIcon = ImageLoadingTools.getResourceIcon("org/youscope/plugin/microplatemeasurement/images/microplate-measurement.jpg", "Microplate Measurement");
		if(microplateMeasurementIcon != null)
		{
			JPanel imagePanel = new JPanel(new BorderLayout());
			imagePanel.setOpaque(false);
			JLabel imageLabel = new JLabel(microplateMeasurementIcon, SwingConstants.LEFT);
			imageLabel.setBackground(Color.WHITE);
			imageLabel.setOpaque(true);
			imageLabel.setBorder(new LineBorder(Color.BLACK, 1));
			JLabel legendLabel = new JLabel("<html><b>Figure 1:</b> Flowchart of a microplate measurement.</html>", SwingConstants.LEFT);
			imagePanel.add(imageLabel, BorderLayout.CENTER);
			imagePanel.add(legendLabel, BorderLayout.SOUTH);
			add(imagePanel, BorderLayout.WEST);
		}
	}

}