
package com.nttdata.de.ityx.cx.ie.conditions;

import com.nttdata.de.lib.logging.SkyLogger;


public class RequiredEmail extends AbstractCondition {

    @Override
    public String getTagMatchToCheck() {
        return "email";
    }
    
    
    
    public boolean checkValue(String value){
        if (value.contains("sky.de")){
            SkyLogger.getCXIELogger().debug("IE:RequiredEmail:" + value + " Rejected: Sky-Adresse.");
            return false;
        }
        return true;
    }
    
    
    
}
