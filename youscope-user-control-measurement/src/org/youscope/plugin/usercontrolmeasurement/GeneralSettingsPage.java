package org.youscope.plugin.usercontrolmeasurement;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.youscope.addon.AddonException;
import org.youscope.addon.measurement.MeasurementAddonUIPage;
import org.youscope.clientinterfaces.YouScopeClient;
import org.youscope.clientinterfaces.YouScopeFrame;
import org.youscope.common.saving.SaveSettingsConfiguration;
import org.youscope.uielements.StandardFormats;
import org.youscope.uielements.SubConfigurationPanel;

class GeneralSettingsPage extends MeasurementAddonUIPage<UserControlMeasurementConfiguration>
{

	/**
	 * Serial Verision UID.
	 */
	private static final long				serialVersionUID		= 885352612109223078L;

	private final YouScopeClient	client;

	private JTextField						nameField				= new JTextField("unnamed");

	private SubConfigurationPanel<SaveSettingsConfiguration> saveSettingPanel = null;
	
	GeneralSettingsPage(YouScopeClient client)
	{
		this.client = client;
	}

	@Override
	public void loadData(UserControlMeasurementConfiguration configuration)
	{
		nameField.setText(configuration.getName());
		saveSettingPanel.setConfiguration(configuration.getSaveSettings());
	}

	@Override
	public boolean saveData(UserControlMeasurementConfiguration configuration)
	{
		
		configuration.setName(nameField.getText());
		configuration.setSaveSettings(saveSettingPanel.getConfiguration());

		return true;
	}

	@Override
	public void setToDefault(UserControlMeasurementConfiguration configuration)
	{
		try {
			configuration.setSaveSettings(client.getAddonProvider().getComponentMetadata("YouScope.StandardSaveSettings", SaveSettingsConfiguration.class).getConfigurationClass().newInstance());
		} catch (@SuppressWarnings("unused") InstantiationException | IllegalAccessException | AddonException e) {
			// do nothing.
		}
	}

	@Override
	public String getPageName()
	{
		return "Measurement Properties";
	}

	@Override
	public void createUI(YouScopeFrame frame)
	{
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints newLineConstr = StandardFormats.getNewLineConstraint();
		GridBagConstraints bottomConstr = StandardFormats.getBottomContstraint();
		
		StandardFormats.addGridBagElement(new JLabel("Name:"), layout, newLineConstr, this);
		StandardFormats.addGridBagElement(nameField, layout, newLineConstr, this);

		// Panel to choose save settings
		saveSettingPanel = new SubConfigurationPanel<SaveSettingsConfiguration>("Save type:", null, SaveSettingsConfiguration.class, client, frame);
		StandardFormats.addGridBagElement(saveSettingPanel, layout, bottomConstr, this);
		setBorder(new TitledBorder("Measurement Properties"));
	}
}
