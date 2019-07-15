package com.nttdata.de.ityx.cx.ie.converter.sky;

import com.nttdata.de.ityx.cx.ie.converter.AbstractNumberConversion;
import com.nttdata.de.ityx.cx.ie.validator.ValueRejectionException;

import java.util.regex.Pattern;

public abstract class AbstractSkyNumberConversion extends AbstractNumberConversion {
    protected final Pattern chkB_PBPaginationSignature = Pattern.compile("(([0-2]?[\\d])|(3[01]))[\\s\\.\\-\\,\\;:]?((0\\d)|(1[012]))[\\s\\.\\-\\,\\;:]?201[2-4]");
    protected final Pattern chkB_skyMagicNumbers = Pattern.compile("(^004[93])|(145451)|(85774)|(22033)|(^0180)|(80699)|(01801?(5|6)((1100)|(886))\\d*)|((5|6)((1100)|(886))\\d*)|(118376113)|(667582091)|(70020270)|(3009202159)|(8209124655)|(10787545)|(01005315313)|(0667582091)|(08107002)|(02700667582091)|(666877020)|(((DE)?95)?(7002)?(0270)?(0666)?877020)|(000000001000\\d*)");
    protected final Pattern chkB_unityMediaMagicNumbers = Pattern.compile("(101330)|(^44713)|(018055l4451)|(^01805663140)|(^01805.{0,3}663140)|(^01805)|(663140$)");
    protected final Pattern chk_plzSky = Pattern.compile("(22033)|(85774)|(80699)");
    
    @Override
    public String executeConversion(String value) throws ValueRejectionException {
        value=super.executeConversion(value);
        if (value.length()<=10){
            checkBlacklistPartPattern(chkB_PBPaginationSignature, value, "PBPaginationSignature");
            checkBlacklistPartPattern(chkB_skyMagicNumbers, value, "skyMagicNumbers");
            checkBlacklistPartPattern(chkB_unityMediaMagicNumbers, value, "unityMediaMagicNumbers");
            
            if (value.length()>=4 && value.length()<7){
                 checkBlacklistPartPattern(chk_plzSky, value, "skyPlz");
            }
        }
        return value;
    }
}
