
package com.nttdata.de.ityx.cx.ie.conditions;

public class RequiredVerifiedSmartcardNumber  extends AbstractCondition {

    @Override
    public String getTagMatchToCheck() {
        return "VerifiedSmartcardNumber";
    }
}
