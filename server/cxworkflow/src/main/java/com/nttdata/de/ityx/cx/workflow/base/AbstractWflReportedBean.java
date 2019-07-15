/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nttdata.de.ityx.cx.workflow.base;

import com.nttdata.de.ityx.cx.sky.reporting.WorkflowReportingBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.designer.state.StateResult;
import de.ityx.contex.interfaces.designer.IExflowState;
import de.ityx.contex.interfaces.designer.IFlowObject;

/**
 *
 * @author MEINUG
 */
public abstract class AbstractWflReportedBean extends AbstractWflBean {

    @Override
    public StateResult execute(int arg0, IFlowObject flowObject, IExflowState arg2) throws Exception {
        try {
            String docid = DocContainerUtils.getDocID(flowObject);
            if (docid != null) {
                parameterMap.put(Thread.currentThread().getId(), docid);
                setWflChannel(flowObject);
            }
            execute(flowObject);
            (new WorkflowReportingBean()).execute(flowObject, arg2, false);
        } catch (Exception e) {
            SkyLogger.getWflLogger().error("ARB ERROR: " + e.getMessage(), e);
        }
        return StateResult.STATEOK;
    }



    @Override
    public KeyConfiguration[] getKeys() {
        //useless here, can be overwritten by subclasses
        return null;
    }

}
