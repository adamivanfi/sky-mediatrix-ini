package com.nttdata.de.ityx.cx.ie.converter.sky;

import java.util.regex.Pattern;

public class PlzConversion extends AbstractSkyNumberConversion {

    private final String logPreafix = getClass().getSimpleName();

    protected final Pattern valueFormat = Pattern.compile("[0-9]{4,5}");
    protected final Pattern preafix = Pattern.compile("\\b((?i)(P(ost)?L(eit)?Z(ahl)?))[:;\\.,\\s_\\-]*D?A?");

    private static PlzConversion instance = null;

    public static PlzConversion getInstance() {
        if (instance == null) {
            instance = new PlzConversion();
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
