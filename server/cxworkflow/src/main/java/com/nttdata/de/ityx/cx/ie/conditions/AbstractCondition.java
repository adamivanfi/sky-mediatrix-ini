package com.nttdata.de.ityx.cx.ie.conditions;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.dbo.interfaces.GeneralPointsInterface;
import de.ityx.contex.interfaces.document.StreamedDocument;
import de.ityx.contex.interfaces.extag.ExtagBaseInterface;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.ArrayList;

public abstract class AbstractCondition extends de.ityx.contex.dbo.extag.conditions.Extag_regexcondition
    implements de.ityx.contex.interfaces.extag.Condition, de.ityx.contex.interfaces.extag.GroupCondition,
		de.ityx.contex.interfaces.extag.BeanCondition, de.ityx.contex.interfaces.extag.BeanGroupCondition{

    public abstract String getTagMatchToCheck();

    @Override
    public long validGroupPoints(StreamedDocument sd, TagMatch tm) {
        return validPoints(sd, tm);
    }


    @Override
    public long validPoints(StreamedDocument sd, TagMatch tm) {

        String tagmatchToCheck = getTagMatchToCheck();
        TagMatch child = tm.getTagMatch(tagmatchToCheck);
        if (child == null) {
            SkyLogger.getCXIELogger().debug("IE:TMToCheck:" + tagmatchToCheck + " isEmpty. Rejected.");

			for (String key  : tm.getChildren().keySet() ){
				TagMatch ttm=tm.getChildren().get(key);
				SkyLogger.getCXIELogger().debug("IE:TMToCheck:AT:" + key + ":"+ttm.getTagValue());
				}
            return 0;
        }

        String childValue = child.getTagValue();
        if (isEmptyNumber(childValue)) {
            SkyLogger.getCXIELogger().debug("IE:TMToCheck:" + tagmatchToCheck + " emptyValue. Rejected.");
            for (String key  : tm.getChildren().keySet() ){
				TagMatch ttm=tm.getChildren().get(key);
				SkyLogger.getCXIELogger().debug("IE:TMToCheck:AT:" + key + ":"+ttm.getTagValue());
			}
			return 0;
        } else if (!checkValue(childValue)) {
            SkyLogger.getCXIELogger().debug("IE:TMToCheck:" + tagmatchToCheck + " valueCheck. Rejected.");
            for (String key  : tm.getChildren().keySet() ){
				TagMatch ttm=tm.getChildren().get(key);
				SkyLogger.getCXIELogger().debug("IE:TMToCheck:AT:" + key + ":"+ttm.getTagValue());
			}
			return 0;
        } else {
            return 1;
        }
    }

    public boolean checkValue(String value) {
        return true;
    }

    protected boolean isEmptyNumber(String value) {
        return (value == null || value.isEmpty() || "0".equals(value.trim()));
    }


	@Override
	public void beaninit(GeneralPointsInterface generalPointsInterface, ArrayList<ExtagBaseInterface> arrayList) {

	}
}
