package com.nttdata.de.ityx.cx.ie.converter;

import com.nttdata.de.ityx.cx.ie.validator.ValueRejectionException;

import java.util.regex.Pattern;

/**
 *
 * @author MEINUG
 */
public class BankAccountNumberConversion extends AbstractNumberConversion {
   private final String logPreafix = getClass().getSimpleName();

    protected final Pattern valueFormat = Pattern.compile("[\\d]{3,15}");
    protected final Pattern preafix = Pattern.compile("(?iu)\\b(k(on)?to)?[\\.,_\\-\\s]{0,3}(nu?m?m?b?e?[ro])?[:;,_\\-\\.~\\+\\*/ \\t\\x0B\\f\\u00A0]{0,25}");
    
    private static BankAccountNumberConversion instance=null;
    public static BankAccountNumberConversion getInstance(){
        if (instance==null){
            instance=new BankAccountNumberConversion();
        }
        return instance;
    }
    
    @Override
    public String executeConversion(String value) throws ValueRejectionException {
        value=super.executeConversion(value);
        value=value.replaceFirst("^[0]*", ""); //remove leading zeros
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
