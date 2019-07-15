package com.nttdata.de.ityx.cx.ie.conditions;

/**
 * Created by meinusch on 16.03.15.
 */
public class RequiredCustomerNumber extends AbstractCondition {

	@Override
	public String getTagMatchToCheck() {
		return "customernumber";
	}
}
