/**
 *
 */
package com.nttdata.de.ityx.cx.sky.routing;

import com.nttdata.de.ityx.cx.sky.enrichment.SetDocumentMetadata;
import com.nttdata.de.ityx.cx.sky.routing.InternalRoutingEntry.Kundendaten;
import com.nttdata.de.ityx.cx.workflow.base.AbstractWflReportedBean;
import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.ityx.sharedservices.utils.ContexDbConnector;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.ntlangdetect.LangDetectException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.IOException;
import java.util.*;

/**
 * Sets flow parameters in order to choose the appropriate conditons in the
 * flow.
 *
 * @author DHIFLM
 */
public class SetInternalRoutingBean extends AbstractWflReportedBean {

	private static final Long serialVersionUID = -7855448339962583132L;
	private static final String BUNDLE_NAME = "de.ityx.properties.specialRouting";
	public static final String ROUTING_SPEZIELL = "Routing Speziell";
	public static final String CONTEX_INTERNAL_ROUTING = "ContexInternalRouting";
	private static final String PRIORITY = "priority";
	private static final String DE = "de";
	private static final String LANGUAGE = "Language";
	private static final String DO_VALIDATION = "doValidation";
	private static final String DO_EXTRACTION = "doExtraction";
	private static final Boolean TRUE = Boolean.TRUE;
	private static final Boolean FALSE = Boolean.FALSE;

	private final Map<String, List<String>> specialMap = new HashMap<>();
	//	private transient Session				session					= null;
	private transient EntityManager em = null;

	public SetInternalRoutingBean() {
		ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);
		for (String key : bundle.keySet()) {
			String[] keywords = bundle.getString(key).split(",");
			ArrayList<String> keywordList = new ArrayList<>();
			for (String keyword : keywords) {
				if (keyword.trim().length() > 0) {
					keywordList.add(keyword);
				}
			}
			specialMap.put(key, keywordList);
		}
		
		final String server;
		try {
			server = BeanConfig.getReqString(ContexDbConnector.DBHOST);
			final String port = BeanConfig.getString(ContexDbConnector.DBPORT, "1525");
			final String database = BeanConfig.getReqString(ContexDbConnector.DBNAME);
			final String user = BeanConfig.getReqString(ContexDbConnector.DBUSER);
			final String password = BeanConfig.getReqString(ContexDbConnector.DBPASSWD);
			Properties p = new Properties();
			p.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
			p.setProperty("hibernate.connection.driver_class", "oracle.jdbc.driver.OracleDriver");
			p.setProperty("hibernate.connection.url", "jdbc:oracle:thin:@" + server + ":" + port + ":" + database);
			p.setProperty("hibernate.connection.username", user);
			p.setProperty("hibernate.connection.password", password);
			SkyLogger.getItyxLogger().debug(" : Creating factory");
			EntityManagerFactory pf = Persistence.createEntityManagerFactory("ityxPersistenceUnit", p);
			em = pf.createEntityManager();
		} catch (Exception e) {
			SkyLogger.getItyxLogger().error( "Cannot build Persistence: EntityManagerFactory:"+e.getMessage());

		}
	}

	
	public void execute(IFlowObject flow) throws LangDetectException, IOException {
		String logPrefix = getClass().getName() + "#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ":" + DocContainerUtils.getDocID(flow) + " ";
		SkyLogger.getItyxLogger().debug(logPrefix + ": enter");

		Boolean doExtraction = TRUE;
		Boolean doValidation = TRUE;
		//CDocumentContainer doc = DocContainerUtils.getDocContainer(flow);
		final CDocument document = DocContainerUtils.getDoc(flow);
		// String language = "";
		// try {
		// final String content = document.getContentAsString();
		// LanguageInfo languageInfo = LanguageDetection
		// .determineLanguage(content);
		// language = languageInfo != null ? languageInfo.getISOLanguage()
		// : DE;
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// document.setNote(LANGUAGE, language);
		// if (language.equals(DE)) {
		//
		// SkyLogger.getItyxLogger().debug(logPrefix + ": DE");

		String channel = (String) document.getNote(TagMatchDefinitions.CHANNEL);
		String receive = null;
		String subject = "test";
		if (document.getClass().equals(EmailDocument.class)) {
			final EmailDocument edoc = (EmailDocument) document;
			receive = edoc.getTo();
			subject = edoc.getSubject();
		} else if (channel.equals(SetDocumentMetadata.FAX)) {
			receive = (String) document.getNote(SetDocumentMetadata.RECEIVING_FAX);
		} else {
			receive = "test";
		}

		String formtype = DocContainerUtils.getFormtype(document);

		// Damit sollte formtype einheitlich und richtig gesetzt sein
		DocContainerUtils.setFormtype(DocContainerUtils.getDocContainer(flow), document, formtype);

		List<InternalRoutingEntry> list = null;
		synchronized (serialVersionUID) {
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<InternalRoutingEntry> query = criteriaBuilder.createQuery(InternalRoutingEntry.class);
			query.orderBy(criteriaBuilder.asc(query.from(InternalRoutingEntry.class).get(PRIORITY)));
			list = em.createQuery(query).getResultList();
			
			SkyLogger.getItyxLogger().info(logPrefix + ": rules loaded: to:" + receive + " s:" + subject + " f:" + formtype);
			for (InternalRoutingEntry entry : list) {
				// First matching rule found?
				if (isMatch(document, formtype, receive, subject, entry)) {
					SkyLogger.getItyxLogger().info(logPrefix + ": internal rule found:" + entry.getPriority() + ":" + entry.getCustomerData().toString() + "," + entry.getAddress() + "," + entry.getCategory());
					Kundendaten data = entry.getCustomerData();
					switch (data) {
						case NONE:
							doExtraction = FALSE;
							doValidation = FALSE;
							break;
						case EXTRACT:
							doValidation = FALSE;
							break;
					}
					document.setNote(CONTEX_INTERNAL_ROUTING, entry.getPriority() + ":" + entry.getCustomerData().toString() + "," + entry.getAddress() + "," + entry.getCategory());

					// Leaves the loop.
					break;
				}
			}
		}
		flow.put(DO_EXTRACTION, doExtraction);
		flow.put(DO_VALIDATION, doValidation);
		document.setNote("doValidation", doValidation != null ? doValidation : new Boolean(true));

		SkyLogger.getItyxLogger().debug(logPrefix + ": exit");
	}


	protected boolean isMatch(CDocument document, String formtype, String receive, String mysubject, InternalRoutingEntry entry) {
		String address = entry.getAddress();
		String category = entry.getCategory();
		String subject = entry.getSubject();
		if (address.equals(ROUTING_SPEZIELL)) {
			SkyLogger.getItyxLogger().debug(ROUTING_SPEZIELL);
			receive = ROUTING_SPEZIELL;
			formtype = specialFormtype(document, category.replaceAll(" ", "_"));
			if (formtype != null) {
				document.setNote(ROUTING_SPEZIELL, formtype);
			}
		}
		return (address.equals("*") || (receive != null && receive.toLowerCase().contains(address.toLowerCase()))) && (category.equals("*") || category.equals(formtype)) && ((subject.equals("*") || subject.equals(mysubject)));
	}

	protected String specialFormtype(CDocument document, String category) {
		String content = document.getContentAsString();
		for (String keyword : specialMap.get(category)) {
			if (content.contains(keyword)) {
				SkyLogger.getItyxLogger().debug(ROUTING_SPEZIELL + " : " + category + " match found: " + keyword);
				return category;
			}
		}
		return null;
	}

	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{new KeyConfiguration(DO_EXTRACTION, Boolean.class), new KeyConfiguration(DO_VALIDATION, Boolean.class)};
	}
}
