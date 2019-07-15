/**
 * 
 */
package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * @was Messages.java
 */
public class Messages {
	private static final String			BUNDLE_NAME		= "com.nttdata.de.sky.ityx.properties.messages";

	private static ResourceBundle	RESOURCE_BUNDLE;

	private static Messages instance;
	
	private Messages() {
		RESOURCE_BUNDLE	= ResourceBundle.getBundle(BUNDLE_NAME);
	}

	public static String getString(String key) {
		if (instance==null) {
			instance = new Messages();
		}
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
