
package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.List;

/**
 *
 * @author MEINUG
 */
public class FHVM_CorrectMandateFormtype extends AbstractWflBean {

    public static final String MANDATE = "mandateDoc";
    public static final String CONTAINSMANDATEDOC = "containsMandateDoc";

    @Override
    public void execute(IFlowObject flow) throws Exception {
    	String parameterMandate = FlowUtils.getRequiredNonEmptyString(flow, "FHV_docStateMediatrixWrite");

        if (flow.get(CONTAINSMANDATEDOC) != null && ((Boolean) flow.get(CONTAINSMANDATEDOC))) {
            CDocumentContainer mDocC = (CDocumentContainer) flow.get(MANDATE);
            CDocument mDoc = DocContainerUtils.getDoc(mDocC);
            String docid = DocContainerUtils.getDocID(mDoc);

            String validity = null;
            if (mDoc.getTags() != null) {
                for (TagMatch tm : mDoc.getTags()) {
                    //SignatureFlag
                    if (tm.getIdentifier().equals(TagMatchDefinitions.SEPA_SIGNATURE_FLAG)) {
                        validity = tm.getTagValue();
                    }
                }
            }
            // s.Mail von Robert von Di. 29.07.2014 : Fachhandelsverträge mit Mandaten in Mediatrix
            validity="1";
            boolean hasCust = hasCustomer(mDocC);
            boolean hasMandateRef= hasMandateRef(mDoc);
            if (!hasCust || !hasMandateRef || validity.equals("0")) {
                validity = "0";
                mDoc.setNote("titel", SEPA_MANDAT_MANUELE_VERARBEITUNG + mDoc.getTitle());
                mDoc.setTitle(SEPA_MANDAT_MANUELE_VERARBEITUNG + mDoc.getTitle());
                mDoc.setNote(TagMatchDefinitions.SEPA_SIGNATURE_FLAG, validity.equals("0") ? "0" : "1");
                mDocC.setNote(TagMatchDefinitions.ARCHIVE_FLAG, false);
                if (hasCust) {
                    flow.put("parameterMandate", FlowUtils.getRequiredNonEmptyString(flow, "FHV_mandateStateWaitForContactId"));
                    parameterMandate = FlowUtils.getRequiredNonEmptyString(flow, "FHV_mandateStateWaitForContactId");
                    } else {
                    flow.put("parameterMandate", FlowUtils.getRequiredNonEmptyString(flow, "FHV_docStateMediatrixWrite"));
                }
            } else { // and hasCustomer
                mDoc.setNote(TagMatchDefinitions.SEPA_SIGNATURE_FLAG, "1");
                mDoc.setNote("titel", SEPA_MANDAT_AUTOMAT_VERARBEITUNG + mDoc.getTitle());
                mDoc.setTitle(SEPA_MANDAT_AUTOMAT_VERARBEITUNG + mDoc.getTitle());
                mDocC.setNote(TagMatchDefinitions.ARCHIVE_FLAG, true);
                flow.put("parameterMandate", FlowUtils.getRequiredNonEmptyString(flow, "FHV_mandateStateWaitForContactId"));
                parameterMandate = FlowUtils.getRequiredNonEmptyString(flow, "FHV_mandateStateWaitForContactId");
                 }
            SkyLogger.getWflLogger().debug("FHV430: " + docid + " Title:" + mDoc.getTitle()+" Form:"+ DocContainerUtils.getFormtype(mDoc)+" Cust:"+hasCust+" Signed:"+validity);
            
            DocContainerUtils.setFormtype(mDocC, mDoc, TagMatchDefinitions.SEPA_MANDATE);
            mDoc.setNote(TagMatchDefinitions.SEPA_SIGNATURE_FLAG, validity);
//            flow.put(MANDATE, mDocC);
            ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(),  parameterMandate, mDocC, mDoc);
        } else {
            SkyLogger.getWflLogger().error("FHV430: " + DocContainerUtils.getDocID(flow) + " Flow for CDocument doesn't contain expected Mandate ");
        }

    }

    protected Boolean hasCustomer(CDocumentContainer cont) {
        Boolean hasCustomer = false;
        for (TagMatch tm : (List<TagMatch>) cont.getTags()) {
            // Customer is indexed.
            if (tm.getIdentifier().equals(TagMatchDefinitions.CUSTOMER_ID)) {
                String value = tm.getTagValue();
                if (value != null && value.trim().length() > 0) {
                    hasCustomer = true;
                    break;
                }
            }
        }
        return hasCustomer;
    }
    
    
    protected Boolean hasMandateRef(CDocument document) {
        return !DocContainerUtils.isEmpty((String) document.getNote(TagMatchDefinitions.SEPA_MANDATE_NUMBER));
    }

}
