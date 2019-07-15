package de.ityx.sky.outbound.data;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * gets the language specific label text.
 */
public class Messages {
    private static final String         BUNDLE_NAME     = "de.ityx.sky.outbound.client.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        }
        catch(MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
