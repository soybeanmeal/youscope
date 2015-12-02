/**
 * 
 */
package ch.ethz.csb.youscope.client.uielements;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author langmo
 */
public class StandardFormats
{
	/**
	 * Returns the standard format for doubles (e.g. for FormattedTextFields).
	 * 
	 * @return Standard format for double values.
	 */
	public static NumberFormat getDoubleFormat()
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

	/**
	 * Returns the standard format for integers (e.g. for FormattedTextFields).
	 * 
	 * @return Standard format for integer values.
	 */
	public static NumberFormat getIntegerFormat()
	{
		NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
		format.setMaximumIntegerDigits(100);
		format.setParseIntegerOnly(true);
		format.setGroupingUsed(false);
		return format;
	}

	/**
	 * Returns a constraint which can be e.g. used in a GridBagLayout. An element added with this
	 * constraint will fill its current line.
	 * 
	 * @return Standard constraint for GridBagLayout elements.
	 */
	public static GridBagConstraints getNewLineConstraint()
	{
		GridBagConstraints newLineConstr = new GridBagConstraints();
		newLineConstr.fill = GridBagConstraints.HORIZONTAL;
		newLineConstr.gridwidth = GridBagConstraints.REMAINDER;
		newLineConstr.anchor = GridBagConstraints.NORTHWEST;
		newLineConstr.gridx = 0;
		newLineConstr.weightx = 1.0;
		return newLineConstr;
	}

	/**
	 * Returns a constraint which can be e.g. used in a GridBagLayout. An element added with this
	 * constraint will fill the entire rest of the layout.
	 * @return Standard constraint for GridBagLayout elements.
	 */
	public static GridBagConstraints getBottomContstraint()
	{
		GridBagConstraints bottomConstr = new GridBagConstraints();
		bottomConstr.weighty = 1.0;
		bottomConstr.weightx = 1.0;
		bottomConstr.fill = GridBagConstraints.BOTH;
		bottomConstr.gridwidth = GridBagConstraints.REMAINDER;
		return bottomConstr;
	}

	/**
	 * Adds the given component to the container using the given layout and constraints.
	 * @param component the component to add.
	 * @param layout the respective layout of the container.
	 * @param constr the constraints with which the component should be added.
	 * @param parent the container to which the element should be added.
	 */
	public static void addGridBagElement(Component component, GridBagLayout layout, GridBagConstraints constr, Container parent)
	{
		layout.setConstraints(component, constr);
		parent.add(component);
	}
}
