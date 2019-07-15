package com.nttdata.de.ityx.cx.workflow.incoming.i2_preprocessing;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
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
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitAndRoute extends AbstractWflBean {
	
	public static final Pattern SEPA_BARCODE_PATTERN = Pattern.compile("SEPA_B2C_Mandat");
	public static final Pattern SEPA_REGEX_PATTERN = Pattern.compile("(?i)[SEPAÄ\\- ]{4,7}L[aä]stsc[h\\s]{1,3}[riftnlI\\s]{3,8}[mrinaäd\\s]{4,9}[tf]");
	public static final Pattern SEPA_REGEX_WHITELISTPATTERN = Pattern.compile("(?i)(Gl[äaii]{1,2}ubiger-Identifikationsnummer)|(Mandatsreferenznummer)|([Bß8]elastungsdatum)");
	public static final Pattern SEPA_REGEX_BLACKLISTPATTERN = Pattern.compile("(?i)(beigef[üuii]{1,2}gtes [SEPA\\- ]{5,7}Lastsch ?riftmandat)|([SEPA\\- ]{5,7}Lastsch ?riftmandat vollst[äaii]{1,2}ndig)|(Gewerbekunden)");
	
	private static final String VOS_BARCODE = "(648433)|(vosbelege)";
	private static final Pattern VOS_PATTERN = Pattern.compile(VOS_BARCODE);
	private static final Pattern VOS_SUBJECT_PATTERN = Pattern.compile("((Arbeitsauftrag[\\s\\.]{1,3}f[üiu]{1,2}r[\\s\\.]{1,3}((Ger[äa]tet[äa]usch)|(Installation)))|(((Servicebeleg)|(Auftrag))[\\s\\.]{1,3}Vor-Ort-Problembehebung)|(Messprotokoll)|(Arbeitsauftrag[\\s\\w\\n\\r\\t.&\\.,:;_\\-\\u0000-\\uFFff]{1,200}((SPORTSBAR[\\s\\w\\n\\r\\t.&\\.,:;_\\-\\u0000-\\uFFff]{1,200}((Problembehebung)|(Installation)))|(EVENT)))|([8S00 ]{1,2}JP9[eJfc]{2}Gk7Ev7[Y|V]{1,2}Af))");
	
	private final String standardParameter = "300_Classification";
	
	private final String mandateParameter = "401_CustomerIndexingMandate";
	
	private final String vosParameter = "400_CustomerIndexing";
	
	
	@Override
	public void execute(IFlowObject flow) throws Exception {
		
		CDocument doc = DocContainerUtils.getDoc(flow);
		CDocumentContainer cont = DocContainerUtils.getDocContainer(flow);
		String docId = (String) doc.getNote(TagMatchDefinitions.DOCUMENT_ID);
		
		List<Integer> mandatePages = new LinkedList<>();
		List<Integer> coverPages = new LinkedList<>();
		
		String oldformtype=DocContainerUtils.getFormtype(cont);
		
		boolean vosUpload = ("vosbelege_mks".equals(oldformtype) || "vosbelege_sp".equals(oldformtype));
		boolean vosDocument = WorkflowTextExtractionUtils.documentBarcodeMatchPattern(doc, VOS_PATTERN) || checkIfVosMultipagesFax(doc) || vosUpload;
		
		for (CPage page : doc.getPages()) {
			if (vosDocument) {
				
				Integer pageNo = page.getPageno();
				CDocument newVosDoc = DocContainerUtils.getPageDocument(pageNo, doc);
				Connection con = ContexDbConnector.getAutoCommitConnection();
				
				String newVosDocId = DocIdGenerator.createUniqueDocumentId(con, TagMatchDefinitions.DocumentDirection.SPLITTED, TagMatchDefinitions.Channel.BRIEF, docId);
				try {
					con.close();
				} catch (SQLException e) {
					SkyLogger.getWflLogger().warn("Problem closing connection for " + newVosDocId + "\n Cause: " + e.getMessage(), e);
				}
				CDocumentContainer vosContainer = createContainerAndCopyMetadata(cont, newVosDoc, newVosDocId);
				if (!vosUpload) {
					DocContainerUtils.setFormtype(vosContainer, newVosDoc, "vosbelege");
				}else{
					DocContainerUtils.setExtFormtype(vosContainer, newVosDoc, oldformtype);
				}
				try {
					ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(), vosParameter, vosContainer, newVosDoc);
				} catch (Exception e) {
					SkyLogger.getWflLogger().error("Problems with scheduling vosBelege:" + newVosDocId + e.getMessage(), e);
					throw e;
				}
				
			} else if (WorkflowTextExtractionUtils.pageBarcodeMatchPattern(page, SEPA_BARCODE_PATTERN) || (WorkflowTextExtractionUtils.pageTextMatchPattern(page, SEPA_REGEX_PATTERN) && (WorkflowTextExtractionUtils.pageTextMatchPattern(page, SEPA_REGEX_WHITELISTPATTERN) && !WorkflowTextExtractionUtils.pageTextMatchPattern(page, SEPA_REGEX_BLACKLISTPATTERN)))) {
				
				Integer pageNo = page.getPageno();
				mandatePages.add(pageNo);
				
				CDocument newMandateDoc = DocContainerUtils.getPageDocument(pageNo, doc);
				Connection con = ContexDbConnector.getAutoCommitConnection();
				String newMandateDocID = DocIdGenerator.createUniqueDocumentId(con, TagMatchDefinitions.DocumentDirection.SPLITTED, TagMatchDefinitions.Channel.BRIEF, docId);
				try {
					con.close();
				} catch (SQLException e) {
					SkyLogger.getWflLogger().warn("Problem closing connection for " + newMandateDocID + "\n Cause: " + e.getMessage(), e);
				}
				CDocumentContainer mandateContainer = createContainerAndCopyMetadata(cont, newMandateDoc, newMandateDocID);
				DocContainerUtils.setFormtype(mandateContainer, newMandateDoc, TagMatchDefinitions.SEPA_MANDATE);
				try {
					ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(), mandateParameter, mandateContainer, newMandateDoc);
				} catch (Exception e) {
					SkyLogger.getWflLogger().error("Problems with scheduling mandate:" + newMandateDocID + e.getMessage(), e);
					throw e;
				}
			} else {
				coverPages.add(page.getPageno());
			}
		}
		
		if (coverPages.size() > 0) {
			if (mandatePages.size() > 0) {
				doc.setNote(TagMatchDefinitions.SEPA_MANDATE, StringUtils.join(mandatePages, "','"));
				doc.setTitle(SEPA_MANDAT_MULTI_CASE + doc.getTitle());
			}
			
			ShedulerUtils.storeDocAndScheduleProcess(ShedulerUtils.getDefaultUnit(), ShedulerUtils.getDefaultMaster(), standardParameter, cont, doc);
		}
	}
	
	
	private boolean checkIfVosMultipagesFax(CDocument cdoc) {
		//CDocument cdoc = DocContainerUtils.getDoc(flow);
		if (cdoc != null) {
			int matcherPageCounter = 0;
			int pagesInDocument = cdoc.getPageCount();
			String channel = (String) cdoc.getNote(TagMatchDefinitions.CHANNEL);
			if (channel != null && "FAX".equalsIgnoreCase(channel)) {
				
				for (CPage page : cdoc.getPages()) {
					String text = page.getContentAsString();
					if (text != null) {
						Matcher matcher = VOS_SUBJECT_PATTERN.matcher(text);
						if (matcher.find()) {
							matcherPageCounter++;
						}
					}
					if ((pagesInDocument == 1 && matcherPageCounter > 0) || (matcherPageCounter > 1)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{new KeyConfiguration("splitteddocs", LinkedList.class), new KeyConfiguration("splittedcount", Integer.class), new KeyConfiguration("SEPA_extracted", Boolean.class), new KeyConfiguration("SEPA_extractedS", String.class)};
	}
}
