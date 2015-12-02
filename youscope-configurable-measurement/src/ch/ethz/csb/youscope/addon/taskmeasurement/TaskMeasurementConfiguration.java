/**
 * 
 */
package ch.ethz.csb.youscope.addon.taskmeasurement;

import java.util.ArrayList;
import java.util.Collection;

import ch.ethz.csb.youscope.shared.configuration.ConfigurationException;
import ch.ethz.csb.youscope.shared.configuration.MeasurementConfiguration;
import ch.ethz.csb.youscope.shared.configuration.TaskConfiguration;
import ch.ethz.csb.youscope.shared.configuration.TaskContainer;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents the configuration of a user configurable measurement.
 * 
 * @author Moritz Lang
 */
@XStreamAlias("task-measurement")
public class TaskMeasurementConfiguration extends MeasurementConfiguration implements TaskContainer
{
	/**
	 * Serial Version UID.
	 */
	private static final long	serialVersionUID	= -6934063013631887402L;

	/**
	 * The identifier for this measurement type.
	 */
	public static final String	TYPE_IDENTIFIER		= "CSB::ConfiguratableMeasurement";

	@Override 
	public String getTypeIdentifier()
	{
		return TYPE_IDENTIFIER;
	}

	/**
	 * A list of all the jobs which should be done during the measurement.
	 */
	@XStreamAlias("tasks")
	private ArrayList<TaskConfiguration>	tasks	= new ArrayList<TaskConfiguration>();

	/**
	 * @return the tasks
	 */
	@Override
	public TaskConfiguration[] getTasks()
	{
		return tasks.toArray(new TaskConfiguration[tasks.size()]);
	}

	/**
	 * @param tasks the tasks to set
	 */
	public void setTasks(Collection<TaskConfiguration> tasks)
	{
		this.tasks.clear();
		this.tasks.addAll(tasks);
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		TaskMeasurementConfiguration clone = (TaskMeasurementConfiguration)super.clone();
		clone.tasks = new ArrayList<TaskConfiguration>();
		for(int i = 0; i < tasks.size(); i++)
		{
			clone.tasks.add((TaskConfiguration)tasks.get(i).clone());
		}
		return clone;
	}

	@Override
	public void checkConfiguration() throws ConfigurationException {
		super.checkConfiguration();
		if(tasks == null)
			throw new ConfigurationException("Tasks are null.");
		for(TaskConfiguration task: tasks)
			task.checkConfiguration();
		
	}

	@Override
	public int getNumTasks() {
		return tasks.size();
	}

	@Override
	public TaskConfiguration getTask(int taskID) throws IndexOutOfBoundsException {
		return tasks.get(taskID);
	}

	@Override
	public void removeTask(int taskID) throws IndexOutOfBoundsException {
		tasks.remove(taskID);
	}

	@Override
	public void addTask(TaskConfiguration taskConfiguration) {
		tasks.add(taskConfiguration);
	}

	@Override
	public void insertTask(TaskConfiguration taskConfiguration, int taskID) throws IndexOutOfBoundsException {
		tasks.add(taskID, taskConfiguration);
	}
}