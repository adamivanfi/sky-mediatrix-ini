package com.nttdata.de.ityx.cx.ie.converter.sky;

import com.nttdata.de.ityx.cx.ie.validator.ValueRejectionException;

import java.util.regex.Pattern;

public class MandateNumberConversion extends AbstractSkyNumberConversion {

    private final String logPreafix = getClass().getSimpleName();

    protected final Pattern valueFormat = Pattern.compile("[\\d]{10}\\-?[\\d]{4}");
    protected final Pattern preafix = Pattern.compile("(?i)M[aänd]{4,5}ts?(e|o)?[:;,_\\-\\.~\\+\\*/ \\t\\x0B\\f\\u00A0]{0,5}((r|n)(e|o)(f|t)((e|o)(r|n)(e|o)nz)?)?[:;,_\\-\\.~\\+\\*/ \\t\\x0B\\f\\u00A0]{0,5}((n([uü]m[mb]?(e|o))?(r|n)?o?)|(id))?[:;,_\\-\\.~\\+\\*/ \\t\\x0B\\f\\u00A0]{0,25}");

    
    private static MandateNumberConversion instance=null;
    public static MandateNumberConversion getInstance(){
        if (instance==null){
            instance=new MandateNumberConversion();
        }
        return instance;
    }
    
    @Override
    public String executeConversion(String value) throws ValueRejectionException {
        value=super.executeConversion(value);
        if (value.length()>10){
             value=value.substring(0, 10)+"-"+value.substring(10);
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
