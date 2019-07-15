package com.nttdata.de.ityx.cx.workflow.base;

import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.interfaces.designer.IFlowObject;

/**
 * Created by meinusch on 17.02.15.
 */
public class FlowUtils {
	private static Boolean getBoolean(IFlowObject flowObject, String key, boolean defaultValue, boolean useDefault) throws Exception {
		Object obj = flowObject.get(key);
		if (obj != null && obj instanceof Boolean) {
			return (Boolean) obj;
		} else if ( obj!=null && ((String) obj).trim().matches(TagMatchDefinitions.FALSE)) {
			return false;
		} else if (obj!=null && ((String) obj).trim().matches("true")) {
			return true;
		} else if (useDefault) {
			return defaultValue;
		} else {
			if (useDefault) {
				return defaultValue;
			}
			throw new Exception("Required key " + key + " not available in FlowObject");
		}
	}

	public static Boolean getOptionalBoolean(IFlowObject flowObject, String key, boolean defaultValue) {
		try {
			return getBoolean(flowObject, key, defaultValue, true);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	protected static Boolean getRequiredBoolean(IFlowObject flowObject, String key) throws Exception {
		return getBoolean(flowObject, key, false, false);
	}

	private static String getString(IFlowObject flowObject, String key, String defaultValue, boolean useDefault) throws Exception {
		Object obj = flowObject.get(key);
		if (obj != null && obj instanceof String) {
			String s=(String) obj;
			return (String) obj;
		} else if (useDefault) {
			return defaultValue;
		} else {
			throw new Exception("Required key " + key + " not available in FlowObject");
		}
	}

	public static String getNonEmptyString(IFlowObject flowObject, String key, String defaultValue) throws Exception {
		Object obj = flowObject.get(key);
		if (obj != null && obj instanceof String) {
			String s=(String) obj;
			if (isNotEmpty(s)){
				return s;
			}
		}
		if (isNotEmpty(defaultValue)) {
			return defaultValue;
		} else {
			return null;
		}
	}

	private static boolean isNotEmpty(String defaultValue) {
		return ! (defaultValue==null || defaultValue.trim().isEmpty() || defaultValue.trim().equalsIgnoreCase("0")|| defaultValue.trim().equalsIgnoreCase("null") );
		
	}

	/**
	 * returns the String from the FlowObject
	 *
	 * @param flowObject FlowObject to use
	 * @param key used to lookup the value from FlowObject
	 * @return value from FlowObject
	 * @throws Exception in case the key is not found in the FlowObject
	 */
	public static String getRequiredString(IFlowObject flowObject, String key) throws Exception {
		return getString(flowObject, key, null, false);
	}

	/**
	 * returns the String from the FlowObject that is not allowed to be empty
	 *
	 * @param flowObject FlowObject to use
	 * @param key used to lookup the value from FlowObject
	 * @return value from FlowObject
	 * @throws Exception in case the key is not found in the FlowObject
	 */
	public static String getRequiredNonEmptyString(IFlowObject flowObject, String key) throws Exception {
		String result = getString(flowObject, key, null, false);
		if ("".equals(result)) {
			throw new Exception("String for key " + key + " is empty");
		}
		return result;
	}

	/**
	 * return the String from the FlowObject - if not available the default
	 * value is returned
	 *
	 * @param flowObject FlowObject to use
	 * @param key used to lookup the value from FlowObject
	 * @param defaultValue to return in case the key is not found in FlowObject
	 * @return value from FlowObject (or defaultValue)
	 */
	public static String getOptionalString(IFlowObject flowObject, String key, String defaultValue) {
		try {
			return getString(flowObject, key, defaultValue, true);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private static int getInt(IFlowObject flowObject, String key, int defaultValue, boolean useDefault) throws Exception {
		Object obj = flowObject.get(key);
		if (obj != null && obj instanceof String) {
			try {
				return Integer.parseInt((String) obj);
			} catch (NumberFormatException ex) {
				if (useDefault) {
					return defaultValue;
				} else {
					throw new Exception("Required key " + key + " does not contain a number: " + obj);
				}
			}
		} else if (obj != null && obj instanceof Integer) {
			return (Integer) obj;
		} else {
			if (useDefault) {
				return defaultValue;
			} else {
				throw new Exception("Required key " + key + " not available in FlowObject");
			}
		}
	}

	/**
	 * return the int from the FlowObject - if not available the default value
	 * is returned
	 *
	 * @param flowObject FlowObject to use
	 * @param key used to lookup the value from FlowObject
	 * @param defaultValue to return in case the key is not found in FlowObject
	 * @return value from FlowObject (or defaultValue)
	 */
	public static int getOptionalInt(IFlowObject flowObject, String key, int defaultValue) {
		try {
			return getInt(flowObject, key, defaultValue, true);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * returns the int from the FlowObject
	 *
	 * @param flowObject FlowObject to use
	 * @param key used to lookup the value from FlowObject
	 * @return value from FlowObject
	 * @throws Exception in case the key is not found in the FlowObject
	 */
	protected static int getRequiredInt(IFlowObject flowObject, String key) throws Exception {
		return getInt(flowObject, key, 0, false);
	}
}
