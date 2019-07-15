package com.nttdata.de.ityx.cx.ie.converter.sky;

import com.nttdata.de.ityx.cx.ie.validator.ValueRejectionException;

import java.util.regex.Pattern;

public class CustomerNumberConversion extends AbstractSkyNumberConversion {

    private final String logPreafix = getClass().getSimpleName();

    protected final Pattern mandateSuffix = Pattern.compile("0{3}[\\d]{1}");
    protected final Pattern valueFormat = Pattern.compile("[0-9]{10}");
    protected final Pattern preafix = Pattern.compile("(?i)((K[uü]?(r|n)?d?(e|o)?(r|n)?)|(Cu?s?t?(om(e|o)(r|n))?))[:;,_\\-\\.~\\+\\*/ \\t\\x0B\\f\\u00A0]?((N([üu]m[mb]?(e|o))?(r|n)?o?)|(Id))?[:;,_\\-\\.~\\+\\*/ \\t\\x0B\\f\\u00A0\\s]*");

    private static CustomerNumberConversion instance = null;

    public static CustomerNumberConversion getInstance() {
        if (instance == null) {
            instance = new CustomerNumberConversion();
        }
        return instance;
    }
    @Override
    public String executeConversion(String value) throws ValueRejectionException {
        value=super.executeConversion(value);
        if (value.length()==14 && mandateSuffix.matcher(value.substring(10)).matches()){
             value=value.substring(0, 10);
        }
        return value;
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
