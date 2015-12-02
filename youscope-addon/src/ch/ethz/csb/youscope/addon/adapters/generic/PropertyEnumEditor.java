package ch.ethz.csb.youscope.addon.adapters.generic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ch.ethz.csb.youscope.client.addon.YouScopeClient;
import ch.ethz.csb.youscope.shared.YouScopeServer;
import ch.ethz.csb.youscope.shared.configuration.Configuration;

/**
 * An editor for enum properties of a configuration.
 * @author Moritz Lang
 *
 */
public class PropertyEnumEditor extends PropertyEditorAdapter
{

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -8734624486559686392L;

	private final JComboBox<Object> field = new JComboBox<Object>();
	/**
	 * Constructor.
	 * @param property The property which should be edited.
	 * @param configuration The configuration which should be edited.
	 * @param client YouScope client
	 * @param server YouScope server.
	 * @throws GenericException
	 */
	public PropertyEnumEditor(Property property, Configuration configuration, YouScopeClient client, YouScopeServer server) throws GenericException
	{
		super(property, configuration, client, server, Enum.class);
		for(Object constant : property.getType().getEnumConstants())
		{
			field.addItem(constant);
		}
		field.setSelectedItem(getValue());
		addLabel();
		add(field);
		field.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					commitEdits();
					notifyPropertyValueChanged();
				} 
				catch (GenericException e1) 
				{
					sendErrorMessage("Could not set value of property " + getProperty().getName() + ".", e1);
				}
			}
		});
	}
	
	@Override
	public void commitEdits() throws GenericException 
	{
		setValue(field.getSelectedItem());
	}

}
