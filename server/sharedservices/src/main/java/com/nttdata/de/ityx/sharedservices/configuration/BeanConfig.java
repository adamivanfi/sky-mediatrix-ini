package com.nttdata.de.ityx.sharedservices.configuration;

import com.nttdata.de.lib.logging.SkyLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Contains all Configuration items used by WorkflowJavaBeans
 * <p/>
 * Created by meinusch on 17.02.15.
 */
public class BeanConfig {
	private static ResourceBundle flowConfig;
	private static final Long serialVersionUID=7853348331162583772L;

	private static ResourceBundle getBeanConfig() {
		if (flowConfig == null) {
			//InputStream is = BeanConfig.class.getClassLoader().getResourceAsStream(System.getProperty("ityx.conf", "c:/mediatrix/conf") + "/" + "WflBeanConfig.properties");
			String wflfile = System.getProperty("ityx.conf", "c:/mediatrix/conf") + "/" + "WflBeanConfig.properties";
			synchronized (serialVersionUID) {
				try {
					SkyLogger.getItyxLogger().debug("Loading WflBeanConfig.properties " + wflfile);
					InputStream is = new FileInputStream(new File(wflfile));
					SkyLogger.getItyxLogger().debug("Loaded WflBeanConfig.properties IS");
					flowConfig = new PropertyResourceBundle(is);
					SkyLogger.getItyxLogger().info("Loaded WflBeanConfig.properties");

				} catch (IOException e) {
					SkyLogger.getItyxLogger().error("Unable to load WflBeanConfig.properties");
					flowConfig = null;
				}
			}
		}
		return flowConfig;
	}

	public static boolean getReqBoolean(String key) throws Exception {
		Object o = getBeanConfig().getObject(key);
		if (o instanceof Boolean) {
			return (Boolean) o;
		} else if (o instanceof String) {
			try {
				return Boolean.parseBoolean((String) o);
			} catch (Exception e) {
				SkyLogger.getItyxLogger().error("BeanConfig: Unable to convert Value:" + o + " to Boolean for ConfProperty:" + key);
				throw new Exception("BeanConfig: Unable to convert Value:" + o + " to Boolean for ConfProperty:" + key);
			}
		} else {
			SkyLogger.getItyxLogger().error("BeanConfig: Unable to convert Value:" + o + " to Boolean for ConfProperty:" + key);
			throw new Exception("BeanConfig: Unable to convert Value:" + o + " to Boolean for ConfProperty:" + key);
		}
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		ResourceBundle bc = getBeanConfig();
		Object o = null;
		try {
			o = bc.getObject(key);
		} catch (MissingResourceException e) {
			SkyLogger.getItyxLogger().debug("BeanConfig: Key not found:" + key);
			return defaultValue;
		}

		if (o instanceof Boolean) {
			return (Boolean) o;
		} else if (o instanceof String) {
			try {
				return Boolean.parseBoolean((String) o);
			} catch (Exception e) {
				SkyLogger.getItyxLogger().error("BeanConfig: Unable to convert Value:" + o + " to Boolean for ConfProperty:" + key);
				//throw new Exception("BeanConfig: Unable to convert Value:" + o + " to Boolean for ConfProperty:" + key);
			}
		} else {
			SkyLogger.getItyxLogger().warn("BeanConfig: Unable to convert Value:" + o + " to Boolean for ConfProperty:" + key);
			return defaultValue;
		}
		SkyLogger.getItyxLogger().debug("BeanConfig: Unable to load Boolean ConfProperty:" + key);
		return defaultValue;

	}


	public static String getReqString(String key) throws Exception {
		Object o = null;
		try {
			o = getBeanConfig().getObject(key);
		} catch (MissingResourceException e) {
			SkyLogger.getItyxLogger().debug("BeanConfig: Unable to find String ConfProperty:" + key);
		}
		if (o != null && o instanceof String) {
			return (String) o;
		} else if (o != null) {
			SkyLogger.getItyxLogger().error("BeanConfig: Unable to convert Value:" + o + " to String for ConfProperty:" + key);
			throw new Exception("BeanConfig: Unable to convert Value:" + o + " to String for ConfProperty:" + key);
		} else {
			SkyLogger.getItyxLogger().error("BeanConfig: Unable to load String ConfProperty:" + key);
			throw new Exception("BeanConfig: Unable to load String ConfProperty:" + key);
		}
	}

	public static String getString(String key, String defaultValue) {
		Object o = null;
		try {
			o = getBeanConfig().getObject(key);
		} catch (MissingResourceException e) {
			SkyLogger.getItyxLogger().debug("BeanConfig: Unable to find String ConfProperty:" + key);
			return defaultValue;
		}
		if (o instanceof String) {
			return (String) o;
		} else {
			SkyLogger.getItyxLogger().warn("BeanConfig: Unable to convert Value:" + o + " to String for ConfProperty:" + key);
			return defaultValue;
		}
	}


	public static int getReqInt(String key) throws Exception {
		Object o = null;
		try {
			o = getBeanConfig().getObject(key);
		} catch (MissingResourceException e) {
			SkyLogger.getItyxLogger().debug("BeanConfig: Unable to find int ConfProperty:" + key);
		}
		if (o != null && o instanceof Integer) {
			return (Integer) o;
		} else if (o != null && o instanceof String) {
			try {
				return Integer.parseInt((String) o);
			} catch (Exception e) {
				SkyLogger.getItyxLogger().error("BeanConfig: Unable to convert Value:" + o + " to Int for ConfProperty:" + key);
				throw new Exception("BeanConfig: Unable to convert Value:" + o + " to Int for ConfProperty:" + key);
			}
		} else if (o != null) {
			try {
				return Integer.parseInt(o.toString());
			} catch (Exception e) {
				SkyLogger.getItyxLogger().error("BeanConfig: Unable to convert Value:" + o + " to Int for ConfProperty:" + key);
				throw new Exception("BeanConfig: Unable to convert Value:" + o + " to Int for ConfProperty:" + key);
			}
		} else {
			SkyLogger.getItyxLogger().error("BeanConfig: Unable to load Int ConfProperty:" + key);
			throw new Exception("BeanConfig: Unable to load Int ConfProperty:" + key);
		}
	}

	public static int getInt(String key, int defaultValue) {
		Object o = null;
		try {
			o = getBeanConfig().getObject(key);
		} catch (MissingResourceException e) {
			SkyLogger.getItyxLogger().debug("BeanConfig: Unable to find int ConfProperty:" + key);
			return defaultValue;
		}
		if (o instanceof Integer) {
			return (Integer) o;
		} else if (o instanceof String) {
			try {
				return Integer.parseInt((String) o);
			} catch (Exception e) {
				SkyLogger.getItyxLogger().warn("BeanConfig: Unable to convert Value:" + o + " to Int for ConfProperty:" + key);
				return defaultValue;
			}
		} else {
			try {
				return Integer.parseInt(o.toString());
			} catch (Exception e) {
				SkyLogger.getItyxLogger().warn("BeanConfig: Unable to convert Value:" + o + " to Int for ConfProperty:" + key);
				return defaultValue;
			}
		}
	}

	public static long getReqLong(String key) throws Exception {
		Object o = getBeanConfig().getObject(key);
		if (o instanceof Long) {
			return (Long) o;
		} else {
			SkyLogger.getItyxLogger().error("BeanConfig: Unable to convert Value:" + o + " to Long for ConfProperty:" + key);
			throw new Exception("BeanConfig: Unable to convert Value:" + o + " to Long for ConfProperty:" + key);
		}
	}

	public static long getLong(String key, long defaultValue) {
		ResourceBundle bc = getBeanConfig();
		Object o = null;
		try {
			o = bc.getObject(key);
		} catch (MissingResourceException e) {
			SkyLogger.getItyxLogger().debug("BeanConfig: Key not found:" + key);
			return defaultValue;
		}
		if (o instanceof Long) {
			return (Long) o;
		} else {
			SkyLogger.getItyxLogger().warn("BeanConfig: Unable to convert Value:" + o + " to Long for ConfProperty:" + key);
			return defaultValue;
		}
	}

}
