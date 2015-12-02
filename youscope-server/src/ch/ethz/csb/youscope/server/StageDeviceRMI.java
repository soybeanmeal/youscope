/**
 * 
 */
package ch.ethz.csb.youscope.server;

import java.awt.geom.Point2D.Double;
import java.rmi.RemoteException;

import ch.ethz.csb.youscope.server.microscopeaccess.StageDeviceInternal;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeException;
import ch.ethz.csb.youscope.shared.microscope.MicroscopeLockedException;
import ch.ethz.csb.youscope.shared.microscope.StageDevice;

/**
 * @author langmo
 */
class StageDeviceRMI extends DeviceRMI implements StageDevice
{
    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 1261971329588759872L;

    private StageDeviceInternal stageDevice;

    StageDeviceRMI(StageDeviceInternal stageDevice, int accessID) throws RemoteException
    {
        super(stageDevice, accessID);
        this.stageDevice = stageDevice;
    }

    @Override
	public Double getPosition() throws RemoteException, MicroscopeException, InterruptedException
    {
        return stageDevice.getPosition();
    }

    @Override
	public void setPosition(double x, double y) throws RemoteException, MicroscopeLockedException,
            MicroscopeException, InterruptedException
    {
        stageDevice.setPosition(x, y, accessID);
    }

    @Override
	public void setRelativePosition(double dx, double dy) throws RemoteException,
            MicroscopeLockedException, MicroscopeException, InterruptedException
    {
        stageDevice.setRelativePosition(dx, dy, accessID);
    }

    @Override
	public void setTransposeX(boolean transpose) throws MicroscopeLockedException
    {
        stageDevice.setTransposeX(transpose, accessID);
    }

    @Override
	public void setTransposeY(boolean transpose) throws MicroscopeLockedException
    {
        stageDevice.setTransposeY(transpose, accessID);
    }

    @Override
	public boolean isTransposeX()
    {
        return stageDevice.isTransposeX();
    }

    @Override
	public boolean isTransposeY()
    {
        return stageDevice.isTransposeY();
    }

    @Override
	public double getUnitMagnifier()
    {
        return stageDevice.getUnitMagnifier();
    }

    @Override
	public void setUnitMagnifier(double unitMagnifier) throws MicroscopeLockedException
    {
        stageDevice.setUnitMagnifier(unitMagnifier, accessID);
    }

}
