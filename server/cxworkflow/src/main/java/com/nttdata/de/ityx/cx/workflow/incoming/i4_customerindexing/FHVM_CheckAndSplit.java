package com.nttdata.de.ityx.cx.workflow.incoming.i4_customerindexing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.cx.workflow.base.FlowUtils;
import com.nttdata.de.ityx.cx.workflow.incoming.i2_preprocessing.SplitAndRoute;
import com.nttdata.de.ityx.cx.workflow.utils.WorkflowTextExtractionUtils;
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
import de.ityx.contex.interfaces.document.CPage;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class FHVM_CheckAndSplit extends AbstractWflBean {

	public static final String CONTRACT = "contractDoc";
	public static final String MANDATE = "mandateDoc";

	public static final String CONTAINSCONTRACTDOC = "containsContractDoc";
	public static final String CONTAINSMANDATEDOC = "containsMandateDoc";

	@Override
	public void execute(IFlowObject flow) throws Exception {
		Boolean containsContractDoc = false;
		Boolean containsMandateDoc = false;
		
		CDocumentContainer cont = DocContainerUtils.getDocContainer(flow);
		CDocument doc = DocContainerUtils.getDoc(flow, cont);
		String docId = DocContainerUtils.getDocID(doc);

		boolean hasCustomer = hasCustomer(cont);
		String contact = FlowUtils.getRequiredNonEmptyString(flow, "FHV_docStateWaitForContactId");
		String contractParameter = hasCustomer ? contact : FlowUtils.getRequiredNonEmptyString(flow, "FHV_docStateMediatrixWrite");
		String mandate = FlowUtils.getRequiredNonEmptyString(flow, "FHV_mandateStateWaitForContactId");
		String mandateParameter = hasCustomer ? mandate : FlowUtils.getRequiredNonEmptyString(flow, "FHV_docStateMediatrixWrite");
		
		List<Integer> allPages = new LinkedList<>();
		List<Integer> mandatePages = new LinkedList<>();
		List<Integer> contractPages = new LinkedList<>();

		if (doc != null) {
			for (CPage page : doc.getPages()) {
				allPages.add(page.getPageno());
				if (WorkflowTextExtractionUtils.pageBarcodeMatchPattern(page, SplitAndRoute.SEPA_BARCODE_PATTERN) || (WorkflowTextExtractionUtils.pageTextMatchPattern(page, SplitAndRoute.SEPA_REGEX_PATTERN) && (WorkflowTextExtractionUtils.pageTextMatchPattern(page, SplitAndRoute.SEPA_REGEX_WHITELISTPATTERN) && !WorkflowTextExtractionUtils.pageTextMatchPattern(page, SplitAndRoute.SEPA_REGEX_BLACKLISTPATTERN)))) {
					mandatePages.add(page.getPageno());
				} else {
					contractPages.add(page.getPageno());
				}
			}
			SkyLogger.getWflLogger().info("FHV430: " + docId + " contains " + mandatePages.size() + " mandate page(s) and " + contractPages.size() + " contract pages");

			if (mandatePages.size() > 0) { // Mandat erkannt
				if (contractPages.size() > 0) { // Mandat und Vertrag gefunden -> Splitting
					CDocument mandateDoc = doc.clone().cutDocument(allPages, mandatePages);
					Connection con = ContexDbConnector.getAutoCommitConnection();
					String mandateDocID = DocIdGenerator.createUniqueDocumentId(con, TagMatchDefinitions.DocumentDirection.SPLITTED, TagMatchDefinitions.Channel.BRIEF, docId);
		    		try {
		    			con.close();
					} catch (SQLException e) {
						SkyLogger.getWflLogger().warn("Problem closing connection for Sequence " + mandateDocID + "\n Cause: " + e.getMessage(), e);
					}
					copyMetadata(doc, mandateDoc);
					CDocumentContainer mandateContainer = createContainerAndCopyMetadata(cont, mandateDoc, mandateDocID);
					DocContainerUtils.setFormtype(mandateContainer, mandateDoc, TagMatchDefinitions.SEPA_MANDATE);
					flow.put(MANDATE, mandateContainer);
					// flow.put(CONTAINSMANDATEDOC, true);
					containsMandateDoc = true;

					//FHVertrag
					DocContainerUtils.setFormtype(cont, doc, TagMatchDefinitions.FH_VERTRAG);
					if (hasCustomer) {
						doc.setTitle(FHV_AUTO_MANDAT + doc.getTitle());
					} else {
						doc.setTitle(FHV_MANUEL + doc.getTitle());
					}
					cont.setNote(TagMatchDefinitions.ARCHIVE_FLAG, hasCustomer);
					DocContainerUtils.setDocID(cont, doc, docId);
					flow.put(CONTRACT, cont);
					// flow.put(CONTAINSCONTRACTDOC, true);
					containsContractDoc = true;

					SkyLogger.getWflLogger().info("FHV430: " + docId + " / " + mandateDocID + " contract splitted, mandate doc created");
					ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(), mandateParameter, mandateContainer, mandateDoc);
					ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(), contractParameter, cont, doc);

				} else {
					flow.put(MANDATE, cont);
					DocContainerUtils.setFormtype(cont, doc, TagMatchDefinitions.SEPA_MANDATE);
					// flow.put(CONTAINSCONTRACTDOC, false);
					containsContractDoc = false;
					// flow.put(CONTAINSMANDATEDOC, true);
					containsMandateDoc = true;
					SkyLogger.getWflLogger().info("FHV430: " + docId + " mandate only document");
					ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(), mandateParameter, cont, doc);
				}

			} else { // Kein Mandat erkannt -> DocVerarbeitung als Vertrag
				//doc.setFormtype(TagMatchDefinitions.FH_VERTRAG);
				DocContainerUtils.setFormtype(cont, doc, TagMatchDefinitions.FH_VERTRAG);
				cont.setNote(TagMatchDefinitions.ARCHIVE_FLAG, hasCustomer);
				if (hasCustomer) {
					doc.setTitle(FHV_AUTO + doc.getTitle());
				} else {
					doc.setTitle(FHV_MANUEL + doc.getTitle());
				}
//				flow.put(CONTRACT, cont);
				// flow.put(CONTAINSCONTRACTDOC, true);
				containsContractDoc = true;
				// flow.put(CONTAINSMANDATEDOC, false);
				containsMandateDoc = false;
				SkyLogger.getWflLogger().info("FHV430: " + docId + " contract only document");
				ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(), contractParameter, cont, doc);
			}

			//String contractProcess = contractParameter.equals(contact)?"FHV_500_OpenSiebelSR_v3.00":"FHV_602_MXInjection_v3.00";
//			ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(), contractParameter, cont, doc);
//			flow.put("parameterContract", contractParameter);
//			flow.put(CONTAINSCONTRACTDOC, containsContractDoc);
//			flow.put("containsContractDocS", containsContractDoc?"1":"0");
//			flow.put(CONTAINSMANDATEDOC, containsMandateDoc);
//			flow.put("containsMandateDocS", containsMandateDoc?"1":"0");
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

	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{new KeyConfiguration(MANDATE, CDocumentContainer.class), new KeyConfiguration(CONTRACT, CDocumentContainer.class),
				new KeyConfiguration(CONTAINSCONTRACTDOC, Boolean.class),
				new KeyConfiguration("containsContractDocS", String.class),
				new KeyConfiguration(CONTAINSMANDATEDOC, Boolean.class),
				new KeyConfiguration("containsContractDocS", String.class),
				new KeyConfiguration("parameterContract", String.class)};
	}
}
