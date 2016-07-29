/**
 * 
 */
package org.youscope.plugin.customjob;


import org.youscope.common.job.EditableJobContainer;
import org.youscope.common.job.Job;


/**
 * A job consisting of other jobs, which can be configured by the user. The single jobs will be run after each other in the order
 * they are created. It is guaranteed that in between two jobs no other tasks are executed by the
 * microscope.
 * 
 * @author Moritz Lang
 */
public interface CustomJob extends Job, EditableJobContainer
{
	// no extra members
}