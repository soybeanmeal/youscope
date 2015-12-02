/**
 * 
 */
package ch.ethz.csb.youscope.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.ServiceLoader;

import ch.ethz.csb.youscope.client.addon.CallbackAddonFactory;
import ch.ethz.csb.youscope.client.addon.YouScopeClient;
import ch.ethz.csb.youscope.shared.YouScopeServer;
import ch.ethz.csb.youscope.shared.measurement.callback.Callback;
import ch.ethz.csb.youscope.shared.measurement.callback.CallbackCreationException;
import ch.ethz.csb.youscope.shared.measurement.callback.CallbackProvider;

/**
 * @author Moritz Lang
 *
 */
class CallbackProviderImpl extends UnicastRemoteObject implements CallbackProvider
{

	/**
	 * Serial Version UID.
	 */
	private static final long	serialVersionUID	= 6240666905518828924L;
	
	private HashMap<String, CallbackAddonFactory> callbackFactories = null;

	private final YouScopeClient client;
	private final  YouScopeServer server;
	/**
	 * @throws RemoteException
	 */
	protected CallbackProviderImpl(YouScopeClient client, YouScopeServer server) throws RemoteException
	{
		super();
		this.client = client;
		this.server = server;
	}
	
	private void initialize()
	{
		if(callbackFactories != null)
			return;
		callbackFactories = new HashMap<String, CallbackAddonFactory>();
		for(CallbackAddonFactory callbackFactory : ServiceLoader.load(CallbackAddonFactory.class, CallbackProviderImpl.class.getClassLoader()))
		{
			for(String typeIdentifier : callbackFactory.getSupportedTypeIdentifiers())
			{
				callbackFactories.put(typeIdentifier, callbackFactory);
			}
		}
	}

	@Override
	public Callback createCallback(String typeIdentifier) throws CallbackCreationException {
		initialize();
		CallbackAddonFactory factory = callbackFactories.get(typeIdentifier);
		if(factory == null)
			throw new CallbackCreationException("No callback available with type identifier "+typeIdentifier+".");
		return factory.createCallback(typeIdentifier, client, server);
	}

	@Override
	public <T extends Callback> T createCallback(String typeIdentifier, Class<T> callbackInterface)
			throws RemoteException, CallbackCreationException {
		Callback callback = createCallback(typeIdentifier);
		if(callbackInterface.isInstance(callback))
			return callbackInterface.cast(callback);
		throw new CallbackCreationException("Callback with callback type identifier " + typeIdentifier+" is of class "+callback.getClass().getName()+", which does not implement interface "+callbackInterface.getName()+".");
	}
}
