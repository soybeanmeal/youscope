package org.youscope.addon.measurement;

import org.youscope.addon.AddonException;
import org.youscope.addon.component.ConstructionContext;
import org.youscope.common.configuration.ConfigurationException;
import org.youscope.common.configuration.MeasurementConfiguration;
import org.youscope.common.measurement.Measurement;

/**
 * Interface which has to be implemented if the creation of a component, given its configuration, should be done
 * differently from the default mechanism.
 * @author Moritz Lang
 * @param <C> The configuration class consumed for the creation.
 *
 */
public interface CustomMeasurementInitializer<C extends MeasurementConfiguration>
{
	/**
	 * Called when this addon should initialize the the measurement according to its configuration.
	 * @param measurement The measurement which should be initialized.
	 * @param configuration The configuration according to which the measurement should be initialized.
	 * @param constructionContext An interface to an object allowing to initialize the various measurement components.
	 * @throws ConfigurationException Thrown if the configuration is invalid.
	 * @throws AddonException Thrown if an error occurred during the initialization.
	 */
	void initializeMeasurement(Measurement measurement, C configuration, ConstructionContext constructionContext) throws ConfigurationException, AddonException;
}
