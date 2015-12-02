package ch.ethz.csb.youscope.addon.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ch.ethz.csb.youscope.client.addon.YouScopeFrame;
import ch.ethz.csb.youscope.client.uielements.DynamicPanel;

class MiscConfigurationPanel extends DynamicPanel
{

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 3734765053405836997L;
	private final JLabel								controllerTableLabel				= new JLabel("Controller-table file name (without extension):");
	private final JTextField							controllerTableField				= new JTextField();
	private final JCheckBox								saveControllerTableField			= new JCheckBox("Save controller-table", true);
    
	MiscConfigurationPanel(ControllerJobConfiguration controllerConfiguration, final YouScopeFrame frame)
	{
		saveControllerTableField.setOpaque(false);
		add(saveControllerTableField);
		add(controllerTableLabel);
		add(controllerTableField);
		addFillEmpty();
		saveControllerTableField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				boolean selected = saveControllerTableField.isSelected();
				
				controllerTableLabel.setVisible(selected);
				controllerTableField.setVisible(selected);
				frame.pack();
				
			}
		});
		
		// load settings
		String controllerTableName = controllerConfiguration.getControllerTableSaveName();
		if(controllerTableName == null)
		{
			controllerTableField.setText("controller-table");
			saveControllerTableField.setSelected(false);
			controllerTableField.setVisible(false);
			controllerTableLabel.setVisible(false);
		}
		else
		{
			if (controllerTableName.length() < 1)
			{
				controllerTableName = "controller-table";
			}
			controllerTableField.setText(controllerTableName);
			saveControllerTableField.setSelected(true);
			controllerTableField.setVisible(true);
			controllerTableLabel.setVisible(true);
		}
	}
	
	void commitChanges(ControllerJobConfiguration configuration)
	{
		configuration.setControllerTableSaveName(saveControllerTableField.isSelected() ? controllerTableField.getText() : null);
	}
}
