/**
 * 
 */
package ch.ethz.csb.youscope.client.uielements;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import ch.ethz.csb.youscope.client.addon.YouScopeClient;
import ch.ethz.csb.youscope.client.addon.YouScopeFrame;
import ch.ethz.csb.youscope.shared.YouScopeServer;
import ch.ethz.csb.youscope.shared.configuration.TaskConfiguration;
import ch.ethz.csb.youscope.shared.configuration.TaskContainer;

/**
 * Panel which allows the user to add, remove and edit tasks of e.g. an advanced measurement.
 * @author langmo
 */
public class TasksDefinitionPanel extends JPanel
{

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -5426747514806820936L;

    private JList<String> taskList;

    private TaskContainer configuration;

    private final YouScopeFrame frame;
    private YouScopeClient client; 
	private YouScopeServer server;
    /**
     * Constructor.
     * @param client Interface to the client.
     * @param server Interface to the server.
     * @param frame Frame in which this panel is added.
     * @param configuration The element containing the tasks definitions.
     */
    public TasksDefinitionPanel(YouScopeClient client, YouScopeServer server, YouScopeFrame frame, TaskContainer configuration)
    {
        super(new BorderLayout());
        this.configuration = configuration;
        this.frame = frame;
        this.client = client;
		this.server = server;

        // Load icons
        String addButtonFile = "icons/block--plus.png";
        String deleteButtonFile = "icons/block--minus.png";
        String editButtonFile = "icons/block--pencil.png";

        String upButtonFile = "icons/arrow-090.png";
        String downButtonFile = "icons/arrow-270.png";

        ImageIcon addButtonIcon = null;
        ImageIcon deleteButtonIcon = null;
        ImageIcon editButtonIcon = null;
        ImageIcon upButtonIcon = null;
        ImageIcon downButtonIcon = null;
        try
        {
            URL addButtonURL = getClass().getClassLoader().getResource(addButtonFile);
            if (addButtonURL != null)
                addButtonIcon = new ImageIcon(addButtonURL, "Add Job");

            URL deleteButtonURL = getClass().getClassLoader().getResource(deleteButtonFile);
            if (deleteButtonURL != null)
                deleteButtonIcon = new ImageIcon(deleteButtonURL, "Delete Job");

            URL editButtonURL = getClass().getClassLoader().getResource(editButtonFile);
            if (editButtonURL != null)
                editButtonIcon = new ImageIcon(editButtonURL, "Edit Job");

            URL upButtonURL = getClass().getClassLoader().getResource(upButtonFile);
            if (upButtonURL != null)
                upButtonIcon = new ImageIcon(upButtonURL, "Move upwards");

            URL downButtonURL = getClass().getClassLoader().getResource(downButtonFile);
            if (downButtonURL != null)
                downButtonIcon = new ImageIcon(downButtonURL, "Move downwards");

        } catch (@SuppressWarnings("unused") Exception e)
        {
            // Do nothing.
        }

        JButton newTaskButton = new JButton("New Task", addButtonIcon);
        newTaskButton.setHorizontalAlignment(SwingConstants.LEFT);
        newTaskButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                	YouScopeFrame newFrame = TasksDefinitionPanel.this.frame.createModalChildFrame();
                	TaskConfigurationPanel configFrame = new TaskConfigurationPanel(TasksDefinitionPanel.this.client, TasksDefinitionPanel.this.server, TasksDefinitionPanel.this.frame);
                	configFrame.showInFrame(newFrame, "Measurement Task", new TaskConfigurationListenerImpl(-1));
                    newFrame.setVisible(true);
                }
            });

        JButton deleteTaskButton = new JButton("Delete Task", deleteButtonIcon);
        deleteTaskButton.setHorizontalAlignment(SwingConstants.LEFT);
        deleteTaskButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    int idx = taskList.getSelectedIndex();
                    if (idx == -1)
                        return;
                    TasksDefinitionPanel.this.configuration.removeTask(idx);
                    refreshTaskList();
                }
            });

        JButton editTaskButton = new JButton("Edit Task", editButtonIcon);
        editTaskButton.setHorizontalAlignment(SwingConstants.LEFT);
        editTaskButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    int idx = taskList.getSelectedIndex();
                    if (idx == -1)
                        return;
                    TaskConfiguration task = TasksDefinitionPanel.this.configuration.getTask(idx);
                    
                    YouScopeFrame newFrame = TasksDefinitionPanel.this.frame.createModalChildFrame();
                	TaskConfigurationPanel configFrame = new TaskConfigurationPanel(TasksDefinitionPanel.this.client, TasksDefinitionPanel.this.server, TasksDefinitionPanel.this.frame);
                	configFrame.setConfigurationData(task);
                	configFrame.showInFrame(newFrame, "Measurement Task", new TaskConfigurationListenerImpl(idx));
                    newFrame.setVisible(true);
                }
            });

        add(new JLabel("Tasks:"), BorderLayout.NORTH);

        // add Button Panel
        JPanel centralPanel = new JPanel(new BorderLayout(2, 2));
        GridBagLayout elementsLayout = new GridBagLayout();
        GridBagConstraints newLineConstr = new GridBagConstraints();
        newLineConstr.fill = GridBagConstraints.HORIZONTAL;
        newLineConstr.gridwidth = GridBagConstraints.REMAINDER;
        newLineConstr.anchor = GridBagConstraints.NORTHWEST;
        newLineConstr.gridx = 0;
        newLineConstr.weightx = 1.0;
        newLineConstr.weighty = 0;
        GridBagConstraints bottomConstr = new GridBagConstraints();
        bottomConstr.weighty = 1.0;

        JPanel jobButtonPanel = new JPanel(elementsLayout);
        StandardFormats.addGridBagElement(newTaskButton, elementsLayout, newLineConstr, jobButtonPanel);
        StandardFormats.addGridBagElement(editTaskButton, elementsLayout, newLineConstr, jobButtonPanel);
        StandardFormats.addGridBagElement(deleteTaskButton, elementsLayout, newLineConstr, jobButtonPanel);
        StandardFormats.addGridBagElement(new JPanel(), elementsLayout, bottomConstr, jobButtonPanel);

        add(jobButtonPanel, BorderLayout.EAST);

        taskList = new JList<String>();
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane taskListPane = new JScrollPane(taskList);
        taskListPane.setPreferredSize(new Dimension(250, 70));
        taskListPane.setMinimumSize(new Dimension(10, 10));
        centralPanel.add(taskListPane, BorderLayout.CENTER);

        JButton upButton;
        if (upButtonIcon == null)
            upButton = new JButton("Up");
        else
            upButton = new JButton(upButtonIcon);
        JButton downButton;
        if (downButtonIcon == null)
            downButton = new JButton("Down");
        else
            downButton = new JButton(downButtonIcon);
        upButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent arg0)
                {
                    moveUpDown(true);
                }
            });
        downButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent arg0)
                {
                    moveUpDown(false);
                }
            });
        JPanel upDownPanel = new JPanel(new GridLayout(1, 2, 2, 2));
        upDownPanel.add(upButton);
        upDownPanel.add(downButton);
        centralPanel.add(upDownPanel, BorderLayout.SOUTH);
        add(centralPanel, BorderLayout.CENTER);

        refreshTaskList();
    }

    private void moveUpDown(boolean moveUp)
    {
        int idx = taskList.getSelectedIndex();
        if (idx == -1 || (moveUp && idx == 0)
                || (!moveUp && idx + 1 >= configuration.getNumTasks()))
            return;
        int newIdx;
        if (moveUp)
            newIdx = idx - 1;
        else
            newIdx = idx + 1;
        TaskConfiguration task = configuration.getTask(idx);
        configuration.removeTask(idx);
        configuration.insertTask(task, newIdx);
        refreshTaskList();
    }

    private class TaskConfigurationListenerImpl implements TaskConfigurationListener
    {
    	private final int idx;
    	TaskConfigurationListenerImpl(int idx)
    	{
    		this.idx = idx;
    	}
	    @Override
	    public void taskConfigurationFinished(TaskConfiguration task)
	    {
	    	if(idx < 0)
	    		configuration.addTask(task);
	    	else
	    	{
	    		configuration.removeTask(idx);
	    		configuration.insertTask(task, idx);
	    	}
	    	refreshTaskList();
	    }
    }
	    
    private void refreshTaskList()
    {
        Vector<String> taskDescriptions = new Vector<String>();
        for (int i = 0; i < configuration.getNumTasks(); i++)
        {
            taskDescriptions.add("<html><body>"
                    + configuration.getTask(i).getDescription() + "</body></html>");
        }
        taskList.setListData(taskDescriptions);
    }

}
