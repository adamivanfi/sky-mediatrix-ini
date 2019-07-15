package com.nttdata.de.ityx.cx.sky.archiving;

import com.nttdata.de.sky.archive.AbstractArchiveMetaData;
import com.nttdata.de.sky.archive.Archive;
import com.nttdata.de.sky.archive.ArchiveMetaDataFactory;
import com.nttdata.de.sky.archive.Metavalue;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ArchiveMetaDataTest {

	private static final String	TEST_DOC_ID			= "ITYX20130503105644344-53683340";
	private static final String	TEST_MANDATE_NUMBER	= "0123456789";

	@Ignore
	@Test
	public void testValidation() {
		AbstractArchiveMetaData instance = null;
		try {
			instance = ArchiveMetaDataFactory.getInstance("CDocumentArchiveMetaData");
		} catch (Exception e) {
			fail(e.getMessage());
		}
		 assert instance.validateArchiveXML(getClass().getResourceAsStream("/archive.xml")) == true;
		 assert instance.validateArchiveXML(getClass().getResourceAsStream("/noarchive.xml")) == false;
	}

	@Ignore
	@Test
	public void testCollectMetadata_CDocumentArchiveMetaData() throws ClassCastException, NoSuchMethodException {
		AbstractArchiveMetaData instance = null;
		try {
			instance = ArchiveMetaDataFactory.getInstance("CDocumentArchiveMetaData");
		} catch (Exception e) {
			fail(e.getMessage());
		}
		CDocument doc =  StringDocument.getInstance("");
		List<TagMatch> tags = new ArrayList<>();
		tags.add(new TagMatch(CDocumentArchiveMetaData.TIMESTAMP_CREATED, CDocumentArchiveMetaData.TIMESTAMP_CREATED));
		tags.add(new TagMatch(CDocumentArchiveMetaData.TIMESTAMP_TRANSFERRED, CDocumentArchiveMetaData.TIMESTAMP_TRANSFERRED));
                tags.add(new TagMatch(TagMatchDefinitions.MX_DIRECTION, TagMatchDefinitions.Direction.OUTBOUND.toString()));
		tags.add(new TagMatch(TagMatchDefinitions.DOCUMENT_ID, TEST_DOC_ID));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_ID, TagMatchDefinitions.CUSTOMER_ID));
		tags.add(new TagMatch(TagMatchDefinitions.FORM_TYPE_CATEGORY, TagMatchDefinitions.DOCUMENT_TYPE));
                tags.add(new TagMatch(TagMatchDefinitions.DOCUMENT_TYPE, TagMatchDefinitions.DOCUMENT_TYPE)); 
		tags.add(new TagMatch(TagMatchDefinitions.CHANNEL, TagMatchDefinitions.CHANNEL));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_FIRST_NAME, TagMatchDefinitions.CUSTOMER_FIRST_NAME));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_LAST_NAME, TagMatchDefinitions.CUSTOMER_LAST_NAME));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_STREET, TagMatchDefinitions.CUSTOMER_STREET));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_ZIP_CODE, TagMatchDefinitions.CUSTOMER_ZIP_CODE));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_CITY, TagMatchDefinitions.CUSTOMER_CITY));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_COUNTRY, TagMatchDefinitions.CUSTOMER_COUNTRY));
		doc.setTags(tags);
		CDocumentContainer cont=new CDocumentContainer();
                cont.addDocument(doc);
                Map<String, String>  metaMap=instance.collectMetadata(null,cont);
		assertTrue(instance.isMetadataComplete(metaMap, 0));
		assertTrue(metaMap.get(TagMatchDefinitions.DOCUMENT_ID).equals(TEST_DOC_ID));
	}

	@Test
	public void buildMetaDataXML_CDocumentArchiveMetaData() {
		AbstractArchiveMetaData instance = null;
		try {
			instance = ArchiveMetaDataFactory.getInstance("com.nttdata.de.ityx.cx.sky.archiving.CDocumentArchiveMetaData");
		} catch (Exception e) {
			fail(e.getMessage());
		}
		Map<String, String> metaMap = new HashMap<>();
		metaMap.put(CDocumentArchiveMetaData.TIMESTAMP_CREATED, CDocumentArchiveMetaData.TIMESTAMP_CREATED);
		metaMap.put(TagMatchDefinitions.MX_DIRECTION, TagMatchDefinitions.Direction.OUTBOUND.toString());
		metaMap.put(TagMatchDefinitions.DOCUMENT_ID, TEST_DOC_ID);
		metaMap.put(TagMatchDefinitions.CUSTOMER_ID, TagMatchDefinitions.CUSTOMER_ID);
		metaMap.put(TagMatchDefinitions.FORM_TYPE_CATEGORY, TagMatchDefinitions.DOCUMENT_TYPE);
                metaMap.put(TagMatchDefinitions.DOCUMENT_TYPE, TagMatchDefinitions.DOCUMENT_TYPE);
		metaMap.put(TagMatchDefinitions.CHANNEL, TagMatchDefinitions.CHANNEL);
		metaMap.put(TagMatchDefinitions.CUSTOMER_FIRST_NAME, TagMatchDefinitions.CUSTOMER_FIRST_NAME);
		metaMap.put(TagMatchDefinitions.CUSTOMER_LAST_NAME, TagMatchDefinitions.CUSTOMER_LAST_NAME);
		metaMap.put(TagMatchDefinitions.CUSTOMER_STREET, TagMatchDefinitions.CUSTOMER_STREET);
		metaMap.put(TagMatchDefinitions.CUSTOMER_ZIP_CODE, TagMatchDefinitions.CUSTOMER_ZIP_CODE);
		metaMap.put(TagMatchDefinitions.CUSTOMER_CITY, TagMatchDefinitions.CUSTOMER_CITY);
		metaMap.put(TagMatchDefinitions.CUSTOMER_COUNTRY, TagMatchDefinitions.CUSTOMER_COUNTRY);
		//instance.putInfo(metaMap, false);
                Archive archive = instance.buildMetaDataXML(metaMap, false,1);
		assertTrue(archive.getDocumentid().equals(TEST_DOC_ID));
	}

	@Ignore
	@Test
	public void testCollectMetadata_CDocumenMandateArchiveMetaData() throws ClassCastException, NoSuchMethodException {
		AbstractArchiveMetaData instance = null;
		try {
			instance = ArchiveMetaDataFactory.getInstance("CDocumentMandateArchiveMetaData");
		} catch (Exception e) {
			fail(e.getMessage());
		}
		CDocument doc =  StringDocument.getInstance();
		List<TagMatch> tags = new ArrayList<>();
		tags.add(new TagMatch(CDocumentArchiveMetaData.TIMESTAMP_CREATED, CDocumentArchiveMetaData.TIMESTAMP_CREATED));
		tags.add(new TagMatch(TagMatchDefinitions.MX_DIRECTION, TagMatchDefinitions.Direction.OUTBOUND.toString()));
		tags.add(new TagMatch(TagMatchDefinitions.DOCUMENT_ID, TEST_DOC_ID));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_ID, TagMatchDefinitions.CUSTOMER_ID));
		tags.add(new TagMatch(TagMatchDefinitions.FORM_TYPE_CATEGORY, TagMatchDefinitions.DOCUMENT_TYPE));
                tags.add(new TagMatch(TagMatchDefinitions.DOCUMENT_TYPE, TagMatchDefinitions.DOCUMENT_TYPE));
		tags.add(new TagMatch(TagMatchDefinitions.CHANNEL, TagMatchDefinitions.CHANNEL));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_FIRST_NAME, TagMatchDefinitions.CUSTOMER_FIRST_NAME));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_LAST_NAME, TagMatchDefinitions.CUSTOMER_LAST_NAME));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_STREET, TagMatchDefinitions.CUSTOMER_STREET));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_ZIP_CODE, TagMatchDefinitions.CUSTOMER_ZIP_CODE));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_CITY, TagMatchDefinitions.CUSTOMER_CITY));
		tags.add(new TagMatch(TagMatchDefinitions.CUSTOMER_COUNTRY, TagMatchDefinitions.CUSTOMER_COUNTRY));
		tags.add(new TagMatch(TagMatchDefinitions.SEPA_MANDATE_NUMBER, TEST_MANDATE_NUMBER));
		//tags.add(new TagMatch(TagMatchDefinitions.IBAN, TagMatchDefinitions.IBAN));
		tags.add(new TagMatch(TagMatchDefinitions.BIC, TagMatchDefinitions.BIC));
		tags.add(new TagMatch(TagMatchDefinitions.SEPA_SIGNATURE_FLAG, TagMatchDefinitions.SEPA_SIGNATURE_FLAG));
		tags.add(new TagMatch(TagMatchDefinitions.SEPA_SIGNATURE_DATE, TagMatchDefinitions.SEPA_SIGNATURE_DATE));
		doc.setTags(tags);
		Map<String, String> metaMap = new HashMap<>();
                CDocumentContainer<CDocument> cont= new CDocumentContainer<>();
                cont.addDocument(doc);
                metaMap=instance.collectMetadata(null,cont, metaMap);
		assertTrue(instance.isMetadataComplete(metaMap, 0));
		assertTrue(metaMap.get(TagMatchDefinitions.DOCUMENT_ID).equals(TEST_DOC_ID));
		assertTrue(metaMap.get(TagMatchDefinitions.SEPA_MANDATE_NUMBER).equals(TEST_MANDATE_NUMBER));
	}

	@Test
	public void buildMetaDataXML_CDocumenMandateArchiveMetaData() {
		AbstractArchiveMetaData instance = null;
		try {
			instance = ArchiveMetaDataFactory.getInstance("com.nttdata.de.ityx.cx.sky.archiving.CDocumentMandateArchiveMetaData");
		} catch (Exception e) {
			fail(e.getMessage());
		}
		Map<String, String> metaMap = new HashMap<>();
		metaMap.put(CDocumentArchiveMetaData.TIMESTAMP_CREATED, CDocumentArchiveMetaData.TIMESTAMP_CREATED);
		metaMap.put(TagMatchDefinitions.MX_DIRECTION, TagMatchDefinitions.Direction.OUTBOUND.toString());
		metaMap.put(TagMatchDefinitions.DOCUMENT_ID, TEST_DOC_ID);
		metaMap.put(TagMatchDefinitions.CUSTOMER_ID, TagMatchDefinitions.CUSTOMER_ID);
		metaMap.put(TagMatchDefinitions.FORM_TYPE_CATEGORY, TagMatchDefinitions.DOCUMENT_TYPE);
                metaMap.put(TagMatchDefinitions.DOCUMENT_TYPE, TagMatchDefinitions.DOCUMENT_TYPE);
		metaMap.put(TagMatchDefinitions.CHANNEL, TagMatchDefinitions.CHANNEL);
		metaMap.put(TagMatchDefinitions.CUSTOMER_FIRST_NAME, TagMatchDefinitions.CUSTOMER_FIRST_NAME);
		metaMap.put(TagMatchDefinitions.CUSTOMER_LAST_NAME, TagMatchDefinitions.CUSTOMER_LAST_NAME);
		metaMap.put(TagMatchDefinitions.CUSTOMER_STREET, TagMatchDefinitions.CUSTOMER_STREET);
		metaMap.put(TagMatchDefinitions.CUSTOMER_ZIP_CODE, TagMatchDefinitions.CUSTOMER_ZIP_CODE);
		metaMap.put(TagMatchDefinitions.CUSTOMER_CITY, TagMatchDefinitions.CUSTOMER_CITY);
		metaMap.put(TagMatchDefinitions.CUSTOMER_COUNTRY, TagMatchDefinitions.CUSTOMER_COUNTRY);
		metaMap.put(TagMatchDefinitions.SEPA_MANDATE_NUMBER, TEST_MANDATE_NUMBER);
		//metaMap.put(TagMatchDefinitions.IBAN, TagMatchDefinitions.IBAN);
		metaMap.put(TagMatchDefinitions.BIC, TagMatchDefinitions.BIC);
		metaMap.put(TagMatchDefinitions.SEPA_SIGNATURE_FLAG, TagMatchDefinitions.SEPA_SIGNATURE_FLAG);
		metaMap.put(TagMatchDefinitions.SEPA_SIGNATURE_DATE, TagMatchDefinitions.SEPA_SIGNATURE_DATE);
		Archive archive = instance.buildMetaDataXML(metaMap, false,1);
		assertTrue(archive.getDocumentid().equals(TEST_DOC_ID));
		List<Metavalue> metavalueList = archive.getMeta().getMetarecord().get(0).getMetavalue();
		for (Metavalue metavalue : metavalueList) {
			String key = metavalue.getKey();
			if (key.equals(TagMatchDefinitions.SEPA_MANDATE_NUMBER)) {
				assertTrue(metavalue.getContent().equals(TEST_MANDATE_NUMBER));
				break;
			}
		}
	}
}
