

package com.nttdata.de.ityx.cx.ie.conditions;

import com.nttdata.de.lib.logging.SkyLogger;

import java.util.regex.Pattern;


public class RequiredFirstname extends AbstractCondition {
	
	@Override
	public String getTagMatchToCheck() {
		return "firstname";
	}
	
	
	protected final Pattern chkB_SkyName = Pattern.compile("(?i)((sky)|(premiere))");
	protected final Pattern chkB_KGDName = Pattern.compile("(?i)((kdg)|(kabel deutschland))");
	protected final Pattern chkB_Unity = Pattern.compile("(?i)((kdg)|(kabel deutschland))");
	
	@Override
	public boolean checkValue(String value) {
		if (chkB_SkyName.matcher(value).find() || chkB_KGDName.matcher(value).find() || chkB_Unity.matcher(value).find()) {
			
			SkyLogger.getCXIELogger().debug("IE:RequiredFirstname:" + value + " Rejected: CompanyName");
			return false;
		} else if (value != null && value.length() < 4) {
			SkyLogger.getCXIELogger().debug("IE:RequiredLastname:" + value + " Rejected: FirstNameToShort");
			return false;
		} else {
			return true;
		}
	}
}
