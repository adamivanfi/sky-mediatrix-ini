package com.nttdata.de.ityx.cx.ie.converter;

import java.util.regex.Pattern;

public class BlzConversion extends AbstractNumberConversion {

    private final String logPreafix = getClass().getSimpleName();

    protected final Pattern valueFormat = Pattern.compile("([\\d]{5}|[\\d]{8}|[\\w]{8}|[\\d]{11})");
    protected final Pattern preafix = Pattern.compile("\\b((?i)((B(ank)?L(eit)?Z(ahl)?)|((SWIFT-)?BIC))?)[:;\\.,\\s_\\-]*");

    private static BlzConversion instance = null;

    public static BlzConversion getInstance() {
        if (instance == null) {
            instance = new BlzConversion();
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
