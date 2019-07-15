
package com.nttdata.de.ityx.cx.ie.conditions;


public class RequiredAccount  extends AbstractCondition {

    @Override
    public String getTagMatchToCheck() {
        return "bank_account";
    }
    
}
