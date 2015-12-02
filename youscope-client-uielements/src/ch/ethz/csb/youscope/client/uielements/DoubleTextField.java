/**
 * 
 */
package ch.ethz.csb.youscope.client.uielements;

import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

/**
 * @author Moritz Lang
 *
 */
public class DoubleTextField extends JFormattedTextField
{
	/**
	 * Serial Version UID.
	 */
	private static final long	serialVersionUID	= -4462391850446518462L;
	private final NumberFormatter numberFormatter;
	/**
	 * Default constructor.
	 */
	public DoubleTextField()
	{
		super(new NumberFormatter(StandardFormats.getDoubleFormat()));
		this.numberFormatter = (NumberFormatter)getFormatter();
	}
	/**
	 * Constructor which sets the initial value.
	 * @param value Initial value.
	 */
	public DoubleTextField(double value)
	{
		this();
		this.setValue(value);
	}
	@Override
	public Double getValue()
	{
		Object value = super.getValue();
		if(value == null)
			return 0.0;
		return ((Number)value).doubleValue();
	}
	/**
	 * Sets the minimal allowed value.
	 * @param value
	 */
	public void setMinimalValue(double value)
	{
		numberFormatter.setMinimum(value);
		setFormatter(numberFormatter);
	}
	/**
	 * Sets the maximal allowed value.
	 * @param value
	 */
	public void setMaximalValue(double value)
	{
		numberFormatter.setMaximum(value);
		setFormatter(numberFormatter);
	}
}
