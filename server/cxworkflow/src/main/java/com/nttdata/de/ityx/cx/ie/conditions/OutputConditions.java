package com.nttdata.de.ityx.cx.ie.conditions;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.interfaces.document.StreamedDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.Map;

/**
 * Usable for Debug reasons
 */
public class OutputConditions extends AbstractCondition {

    @Override
    public long validPoints(StreamedDocument sd, TagMatch tm) {
        SkyLogger.getCXIELogger().debug("IE>OC>Root>" + tm.getDisplayName() + "=" + tm.getTagValue());

        Map<String, TagMatch> children = tm.getChildren();

        for (String childKey : children.keySet()) {
            TagMatch childTM = children.get(childKey);
            SkyLogger.getCXIELogger().debug("IE>OC>" + childKey + ":" + childTM.getIdentifier() + "=" + childTM.getTagValue());
        }
        return 1;

    }

    @Override
    public String getTagMatchToCheck() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

}
