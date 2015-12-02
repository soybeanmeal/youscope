/**
 * 
 */
package ch.ethz.csb.youscope.addon.measurementviewer;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/**
 * @author Moritz Lang
 *
 */
class LoadMeasurementGlassPane extends JPanel
{
	/**
	 * Serial Version UID.
	 */
	private static final long	serialVersionUID	= 2626130411854364971L;
	private final JLabel		messageLabel;
	private JProgressBar		waitbar				= null;
	private final int			messageWidth;
	private final int			messageHeight;

	LoadMeasurementGlassPane()
	{
		super(null);
		setOpaque(true);
		messageLabel = new JLabel("<html><center><b>Loading measurement data...</b><br />This may take several seconds.</center></html>", SwingConstants.CENTER);
		messageWidth = messageLabel.getPreferredSize().width;
		messageHeight = messageLabel.getPreferredSize().height;
		messageLabel.setSize(messageWidth, messageHeight);
		add(messageLabel);
		waitbar = new JProgressBar();
		waitbar.setSize(messageWidth, 20);
		add(waitbar);
	}

	public void setLoading(boolean loading)
	{
		waitbar.setIndeterminate(loading);
	}

	@Override
	public void paintComponent(Graphics grp)
	{
		grp.setColor(new Color(1.0F, 1.0F, 1.0F, 0.7F));
		grp.fillRect(0, 0, getWidth(), getHeight());

		int width = getWidth();
		int height = getHeight();

		messageLabel.setLocation((width - messageWidth) / 2, height / 2 - messageHeight - 2);
		waitbar.setLocation((width - messageWidth) / 2, height / 2 + 2);
		
		super.paintComponent(grp);
	}
}
