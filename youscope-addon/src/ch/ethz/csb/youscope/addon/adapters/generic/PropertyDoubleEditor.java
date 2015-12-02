package ch.ethz.csb.youscope.addon.adapters.generic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

import ch.ethz.csb.youscope.client.addon.YouScopeClient;
import ch.ethz.csb.youscope.shared.YouScopeServer;
import ch.ethz.csb.youscope.shared.configuration.Configuration;

/**
 * An editor for double properties of a configuration.
 * @author Moritz Lang
 *
 */
public class PropertyDoubleEditor extends PropertyEditorAdapter
{

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 8526577289748471851L;
	private final JFormattedTextField field;
	/**
	 * Constructor.
	 * @param property The property which should be edited.
	 * @param configuration The configuration which should be edited.
	 * @param client YouScope client
	 * @param server YouScope server.
	 * @throws GenericException
	 */
	public PropertyDoubleEditor(Property property, Configuration configuration, YouScopeClient client, YouScopeServer server) throws GenericException
	{
		super(property, configuration, client, server, Double.class, double.class);
		field = new JFormattedTextField(new NumberFormatter(getDoubleFormat()));
		field.setValue(getValue(Double.class));
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
	private static NumberFormat getDoubleFormat()
	{
		NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
		format.setMinimumFractionDigits(0);
		format.setMaximumFractionDigits(20);
		if(format instanceof DecimalFormat)
		{
			((DecimalFormat)format).setDecimalSeparatorAlwaysShown(true);
		}
		format.setGroupingUsed(false);
		return format;
	}
	
	@Override
	public void commitEdits() throws GenericException 
	{
		setValue(((Number)field.getValue()).doubleValue());
	}

}
