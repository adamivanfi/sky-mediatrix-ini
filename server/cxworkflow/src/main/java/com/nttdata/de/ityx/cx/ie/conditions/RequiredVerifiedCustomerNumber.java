package com.nttdata.de.ityx.cx.ie.conditions;

public class RequiredVerifiedCustomerNumber extends AbstractCondition {

    @Override
    public String getTagMatchToCheck() {
        return "VerifiedCustomerNumber";
    }

}
