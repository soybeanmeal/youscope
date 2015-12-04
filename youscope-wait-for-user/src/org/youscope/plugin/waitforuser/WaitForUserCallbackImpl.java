/**
 * 
 */
package org.youscope.plugin.waitforuser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.youscope.clientinterfaces.YouScopeClient;
import org.youscope.clientinterfaces.YouScopeFrame;
import org.youscope.clientinterfaces.YouScopeFrameListener;
import org.youscope.common.measurement.callback.CallbackException;

/**
 * @author Moritz Lang
 *
 */
class WaitForUserCallbackImpl extends UnicastRemoteObject implements WaitForUserCallback
{

	/**
	 * Serial Version UID.
	 */
	private static final long	serialVersionUID	= 2623954605664239250L;

	private final YouScopeClient client;
	
	private volatile boolean finishWaiting = true;
	
	/**
	 * Constructor.
	 * @param client interface to use scope client.
	 * @throws RemoteException
	 */
	public WaitForUserCallbackImpl(YouScopeClient client) throws RemoteException
	{
		super();
		this.client = client;
	}

	@Override
	public void pingCallback() throws RemoteException
	{
		// do nothing.
	}

	@Override
	public void initializeCallback() throws RemoteException, CallbackException
	{
		finishWaiting = true;
	}

	@Override
	public void uninitializeCallback() throws RemoteException, CallbackException
	{
		finishWaiting = true;
	}

	@Override
	public void waitForUser(String message) throws RemoteException, InterruptedException
	{
		// Create frame
		YouScopeFrame frame = client.createFrame();
		JButton okButton = new JButton("OK");
		class CloseListener implements ActionListener
		{
			private final YouScopeFrame frame;
			CloseListener(YouScopeFrame frame)
			{
				this.frame = frame;
			}
			@Override
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
			}
			
		}
		okButton.addActionListener(new CloseListener(frame));
		JTextArea textArea = new JTextArea(message);
		textArea.setEditable(false);
		JPanel contentPane = new JPanel(new BorderLayout(5, 5));
		contentPane.add(new JScrollPane(textArea), BorderLayout.CENTER);
		contentPane.add(okButton, BorderLayout.SOUTH);
		frame.setContentPane(contentPane);
		
		// Layout frame, add listener and display it.
		frame.pack();
		frame.addFrameListener(new YouScopeFrameListener()
		{
			@Override
			public void frameClosed()
			{
				synchronized(WaitForUserCallbackImpl.this)
				{
					finishWaiting = true;
					WaitForUserCallbackImpl.this.notifyAll();
				}
			}

			@Override
			public void frameOpened()
			{
				// do nothing.
			}
		});
		synchronized(this)
		{
			finishWaiting = false;
			frame.setVisible(true);
			try
			{
				while(!finishWaiting)
				{
					this.wait();
				}
			}
			catch(InterruptedException e)
			{
				frame.setVisible(false);
				throw e;
			}
		}		
	}

	@Override
	public String getTypeIdentifier() throws RemoteException {
		return TYPE_IDENTIFIER;
	}

}