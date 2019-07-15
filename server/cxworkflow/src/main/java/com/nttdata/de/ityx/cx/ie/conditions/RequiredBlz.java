

package com.nttdata.de.ityx.cx.ie.conditions;


public class RequiredBlz extends AbstractCondition {

    @Override
    public String getTagMatchToCheck() {
        return "bank_code";
    }
    
}
