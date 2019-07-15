package com.nttdata.de.ityx.cxworkflow.incoming.i6_index;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.nttdata.de.ityx.cx.workflow.incoming.i6_index.Preparation;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;

import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.image.ImageDocument;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.contexdesigner.exflow.states.FlowObject;

@Ignore
public class PreparationTest {

	public static IFlowObject		FLOW_OBJECT;
	CDocumentContainer<CDocument>	con;


	@BeforeClass
	public static void setUp() {
		FLOW_OBJECT = new FlowObject();
		String parameter = "docStateMediatrixWrite";
		String contactParameter = "docStateWaitForContactId";
		String contactSEPAParameter = "docStateWaitForContactId_SEPA";
		String multiParameter = "docStateManualValidation";
		String ignoreMultiFormtypeSEPA = "ignoreMultiFormtypeSEPA";
		FLOW_OBJECT.put(parameter, parameter);
		FLOW_OBJECT.put(contactParameter, contactParameter);
		FLOW_OBJECT.put(contactSEPAParameter, contactSEPAParameter);
		FLOW_OBJECT.put(multiParameter, multiParameter);
		FLOW_OBJECT.put("WFLDTS", "WFLDTS");
		FLOW_OBJECT.put(ignoreMultiFormtypeSEPA, ignoreMultiFormtypeSEPA);
	}

	@Test
	public void test670Prep_waitforContact() throws Exception {
		CDocument doc =  StringDocument.getInstance("test");
		CDocumentContainer<CDocument> con = new CDocumentContainer<>(doc);
		FLOW_OBJECT.put("doc", con);
		TagMatch tm = new TagMatch("Manual Validation");
		tm.add(new TagMatch(TagMatchDefinitions.MANUAL_FORMTYPE, ""));
		tm.add(new TagMatch("CustomerID", "test"));
		tm.add(new TagMatch("ContractNumber", "test"));
		con.setTags(Arrays.asList(tm));

		Preparation instance = new Preparation();
		instance.execute(FLOW_OBJECT);

		Map<String, Object> assertions = new TreeMap<>();
		assertions.put("parameter", "docStateWaitForContactId_WFLDTS");
		checkAssertions(assertions);
	}

	@Before
	public void setUpTest() {
		CDocument doc =  StringDocument.getInstance("test");
		con = new CDocumentContainer<>(doc);
		FLOW_OBJECT.put("doc", con);
		FLOW_OBJECT.put("parameter", "");
		FLOW_OBJECT.put(Preparation.WITH_MANDATE, "");
	}
@Ignore
	@Test
	public void test670Prep_waitforContactSEPACopy_MI() throws Exception {
		CDocument doc = ImageDocument.getInstance("MultiPage.tif");
		doc.getNotes().put("DocumentID", "test");
		doc.setNote(TagMatchDefinitions.EVAL_FORMTYPE_BEFORE_VALIDATION, "ignoreMultiFormtypeSEPA");
		CDocumentContainer<CDocument> con = new CDocumentContainer<>(doc);
		FLOW_OBJECT.put("doc", con);
		TagMatch tm = new TagMatch("Manual Validation");
		tm.add(new TagMatch(TagMatchDefinitions.MANUAL_FORMTYPE, TagMatchDefinitions.SEPA_MANDATE));
		tm.add(new TagMatch("CustomerID", "test"));
		tm.add(new TagMatch("ContractNumber", "test"));
		con.setTags(Arrays.asList(tm));

		Preparation instance = new Preparation();
		instance.execute(FLOW_OBJECT);

		Map<String, Object> assertions = new TreeMap<>();
		assertions.put("parameter", "docStateManualValidation");
		checkAssertions(assertions);
	}

@Ignore	@Test
	public void test670Prep_waitforContactSEPACopy_AutoFormtype() throws Exception {
	CDocument doc = ImageDocument.getInstance("MultiPage.tif");
	doc.getNotes().put("DocumentID", "test");
		CDocumentContainer<CDocument> con = new CDocumentContainer<>(doc);
		FLOW_OBJECT.put("doc", con);
		TagMatch tm = new TagMatch("Manual Validation");
		tm.add(new TagMatch(TagMatchDefinitions.MANUAL_FORMTYPE, TagMatchDefinitions.SEPA_MANDATE));
		tm.add(new TagMatch("CustomerID", "test"));
		tm.add(new TagMatch("ContractNumber", "test"));
		con.setTags(Arrays.asList(tm));

		Preparation instance = new Preparation();
		instance.execute(FLOW_OBJECT);

		Map<String, Object> assertions = new TreeMap<>();
		assertions.put("parameter", "docStateWaitForContactId_WFLDTS");
		checkAssertions(assertions);
	}

	@Test
	public void test670Prep_waitforContactSEPA() throws Exception {
		CDocument doc =  StringDocument.getInstance("test");
		CDocumentContainer<CDocument> con = new CDocumentContainer<>(doc);
		FLOW_OBJECT.put("doc", con);
		TagMatch tm = new TagMatch("Manual Validation");
		tm.add(new TagMatch(TagMatchDefinitions.MANUAL_FORMTYPE, TagMatchDefinitions.SEPA_MANDATE));
		tm.add(new TagMatch("CustomerID", "test"));
		tm.add(new TagMatch("ContractNumber", "test"));
		con.setTags(Arrays.asList(tm));

		Preparation instance = new Preparation();
		instance.execute(FLOW_OBJECT);

		Map<String, Object> assertions = new TreeMap<>();
		assertions.put("parameter", "docStateWaitForContactId_SEPA");
		checkAssertions(assertions);
	}

	public void checkAssertions(Map<String, Object> assertions) {
		for (String key : assertions.keySet()) {
			String val = (String) FLOW_OBJECT.get(key);
			System.out.println(key + " : " + val);
			assertTrue(val.equals(assertions.get(key)));
		}
	}
}
