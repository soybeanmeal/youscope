/**
 * 
 */
package ch.ethz.csb.youscope.addon.slimjob;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import ch.ethz.csb.youscope.client.addon.YouScopeClient;
import ch.ethz.csb.youscope.client.addon.YouScopeFrame;
import ch.ethz.csb.youscope.client.addon.YouScopeProperties;
import ch.ethz.csb.youscope.client.addon.job.JobConfigurationAddon;
import ch.ethz.csb.youscope.client.addon.job.JobConfigurationAddonListener;
import ch.ethz.csb.youscope.client.uielements.DescriptionPanel;
import ch.ethz.csb.youscope.client.uielements.DynamicPanel;
import ch.ethz.csb.youscope.client.uielements.ImagePanel;
import ch.ethz.csb.youscope.client.uielements.IntegerTextField;
import ch.ethz.csb.youscope.client.uielements.StandardFormats;
import ch.ethz.csb.youscope.shared.ImageEvent;
import ch.ethz.csb.youscope.shared.YouScopeServer;
import ch.ethz.csb.youscope.shared.configuration.ConfigurationException;
import ch.ethz.csb.youscope.shared.configuration.JobConfiguration;
import ch.ethz.csb.youscope.shared.microscope.CameraDevice;
import ch.ethz.csb.youscope.shared.microscope.Channel;
import ch.ethz.csb.youscope.shared.microscope.Device;
import ch.ethz.csb.youscope.shared.microscope.DeviceException;
import ch.ethz.csb.youscope.shared.tools.ImageConvertException;

/**
 * @author Moritz Lang
 */
class SlimJobConfigurationAddon implements JobConfigurationAddon
{

	private final JComboBox<String>								configGroupField		= new JComboBox<String>();

	private final JComboBox<String>								channelField			= new JComboBox<String>();

	private final JFormattedTextField					exposureField			= new JFormattedTextField(StandardFormats.getDoubleFormat());

	private final JCheckBox								saveImagesField			= new JCheckBox("Save images", true);

	private final JTextField							nameField				= new JTextField();

	private final JLabel								nameLabel				= new JLabel("Image name:");
	
	private final JLabel 					cameraLabel = new JLabel("Camera:");
	private final JComboBox<String>					cameraField		= new JComboBox<String>();
	
	private final JLabel 					reflectorLabel = new JLabel("Reflector:");
	private final JComboBox<String>					reflectorField		= new JComboBox<String>();
	
	private final JComboBox<String> modeField = new JComboBox<String>();
	private final JTextField maskFileField = new JTextField();
	private final JPanel maskFilePanel = new JPanel(new BorderLayout());
	private final JLabel maskFileLabel = new JLabel("Mask File:");
	
	private final JLabel centerXLabel = new JLabel("X-position of Mask Center (px):");
	private final JLabel centerYLabel = new JLabel("Y-position of Mask Center (px):");
	private final JLabel innerRadiusLabel = new JLabel("Inner Radius (px):");
	private final JLabel outerRadiusLabel = new JLabel("Outer Radius (px):");
	
	
	private final IntegerTextField centerXField = new IntegerTextField();
	private final IntegerTextField centerYField = new IntegerTextField();
	private final IntegerTextField innerRadiusField = new IntegerTextField();
	private final IntegerTextField outerRadiusField = new IntegerTextField();
	private final IntegerTextField phaseShiftBackgroundField = new IntegerTextField();
	private final IntegerTextField[] phaseShiftMaskFields = new IntegerTextField[SlimJobConfigurationDTO.NUM_PHASE_SHIFT_MASK];
	private final IntegerTextField reflectorDelayField = new IntegerTextField();
	
	private final static String LAST_SLIM_PATH_PROPERTY = "CSB::SLIM::lastPath";
	
	private SlimJobConfigurationDTO	job = new SlimJobConfigurationDTO();

	private YouScopeFrame									frame;
	private YouScopeClient client; 
	private YouScopeServer server;
	private Vector<JobConfigurationAddonListener> configurationListeners = new Vector<JobConfigurationAddonListener>();

	SlimJobConfigurationAddon(YouScopeClient client, YouScopeServer server)
	{
		this.client = client;
		this.server = server;
	}
	
	private void loadConfigGroupNames()
	{
		String[] configGroupNames = null;
		try
		{
			configGroupNames = server.getMicroscope().getChannelManager().getChannelGroupIDs();
		}
		catch(Exception e)
		{
			client.sendError("Could not obtain config group names.", e);
		}

		if(configGroupNames == null || configGroupNames.length <= 0)
		{
			configGroupNames = new String[] {""};
		}

		configGroupField.removeAllItems();
		for(String configGroupName : configGroupNames)
		{
			configGroupField.addItem(configGroupName);
		}
	}

	private void loadChannels()
	{
		String[] channelNames = null;

		Object selectedGroup = configGroupField.getSelectedItem();
		if(selectedGroup != null && selectedGroup.toString().length() > 0)
		{
			try
			{
				Channel[] channels = server.getMicroscope().getChannelManager().getChannels(selectedGroup.toString()); 
				channelNames = new String[channels.length];
				for(int i=0; i<channels.length; i++)
				{
					channelNames[i] = channels[i].getChannelID();
				}
			}
			catch(Exception e)
			{
				client.sendError("Could not obtain channel names of microscope.", e);
			}
		}

		if(channelNames == null || channelNames.length <= 0)
		{
			channelNames = new String[] {""};
		}

		channelField.removeAllItems();
		for(String channelName : channelNames)
		{
			channelField.addItem(channelName);
		}
	}
	
	private String getDefaultCameraName()
	{
		try
		{
			CameraDevice defaultCamera = server.getMicroscope().getCameraDevice();
			if(defaultCamera == null)
				return null;
			return defaultCamera.getDeviceID();
		}
		catch(Exception e)
		{
			client.sendError("Could not get name of default camera.", e);
			return null;
		}
	}
	
	private int loadReflectors()
	{
		ArrayList<String> reflectorNames = new ArrayList<String>();
		try
		{
			Device[] devices = server.getMicroscope().getDevices();
			for(Device device : devices)
			{
				if(device.getDriverID().equals("Reflector"))
					reflectorNames.add(device.getDeviceID());
			}
		}
		catch(RemoteException e)
		{
			client.sendError("Could not obtain names of reflector devices.", e);
		}
		
		reflectorField.removeAllItems();
		for(String reflectorName : reflectorNames)
		{
			reflectorField.addItem(reflectorName);
		}
		return reflectorNames.size();
	}
	
	private int loadCameras()
	{
		String[] cameraNames = null;
		try
		{
			CameraDevice[] cameras = server.getMicroscope().getCameraDevices();
			cameraNames = new String[cameras.length];
			for(int i=0; i< cameras.length; i++)
			{
				cameraNames[i] = cameras[i].getDeviceID();
			}
		}
		catch(RemoteException e)
		{
			client.sendError("Could not obtain names of cameras.", e);
			cameraNames = new String[0];
		}
				
		cameraField.removeAllItems();
		for(String cameraName : cameraNames)
		{
			cameraField.addItem(cameraName);
		}
		return cameraNames.length;
	}
	
	@Override
	public void createUI(YouScopeFrame frame)
	{
		this.frame = frame;
		frame.setTitle("SLIM Job");
		frame.setResizable(true);
		frame.setClosable(true);
		frame.setMaximizable(false);
		
		DynamicPanel elementsPanel = new DynamicPanel();
		elementsPanel.add(cameraLabel);
		elementsPanel.add(cameraField);
		elementsPanel.add(new JLabel("Channel Group:"));
		elementsPanel.add(configGroupField);
		elementsPanel.add(new JLabel("Channel:"));
		elementsPanel.add(channelField);
		elementsPanel.add(new JLabel("Exposure (ms):"));
		elementsPanel.add(exposureField);
		elementsPanel.add(nameLabel);
		elementsPanel.add(nameField);
		elementsPanel.add(saveImagesField);
		
		JButton snapImageButton = new JButton("Snap Image");
		snapImageButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String channel = (String)channelField.getSelectedItem();
				String channelGroup = (String)configGroupField.getSelectedItem();
				double exposure = ((Number)exposureField.getValue()).doubleValue();
				String camera = (String)cameraField.getSelectedItem();
				snapImage(camera, channelGroup, channel, exposure);
			}
		});
		elementsPanel.add(snapImageButton);
		elementsPanel.addFillEmpty();
		
		JButton snapSlimButton = new JButton("Snap SLIM image");
		snapSlimButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				snapSlim();
			}
		});
		
		JButton addJobButton = new JButton("Add Job");
		addJobButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				commitChanges();
				for(JobConfigurationAddonListener listener : configurationListeners)
				{
					listener.jobConfigurationFinished(SlimJobConfigurationAddon.this.job);
				}

				try
				{
					SlimJobConfigurationAddon.this.frame.setVisible(false);
				}
				catch(Exception e1)
				{
					client.sendError("Could not close window.", e1);
					// Should not happen!
				}
			}
		});
		
		
		DynamicPanel reflectorSettings = new DynamicPanel();
		reflectorSettings.add(reflectorLabel);
		reflectorSettings.add(reflectorField);
		
		reflectorSettings.add(new JLabel("Mode:"));
		modeField.addItem("Donut");
		modeField.addItem("Mask File");
		modeField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				boolean donut = modeField.getSelectedIndex()==0;
				maskFileLabel.setVisible(!donut);
				maskFilePanel.setVisible(!donut);
				
				centerXLabel.setVisible(donut);
				centerXField.setVisible(donut);
				centerYLabel.setVisible(donut);
				centerYField.setVisible(donut);
				innerRadiusLabel.setVisible(donut);
				innerRadiusField.setVisible(donut);
				outerRadiusLabel.setVisible(donut);
				outerRadiusField.setVisible(donut);
			}
		});
		reflectorSettings.add(modeField);
		reflectorSettings.add(maskFileLabel);
		maskFilePanel.add(maskFileField, BorderLayout.CENTER);
		JButton maskFileButton = new JButton("Select");
		maskFileButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser fileChooser =
                    new JFileChooser(client.getProperties().getProperty(LAST_SLIM_PATH_PROPERTY, "/"));
	            File file;
	            while(true)
	            {
	            	int returnVal = fileChooser.showDialog(null, "Select");
	            	if (returnVal != JFileChooser.APPROVE_OPTION)
	            	{
	            		return;
	            	}
	            	file = fileChooser.getSelectedFile().getAbsoluteFile();
	            	if(!file.exists())
	            	{
	            		JOptionPane.showMessageDialog(null, "File " + file.toString() + " does not exist.", "File does not exist", JOptionPane. INFORMATION_MESSAGE);
	            		return;
	            	}
					break;
	            }
            
	            client.getProperties().setProperty(LAST_SLIM_PATH_PROPERTY, fileChooser
	            		.getCurrentDirectory().getAbsolutePath());
	            maskFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		});
		maskFilePanel.add(maskFileButton, BorderLayout.EAST);
		reflectorSettings.add(maskFilePanel);
		
		reflectorSettings.add(centerXLabel);
		reflectorSettings.add(centerXField);
		reflectorSettings.add(centerYLabel);
		reflectorSettings.add(centerYField);
		reflectorSettings.add(innerRadiusLabel);
		reflectorSettings.add(innerRadiusField);
		reflectorSettings.add(outerRadiusLabel);
		reflectorSettings.add(outerRadiusField);
		reflectorSettings.addFillEmpty();
		
		DynamicPanel phasePanel = new DynamicPanel();
		phasePanel.add(new JLabel("Phase Shift of Background (0-255):"));
		phasePanel.add(phaseShiftBackgroundField);
		phasePanel.add(new JLabel("Phase Shifts of Masks (0-255):"));
		for(int i=0; i<phaseShiftMaskFields.length; i++)
		{
			phaseShiftMaskFields[i] = new IntegerTextField();
			phasePanel.add(phaseShiftMaskFields[i]);
		}
		phasePanel.add(new JLabel("Slim Delay (ms):"));
		phasePanel.add(reflectorDelayField);
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                JFileChooser fileChooser =
                        new JFileChooser(client.getProperties().getProperty(LAST_SLIM_PATH_PROPERTY, "/"));
                File file;
                while(true)
                {
                	int returnVal = fileChooser.showDialog(null, "Load");
                	if (returnVal != JFileChooser.APPROVE_OPTION)
                	{
                		return;
                	}
                	file = fileChooser.getSelectedFile().getAbsoluteFile();
                	if(!file.exists())
                	{
                		JOptionPane.showMessageDialog(null, "File " + file.toString() + " does not exist.", "File does not exist", JOptionPane. INFORMATION_MESSAGE);
                		return;
                	}
					break;
                }
                
                client.getProperties().setProperty(LAST_SLIM_PATH_PROPERTY, fileChooser
                        .getCurrentDirectory().getAbsolutePath());
                
                BufferedReader reader = null;
                int[] lines = new int[10];
                try
				{
					reader = new BufferedReader(new FileReader(file));
					for(int i=0; i<lines.length; i++)
					{
						String line = reader.readLine();
						if(line == null)
						{
							if(i==9)
							{
								lines[i] = 0;
								break;
							}
							throw new Exception("SLIM configuration must contain at least nine lines.");
						}
						try
						{
							lines[i]= Integer.parseInt(line);
						}
						catch(NumberFormatException ee)
						{
							throw new Exception("Line " + Integer.toString(i+1) + " is not an integer.", ee);
						}
					}
				}
				catch(Exception e1)
				{
					client.sendError("Could not load SLIM configuration protocol " + file.toString()+ ".", e1);
					return;
				}
				finally
				{
					if(reader != null)
					{
						try
						{
							reader.close();
						}
						catch(Exception e1)
						{
							client.sendError("Could not SLIM configuration file " + file.toString()+ ".", e1);
						}
					}						
				}
				// set values 
				centerXField.setValue(lines[0]);
				centerYField.setValue(lines[1]);
				innerRadiusField.setValue(lines[2]);
				outerRadiusField.setValue(lines[3]);
				phaseShiftBackgroundField.setValue(lines[4]);
				for(int i=0; i<4; i++)
				{
					phaseShiftMaskFields[i].setValue(lines[5+i]);
				}
				reflectorDelayField.setValue(lines[9]);
            }
        });
		phasePanel.add(loadButton);
		phasePanel.addFillEmpty();
		
		DescriptionPanel descriptionPanel = new DescriptionPanel("Description", "This job allows to produce SLIM images.\nTo produce such images, at least a \"Reflector\" device and a camera must be installed. After taking four images, the mask and background phase shift is set to zero.");
		descriptionPanel.setMinimumSize(new Dimension(300, 100));
		descriptionPanel.setPreferredSize(new Dimension(300, 100));
		
		// Load state
		int numCams = loadCameras();
		if(numCams <= 0)
		{
			frame.setToErrorState("No camera device defined.\nAdd camera before proceeding.", new Exception("No camera device defined.\nAdd camera before proceeding."));
			return;
		}
		else if(numCams>1)
		{
			cameraField.setVisible(true);
			cameraLabel.setVisible(true);
			
			String camera = job.getCamera();
			if(camera == null || camera.length() < 1)
			{
				// get default camera
				camera = getDefaultCameraName();
			}
			if(camera != null)
			{
				cameraField.setSelectedItem(camera);
			}
		}
		else
		{
			cameraField.setVisible(false);
			cameraLabel.setVisible(false);
		}
		loadConfigGroupNames();
		String configGroup = job.getChannelGroup();
		if(configGroup == null || configGroup.length() < 1)
			configGroup = client.getProperties().getProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL_GROUP, "");
		for(int i = 0; i < configGroupField.getItemCount(); i++)
		{
			if(configGroup.compareTo(configGroupField.getItemAt(i).toString()) == 0)
				configGroupField.setSelectedIndex(i);
		}
		loadChannels();
		for(int i = 0; i < channelField.getItemCount(); i++)
		{
			if(job.getChannel().compareTo(channelField.getItemAt(i).toString()) == 0)
				channelField.setSelectedIndex(i);
		}

		configGroupField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				loadChannels();
			}
		});
		channelField.addItemListener(new ItemListener()
		{
			private String	lastItem	= null;

			@Override
			public void itemStateChanged(ItemEvent itemEvent)
			{
				if(itemEvent.getStateChange() == ItemEvent.DESELECTED)
				{
					lastItem = itemEvent.getItem().toString();
					if(lastItem.length() > 3)
						lastItem = lastItem.substring(0, 3);
				}
				else
				{
					if(lastItem != null && lastItem.compareToIgnoreCase(nameField.getText()) == 0)
					{
						String newName = itemEvent.getItem().toString();
						if(newName.length() > 3)
							newName = newName.substring(0, 3);
						nameField.setText(newName);
					}
				}
			}
		});

		exposureField.setValue(job.getExposure());
		saveImagesField.setSelected(job.isSaveImages());
		
		String name = job.getImageSaveName();
		if(name.length() < 1)
		{
			name = channelField.getSelectedItem().toString();
			if(name.length() > 3)
				name = name.substring(0, 3);
		}
		nameField.setText(name);
		
		int numReflectors = loadReflectors();
		if(numReflectors<=0)
		{
			frame.setToErrorState("No reflector device defined.\nAdd reflector before proceeding.", new Exception("No reflector device defined.\nAdd camera before proceeding."));
			return;
		}
		reflectorField.setVisible(numReflectors>1);
		reflectorLabel.setVisible(numReflectors>1);
		if(job.getReflectorDevice() != null)
			reflectorField.setSelectedItem(job.getReflectorDevice());
		
		centerXField.setMinimalValue(0);
		centerXField.setValue(job.getMaskX());
		
		centerYField.setMinimalValue(0);
		centerYField.setValue(job.getMaskY());
		
		innerRadiusField.setMinimalValue(0);
		innerRadiusField.setValue(job.getInnerRadius());
		
		outerRadiusField.setMinimalValue(0);
		outerRadiusField.setValue(job.getOuterRadius());
		
		String maskFile = job.getMaskFileName();
		if(maskFile != null)
		{
			maskFileField.setText(maskFile);
			modeField.setSelectedIndex(1);
		}
		else
			modeField.setSelectedIndex(0);
		
		reflectorDelayField.setMinimalValue(0);
		reflectorDelayField.setValue(job.getSlimDelayMS());
		
		phaseShiftBackgroundField.setMinimalValue(SlimJobConfigurationDTO.MIN_PHASE_SHIFT);
		phaseShiftBackgroundField.setMaximalValue(SlimJobConfigurationDTO.MIN_PHASE_SHIFT);
		phaseShiftBackgroundField.setValue(job.getPhaseShiftOutside());
		
		for(int i=0; i<phaseShiftMaskFields.length; i++)
		{
			phaseShiftMaskFields[i].setMinimalValue(SlimJobConfigurationDTO.MIN_PHASE_SHIFT);
			phaseShiftMaskFields[i].setMaximalValue(SlimJobConfigurationDTO.MIN_PHASE_SHIFT);
			phaseShiftMaskFields[i].setValue(job.getPhaseShiftMask(i));
		}
		
		phasePanel.setBorder(new TitledBorder("Phase Shift"));
		reflectorSettings.setBorder(new TitledBorder("Mask Type"));
		elementsPanel.setBorder(new TitledBorder("Camera Settings"));
		JPanel mainPanel = new JPanel(new GridLayout(1,3));
		mainPanel.add(elementsPanel);
		mainPanel.add(reflectorSettings);
		mainPanel.add(phasePanel);
		
		JPanel buttonsPanel = new JPanel(new GridLayout(2,1,5,5));
		buttonsPanel.add(snapSlimButton);
		buttonsPanel.add(addJobButton);
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(mainPanel, BorderLayout.CENTER);
		contentPane.add(descriptionPanel, BorderLayout.NORTH);
		contentPane.add(buttonsPanel, BorderLayout.SOUTH);
		frame.setContentPane(contentPane);
		frame.pack();
	}
	
	private void commitChanges()
	{
		job.setChannel((String)configGroupField.getSelectedItem(), (String)channelField.getSelectedItem());
		job.setExposure(((Number)exposureField.getValue()).doubleValue());
		job.setSaveImages(saveImagesField.isSelected());
		job.setImageSaveName(nameField.getText());
		job.setCamera((String)cameraField.getSelectedItem());
		
		job.setReflectorDevice((String)reflectorField.getSelectedItem());
		job.setMaskX(centerXField.getValue());
		job.setMaskY(centerYField.getValue());
		job.setInnerRadius(innerRadiusField.getValue());
		job.setOuterRadius(outerRadiusField.getValue());
		job.setPhaseShiftOutside(phaseShiftBackgroundField.getValue());
		job.setSlimDelayMS(reflectorDelayField.getValue());
		for(int i=0; i<SlimJobConfigurationDTO.NUM_PHASE_SHIFT_MASK; i++)
		{
			job.setPhaseShiftMask(i, phaseShiftMaskFields[i].getValue());
		}
		if(modeField.getSelectedIndex() == 0)
		{
			job.setMaskFileName(null);
		}
		else
			job.setMaskFileName(maskFileField.getText());
		
		client.getProperties().setProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL_GROUP, (String)configGroupField.getSelectedItem());
	}

	private void snapSlim()
	{
		commitChanges();
		// Create slim image window
		final ImagePanel imagePanel = new ImagePanel(client);
		final YouScopeFrame childFrame = imagePanel.toFrame();
		frame.addModalChildFrame(childFrame);
		childFrame.setVisible(true);
		childFrame.startLoading();
		class ImageSnapper implements Runnable
		{
			@Override
			public void run()
			{
				try
				{
					SlimJobConfigurationDTO config = getConfigurationData();
					
					if(config.getReflectorDevice() == null)
						throw new Exception("Reflector device not set.");
					Device reflectorDevice;
					try
					{
						reflectorDevice = server.getMicroscope().getDevice(config.getReflectorDevice());
					}
					catch(DeviceException e1)
					{
						throw new Exception("Could not find reflector with device ID \"" + config.getReflectorDevice() + "\".", e1);
					}
					if(!reflectorDevice.getDriverID().equals("Reflector"))
					{
						throw new Exception("Device set to serve as the reflector must be of type \"Reflector\". DriverID of device \"" + reflectorDevice.getDeviceID() + "\" is \"" + reflectorDevice.getDriverID()+"\".");
					}
					// make images
					ImageEvent[] images = new ImageEvent[4];
					try
					{
						if(config.getMaskFileName() == null)
						{
							reflectorDevice.getProperty("mode").setValue("donut");
							reflectorDevice.getProperty("donut.centerX").setValue(Integer.toString(config.getMaskX()));
							reflectorDevice.getProperty("donut.centerY").setValue(Integer.toString(config.getMaskY()));
							reflectorDevice.getProperty("donut.innerRadius").setValue(Integer.toString(config.getInnerRadius()));
							reflectorDevice.getProperty("donut.outerRadius").setValue(Integer.toString(config.getOuterRadius()));
						}
						else
						{
							reflectorDevice.getProperty("mode").setValue("mask");
							reflectorDevice.getProperty("mask.file").setValue(config.getMaskFileName());
						}
						
						CameraDevice camera;
						if(config.getCamera() == null)
							camera = server.getMicroscope().getCameraDevice();
						else
							camera = server.getMicroscope().getCameraDevice(config.getCamera());
						if(camera == null)
							throw new Exception("Cannot find camera.");
						reflectorDevice.getProperty("phaseShiftBackground").setValue(Integer.toString(config.getPhaseShiftOutside()));
						for(int i=0; i<images.length; i++)
						{
							reflectorDevice.getProperty("phaseShiftForeground").setValue(Integer.toString(config.getPhaseShiftMask(i)));
							if(config.getSlimDelayMS()>0)
								Thread.sleep(config.getSlimDelayMS());
							images[i] = camera.makeImage(config.getChannelGroup(), config.getChannel(), config.getExposure());
						}
						reflectorDevice.getProperty("phaseShiftBackground").setValue("0");
						reflectorDevice.getProperty("phaseShiftForeground").setValue("0");
					}
					catch(Exception e)
					{
						throw new Exception("Could not take SLIM images.", e);
					}
					ImageEvent slimImage = SlimHelper.calculateSlimImage(images);
					// Show image
					childFrame.endLoading();
					imagePanel.setImage(slimImage);
				}
				catch(Exception e)
				{
					childFrame.setToErrorState("Could not take SLIM image.", e);
					return;
				}
			}
		}
		new Thread(new ImageSnapper()).start();
	}
	
	private void snapImage(String camera, String channelGroup, String channel, double exposure)
	{
		// Create snap image window
		ImagePanel imagePanel = new ImagePanel(client);
		YouScopeFrame childFrame = imagePanel.toFrame();
		frame.addModalChildFrame(childFrame);
		childFrame.setVisible(true);
		childFrame.startLoading();
		
		// Make image
		class ImageSnapper implements Runnable
		{
			private final YouScopeFrame childFrame;
			private final ImagePanel imagePanel;
			private final String channelGroup;
			private final String channel;
			private final double exposure;
			private final String camera;
			ImageSnapper(YouScopeFrame childFrame, ImagePanel imagePanel, String camera, String channelGroup, String channel, double exposure)
			{
				this.channel = channel;
				this.channelGroup = channelGroup;
				this.childFrame = childFrame;
				this.imagePanel = imagePanel;
				this.exposure = exposure;
				this.camera = camera;
			}

			@Override
			public void run()
			{
				ImageEvent imageEvent;
				try
				{
					CameraDevice cameraDevice;
					if(camera != null && camera.length() > 0)
						cameraDevice = server.getMicroscope().getCameraDevice(camera);
					else
						cameraDevice = server.getMicroscope().getCameraDevice();
					if(cameraDevice == null)
						throw new Exception("Camera was not found.");
					
					
					imageEvent = cameraDevice.makeImage(channelGroup, channel, exposure);
				}
				catch(Exception e1)
				{
					childFrame.setToErrorState("Error occured while taking image.", e1);
					return;
				}
				if(imageEvent == null)
				{
					childFrame.setToErrorState("No image was returned by the microscope.", null);
					return;
				}
				
				// Show image
				childFrame.endLoading();
				imagePanel.setImage(imageEvent);
			}
		}
		new Thread(new ImageSnapper(childFrame, imagePanel, camera, channelGroup, channel, exposure)).start();
	}
	
	@Override
	public void setConfigurationData(JobConfiguration job) throws ConfigurationException
	{
		if(!(job instanceof SlimJobConfigurationDTO))
			throw new ConfigurationException("Configuration not supported by this addon.");
		this.job = (SlimJobConfigurationDTO)job;
	}

	@Override
	public SlimJobConfigurationDTO getConfigurationData()
	{
		return job;
	}

	@Override
	public void addConfigurationListener(JobConfigurationAddonListener listener)
	{
		configurationListeners.add(listener);
	}

	@Override
	public void removeConfigurationListener(JobConfigurationAddonListener listener)
	{
		configurationListeners.remove(listener);
	}

	@Override
	public String getConfigurationID()
	{
		return SlimJobConfigurationDTO.TYPE_IDENTIFIER;
	}
}
