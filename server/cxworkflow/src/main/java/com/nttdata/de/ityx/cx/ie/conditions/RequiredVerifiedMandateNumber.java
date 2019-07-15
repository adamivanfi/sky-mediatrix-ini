package com.nttdata.de.ityx.cx.ie.conditions;

public class RequiredVerifiedMandateNumber  extends AbstractCondition {

    @Override
    public String getTagMatchToCheck() {
        return "VerifiedMandateNumber";
    }
}
