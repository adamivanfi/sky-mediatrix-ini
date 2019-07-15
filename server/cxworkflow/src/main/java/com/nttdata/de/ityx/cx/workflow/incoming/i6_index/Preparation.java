package com.nttdata.de.ityx.cx.workflow.incoming.i6_index;

import com.nttdata.de.ityx.cx.sky.enrichment.EnrichmentBean;
import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import com.nttdata.de.ityx.sharedservices.utils.ContexDbConnector;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.DocIdGenerator;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Preparation extends AbstractWflReportedBean {

	public static final String WITH_MANDATE = "withMandate";
	public static final String SEPA_MANDAT_VERARBEITUNG = "[SEPA-Mandat manuelle Indizierung]";
	public static final String SBS_GASTRO = "SBS_Gastro";
	public static final String SBS_480_SCS_TO_SBS_FORWARD = "SBS_480_ScsToSbsForward";

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		String logPrefix = getClass().getName() + "#" + new Object() {}.getClass().getEnclosingMethod().getName() + " ";

		CDocumentContainer con = DocContainerUtils.getDocContainer(flowObject);
		CDocument document = DocContainerUtils.getDoc(con);
		String docid=DocContainerUtils.getDocID(document);
		logPrefix+=docid+":";
				String parameter = "600_MXInjection";
//		String parameterP = "600_MXInjectionDoc_v3.00";

		String contactParameter = "500_CRMActivity";
		//String contactParameterP = "500_CRMActivityDoc_v3.00";
		String contactSEPAParameter = "501_CRMActivitySepa";
		//String contactSEPAParameterP = "501_MandateCRMActivity_v3.00";
		String validationParameter = "460_ManualIndexing";
		//String validationParameterP = "460_ManualIndexing_v3.00";

		String multiParameter = contactParameter;
		//String multiParameterP = contactParameterP;

		String mandate = null;
		String customer = null;
		String contract = null;
		String smartcard = null;
		String miqid=null;
		String formtype = null;
		boolean multiPageSEPA = false;
		boolean isFormtypeSEPA = false;
		boolean isFormtypeSBS = false;
		final List tags = con.getTags();
		if (tags != null && !tags.isEmpty()) {
			TagMatch tm = (TagMatch) tags.get(0);
			formtype = tm.getTagValue(TagMatchDefinitions.MANUAL_FORMTYPE);
			tmtrim(tm.getTagMatch("CustomerID"));
			tmtrim(tm.getTagMatch("ContractNumber"));
			customer = tm.getTagValue("CustomerID");
			contract = tm.getTagValue("ContractNumber");
			smartcard = tm.getTagValue("SmartcardNumber");
			miqid = tm.getTagValue(TagMatchDefinitions.DMS_MANUAL_INDEXING_QID);
			if (miqid!=null && !miqid.isEmpty()){
				flowObject.put(TagMatchDefinitions.DMS_MANUAL_INDEXING_QID,miqid);
				document.setNote(TagMatchDefinitions.DMS_MANUAL_INDEXING_QID, miqid);
			}
			
			mandate = tm.getTagValue(TagMatchDefinitions.SEPA_MANDATE_NUMBER);

			if (mandate==null || mandate.isEmpty()) {
				mandate = (String) document.getNote(TagMatchDefinitions.SEPA_MANDATE_NUMBER);
			}
				
			if (mandate != null) {
				flowObject.put("VerifiedMandateNumber", mandate);
			}

			isFormtypeSEPA = formtype != null && formtype.equals(TagMatchDefinitions.SEPA_MANDATE);
			isFormtypeSBS = formtype != null && formtype.toLowerCase().startsWith("sbs_");
			boolean needContact = !isFormtypeSBS;

			if (needContact && !DocContainerUtils.isEmpty(customer)) {
				flowObject.put("VerifiedCustomerNumber", customer);
				flowObject.put("VerifiedDocument", true);
				document.setNote(TagMatchDefinitions.EVAL_CUSTOMER_NUMBER, customer);
				parameter = chooseParameter(contactParameter, contactSEPAParameter, multiParameter, isFormtypeSEPA, multiPageSEPA);
				needContact = false;
			}else{
				SkyLogger.getWflLogger().info(logPrefix+" Customer is Empty!");
			}

			if (!DocContainerUtils.isEmpty(contract)) {
				flowObject.put("VerifiedContractNumber", contract);
				flowObject.put("VerifiedDocument", true);
				document.setNote(TagMatchDefinitions.EVAL_CONTRACT_NUMBER, contract);

				if (needContact) {
					parameter = chooseParameter(contactParameter, contactSEPAParameter, multiParameter, isFormtypeSEPA, multiPageSEPA);
					needContact = false;
				}
			} else {
				flowObject.put("VerifiedContractNumber", "");
			}

			if (!DocContainerUtils.isEmpty(smartcard)) {
				flowObject.put("VerifiedSmartcardNumber", smartcard);
				flowObject.put("VerifiedDocument", true);
				if (needContact) {
					parameter = chooseParameter(contactParameter, contactSEPAParameter, multiParameter, isFormtypeSEPA, multiPageSEPA);
				}
			} else {
				flowObject.put("VerifiedSmartcardNumber", "");
			}
			if (!DocContainerUtils.isEmpty(mandate)) {
				flowObject.put("VerifiedMandateNumber", mandate);
				flowObject.put("VerifiedDocument", true);
				if (needContact) {
					parameter = chooseParameter(contactParameter, contactSEPAParameter, multiParameter, isFormtypeSEPA, multiPageSEPA);
				}
			} else {
				flowObject.put("VerifiedMandateNumber", "");
			}

			int pageCount = document.getPageCount();
			if (isFormtypeSEPA && pageCount > 1) {
				if (document.getNotes() != null && document.getNotes().get("DocumentID") != null) {
					CDocumentContainer mandateContainer = createSEPACopy(con, document, docid);
					flowObject.put("mandateContainer", mandateContainer);
					try{
						EnrichmentBean.enrichContainer(flowObject,mandateContainer, false);
						ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(), contactSEPAParameter, mandateContainer, DocContainerUtils.getDoc(mandateContainer));
					}catch (Exception e){
						SkyLogger.getWflLogger().error(logPrefix+"Problems with scheduling mandate:"+docid+e.getMessage(),e);
						throw e;
					}

					document.setTitle(SEPA_MANDAT_MULTI_CASE + document.getTitle());
					document.setNote(TagMatchDefinitions.AUTOPROCESSING_FLAG, TagMatchDefinitions.SEPA_MANDATE);
					String autoFormtype = (String) document.getNote(TagMatchDefinitions.EVAL_FORMTYPE_BEFORE_VALIDATION);
					String ignoreMultiFormtypeSEPA = FlowUtils.getOptionalString(flowObject, "ignoreMultiFormtypeSEPA", "");
					if (autoFormtype != null && ignoreMultiFormtypeSEPA.contains(autoFormtype)) {
						autoFormtype = "unclassified";
						multiParameter = validationParameter;
						//multiParameterP = validationParameterP;

					}
					document.setFormtype(autoFormtype);
					document.setNote(TagMatchDefinitions.MANUAL_FORMTYPE, autoFormtype);
					multiPageSEPA = true;
				}
			}
		}else{
			SkyLogger.getWflLogger().error(logPrefix+" ContainerTags are Empty");

		}
		flowObject.put(WITH_MANDATE, multiPageSEPA);
		flowObject.put("withMandateS", multiPageSEPA ? "1" : "0");
		document.setNote("EvalTimestamp", "" + System.currentTimeMillis());
		flowObject.put("parameter", isFormtypeSBS? SBS_480_SCS_TO_SBS_FORWARD:parameter);
		String info=(docid + " Formtype:" + formtype + " Cust:" + customer + ", ContractNr:" + contract + " SC:" + smartcard + " withMandate:" + multiPageSEPA + " Parameter:" + parameter);
		setWflStepDetail(flowObject, info);

		Object formRelO=document.getNote(TagMatchDefinitions.FORMTYPERELIABILITY);
		int  formRel=0;
		if (formRelO!=null && formRelO instanceof Integer){
			formRel=(Integer) formRelO;
		}
		if (formRel<1) {
			document.setNote(TagMatchDefinitions.FORMTYPERELIABILITY, 100);
			con.setNote(TagMatchDefinitions.FORMTYPERELIABILITY, 100);
		}
		try {
			EnrichmentBean.enrichContainer(flowObject, con, false);
		}catch (Exception e){
			SkyLogger.getWflLogger().error("Problems with enrichment:"+docid+e.getMessage(),e);
			throw e;
		}

	}

	private String chooseParameter(String contactParameter, String contactSEPAParameter, String multiParameter, boolean isFormtypeSEPA, boolean multiPageSEPA) {
		return  isFormtypeSEPA ? contactSEPAParameter : (multiPageSEPA ? multiParameter : contactParameter);
	}



	public CDocumentContainer createSEPACopy(CDocumentContainer con, CDocument document, String docid) throws CloneNotSupportedException {
		CDocument newdoc = document.clone();
		copyMetadata(document, newdoc);
		newdoc.setNote(TagMatchDefinitions.EVAL_FORMTYPE, TagMatchDefinitions.SEPA_MANDATE);
		newdoc.setTitle(SEPA_MANDAT_VERARBEITUNG + document.getTitle());
		Connection connection=null;
		CDocumentContainer mandateContainer=null;
		try {
			connection = ContexDbConnector.getAutoCommitConnection();
			String newDocID = DocIdGenerator.createUniqueDocumentId(connection, TagMatchDefinitions.DocumentDirection.SPLITTED, TagMatchDefinitions.Channel.BRIEF, docid);
			mandateContainer = createContainerAndCopyMetadata(con, newdoc,newDocID);
			DocContainerUtils.setFormtype(mandateContainer,newdoc,TagMatchDefinitions.SEPA_MANDATE );

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
    	if(connection!=null) {
    		try {
    			connection.close();
			} catch (SQLException e) {
				SkyLogger.getWflLogger().warn("Problem closing connection for Sequence " + docid + "\n Cause: " + e.getMessage(), e);
			}
    	}
		}
		return mandateContainer;
	}

	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{
				new KeyConfiguration("parameter", String.class),
				new KeyConfiguration(WITH_MANDATE, Boolean.class),
				new KeyConfiguration("withMandateS", String.class),
				new KeyConfiguration("VerifiedDocument", String.class),
				new KeyConfiguration("VerifiedCustomerNumber", String.class),
				new KeyConfiguration("VerifiedContractNumber", String.class),
				new KeyConfiguration("VerifiedSmartcardNumber", String.class),
		};
	}
	protected void tmtrim(TagMatch tm) {
		if (tm != null) {
			String textValue = tm.getTagValue();
			if (textValue != null && textValue.length() > 0) {
				String trim = textValue.trim();
				if (!trim.equals(textValue)) {
					tm.setTagValue(trim);
				}
			}
		}
	}

}
