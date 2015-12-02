/**
 * 
 */
package ch.ethz.csb.youscope.addon.lifecelldetection;

import java.util.Vector;

import ch.ethz.csb.youscope.addon.quickdetect.QuickDetectConfiguration;
import ch.ethz.csb.youscope.shared.addon.celldetection.CellDetectionConfiguration;
import ch.ethz.csb.youscope.shared.addon.celldetection.CellVisualizationConfiguration;
import ch.ethz.csb.youscope.shared.configuration.ImageProducerConfiguration;
import ch.ethz.csb.youscope.shared.configuration.JobContainerConfiguration;
import ch.ethz.csb.youscope.shared.configuration.TableProducerConfiguration;
import ch.ethz.csb.youscope.shared.table.TableDefinition;
import ch.ethz.csb.youscope.shared.configuration.JobConfiguration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This job takes an image an detects the cells therein.
 * 
 * @author Moritz Lang
 */
@XStreamAlias("cell-detection-job")
public class CellDetectionJobConfiguration extends JobConfiguration  implements TableProducerConfiguration, JobContainerConfiguration
{

	/**
	 * Serial Version UID.
	 */
	private static final long	serialVersionUID	= -8467192595299726516L;

	/**
	 * The suffix added to the image save name to identify the detected cell image.
	 */
	public static final String DETECTED_CELLS_IMAGE_SUFFIX = "_detect";
	
	/**
	 * The identifier for this measurement type.
	 */
	public static final String	TYPE_IDENTIFIER		= "CSB::CellDetectionJob";
	
	@XStreamAlias("detection-image")
	private JobConfiguration detectionJob = null;
	
	@XStreamAlias("quantification-images")
	private Vector<JobConfiguration> quantificationJobs = new Vector<JobConfiguration>();
	
	@XStreamAlias("detection-algorithm-configuration")
	private CellDetectionConfiguration detectionAlgorithmConfiguration = new QuickDetectConfiguration();
	
	@XStreamAlias("detection-visualization-configuration")
	private CellVisualizationConfiguration visualizationAlgorithmConfiguration = null;
	
	@XStreamAlias("minimal-time-ms")
	private long minimalTimeMS = -1;
	
	/**
	 * The name under which the control image is saved.
	 */
	@XStreamAlias("control-image-save-name")
	private String				controlImageSaveName		= null;
	
	/**
	 * The name under which the segment image is saved.
	 */
	@XStreamAlias("segmentation-image-save-name")
	private String				segmentationImageSaveName		= "segmentation";

	/**
	 * The name under which the cell table is saved.
	 */
	@XStreamAlias("cell-table-save-name")
	private String cellTableSaveName = "cell-table";
		
	@Override 
	public String getTypeIdentifier()
	{
		return TYPE_IDENTIFIER;
	}
	
	@Override
	public JobConfiguration[] getJobs()
	{
		return quantificationJobs.toArray(new JobConfiguration[quantificationJobs.size()]);
	}

	@Override
	public void setJobs(JobConfiguration[] jobs)
	{
		this.quantificationJobs.clear();
		for(JobConfiguration job:jobs)
		{
			if(ImageProducerConfiguration.class.isAssignableFrom(job.getClass()))
				this.quantificationJobs.add(job);
		}
	}

	@Override
	public void addJob(JobConfiguration job)
	{
		if(ImageProducerConfiguration.class.isAssignableFrom(job.getClass()))
			quantificationJobs.add(job);
	}

	@Override
	public void clearJobs()
	{
		quantificationJobs.clear();
	}

	@Override
	public void removeJobAt(int index)
	{
		quantificationJobs.removeElementAt(index);
	}

	@Override
	public void addJob(JobConfiguration job, int index)
	{
		if(ImageProducerConfiguration.class.isAssignableFrom(job.getClass()))
			quantificationJobs.insertElementAt(job, index);
	}
	
	/**
	 * Returns the job which is used to produce the cell detection image.
	 * It is guaranteed that this job implements the ImageProducerConfigiguration interface.
	 * @return Image producing job for cell detection algorithm, or null.
	 */
	public JobConfiguration getDetectionJob()
	{
		return detectionJob;
	}

	/**
	 * Sets the job which should be used to create the image which is used to detect the cells.
	 * The job must implement the ImageProducerConfigiguration interface. If this is not given, the detection job is set to null.
	 * @param detectionJob Image producing job for cell detection algorithm, or null.
	 */
	public void setDetectionJob(JobConfiguration detectionJob)
	{
		if(detectionJob == null)
			this.detectionJob = null;
		else if(ImageProducerConfiguration.class.isAssignableFrom(detectionJob.getClass()))
			this.detectionJob = detectionJob;
		else
			this.detectionJob = null;
	}
	
	/**
	 * Returns the name under which the control image should be saved.
	 * @return name of image, or null, if image should not be saved.
	 */
	public String getControlImageSaveName()
	{
		return controlImageSaveName;
	}
	
	/**
	 * Sets the name under which the control image should be saved. Set to null to not save the image.
	 * @param name Name for the image, or null.
	 */
	public void setControlImageSaveName(String name)
	{
		this.controlImageSaveName = name;
	}
	
	/**
	 * Returns the name under which the segmentation image should be saved.
	 * @return name of image, or null, if image should not be saved.
	 */
	public String getSegmentationImageSaveName()
	{
		return segmentationImageSaveName;
	}
	
	/**
	 * Sets the name under which the segmentation image should be saved. Set to null to not save the image.
	 * @param name Name for the image, or null.
	 */
	public void setSegmentationImageSaveName(String name)
	{
		this.segmentationImageSaveName = name;
	}
	
	/**
	 * Sets the name under which the cell-table should be saved. Set to null to not save the cell table.
	 * @param name Name for the cell table (without extension), or null.
	 */
	public void setCellTableSaveName(String name)
	{
		this.cellTableSaveName = name;
	}

	/**
	 * Returns the name under which the cell table should be saved (without extension).
	 * @return name of cell-table, or null, if cell table should not be saved.
	 */
	public String getCellTableSaveName()
	{
		return cellTableSaveName;
	}

	/**
	 * Sets the configuration of the cell detection algorithm to use.
	 * @param detectionAlgorithmConfiguration Configuration of the algorithm.
	 */
	public void setDetectionAlgorithmConfiguration(CellDetectionConfiguration detectionAlgorithmConfiguration)
	{
		if(detectionAlgorithmConfiguration==null)
			return;
		this.detectionAlgorithmConfiguration = detectionAlgorithmConfiguration;
	}

	/**
	 * Return the configuration of the cell detection algorithm to use.
	 * @return Configuration of the algorithm.
	 */
	public CellDetectionConfiguration getDetectionAlgorithmConfiguration()
	{
		return detectionAlgorithmConfiguration;
	}
	
	/**
	 * Returns the configuration of the cell visualization algorithm to use.
	 * Returns null if not generating a cell detection visualization image.
	 * @return Cell visualization configuration, or null.
	 */
	public CellVisualizationConfiguration getVisualizationAlgorithmConfiguration()
	{
		return visualizationAlgorithmConfiguration;
	}

	/**
	 * Set the configuration of the cell visualization algorithm to use.
	 * Set to null to not generate a cell detection visualization image.
	 * @param visualizationAlgorithmConfiguration Cell visualization configuration, or null.
	 */
	public void setVisualizationAlgorithmConfiguration(CellVisualizationConfiguration visualizationAlgorithmConfiguration)
	{
		this.visualizationAlgorithmConfiguration = visualizationAlgorithmConfiguration;
	}

	@Override
	public String getDescription()
	{
		String description = "<p>Life cell detection</p>";
		description += "<ul style=\"margin-top:0px;margin-bottom:0px;margin-left:12px;list-style-type:none\">";
		if(detectionJob != null)
		{
			description += "<li>" + detectionJob.getDescription() + "</li>";
		}
		for(JobConfiguration job : quantificationJobs)
		{
			description += "<li>" + job.getDescription() + "</li>";
		}
		description += "<li><p>";
		if(segmentationImageSaveName != null)
			description += segmentationImageSaveName + " = ";
		description += "segmentCells()</p></li>";
		if(visualizationAlgorithmConfiguration != null)
		{
			description += "<li><p>";
			if(controlImageSaveName != null)
			{
				description += controlImageSaveName + " = ";
			}
			description += "visualizeCells()</p></li>";
		}
		description += "</ul><p>end</p>";
		return description;
	}

	@Override
	public CellDetectionJobConfiguration clone()
	{
		CellDetectionJobConfiguration config;
		try {
			config = (CellDetectionJobConfiguration) super.clone();
			
			if(detectionAlgorithmConfiguration != null)
				config.detectionAlgorithmConfiguration = detectionAlgorithmConfiguration.clone();
			if(visualizationAlgorithmConfiguration != null)
				config.visualizationAlgorithmConfiguration = (CellVisualizationConfiguration)visualizationAlgorithmConfiguration.clone();
			
			config.quantificationJobs = new Vector<JobConfiguration>();
			for(int i = 0; i < quantificationJobs.size(); i++)
			{
				config.quantificationJobs.add((JobConfiguration)quantificationJobs.elementAt(i).clone());
			}
			if(detectionJob != null)
				config.detectionJob = (JobConfiguration)detectionJob.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Clone not supported.", e);//will not happen.
		}
		return config;
	}

	/**
	 * Sets the minimal time (in ms) the execution of the job should take. If the execution of the job takes less, the rest of the time span will be waited.
	 * This function is mainly ment for real-time applications.
	 * @param minimalTimeMS The minimal time in ms the execution of the job has to take, or -1.
	 */
	public void setMinimalTimeMS(long minimalTimeMS)
	{
		this.minimalTimeMS = minimalTimeMS;
	}

	/**
	 * Returns the minimal time (in ms) the execution of the job should take. If the execution of the job takes less, the rest of the time span will be waited.
	 * This function is mainly ment for real-time applications.
	 * @return The minimal time in ms the execution of the job has to take, or -1.
	 */
	public long getMinimalTimeMS()
	{
		return minimalTimeMS;
	}

	@Override
	public TableDefinition getProducedTableDefinition() {
		return detectionAlgorithmConfiguration.getProducedTableDefinition();
	}
}
