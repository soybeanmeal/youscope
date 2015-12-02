/**
 * 
 */
package ch.ethz.csb.youscope.addon.cellx;

import ch.ethz.csb.youscope.shared.addon.celldetection.CellDetectionTableColumns;
import ch.ethz.csb.youscope.shared.table.ColumnDefinition;
import ch.ethz.csb.youscope.shared.table.TableDefinition;

/**
 * Provides information on the layout of the tables produced by the CellX cell detection algorithm.
 * @author Moritz Lang
 *
 */
public class CellXTable
{
	/**
	 * Use static methods.
	 */
	private CellXTable()
	{
		// use static methods.
	}
	
	private static TableDefinition tableDefinition = null;
	
	/**
	 * Returns the table layout for the quick-detect cell-detecktion algorithm.
	 * @return Layout for the cell-detection table.
	 */
	public synchronized static TableDefinition getTableDefinition()
	{
		if(tableDefinition != null)
			return tableDefinition;
		
		ColumnDefinition<?>[] columnDefinitions = new ColumnDefinition<?>[]
				{
					ColumnDefinition.createIntegerColumnDefinition("cell.frame", "", false),
					CellDetectionTableColumns.TABLE_COLUMN_CELL_ID,
					CellDetectionTableColumns.TABLE_COLUMN_XPOSITION_PX,
					CellDetectionTableColumns.TABLE_COLUMN_YPOSITION_PX,
					CellDetectionTableColumns.TABLE_COLUMN_MAJOR_AXIS_PX,
					CellDetectionTableColumns.TABLE_COLUMN_MINOR_AXIS_PX,
					CellDetectionTableColumns.TABLE_COLUMN_ORIENTATION,
					CellDetectionTableColumns.TABLE_COLUMN_AREA_PX,
					CellDetectionTableColumns.TABLE_COLUMN_VOLUME_PX,
					CellDetectionTableColumns.TABLE_COLUMN_PERIMETER_PX,
					ColumnDefinition.createDoubleColumnDefinition("cell.mem.area", "", true),
					ColumnDefinition.createDoubleColumnDefinition("cell.mem.volume", "", true),
					ColumnDefinition.createDoubleColumnDefinition("cell.nuc.radius", "", true),
					ColumnDefinition.createDoubleColumnDefinition("cell.nuc.area", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.background.median", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.background.std", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.cell.total", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.cell.q75", "", true),
					CellDetectionTableColumns.TABLE_COLUMN_FLUORESCENCE,
					ColumnDefinition.createDoubleColumnDefinition("fluo.cell.q25", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.mem.total", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.mem.q75", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.mem.median", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.mem.q25", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.nuc.total", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.nuc.q75", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.nuc.median", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.nuc.q25", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.bright.total", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.bright.q75", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.bright.median", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.bright.q25", "", true),
					ColumnDefinition.createDoubleColumnDefinition("fluo.bright.euler", "", true),
					CellDetectionTableColumns.TABLE_COLUMN_CELL_TRACKING_ID
				};
		
		tableDefinition = new TableDefinition("Cell-Detection Results", 
				"Contains information about the cells detected in the images by the CellX algorithm.",
				
				columnDefinitions);
		return tableDefinition;		
	}
}
