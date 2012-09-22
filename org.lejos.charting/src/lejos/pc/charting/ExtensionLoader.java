package lejos.pc.charting;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Hashtable;

import lejos.pc.charting.extensions.AbstractTunneledMessagePanel;
import lejos.pc.charting.extensions.MessageSender;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Load plug-ins.
 * 
 * @author Kirk P. Thompson
 *
 */
class ExtensionLoader {
//	private final static String HANDLER_TYPE_ID = "handlerTypeID";
	final static String TAB_LABEL = "tabLabel";
	final static String TAB_HOVER_TEXT = "tabHoverText";
	final static String PLUGIN_CLASS = "class";
	
	
	/**
	 * struct for plug-in definitions
	 *
	 */
	static class PlugInDefinition {
		private HashMap<String, String> attributes = new HashMap<String, String>();
		private Constructor<?> plugInConstructor = null;
		
		public HashMap<String, String> getMap() {
			return attributes;
		}
		
		/**
		 * Get a new plug-in instance for this type via reflection
		 * 
		 * @param handlerID The handler ID to assign this instance (specified by AbstractTunneledMessagePanel '
		 * constructor)
		 * @param messageConduit
		 * @return The plug-in panel instance. null if problem with instantiating
		 * @see AbstractTunneledMessagePanel
		 */
		public AbstractTunneledMessagePanel getPluginInstance(int handlerID, MessageSender messageConduit) {
			AbstractTunneledMessagePanel plugInInstance = null;
			try {
				plugInInstance = (AbstractTunneledMessagePanel) plugInConstructor.newInstance(
						new Object[] {new Integer(handlerID), messageConduit}
				);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return plugInInstance;
		}
		
		private void setItem(String key, String value){
			attributes.put(key, value);
			if (key.equals(PLUGIN_CLASS)) {
				setClass(value);
			}
		}
		
		/**
		 * Load the plugin class via reflection
		 * 
		 * @param FQN fully qualified classname. Must be in classpath
		 */
		private void setClass(String FQN){
			Class<?> tempClass = null;
			try {
//				System.out.println("FQN=" + FQN);
				String classpath = ConfigurationManager.getConfigItem(ConfigurationManager.CONFIG_ADD_PLUGINS_PATH, ".");
				tempClass = loadPluginClass(classpath, FQN);
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			
			if (tempClass==null) {
				System.out.println("!** loadPlugIn: tempClass is null. Returning null.");
				return;
			}
//			System.out.println("ForName test:" + tempClass.toString());
			
			try {
				plugInConstructor = tempClass.getConstructor(new Class[] {int.class, MessageSender.class});
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static Class<?> loadPluginClass(String newPath, String classname) throws ClassNotFoundException{
		ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
		URLClassLoader urlClassLoader = null;
		try {
			urlClassLoader = new URLClassLoader(
				new URL[]{new File(newPath).toURI().toURL()}, currentThreadClassLoader);
//			System.out.println(new File(newPath).toURI().toURL().toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return urlClassLoader.loadClass(classname);
	}
	
	/**
	 * @return The Vector of PlugInDefinitions as specified in the NXJChartingLogger.xml file.
	 */
	static Hashtable<Integer, PlugInDefinition> getPlugIns(){
		Hashtable<Integer, PlugInDefinition> pluginList = new Hashtable<Integer, PlugInDefinition>();
		String tempStr;
		String[] attributeNames = {TAB_LABEL, TAB_HOVER_TEXT, PLUGIN_CLASS};
		PlugInDefinition tempElement=null;
		
		Document doc = ConfigurationManager.getConfigDOM();
		
		if (doc==null){
			System.out.println("ExtensionLoader.getPlugIns(): failed to load DOM");
			return null;
		}
		
		Node plugins = ConfigurationManager.findSubNode("extensions", doc.getFirstChild());
		NodeList nl = plugins.getChildNodes();
		outer: for (int i=0;i<nl.getLength();i++){
			if (nl.item(i).getNodeType()==Node.ELEMENT_NODE && nl.item(i).getNodeName().equals("plugin")) {
				// get attributes
				NamedNodeMap nnm = nl.item(i).getAttributes();
				if (nnm==null) continue;
				
				tempElement = new PlugInDefinition();
				// add the attibutes to the datastruct
				Integer handlerTypeID = null;
				for (int j=0;j<attributeNames.length;j++){
					tempStr = nnm.getNamedItem(attributeNames[j]).getNodeValue();
					if (tempStr==null) break outer;
					// add the plug-in definition
					tempElement.setItem(attributeNames[j], tempStr);
				}
				// verify the class instantiates and get its handlerTypeID
				handlerTypeID = verifyClass(tempElement);
				if (handlerTypeID==null) {
					System.out.println("Problem with instantiating plug-in " + tempElement.getMap().get(PLUGIN_CLASS));
					continue;
				}
				
				// add the handler struct to the vector
				PlugInDefinition existenceTest = pluginList.put(handlerTypeID, tempElement);
				if (existenceTest != null){
					System.out.println("replaced plug-in type " + handlerTypeID + " handler: " + 
						existenceTest.getMap().get(PLUGIN_CLASS) + " with " + tempElement.getMap().get(PLUGIN_CLASS));
				} else {
					System.out.println("loaded plug-in type " + handlerTypeID + 
							" handler: " + PLUGIN_CLASS + "=" + tempElement.getMap().get(PLUGIN_CLASS) );
				}
			}
		}
//		System.out.println("plug-in parse complete");
		return pluginList;
	}
	
	/**
	 * @param item the PlugInDefinition to test and get the handlerTypeID from
	 * @return the handlerTypeID for the plug-in. null if problem with instantiating
	 */
	private static Integer verifyClass(PlugInDefinition item){
		/**
		 * Use MessageSender type to keep ExtensionGUIManager private but still allow message transfer.
		 *
		 */
		class MessageConduit implements MessageSender{
			public void sendMessage(int typeID, byte[] msg) {
				// do-nothing stub
			}
		}
		
		AbstractTunneledMessagePanel targetPanel = item.getPluginInstance(0, new MessageConduit());
		if (targetPanel==null) {
    		return null;
    	}
		
		return new Integer(targetPanel.getHandlerTypeID());
	}
}
