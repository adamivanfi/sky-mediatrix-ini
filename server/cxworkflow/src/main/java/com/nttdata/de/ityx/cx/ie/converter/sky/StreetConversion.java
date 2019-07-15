/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nttdata.de.ityx.cx.ie.converter.sky;

import com.nttdata.de.ityx.cx.ie.validator.ValueRejectionException;

import java.util.regex.Pattern;

public class StreetConversion extends com.nttdata.de.ityx.cx.ie.converter.NameConversion {

    protected final Pattern chkB_SkyStrasse = Pattern.compile("(?i)((MEDIENALEE)|(BETA((-STR)|(STRA[ßS]{1,2}E))))");
    private final String logPreafix = getClass().getSimpleName();

    private static StreetConversion instance = null;

    public static StreetConversion getInstance() {
        if (instance == null) {
            instance = new StreetConversion();
        }
        return instance;
    }

    @Override
    public String executeConversion(String value) throws ValueRejectionException {
       value=super.executeConversion(value);
       checkBlacklistPartPattern(chkB_SkyStrasse, value, "Medienalee or Betastrasse");
       return value;
    }

    @Override
    public String getLogPreafix() {
        return logPreafix;
    }
    
}
