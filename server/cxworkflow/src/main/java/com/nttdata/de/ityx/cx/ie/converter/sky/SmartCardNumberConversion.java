package com.nttdata.de.ityx.cx.ie.converter.sky;

import com.nttdata.de.ityx.cx.ie.converter.AbstractNumberConversion;

import java.util.regex.Pattern;

public class SmartCardNumberConversion extends AbstractNumberConversion {

    private final String logPreafix = getClass().getSimpleName();

    protected final Pattern valueFormat = Pattern.compile("[0-9]{11,14}");
    protected final Pattern preafix = Pattern.compile("\\b(?i)((Smar[tdf][ck]ar[dt]e?)|(S(eri((en)|(al)))?))[\\s_\\.\\-,]{0,5}((N(um[mb]?e)?r?o?)|(Id))[:;,_\\-\\.~\\+\\*/ \\t\\x0B\\f\\u00A0]{0,25}");

    private static SmartCardNumberConversion instance = null;

    public static SmartCardNumberConversion getInstance() {
        if (instance == null) {
            instance = new SmartCardNumberConversion();
        }
        return instance;
    }

    @Override
    public Pattern getValueFormat() {
        return valueFormat;
    }

    @Override
    public Pattern getPreafixPattern() {
        return preafix;
    }

    @Override
    public String getLogPreafix() {
        return logPreafix;
    }
}
