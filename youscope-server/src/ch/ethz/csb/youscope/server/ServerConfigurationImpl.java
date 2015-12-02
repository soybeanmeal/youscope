/**
 * 
 */
package ch.ethz.csb.youscope.server;

/**
 * @author langmo
 * 
 */
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.script.ScriptEngineFactory;

import ch.ethz.csb.youscope.shared.YouScopeServerConfiguration;
import ch.ethz.csb.youscope.shared.addon.GeneralPurposeAddon;

/**
 * @author langmo
 */
class ServerConfigurationImpl extends UnicastRemoteObject implements YouScopeServerConfiguration
{

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 856785424972712007L;

    /**
     * @throws RemoteException
     */
    protected ServerConfigurationImpl() throws RemoteException
    {
        super();
    }

    @Override
	public String[] getSupportedImageFormats()
    {
        Thread.currentThread()
                .setContextClassLoader(ServerConfigurationImpl.class.getClassLoader());
        ImageIO.scanForPlugins();
        String[] result = ImageIO.getWriterFormatNames();
        Arrays.sort(result);

        return result;
    }

    @Override
	public byte[] getIP() throws UnknownHostException
    {
        InetAddress addr = InetAddress.getLocalHost();
        // Get IP Address
        return addr.getAddress();
    }

    @Override
	public String[] getSupportedScriptEngines() throws RemoteException
    {
        Vector<String> engineNames = new Vector<String>();
        for (ScriptEngineFactory factory : ServerSystem.getScriptEngineManager()
                .getEngineFactories())
        {
            engineNames.add(factory.getEngineName());
        }

        return engineNames.toArray(new String[engineNames.size()]);
    }

    @Override
	public <T extends GeneralPurposeAddon> T getGeneralAddon(Class<T> addonInterface)
            throws RemoteException
    {
        return ServerSystem.getGeneralAddon(addonInterface);
    }

    @Override
	public <T extends GeneralPurposeAddon> T[] getGeneralAddons(Class<T> addonInterface)
            throws RemoteException
    {
        return ServerSystem.getGeneralAddons(addonInterface);
    }

    @Override
	public GeneralPurposeAddon[] getGeneralAddons() throws RemoteException
    {
        return ServerSystem.getGeneralAddons();
    }
}
