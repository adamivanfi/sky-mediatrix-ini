
package com.nttdata.de.ityx.cx.ie.conditions;

import com.nttdata.de.lib.logging.SkyLogger;

import java.util.regex.Pattern;


public class RequiredStreet  extends AbstractCondition {
        protected final Pattern chkB_SkyStrasse = Pattern.compile("(?i)((MEDIENALEE)|(BETA((-STR)|(STRA[ßS]{1,2}E))))");
     
    @Override
    public String getTagMatchToCheck() {
        return "streeet";
    }
    
    
    @Override
    public boolean checkValue(String value){
        if (chkB_SkyStrasse.matcher(value).find()){
            SkyLogger.getCXIELogger().debug("IE:RequiredStreet:" + value + " Rejected: CompanyStreet");
            return false;
        }else{
            return true;
        }
    }
}
