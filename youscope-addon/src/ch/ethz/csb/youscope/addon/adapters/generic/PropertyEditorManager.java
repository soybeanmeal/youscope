package ch.ethz.csb.youscope.addon.adapters.generic;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import ch.ethz.csb.youscope.client.addon.YouScopeClient;
import ch.ethz.csb.youscope.shared.YouScopeServer;
import ch.ethz.csb.youscope.shared.configuration.CameraConfiguration;
import ch.ethz.csb.youscope.shared.configuration.ChannelConfiguration;
import ch.ethz.csb.youscope.shared.configuration.Configuration;
import ch.ethz.csb.youscope.shared.configuration.FocusConfiguration;

/**
 * Manager to create an editor for a given property (type) of a configuration.
 * @author Moritz Lang
 *
 */
public class PropertyEditorManager 
{
	private final HashMap<Class<?>, Class<? extends PropertyEditor>> editors = new HashMap<Class<?>, Class<? extends PropertyEditor>>(20);
	/**
	 * Constructor.
	 */
	public PropertyEditorManager()
	{
		editors.put(double.class, PropertyDoubleEditor.class);
		editors.put(Double.class, PropertyDoubleEditor.class);
		editors.put(String.class, PropertyStringEditor.class);
		editors.put(int.class, PropertyIntegerEditor.class);
		editors.put(Integer.class, PropertyIntegerEditor.class);
		editors.put(boolean.class, PropertyBooleanEditor.class);
		editors.put(Boolean.class, PropertyBooleanEditor.class);
		editors.put(ChannelConfiguration.class, PropertyChannelEditor.class);
		editors.put(FocusConfiguration.class, PropertyFocusEditor.class);
		editors.put(CameraConfiguration.class, PropertyCameraEditor.class);
		editors.put(Enum.class, PropertyEnumEditor.class);
	}
	/**
	 * Returns an editor for the given property, or throws an exception if no editor is available for the property type.
	 * @param property The property which should be edited.
	 * @param configuration The configuration which should be edited.
	 * @param client 
	 * @param server 
	 * @return An editor for the property.
	 * @throws GenericException
	 */
	public PropertyEditor getEditor(Property property, Configuration configuration, YouScopeClient client, YouScopeServer server) throws GenericException
	{
		Class<?> type = property.getType();
		Class<? extends PropertyEditor> editorClass = editors.get(type);
		if(editorClass == null)
		{
			// test if enumeration type
			if(type.isEnum())
			{
				editorClass = PropertyEnumEditor.class;
			}
			else
				throw new GenericException("No editor available for property " + property.getName() + " having type " + type.getName() + ".");
		}
		
		Constructor<? extends PropertyEditor> constructor;
		try {
			constructor = editorClass.getDeclaredConstructor(Property.class, Configuration.class, YouScopeClient.class, YouScopeServer.class);
		
			constructor.setAccessible(true);
			return constructor.newInstance(property, configuration, client, server);
		} 
		catch (Exception e) 
		{
			throw new GenericException("Error while creating editor for property " + property.getName() + " having type " + type.getName() + ".", e);
		}
	}
}
