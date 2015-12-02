/**
 * 
 */
package ch.ethz.csb.youscope.addon.nemesys;

import ch.ethz.csb.youscope.shared.table.ColumnDefinition;
import ch.ethz.csb.youscope.shared.table.TableDefinition;

/**
 * Class to provide information about the layout of the table consumed by the nemesys table consumer.
 * @author Moritz Lang
 *
 */
class NemesysStateTable
{
	/**
	 * Column specifying the ID of the flow unit whose target flow should be changed.
	 */
	public final static ColumnDefinition<Integer> COLUMN_FLOW_UNIT = ColumnDefinition.createIntegerColumnDefinition("Flow unit ID", "Zero based index of the flow unit whose actual flow rate was measured.", false);
	/**
	 * Column specifying the current flow rate in ul/min of the flow unit.
	 */
	public final static ColumnDefinition<Double> COLUMN_FLOW_RATE = ColumnDefinition.createDoubleColumnDefinition("Flow rate", "Current flow rate in microliter per minute.", false);
	/**
	 * Column specifying the current volume in ul of the flow unit.
	 */
	public final static ColumnDefinition<Double> COLUMN_VOLUME = ColumnDefinition.createDoubleColumnDefinition("Volume", "Current volume in microliter.", false);
 	
	/**
	 * Private constructor. Use static methods.
	 */
	private NemesysStateTable()
	{
		// only static methods.
	}
	
	private static TableDefinition tableDefinition = null;
	
	/**
 	 * Returns the layout of the Fluigent control table.
 	 * @return Fluigent control table layout.
 	 */
 	public static synchronized TableDefinition getTableDefinition()
 	{
 		if(tableDefinition != null)
 			return tableDefinition;
 		
 		tableDefinition = new TableDefinition("Nemesys syringe system monitor table", "Table containing the current flow rate and volumes of the Nemesys syringe system.",
 				COLUMN_FLOW_UNIT,COLUMN_FLOW_RATE,COLUMN_VOLUME);
 		return tableDefinition;
 	}
}
