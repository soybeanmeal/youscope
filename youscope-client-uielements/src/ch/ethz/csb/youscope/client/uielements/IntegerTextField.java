/**
 * 
 */
package ch.ethz.csb.youscope.client.uielements;

import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

/**
 * @author Moritz Lang
 * A simple text field accepting integer values (and nothing else). Integers are formatted in the YouScope typical way.
 * Lower and upper bounds for the integer values can be set.
 */
public class IntegerTextField extends JFormattedTextField
{
	/**
	 * Serial Version UID.
	 */
	private static final long	serialVersionUID	= -4462393850446518462L;
	private final NumberFormatter numberFormatter;
	/**
	 * Default constructor.
	 */
	public IntegerTextField()
	{
		super(new NumberFormatter(StandardFormats.getIntegerFormat()));
		this.numberFormatter = (NumberFormatter)getFormatter();
	}
	/**
	 * Constructor which sets the initial value.
	 * @param value Initial value.
	 */
	public IntegerTextField(int value)
	{
		this();
		this.setValue(value);
	}
	@Override
	public Integer getValue()
	{
		Object value = super.getValue();
		if(value == null)
			return 0;
		return ((Number)value).intValue();
	}
	/**
	 * Sets the minimal allowed value.
	 * @param value
	 */
	public void setMinimalValue(int value)
	{
		numberFormatter.setMinimum(value);
		setFormatter(numberFormatter);
	}
	/**
	 * Sets the maximal allowed value.
	 * @param value
	 */
	public void setMaximalValue(int value)
	{
		numberFormatter.setMaximum(value);
		setFormatter(numberFormatter);
	}
}
