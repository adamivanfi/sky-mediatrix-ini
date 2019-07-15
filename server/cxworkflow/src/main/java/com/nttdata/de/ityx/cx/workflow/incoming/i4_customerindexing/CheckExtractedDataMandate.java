package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import de.ityx.contex.interfaces.designer.IFlowObject;

import java.util.Map;

public class CheckExtractedDataMandate extends CheckExtractedData {

    // for SL3K
    @Override
    public String getParameter(IFlowObject flowObject, boolean verifiedDocument, boolean doValidation) throws Exception {
        String parameter = null;
        if (verifiedDocument) {
            parameter = "501_CRMActivitySepa";FlowUtils.getRequiredString(flowObject, "docStateWaitForContactId_SEPA");
        } else {
            parameter = "461_ManualIndexingSepa";//FlowUtils.getRequiredString(flowObject, "docStateManualValidation_SEPA");
        }
        return parameter;
    }
    
     public String getReportingStep(IFlowObject flow) {
        Object mobj = flow.get("reporting_map");
        if (mobj != null && !mobj.getClass().equals(String.class)) {
            Map<String, Object> map = (Map<String, Object>) mobj;
            Integer stepReporting = (Integer) map.get("REPORTING_STEP_COUNTER");
            switch (stepReporting) {
                 case 1:
                    return "MandateL_KundennummerVertrag";
                case 2:
                    return "MandateKundennummerVertrag";
                case 3:
                    return "VertragNrL";
                case 4:
                    return "KundenNrLVertrag";
                case 5:
                    return "VertragNrKundenNr";
                case 6:
                    return "SmcVertrag";
                case 7:
                    return "SmcKundenNr";
                case 8:
                    return "VertragNr_Name";
                case 9:
                    return "VertragNr_Email";
                case 10:
                    return "Smc_Email";
                case 11:
                    return "Smc_Name";
                case 12:
                    return "KundenNrL";
                case 13:
                    return "KundenNr_Name";
                case 14:
                    return "KundenNr_Email";
                case 15:
                    return "ZIP_LastnameFirstname";
                case 16:
                    return "ZIP_LastnameStreet";
                case 17:
                    return "Bank_Lastname";
                case 18:
                    return "Bank_Email";
                default:
                    return stepReporting + "";
            }
        }
        return "";
    }
}
