package com.nttdata.de.ityx.cx.sky.archiving;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.sql.Connection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Handles archive metadata.
 *
 * @author DHIFLM
 */
public class CDocumentArchiveMetaData extends AbstractArchiveMetaData {

	protected static CDocumentArchiveMetaData instance;

	public CDocumentArchiveMetaData() throws Exception {
		super();
	}

	@Override
	public Map<String, String> collectMetadata(Connection con, Object source, Map<String, String> srcMap) throws ClassCastException, NoSuchMethodException {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName();

		if (!de.ityx.contex.impl.document.CDocumentContainer.class.isAssignableFrom(source.getClass())) {
			SkyLogger.getMediatrixLogger().error(logPrefix + "Incompatible parameter class: " + source.getClass().getName() + " cannot be an instance of de.ityx.contex.interfaces.document.CDocumentContainer");
			throw new ClassCastException("Incompatible parameter class: " + source.getClass().getName() + " cannot be an instance of de.ityx.contex.impl.document.CDocumentContainer");
		}
		Map<String, String> metaMap = new TreeMap<>();
		if (srcMap != null) {
			metaMap.putAll(srcMap);
		}
		//CDocument doc = (CDocument) source;
		CDocumentContainer<CDocument> container = (CDocumentContainer<CDocument>) source;
		for (CDocument doc : container.getDocuments()) {

			for (TagMatch tag : doc.getTags()) {
				metaMap.put(tag.getIdentifier(), tag.getTagValue());
				if (tag.getIdentifier().startsWith(X_TAGMATCH)) {
					metaMap.put(tag.getIdentifier().replace(X_TAGMATCH, "").trim(), tag.getTagValue());
				}
			}
		}
		String tmdocid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
		if (tmdocid != null && !tmdocid.isEmpty()) {
			metaMap.put(TagMatchDefinitions.X_TAGMATCH_DOCUMENT_ID, tmdocid);
		}
		//Dokumente die von Contex kommen sind voerst nur INBOUND
		metaMap.put(TagMatchDefinitions.MX_DIRECTION, TagMatchDefinitions.Direction.INBOUND.toString());

		String channelS = metaMap.get(TagMatchDefinitions.CHANNEL);
		if (channelS == null || channelS.isEmpty()) {
			int doctype = (Integer) container.getNote("doctype");
			// ECODE(docpooltype,1,'Letter', 3, 'Fax', 2,'Email'
			if (doctype == 1) {
				metaMap.put(TagMatchDefinitions.CHANNEL, TagMatchDefinitions.Channel.BRIEF.toString());
			} else if (doctype == 2) {
				metaMap.put(TagMatchDefinitions.CHANNEL, TagMatchDefinitions.Channel.EMAIL.toString());
			} else if (doctype == 3) {
				metaMap.put(TagMatchDefinitions.CHANNEL, TagMatchDefinitions.Channel.FAX.toString());
			} else {
				metaMap.put(TagMatchDefinitions.CHANNEL, TagMatchDefinitions.Channel.EMAIL.toString());
			}
		}
		//translates special Tagmatches and computes creationtime
		metaMap = super.collectMetadata(con, source, metaMap);
		return metaMap;
	}
}
