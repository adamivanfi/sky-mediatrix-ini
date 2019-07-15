package com.nttdata.de.sky.ityx.common;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meinusch on 10.08.15.
 */
public class DocIDClient {

	public static String getOrGenerateDocId(Question question, TagMatchDefinitions.DocumentDirection doctype, TagMatchDefinitions.Channel channel, String parentDocID) {
		String documentid = getDocId(question);
		if (documentid != null && !documentid.isEmpty()) {
			return documentid;
		}
		List<Object> parameter = new ArrayList<Object>();
		parameter.add((question.getProjectId() > 0) ? question.getProjectId() : 110);
		parameter.add(doctype);
		parameter.add(channel);
		if (parentDocID != null) {
			parameter.add(parentDocID);
		}
		List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_CREATE_DOCUMENT_ID.name(), parameter);

		if (result == null) {
			SkyLogger.getClientLogger().fatal("Problem getting DocID: " + question.getId() + ": result is empty");
			return null;
		} else {
			Object result2 = result.get(0);
			if (result2 == null || !(result2 instanceof String)) {
				SkyLogger.getClientLogger().fatal("Problem getting DocID: " + question.getId() + ": result is not string" + result2);
				for (Object r : result) {
					SkyLogger.getClientLogger().fatal("Problem getting DocID: " + question.getId() + ": result_item:" + r);
				}
				return null;
			} else {
				documentid = (String) result2;
			}
		}
		documentid = (String) result.get(0);
		question.setDocId(documentid);
		question.setHeaders(TagMatchDefinitions.addOrReplaceXTMHeader(question.getHeaders(), TagMatchDefinitions.DOCUMENT_ID, documentid));
		return documentid;

	}


	public static String getDocId(Question question) {
		String documentid = question.getDocId();
		if (documentid != null && !documentid.isEmpty()) {
			return documentid;
		}
		documentid = TagMatchDefinitions.extractXTMHeader(question.getHeaders(), TagMatchDefinitions.DOCUMENT_ID);
		if (documentid != null && !documentid.isEmpty()) {
			question.setDocId(documentid);
		}
		return documentid;
	}
}
