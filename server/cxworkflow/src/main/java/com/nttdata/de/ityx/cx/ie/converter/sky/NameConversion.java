package com.nttdata.de.ityx.cx.ie.converter.sky;

import com.nttdata.de.ityx.cx.ie.validator.ValueRejectionException;

import java.util.regex.Pattern;

public class NameConversion  extends com.nttdata.de.ityx.cx.ie.converter.NameConversion {

    protected final Pattern chkB_SkyName = Pattern.compile("(?i)((sky( deutschland( gmbh)?)?)|(premiere))");
    protected final Pattern chkB_KGDName = Pattern.compile("(?i)((kdg)|(kabel deutschland))");
    protected final Pattern chkB_Unity = Pattern.compile("(?i)((kdg)|(kabel deutschland))");
    protected final Pattern chkB_commonFailrues = Pattern.compile("(?i)(gmbh)");
    
    private final String logPreafix = getClass().getSimpleName();

    private static NameConversion instance = null;

    public static NameConversion getInstance() {
        if (instance == null) {
            instance = new NameConversion();
        }
        return instance;
    }

    @Override
    public String executeConversion(String value) throws ValueRejectionException {
       value=super.executeConversion(value);
       checkBlacklistPartPattern(chkB_SkyName, value, "SKY-Name");
       checkBlacklistPartPattern(chkB_KGDName, value, "KabelDeutschland-PartnerunternehmenName");
       checkBlacklistPartPattern(chkB_Unity, value, "Unitymedia-PartnerunternehmenName"); 
       checkBlacklistPartPattern(chkB_commonFailrues, value, "CommonFailrues: GmbH"); 
       return value;
    }

    @Override
    public String getLogPreafix() {
        return logPreafix;
    }
}
    

