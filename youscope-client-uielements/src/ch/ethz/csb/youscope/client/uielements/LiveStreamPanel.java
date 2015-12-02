package ch.ethz.csb.youscope.client.uielements;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import ch.ethz.csb.youscope.client.addon.YouScopeClient;
import ch.ethz.csb.youscope.client.addon.YouScopeFrame;
import ch.ethz.csb.youscope.client.addon.YouScopeFrameListener;
import ch.ethz.csb.youscope.client.addon.YouScopeProperties;
import ch.ethz.csb.youscope.shared.ImageEvent;
import ch.ethz.csb.youscope.shared.ImageListener;
import ch.ethz.csb.youscope.shared.YouScopeServer;
import ch.ethz.csb.youscope.shared.measurement.Measurement;
import ch.ethz.csb.youscope.shared.measurement.MeasurementProvider;

/**
 * A panel to configure, start and stop a live stream.
 * @author Moritz Lang
 *
 */
public class LiveStreamPanel extends ImagePanel {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 5555935386353363014L;
	private final YouScopeClient client;
	private final YouScopeServer server;
	
	private final ChannelControl channelControl;
	private final StartStopControl startStopControl;
	private volatile Measurement measurement = null;
	private volatile ImageHandler imageHandler = null;
	private volatile boolean streamRunning = false;
	private JFrame fullScreenFrame = null;
	
	private YouScopeFrame frame = null;
	
	private boolean autostart = false;
	/**
	 * Constructor.
	 * @param client YouScope client.
	 * @param server YouScope server.
	 */
	public LiveStreamPanel(YouScopeClient client, YouScopeServer server) 
	{
		super(client);
		setNoImageText("Press start to start imaging");
		setTitle("LiveStream");
		this.client = client;
		this.server = server;
		
		channelControl = new ChannelControl();
		startStopControl = new StartStopControl();
		
		setUserChoosesAutoAdjustContrast(true);
		insertControl("Imaging", channelControl, 0);
		addControl("Control", startStopControl);
	}
	
	/**
	 * Set to true to show the LiveStream in full-screen mode. Set to fals again to stop full-sceen again.
	 * @param fullScreen True to start, false to stop full-screen.
	 */
	public void setFullScreen(boolean fullScreen)
	{
		if(!fullScreen && fullScreenFrame != null)
		{
			GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].setFullScreenWindow(null);
			fullScreenFrame.setVisible(false);
			fullScreenFrame.dispose();
			fullScreenFrame = null;
			if(frame != null)
			{
				frame.setContentPane(this);
			}
		}
		else if(fullScreen)
		{
			if(frame != null)
				frame.setContentPane(new JLabel("<html>LiveStream is currently<br />in full-screen mode</html>"));
			fullScreenFrame = new JFrame("LiveStream");
			fullScreenFrame.setUndecorated(true);
			fullScreenFrame.setLayout(new BorderLayout());
			fullScreenFrame.add(this, BorderLayout.CENTER);
			fullScreenFrame.pack();
			fullScreenFrame.setVisible(true);
			fullScreenFrame.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosed(WindowEvent arg0) {
					setFullScreen(false);
				}
	
				@Override
				public void windowClosing(WindowEvent arg0) {
					setFullScreen(false);
				}
			});
			GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].setFullScreenWindow(fullScreenFrame);
		}
		startStopControl.updateButtons();
	}
	
	/**
	 * Call this function such that the current channel settings get saved. When starting a LiveStream again, the same settings will appear.
	 */
	public void saveSettings()
	{
		String camera = channelControl.getCamera();
    	String channelGroup = channelControl.getChannelGroup();
    	String channel = channelControl.getChannel();
    	double exposure = channelControl.getExposure();
    	int imagingPeriod = channelControl.getImagingPeriod();
    	
    	YouScopeProperties props = client.getProperties();
    	props.setProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL, channel);
    	props.setProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL_GROUP, channelGroup);
    	props.setProperty(YouScopeProperties.PROPERTY_LAST_LIVE_CAMERA, camera);
    	props.setProperty(YouScopeProperties.PROPERTY_LAST_LIVE_EXPOSURE, exposure);
    	props.setProperty(YouScopeProperties.PROPERTY_LAST_LIVE_PERIOD, imagingPeriod);
	}
	
	/**
	 * Returns a frame listener which can be added to the frame to which this panel is added, taking care that the LiveStream automatically
	 * stops when the frame closes (and, that it automatically starts when the frame opens, if autostart is set to true).
	 * @return Frame listener.
	 */
	public YouScopeFrameListener getFrameListener()
	{
		return new YouScopeFrameListener() {
			
			@Override
			public void frameOpened() {
				if(autostart)
					startLiveStream();
			}
			
			@Override
			public void frameClosed() {
				stopLiveStream();
				saveSettings();
			}
		};
	}
	
	@Override
	public YouScopeFrame toFrame()
	{
		if(frame == null)
		{
			frame = super.toFrame();
			frame.addFrameListener(getFrameListener());
		}
		return frame;
	}
	
	private class ImageHandler extends Thread
    {
    	private volatile ImageEvent nextImage;
    	private volatile boolean shouldRun = true;
    	private class ImageListenerImpl extends UnicastRemoteObject implements ImageListener
    	{
    		/**
			 * Serial Version UID.
			 */
			private static final long	serialVersionUID	= -4135351939442377119L;

			/**
    		 * Constructor
			 * @throws RemoteException
			 */
			protected ImageListenerImpl() throws RemoteException
			{
				super();
			}

			@Override
    		public void imageMade(ImageEvent e) throws RemoteException
    		{
    			synchronized(ImageHandler.this)
    			{
    				nextImage = e;
    				ImageHandler.this.notifyAll();
    			}
    		}
    	}
    	private volatile ImageListenerImpl listener = null;
		
		
		synchronized void stopListening()
		{
			shouldRun = false;
			this.notifyAll();
		}
		
		ImageListener startListening() throws RemoteException
		{
			start();
			if(listener == null)
				listener = new ImageListenerImpl();
			return listener;
		}
    	
		@Override
		public void run()
		{
			while(shouldRun)
			{
				// get image.
				ImageEvent image = null;
				synchronized(this)
				{
					while(shouldRun && nextImage == null)
					{
						try
						{
							this.wait();
						}
						catch(InterruptedException e)
						{
							client.sendError("Microscope image processor was interrupted.", e);
							return;
						}
					}
					if(!shouldRun)
						return;
					image = nextImage;
					nextImage = null;
				}
				
				// process image
				setImage(image);
				
			}
			
		}
		
    }
	private class ChannelControl extends DynamicPanel
	{
		/**
		 * Serial Version UID.
		 */
		private static final long serialVersionUID = 6561875723795313098L;
		final CameraField cameraField = new CameraField(client, server);
		final ChannelField channelField = new ChannelField(client, server);
		final DoubleTextField exposureField = new DoubleTextField();
		final IntegerTextField imagingPeriodField = new IntegerTextField();
		ChannelControl()
		{
			exposureField.setMinimalValue(0);
			imagingPeriodField.setMinimalValue(0);
			
			// Load settings
			exposureField.setValue(client.getProperties().getProperty(YouScopeProperties.PROPERTY_LAST_LIVE_EXPOSURE, 50.0));
			imagingPeriodField.setValue(client.getProperties().getProperty(YouScopeProperties.PROPERTY_LAST_LIVE_PERIOD, 100));
			cameraField.setCamera(client.getProperties().getProperty(YouScopeProperties.PROPERTY_LAST_LIVE_CAMERA, (String)null));
			channelField.setChannel(client.getProperties().getProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL_GROUP, (String)null), 
					client.getProperties().getProperty(YouScopeProperties.PROPERTY_LAST_CHANNEL, (String)null));
			
			ActionListener changeListener = new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					if(streamRunning)
						startLiveStream();
				}
			};
			
			JLabel label;
			if(cameraField.isChoice())
			{
				label = new JLabel("Camera:");
				add(label);
				add(cameraField);
				cameraField.addActionListener(changeListener);
			}
			
			label = new JLabel("Channel:");
			label.setForeground(Color.WHITE);
			add(label);
			add(channelField);
			channelField.addActionListener(changeListener);
			
			label = new JLabel("Exposure (ms):");
			label.setForeground(Color.WHITE);
			add(label);
			add(exposureField);
			exposureField.addActionListener(changeListener);
			
			label = new JLabel("Imaging Period (ms):");
			label.setForeground(Color.WHITE);
			add(label);
			add(imagingPeriodField);
			imagingPeriodField.addActionListener(changeListener);
		}
		String getChannel()
		{
			return channelField.getChannel();
		}
		String getChannelGroup()
		{
			return channelField.getChannelGroup();
		}
		double getExposure()
		{
			return exposureField.getValue();
		}
		int getImagingPeriod()
		{
			return imagingPeriodField.getValue();
		}
		String getCamera()
		{
			return cameraField.getCameraDevice();
		}
	}
	
	/**
	 * If set to true, the stream automatically starts when the window is made visible.
	 * Note: the stream automatically stops if the frame is hidden.
	 * Has only an effect if #toFrame() is used.
	 * @param autostart true to automatically start imaging when frame shows up.
	 */
	public void setAutoStartStream(boolean autostart)
	{
		this.autostart = autostart;
	}
	
	/**
	 * Set to true to display a button with which the user can choose on him/herself if the LiveStream should be shown in full-screen mode. Default is false.
	 * @param userChooses True to display fullscreen button.
	 */
	public void setUserChoosesFullScreen(boolean userChooses)
	{
		startStopControl.fullScreenButton.setVisible(userChooses);
	}
	
	private class StartStopControl extends DynamicPanel
	{
		/**
		 * Serial Version UID.
		 */
		private static final long serialVersionUID = -4369903187110180162L;
		private final String START_TEXT = "start imaging";
		private final String STOP_TEXT = "stop imaging";
		private final String SNAP_TEXT = "snap image";
		
		private final String START_FULLSCREEN_TEXT = "full-screen";
		private final String STOP_FULLSCREEN_TEXT = "stop full-screen";
		
		final JButton startStopButton = new JButton(START_TEXT);
		final JButton snapImageButton = new JButton(SNAP_TEXT);
		final JButton fullScreenButton = new JButton(START_FULLSCREEN_TEXT);
		
		final Icon startIcon = ImageLoadingTools.getResourceIcon("icons/film.png", "start stream");
		final Icon stopIcon = ImageLoadingTools.getResourceIcon("icons/cross-button.png", "stop stream");
		final Icon snapIcon = ImageLoadingTools.getResourceIcon("icons/picture.png", "snap image");
		final Icon startFullScreenIcon = ImageLoadingTools.getResourceIcon("icons/monitor-image.png", "snap image");
		final Icon stopFullScreenIcon = ImageLoadingTools.getResourceIcon("icons/monitor-window-3d.png", "snap image");
		StartStopControl()
		{
			startStopButton.setText(START_TEXT);
			if(startIcon != null)
			{
				startStopButton.setIcon(startIcon);
			}
			startStopButton.setOpaque(false);
			startStopButton.setHorizontalAlignment(SwingConstants.LEFT);
			startStopButton.setToolTipText(START_TEXT);
			add(startStopButton);
			startStopButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!streamRunning)
					{
						startLiveStream();
					}
					else
					{
						stopLiveStream();
					}
				}
			});
			
			snapImageButton.setText(SNAP_TEXT);
			if(snapIcon != null)
			{
				snapImageButton.setIcon(snapIcon);
			}
			snapImageButton.setOpaque(false);
			snapImageButton.setHorizontalAlignment(SwingConstants.LEFT);
			snapImageButton.setToolTipText(SNAP_TEXT);
			add(snapImageButton);
			snapImageButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					snapImage();
				}
			});
			
			fullScreenButton.setText(START_FULLSCREEN_TEXT);
			if(startFullScreenIcon != null)
			{
				fullScreenButton.setIcon(startFullScreenIcon);
			}
			fullScreenButton.setOpaque(false);
			fullScreenButton.setHorizontalAlignment(SwingConstants.LEFT);
			fullScreenButton.setToolTipText(START_FULLSCREEN_TEXT);
			add(fullScreenButton);
			fullScreenButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					if(fullScreenFrame != null)
						setFullScreen(false);
					else
						setFullScreen(true);
				}
			});
			fullScreenButton.setVisible(false);
		}
		void updateButtons()
		{
			if(streamRunning)
			{
				startStopButton.setText(STOP_TEXT);
				if(stopIcon != null)
				{
					startStopButton.setIcon(stopIcon);
				}
				startStopButton.setToolTipText(STOP_TEXT);
				snapImageButton.setEnabled(false);
			}
			else
			{
				startStopButton.setText(START_TEXT);
				if(startIcon != null)
				{
					startStopButton.setIcon(startIcon);
				}
				startStopButton.setToolTipText(START_TEXT);
				snapImageButton.setEnabled(true);
			}
			if(fullScreenFrame != null)
			{
				fullScreenButton.setText(STOP_FULLSCREEN_TEXT);
				if(stopFullScreenIcon != null)
				{
					fullScreenButton.setIcon(stopFullScreenIcon);
				}
				fullScreenButton.setToolTipText(STOP_FULLSCREEN_TEXT);
			}
			else
			{
				fullScreenButton.setText(START_FULLSCREEN_TEXT);
				if(startFullScreenIcon != null)
				{
					fullScreenButton.setIcon(startFullScreenIcon);
				}
				fullScreenButton.setToolTipText(START_FULLSCREEN_TEXT);
			}
		}
	}
	
	/**
     * Stops the live stream. Does nothing if live stream is not running.
     * The call immediately returns, even if the stopping of the live stream takes longer.
     */
    public void stopLiveStream()
    {
    	streamRunning = false;
    	startStopControl.updateButtons();
        synchronized (this)
        {
            if (measurement != null)
            {
                try
                {
                    measurement.stopMeasurement();
                } 
                catch (RemoteException e)
                {
                    client.sendError("Could not stop measurement.", e);
                }
                measurement = null;
            }
            if(imageHandler != null)
            {
            	imageHandler.stopListening();
                imageHandler = null;
            }
        }
    }
    
    /**
     * Stops the live stream. Does nothing if live stream is not running.
     * The call returns after the live stream has stopped.
     */
    public void stopLiveStreamAndWait()
    {
    	streamRunning = false;
    	startStopControl.updateButtons();
    	synchronized (this)
        {
            if (measurement != null)
            {
                try
                {
                    measurement.stopMeasurement();
                    measurement.waitForMeasurementFinish();
                } 
                catch (RemoteException e)
                {
                	client.sendError("Could not stop measurement.", e);
                }

                measurement = null;
            }
            if(imageHandler != null)
            {
            	imageHandler.stopListening();
                imageHandler = null;
            }
        }
    }

    /**
     * Snaps a single image with the current settings.
     */
    public void snapImage()
    {
    	String camera = channelControl.getCamera();
    	String channelGroup = channelControl.getChannelGroup();
    	String channel = channelControl.getChannel();
    	double exposure = channelControl.getExposure();
    	try {
			setImage(server.getMicroscope().getCameraDevice(camera).makeImage(channelGroup, channel, exposure));
		} catch (Exception e) {
			client.sendError("Could not snap image.", e);
		} 
    }
    
    /**
     * Starts the live stream. If the live stream is already running, it restarts it.
     */
    public void startLiveStream()
    {
    	String camera = channelControl.getCamera();
    	String channelGroup = channelControl.getChannelGroup();
    	String channel = channelControl.getChannel();
    	double exposure = channelControl.getExposure();
    	int imagingPeriod = channelControl.getImagingPeriod();
    	
    	// stop any previous measurement
    	if(streamRunning)
    	{
    		stopLiveStreamAndWait();	
    	}
    	streamRunning = true;
    	startStopControl.updateButtons();
    	
        // Create measurement on server
        try
        {
            MeasurementProvider measurementFactory =server.getMeasurementFactory();
            synchronized (this)
            {
                // Create measurement
            	imageHandler = new ImageHandler();
                measurement = measurementFactory.createContinuousMeasurement((camera == null || camera.length() < 1) ? null : camera, channelGroup, channel, imagingPeriod, exposure, imageHandler.startListening());
                
                // Start measurement
                measurement.startMeasurement();
            }
        } 
        catch (Exception e)
        {
        	client.sendError("Could not create/start measurement", e);
            measurement = null;
            return;
        }
    }
}
