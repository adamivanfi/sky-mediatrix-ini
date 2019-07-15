package com.nttdata.de.ityx.cx.ie.conditions;

public class RequiredVerifiedContractNumber  extends AbstractCondition {

    @Override
    public String getTagMatchToCheck() {
        return "VerifiedContractNumber";
    }
    
}
