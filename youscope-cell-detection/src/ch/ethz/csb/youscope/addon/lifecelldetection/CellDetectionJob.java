/**
 * 
 */
package ch.ethz.csb.youscope.addon.lifecelldetection;


import java.rmi.RemoteException;

import ch.ethz.csb.youscope.shared.ImageListener;
import ch.ethz.csb.youscope.shared.addon.celldetection.CellDetectionAddon;
import ch.ethz.csb.youscope.shared.addon.celldetection.CellVisualizationAddon;
import ch.ethz.csb.youscope.shared.measurement.ImageProducer;
import ch.ethz.csb.youscope.shared.measurement.MeasurementRunningException;
import ch.ethz.csb.youscope.shared.measurement.job.EditableJobContainer;
import ch.ethz.csb.youscope.shared.measurement.job.Job;
import ch.ethz.csb.youscope.shared.table.TableProducer;


/**
 * This job detects cells in the image passed to it. It passes information about the detected cells to its table listeners. If configured respectively,
 * it also produces an image in which the detected cells are highlighted to its image listeners.
 * @author Moritz Lang
 */
public interface CellDetectionJob extends Job, ImageProducer, TableProducer, EditableJobContainer
{
	/**
	 * Adds a listener which is invoked if a new segmentation image was created.
	 * A listener can always be added, even if the respective measurement is already running.
	 * Note: if the cells are not visualized, this is the same as adding the listener with addImageListener().
	 * 
	 * @param listener Listener which should be notified.
	 * @throws RemoteException
	 */
	void addSegmentationImageListener(ImageListener listener) throws RemoteException;

	/**
	 * Removes a previously added image listener.
	 * 
	 * @param listener Listener which was previously added.
	 * @throws RemoteException
	 */
	void removeSegmentationImageListener(ImageListener listener) throws RemoteException;
	
	/**
	 * Adds a listener which is invoked if a new control image was created.
	 * A listener can always be added, even if the respective measurement is already running.
	 * Note: if the cells are visualized, this is the same as adding the listener with addImageListener().
	 * 
	 * @param listener Listener which should be notified.
	 * @throws RemoteException
	 */
	void addControlImageListener(ImageListener listener) throws RemoteException;

	/**
	 * Removes a previously added image listener.
	 * 
	 * @param listener Listener which was previously added.
	 * @throws RemoteException
	 */
	void removeControlImageListener(ImageListener listener) throws RemoteException;
	
	/**
	 * Sets the algorithm which should be used for cell detection.
	 * @param detectionAlgorithm
	 * @throws MeasurementRunningException
	 * @throws RemoteException 
	 */
	void setDetectionAlgorithm(CellDetectionAddon detectionAlgorithm) throws MeasurementRunningException, RemoteException;

	/**
	 * Returns the algorithm which should be used for cell detection, or null.
	 * @return cell detection algorithm or null.
	 * @throws RemoteException 
	 */
	CellDetectionAddon getDetectionAlgorithm() throws RemoteException;
	
	/**
	 * Sets the algorithm which should be used for cell visualization.
	 * @param visualizationAlgorithm
	 * @throws MeasurementRunningException
	 * @throws RemoteException 
	 */
	void setVisualizationAlgorithm(CellVisualizationAddon visualizationAlgorithm) throws MeasurementRunningException, RemoteException;

	/**
	 * Returns the algorithm which should be used for cell visualization, or null.
	 * @return cell detection algorithm or null.
	 * @throws RemoteException 
	 */
	CellVisualizationAddon getVisualizationAlgorithm() throws RemoteException;

	/**
	 * Sets a short string describing the images which are made by this job.
	 * @param description The description which should be returned for the images produced by this job, or null, to switch to the default description.
	 * @throws RemoteException
	 * @throws MeasurementRunningException 
	 */
	void setImageDescription(String description) throws RemoteException, MeasurementRunningException;
	
	/**
	 * Sets the minimal time (in ms) the execution of the job should take. If the execution of the job takes less, the rest of the time span will be waited.
	 * This function is mainly ment for real-time applications.
	 * @param minimalTimeMS The minimal time in ms the execution of the job has to take, or -1.
	 * @throws MeasurementRunningException 
	 * @throws RemoteException 
	 */
	public void setMinimalTimeMS(long minimalTimeMS) throws MeasurementRunningException, RemoteException;

	/**
	 * Returns the minimal time (in ms) the execution of the job should take. If the execution of the job takes less, the rest of the time span will be waited.
	 * This function is mainly ment for real-time applications.
	 * @return The minimal time in ms the execution of the job has to take, or -1.
	 * @throws RemoteException 
	 */
	public long getMinimalTimeMS() throws RemoteException;
}
