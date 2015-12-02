/**
 * 
 */
package ch.ethz.csb.youscope.addon.ansisbsmicroplates;

import java.io.Serializable;

import ch.ethz.csb.youscope.shared.MicroplateType;

/**
 * @author Moritz Lang
 *
 */
public class AnsiSBS384MicroplateType implements MicroplateType, Cloneable, Serializable
{
	/**
	 * Serial Version UID.
	 */
	private static final long	serialVersionUID	= -5153024292890769481L;
	/**
	 * The ID of this microplate type.
	 */
	public static final String TYPE_ID = "ANSI_SBS_384";
	@Override
	public int getNumWellsX()
	{
		return 24;
	}

	@Override
	public int getNumWellsY()
	{
		return 16;
	}

	@Override
	public double getWellWidth()
	{
		return 4500.0;
	}

	@Override
	public double getWellHeight()
	{
		return 4500.0;
	}

	@Override
	public String getMicroplateID()
	{
		return TYPE_ID;
	}

	@Override
	public String getMicroplateName()
	{
		return "384 well microplate (ANSI/SBS 1-2004 through ANSI/SBS 4-2004).";
	}
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
