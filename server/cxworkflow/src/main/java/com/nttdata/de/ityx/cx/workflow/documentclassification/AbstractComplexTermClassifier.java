package com.nttdata.de.ityx.cx.workflow.documentclassification;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.interfaces.designer.IFlowObject;

public abstract class AbstractComplexTermClassifier extends AbstractWflReportedBean {

    @Override 
    public void execute(IFlowObject flowObject) throws Exception {
        if (classify(flowObject)) {
            flowObject.put("classified", true);
            SkyLogger.getWflLogger().info("ComplexTermClassifier: " + DocContainerUtils.getDocID(flowObject) + " "+ DocContainerUtils.getFormtype(flowObject) );
                
        }
    }
    public abstract boolean classify(IFlowObject flowObject) ;

	@Override
    public KeyConfiguration[] getKeys() {
        return new KeyConfiguration[]{
				new KeyConfiguration(TagMatchDefinitions.EVAL_FORMTYPE, String.class),
				new KeyConfiguration("classified", Boolean.class)
		};
    }
}
