/**
 * 
 */
package ch.ethz.csb.youscope.addon.dropletmicrofluidics;

import ch.ethz.csb.youscope.addon.autofocus.AutoFocusJobConfiguration;
import ch.ethz.csb.youscope.addon.brentfocussearch.BrentFocusSearchConfiguration;
import ch.ethz.csb.youscope.addon.dropletmicrofluidics.defaultobserver.DefaultObserverConfiguration;
import ch.ethz.csb.youscope.addon.dropletmicrofluidics.tablecontroller.TableControllerConfiguration;
import ch.ethz.csb.youscope.addon.simplefocusscores.AutocorrelationFocusScoreConfiguration;
import ch.ethz.csb.youscope.shared.configuration.ConfigurationException;
import ch.ethz.csb.youscope.shared.configuration.JobConfiguration;
import ch.ethz.csb.youscope.shared.configuration.TableProducerConfiguration;
import ch.ethz.csb.youscope.shared.resource.dropletmicrofluidics.DropletControllerConfiguration;
import ch.ethz.csb.youscope.shared.resource.dropletmicrofluidics.DropletObserverConfiguration;
import ch.ethz.csb.youscope.shared.table.TableDefinition;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Configuration for a job which automatically searches for the focal plane.
 * @author Moritz Lang
 *
 */
@XStreamAlias("droplet-based-microfluidics-job")
class DropletMicrofluidicJobConfiguration extends JobConfiguration implements TableProducerConfiguration
{
	/**
	 * Serial version UID.
	 */
	private static final long	serialVersionUID	= 7911732041188941111L;
	
	/**
	 * The identifier for this job type.
	 */
	public static final String	TYPE_IDENTIFIER	= "IST::DropletBasedMicrofluidicsJob";

	@XStreamAlias("autofocus-configuration")
	private AutoFocusJobConfiguration autofocusConfiguration = new AutoFocusJobConfiguration();
	
	@XStreamAlias("controller-configuration")
	private DropletControllerConfiguration controllerConfiguration = new TableControllerConfiguration();
	
	@XStreamAlias("observer-configuration")
	private DropletObserverConfiguration observerConfiguration = new DefaultObserverConfiguration();
	
	@XStreamAlias("nemesys-device")
	private String nemesysDevice = null;
	
	/**
	 * Constructor.
	 */
	public DropletMicrofluidicJobConfiguration()
	{
		AutocorrelationFocusScoreConfiguration focusScore = new AutocorrelationFocusScoreConfiguration();	
		autofocusConfiguration.setFocusScoreAlgorithm(focusScore);
		BrentFocusSearchConfiguration focusSearch = new BrentFocusSearchConfiguration();
		focusSearch.setFocusLowerBound(-100);
		focusSearch.setFocusUpperBound(100);
		focusSearch.setMaxSearchSteps(100);
		focusSearch.setTolerance(0.3);
		autofocusConfiguration.setFocusSearchAlgorithm(focusSearch);
		autofocusConfiguration.setRememberFocus(true);
		autofocusConfiguration.setResetFocusAfterSearch(false);
	}
	
	/**
	 * Returns the name of the Nemesys device. Initially null, i.e. unset.
	 * @return nemesys device name.
	 */
	public String getNemesysDevice() {
		return nemesysDevice;
	}

	/**
	 * Sets the name of the Nemesys device.
	 * @param nemesysDevice nemesys device name.
	 */
	public void setNemesysDevice(String nemesysDevice) {
		this.nemesysDevice = nemesysDevice;
	}
	
	@Override
	public String getTypeIdentifier()
	{
		return TYPE_IDENTIFIER;
	}

	/**
	 * Returns the configuration for the autofocus.
	 * @return AUtofocus configuration.
	 */
	public AutoFocusJobConfiguration getAutofocusConfiguration() {
		return autofocusConfiguration;
	}

	/**
	 * Sets the configuration for the autofocus.
	 * @param autofocusConfiguration Autofocus configuration.
	 */
	public void setAutofocusConfiguration(
			AutoFocusJobConfiguration autofocusConfiguration) {
		this.autofocusConfiguration = autofocusConfiguration;
	}

	@Override
	public String getDescription() {
		return "Droplet based microfluidics";
	}

	public DropletControllerConfiguration getControllerConfiguration() {
		return controllerConfiguration;
	}

	public void setControllerConfiguration(DropletControllerConfiguration controllerConfiguration) {
		this.controllerConfiguration = controllerConfiguration;
	}

	public DropletObserverConfiguration getObserverConfiguration() {
		return observerConfiguration;
	}

	public void setObserverConfiguration(DropletObserverConfiguration observerConfiguration) {
		this.observerConfiguration = observerConfiguration;
	}

	@Override
	public TableDefinition getProducedTableDefinition() {
		return DropletMicrofluidicTable.getTableDefinition();
	}

	@Override
	public void checkConfiguration() throws ConfigurationException {
		super.checkConfiguration();
		
		if(autofocusConfiguration == null)
			throw new ConfigurationException("Autofocus not configured.");
		autofocusConfiguration.checkConfiguration();
		if(controllerConfiguration == null)
			throw new ConfigurationException("Controller not configured.");
		controllerConfiguration.checkConfiguration();
		if(nemesysDevice == null || nemesysDevice.length() <=0)
			throw new ConfigurationException("Nemesys device not set.");
		if(observerConfiguration == null)
			throw new ConfigurationException("Observer not configured.");
		observerConfiguration.checkConfiguration();
	}

}
