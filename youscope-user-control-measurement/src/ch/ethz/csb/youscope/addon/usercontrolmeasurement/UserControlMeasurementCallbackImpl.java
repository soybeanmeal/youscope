/**
 * 
 */
package ch.ethz.csb.youscope.addon.usercontrolmeasurement;

import java.awt.Dimension;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import ch.ethz.csb.youscope.client.addon.YouScopeClient;
import ch.ethz.csb.youscope.client.addon.YouScopeFrame;
import ch.ethz.csb.youscope.client.addon.YouScopeFrameListener;
import ch.ethz.csb.youscope.shared.ImageEvent;
import ch.ethz.csb.youscope.shared.YouScopeServer;
import ch.ethz.csb.youscope.shared.measurement.callback.CallbackException;

/**
 * @author Moritz Lang
 *
 */
class UserControlMeasurementCallbackImpl extends UnicastRemoteObject implements UserControlMeasurementCallback
{

	/**
	 * Serial Version UID.
	 */
	private static final long	serialVersionUID	= 2623954605664232222L;

	private final YouScopeClient client;
	private final YouScopeServer server;
	
	private volatile YouScopeFrame frame = null;
	
	private final ArrayList<UserControlMeasurementCallbackListener> listeners = new ArrayList<UserControlMeasurementCallbackListener>();
	
	private volatile UserControlMeasurementFrame userFrame = null;
	/**
	 * Constructor.
	 * @param client interface to use scope client.
	 * @param server interface to the YouScope server.
	 * @throws RemoteException
	 */
	public UserControlMeasurementCallbackImpl(YouScopeClient client, YouScopeServer server) throws RemoteException
	{
		super();
		this.client = client;
		this.server = server;
	}

	@Override
	public void pingCallback() throws RemoteException
	{
		// do nothing.
	}

	@Override
	public synchronized void initializeCallback() throws RemoteException, CallbackException
	{
		frame = client.createFrame();
		// Allow closing, at stop measurement if frame is closed.
		frame.setClosable(false);
		frame.setMaximizable(true);
		frame.setResizable(true);
		frame.setTitle("User Control Measurement");
		frame.addFrameListener(new YouScopeFrameListener()
		{
			@Override
			public void frameClosed()
			{
				sendCallbackClosed();
			}

			@Override
			public void frameOpened()
			{
				// do nothing.
			}
		});
		
		try
		{
			userFrame = new UserControlMeasurementFrame(client, server, this);
		}
		catch(Exception e)
		{
			throw new CallbackException("Could not show user control frame.", e);
		}
		frame.setContentPane(userFrame);
		frame.pack();
		Dimension size = frame.getSize();
		size.width = 800;
		frame.setSize(size);
		frame.setVisible(true);
	}

	@Override
	public synchronized void uninitializeCallback() throws RemoteException, CallbackException
	{
		if(frame != null)
			frame.setVisible(false);
	}

	@Override
	public void addCallbackListener(UserControlMeasurementCallbackListener listener) throws RemoteException
	{
		synchronized(listeners)
		{
			listeners.add(listener);
		}
	}

	@Override
	public void removeCallbackListener(UserControlMeasurementCallbackListener listener) throws RemoteException
	{
		synchronized(listeners)
		{
			listeners.remove(listener);
		}
	}

	@Override
	public String getCurrentChannel() throws RemoteException
	{
		if(userFrame != null)
			return userFrame.getCurrentChannel();
		return null;
	}

	@Override
	public String getCurrentChannelGroup() throws RemoteException
	{
		if(userFrame != null)
			return userFrame.getCurrentChannelGroup();
		return null;
	}

	@Override
	public double getCurrentExposure() throws RemoteException
	{
		if(userFrame != null)
			return userFrame.getCurrentExposure();
		return -1;
	}

	void sendChannelSettingsChanged()
	{
		synchronized(listeners)
		{
			for(int i=0; i<listeners.size(); i++)
			{
				try
				{
					listeners.get(i).channelSettingsChanged();
				}
				catch(RemoteException e)
				{
					client.sendError("Could not send to measurement that channel settings changed.", e);
					
				}
			}
		}
	}
	
	void sendSnapImage()
	{
		synchronized(listeners)
		{
			for(int i=0; i<listeners.size(); i++)
			{
				try
				{
					listeners.get(i).snapImage();
				}
				catch(RemoteException e)
				{
					client.sendError("Could not send to measurement to snap an image.", e);
					
				}
			}
		}
	}
		
	void sendCallbackClosed()
	{
		synchronized(listeners)
		{
			for(int i=0; i<listeners.size(); i++)
			{
				try
				{
					listeners.get(i).callbackClosed();
				}
				catch(RemoteException e)
				{
					client.sendError("Could not send to measurement to finish.", e);
					
				}
			}
		}
	}

	@Override
	public void newImage(ImageEvent e) throws RemoteException 
	{
		if(userFrame != null)
			userFrame.newImage(e);
	}

	@Override
	public void snappedImage() throws RemoteException
	{
		if(userFrame != null)
			userFrame.snappedImage();
	}

	@Override
	public String getTypeIdentifier() throws RemoteException {
		return TYPE_IDENTIFIER;
	}
}
