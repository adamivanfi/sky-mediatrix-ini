/**
 * 
 */
package com.nttdata.de.sky.ityx.mediatrix.businessrules.server;

import de.ityx.base.Global;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author DHIFLM
 * 
 */
public class BRProperties {

	private static BRProperties instance;

	final private Properties brProps = new Properties();

	private BRProperties() {
		String propFile = Global.getProperty("br.props", null);
		System.err.println("getDefaultFactory() propFile: " + propFile);
		if (propFile != null) {
			try {
				brProps.load(new FileInputStream(propFile));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static final Long serialVersionUID=7855448339962583772L;

	public static BRProperties getInstance() {
		synchronized (serialVersionUID) {
			if (instance == null) {
				instance = new BRProperties();
			}
		}
		return instance;
	}

	public String getProperty(String key) {
		return brProps.getProperty(key);
	}

	public static void main(String[] args) {
		List<String> internal_mail = Collections.emptyList();
		final BRProperties brProps = BRProperties.getInstance();
		if (brProps != null) {
			final String internalMail = brProps.getProperty("sky.internal");
			if (internalMail != null) {
				internal_mail = Arrays.asList(internalMail
						.replaceAll("\\s", "").split(","));
			} else {
				System.err.println("No sky.internal set in properties.");
			}
		} else {
			System.err.println("Could not load br.properties.");
		}
		for (String mail : internal_mail) {
			System.err.println(mail);
		}
	}
}
