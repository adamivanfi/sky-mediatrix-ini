/**
 * 
 */
package com.nttdata.de.ityx.cx.sky;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.designer.IParameterMap;
import de.ityx.contex.interfaces.document.CDocument;

import java.util.Iterator;

/**
 * @author DHIFLM
 * 
 */
public class MXDocpoolBean extends AbstractWflBean {



	public void execute(IFlowObject flowObject) throws Exception {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name + ":";
		SkyLogger.getItyxLogger().debug(logPrefix + "enter");

		// Reads process input.
		IParameterMap inputMap = flowObject.getInputMap("modelMap");

		String inputValues = "";
		for (Iterator it = inputMap.names(); it.hasNext();) {
			inputValues += it.next().toString() + "; ";
		}
		SkyLogger.getItyxLogger().debug(logPrefix + ": " + inputValues);

		/* output */
		String output = inputMap.getParameter("output").getAsString();
		SkyLogger.getItyxLogger().debug(logPrefix + "output: " + output);

		Object docContainerO = flowObject.get(DocContainerUtils.DOC);
		CDocumentContainer<CDocument> docContainer = null;
		if (docContainerO == null) {
			SkyLogger.getItyxLogger().debug("docContainer is not accessible");
			// ok for ex. ArchiveProzess
		} else if (docContainerO instanceof CDocumentContainer) {
			docContainer = (CDocumentContainer<CDocument>) docContainerO;
		} else {
			SkyLogger.getItyxLogger().debug("docContainer has the type:" + "empty");
		}
		String docId = "";
		if (docContainer != null && docContainer.getDocument(0) != null && docContainer.getDocument(0).getNote(TagMatchDefinitions.DOCUMENT_ID) != null) {
			docId = (String) docContainer.getDocument(0).getNote(TagMatchDefinitions.DOCUMENT_ID);
		}

		if ((docId == null || docId.isEmpty()) && (inputMap.getParameter(TagMatchDefinitions.DOCUMENT_ID) != null)) {
			docId = inputMap.getParameter(TagMatchDefinitions.DOCUMENT_ID).getAsString();
		}
		SkyLogger.getItyxLogger().debug("MXDocpoolBean processed doc with id:" + docId);

		flowObject.put("output", output);
		setSiebelMetaData(flowObject, inputMap);
	}

	/**
	 * @param flowObject
	 * @param inputMap
	 */
	protected void setSiebelMetaData(IFlowObject flowObject, IParameterMap inputMap) {
		/* UniqueDocumentId */
		String docId = inputMap.getParameter(TagMatchDefinitions.DOCUMENT_ID).getAsString();
		SkyLogger.getItyxLogger().debug(docId + ": " + TagMatchDefinitions.DOCUMENT_ID + ": " + docId);
		flowObject.put(TagMatchDefinitions.DOCUMENT_ID, docId);
		String metadataoutput = "";
		/* CustomerID */
		String customerId = inputMap.getParameter(TagMatchDefinitions.CUSTOMER_ID).getAsString();
		metadataoutput += TagMatchDefinitions.CUSTOMER_ID + ": " + customerId + "; ";
		flowObject.put(TagMatchDefinitions.CUSTOMER_ID, customerId);

		/* contactID */
		String contactId = inputMap.getParameter(TagMatchDefinitions.CONTACT_ID).getAsString();
		metadataoutput += TagMatchDefinitions.CONTACT_ID + ": " + contactId + "; ";
		flowObject.put(TagMatchDefinitions.CONTACT_ID, contactId);

		/* channel */
		String channel = inputMap.getParameter(TagMatchDefinitions.CHANNEL).getAsString();
		metadataoutput += TagMatchDefinitions.CHANNEL + ": " + channel + "; ";
		flowObject.put(TagMatchDefinitions.CHANNEL, channel);

		/* Direction */
		String direction = inputMap.getParameter("Direction").getAsString();
		metadataoutput += "Direction: " + direction + "; ";
		flowObject.put("Direction", direction);

		/* ActivityID */
		String activity = inputMap.getParameter(TagMatchDefinitions.ACTIVITY_ID).getAsString();
		metadataoutput += TagMatchDefinitions.ACTIVITY_ID + ": " + activity + "; ";
		flowObject.put(TagMatchDefinitions.ACTIVITY_ID, activity);

		SkyLogger.getItyxLogger().debug(docId + ": SetSiebelMetadata: " + metadataoutput);

		/* Subproject Name */
		// String subprojectName = "";
		// final Parameter tpParam =
		// inputMap.getParameter(TagMatchDefinitions.TP_NAME);
		// if (tpParam != null)
		// subprojectName = tpParam.getAsString();

		// Creates a document with metadata.
		CDocument doc =  StringDocument.getInstance(docId);
		copyMetadata(DocContainerUtils.getDoc(flowObject), doc);   //TODO: Ist hier notwendig? Einspringpunkte prüfen
		doc.setNote(TagMatchDefinitions.DOCUMENT_ID, docId);
		doc.setNote(TagMatchDefinitions.CONTACT_ID, contactId);
		doc.setNote(TagMatchDefinitions.CUSTOMER_ID, customerId);
		doc.setNote(TagMatchDefinitions.CHANNEL, channel);
		doc.setNote("Direction", direction);
		doc.setNote(TagMatchDefinitions.ACTIVITY_ID, activity);

		flowObject.put(DocContainerUtils.DOC, new CDocumentContainer<>(doc));
	}


	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{
				new KeyConfiguration(DocContainerUtils.DOC,CDocumentContainer.class),
				new KeyConfiguration(TagMatchDefinitions.CHANNEL,String.class),
				new KeyConfiguration(TagMatchDefinitions.CONTACT_ID,String.class),
				new KeyConfiguration(TagMatchDefinitions.CUSTOMER_ID,String.class),
				new KeyConfiguration(TagMatchDefinitions.DOCUMENT_ID,String.class),
				new KeyConfiguration("output",String.class),
		};
	}

}
