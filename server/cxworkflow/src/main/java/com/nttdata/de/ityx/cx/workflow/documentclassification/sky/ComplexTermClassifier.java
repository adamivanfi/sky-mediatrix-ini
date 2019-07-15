package com.nttdata.de.ityx.cx.workflow.documentclassification.sky;

import com.nttdata.de.ityx.cx.workflow.documentclassification.AbstractComplexTermClassifier;
import com.nttdata.de.ityx.cx.workflow.utils.WorkflowTextExtractionUtils;
import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author meinusch
 */
public class ComplexTermClassifier extends AbstractComplexTermClassifier {

	private static final Pattern PAuftragsbestaetigungAT_1 = Pattern.compile("IHR SKY ANGEBOT");
	private static final Pattern PAuftragsbestaetigungAT_2 = Pattern.compile("zur Freischaltung der ORF[\\-_]Programm[, .]?");
	public static final String SENDER_TERM = "CANCELLATION_SENDER_TERM";
	public static final String RECEIVER_TERM = "CANCELLATION_RECEIVER_TERM";
	public static final String SUBJECT_TERM = "CANCELLATION_SUBJECT_TERM";
	public static final String BODY_TERM = "CANCELLATION_BODY_TERM";

	// kuendigung_automatisch
	public static final String sender_term = BeanConfig.getString(SENDER_TERM, "noreply@sky.((de)|(at))");
	private static final Pattern Psender_term=Pattern.compile(sender_term);

	public static final String receiver_term = BeanConfig.getString(RECEIVER_TERM, "service@sky.de[\\s,]{0,5}customer_web@sky.de");
	private static final Pattern Preceiver_term=Pattern.compile(receiver_term);

	public static final String subject_term = BeanConfig.getString(SUBJECT_TERM, "Kündigung über Sky Website");
	private Pattern Psubject_term=Pattern.compile(subject_term);
	
	
	@Override
	public boolean classify(IFlowObject flowObject) {
		
		String ityx_environment_type = System.getProperty("ityx_environment_type");
		if (ityx_environment_type != null && ityx_environment_type.equalsIgnoreCase("integration")) {
			Psubject_term=Pattern.compile("INT \\- "+subject_term);
		}
		
		CDocumentContainer cont = DocContainerUtils.getDocContainer(flowObject);
		CDocument doc = DocContainerUtils.getDoc(cont);
		String docid = DocContainerUtils.getDocID(doc);
		// Email
		if (doc != null && doc.getClass().equals(EmailDocument.class)) {
			EmailDocument edoc = ((EmailDocument) doc);
			String header = edoc.getHeaders();
			String subject = edoc.getSubject();

			//SPAM
			if ((header != null && header.contains("X-SKYDE-EOP: This message appears to be spam.")) || (subject != null && subject.contains("SKYDE EOP: MOST LIKELY SPAM"))) {
				doc.setTitle("SPAM:" + doc.getTitle());
				DocContainerUtils.setFormtype(flowObject, cont, doc, "spam");
				doc.setNote(TagMatchDefinitions.EVAL_FORMTYPE, "spam");
				flowObject.put("spam", "true");
				SkyLogger.getWflLogger().debug("300:ComplexTermClassifier:" + DocContainerUtils.getDocID(doc) + " spam:ok");
				return true;
			}

			//Kündigung_automatisch
			boolean isFrom =  WorkflowTextExtractionUtils.textMatchPattern(edoc.getFrom(), Psender_term);
			boolean isTo = WorkflowTextExtractionUtils.textMatchPattern(edoc.getTo(), Preceiver_term);
			boolean isSubject = WorkflowTextExtractionUtils.textMatchPattern(subject, Psubject_term);

			//INCTASK0025221 - E-mails kommen nicht mehr von noreply, deshalb wurde isFrom von der Klausel rausgenommen.
			if (isTo && isSubject) {
				// Perf-Opt
				String body_term = BeanConfig.getString(BODY_TERM, "Kündigungsgrund:");
				boolean isBody = edoc.getBody() != null && edoc.getBody().contains(body_term);
				if (isBody) {
					DocContainerUtils.setFormtype(flowObject, cont, doc, "kuendigung_automatisch");
					doc.setNote(TagMatchDefinitions.EVAL_FORMTYPE, "kuendigung_automatisch");
					SkyLogger.getWflLogger().info("300:ComplexTermClassifier:kuendigung_automatisch:OK:" + docid);
					flowObject.put("kuendigung_automatisch", "true");
					return true;
					//doc.setTitle("kuendigung_automatisch:" + doc.getTitle());
				} else {
					SkyLogger.getWflLogger().info("300:ComplexTermClassifier:kuendigung_automatisch:NOK:" + docid + " from:" + isFrom + " to:" + isTo + " isSubject:" + isSubject + " isBody:" + isBody);
				}

			} else {
				if (!isTo){
					SkyLogger.getWflLogger().info("300:ComplexTermClassifier:kuendigung_automatisch:" + docid + " Rterm:"+receiver_term+" to>"+edoc.getTo()+"<");
				}
				if (!isSubject){
					SkyLogger.getWflLogger().info("300:ComplexTermClassifier:kuendigung_automatisch:" + docid + " isSubject:"+subject_term+" s>"+subject+"<");
				}
				
				SkyLogger.getWflLogger().info("300:ComplexTermClassifier:kuendigung_automatisch:NOK:" + docid + " from:" + isFrom + " to:" + isTo + " isSubject:" + isSubject);
				SkyLogger.getWflLogger().debug("300:ComplexTermClassifier:kuendigung_automatisch:NOK:" + docid + " from:" + edoc.getFrom() + " to:" + edoc.getTo() + " Subject:" + subject);
			}
		}else{
			if ("vosbelege_mks".equals(DocContainerUtils.getFormtype(cont)) || "vosbelege_sp".equals(DocContainerUtils.getFormtype(cont))){
				flowObject.put("kuendigung_automatisch", "false");
				if (doc != null)
					doc.setNote(TagMatchDefinitions.EVAL_FORMTYPE, DocContainerUtils.getFormtype(cont));
				return true;
			}

		}
		flowObject.put("kuendigung_automatisch", "false");

		/*
		 *  Put into comment based on Sky-Incident 273105 by IVANFA.
		 *
		 *
		//Emails and WhitePaper
		//Auftragsbestaetigung_AT
		Set<Integer> p1m = WorkflowTextExtractionUtils.getPagesMatchingTextPattern(doc, PAuftragsbestaetigungAT_1);
		if (p1m != null && !p1m.isEmpty() && p1m.size() > 0) {
			SkyLogger.getWflLogger().debug("300:ComplexTermClassifier: " + DocContainerUtils.getDocID(doc) + " AuftragsbestaetigungAT:pattern1check:ok");
			Set<Integer> p2m = WorkflowTextExtractionUtils.getPagesMatchingTextPattern(doc, PAuftragsbestaetigungAT_2);
			if (p2m != null && !p2m.isEmpty() && p2m.size() > 0) {
				SkyLogger.getWflLogger().debug("300:ComplexTermClassifier: " + DocContainerUtils.getDocID(doc) + " AuftragsbestaetigungAT:pattern2check:size:" + p2m.size());
				for (int i : p2m) {
					SkyLogger.getWflLogger().debug("300:ComplexTermClassifier: " + DocContainerUtils.getDocID(doc) + " AuftragsbestaetigungAT:pattern2check:site:" + i);
					for (int j : p1m) {
						if (i == j) {
							SkyLogger.getWflLogger().debug("300:ComplexTermClassifier: " + DocContainerUtils.getDocID(doc) + " AuftragsbestaetigungAT:pattern2check:confirmed:site:" + i);
							DocContainerUtils.setFormtype(flowObject, cont, doc, "vertragsbestaetigung_at");
							return true;
						}
					}
				}
			}
		}
		*/
		return false;
	}
}
