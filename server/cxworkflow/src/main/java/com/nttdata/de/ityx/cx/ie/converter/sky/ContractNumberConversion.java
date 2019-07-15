package com.nttdata.de.ityx.cx.ie.converter.sky;

import java.util.regex.Pattern;

public class ContractNumberConversion extends AbstractSkyNumberConversion {

    private final String logPreafix = getClass().getSimpleName();
    public final Pattern valueFormat = Pattern.compile("[\\d]{5,10}");

    public final Pattern preafix = Pattern.compile("(((?i)((((Auf)|(V(e|o)?(r|n)?))(t|f)?(r|n)?a?(g|q)?s?)|(Co?n?t?((r|n)act)?))[:;,_\\-\\.~\\+\\*/ \\t\\x0B\\f\\u00A0]?((N([üu]m[mb]?(e|o))?(r|n)?o?)|(Id)))|((U[nirfl]{1,2}[tflr][eöof]r[föÖhtlioäüW]{3,4}[rinjlc]{3,5}[gacj]{1,2})|(C[ou] K[GO])))[:;,_\\-\\.~\\+\\*/ \\t\\x0B\\f\\u00A0]{0,25}");

    private static ContractNumberConversion instance = null;

    public static ContractNumberConversion getInstance() {
        if (instance == null) {
            instance = new ContractNumberConversion();
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
