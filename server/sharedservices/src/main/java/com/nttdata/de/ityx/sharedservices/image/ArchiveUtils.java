package com.nttdata.de.ityx.sharedservices.image;

import com.nttdata.de.ityx.sharedservices.configuration.BeanConfig;
import com.nttdata.de.ityx.sharedservices.utils.NttFileUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.base.Global;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.document.text.email.EmailDocument;
import de.ityx.contex.impl.document.text.string.StringDocument;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.customer.commons.pdf.impl.CreatePDFImpl;
import de.ityx.customer.commons.pdf.model.ByteAttachment;
import de.ityx.customer.commons.pdf.model.EmailContainer;
import de.ityx.mediatrix.data.Answer;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.mozilla.universalchardet.UniversalDetector;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by meinusch on 20.07.15.
 */
public class ArchiveUtils {
	
	//	private static final String			DIRECTION_PARAMETER		= "Direction";
	protected static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	public static final String ARCHIVE_BASE_DIR = "MoveFileToArchive_BaseDir";
	
	enum ArchivePrepStatus {SRC, TMP, DST}
	
	public static String getBaseArchPath() {
		return BeanConfig.getString(ARCHIVE_BASE_DIR, Global.getProperty("sky.archive.base", TMP_DIR));
	}
	
	public static String getArchFileName(Map<String, String> metaMap, Integer mcounter, ArchivePrepStatus status, String extension) throws IOException {
		String direction = metaMap.get(TagMatchDefinitions.MX_DIRECTION);
		String path = getBaseArchPath() + File.separator + direction + File.separator + status.toString() + File.separator + getArchFileNamePlain( metaMap,  mcounter,  status,  extension);
		NttFileUtils.createDirIfNotExists(path);
		return path;
	}
	
	public static String getArchFileNamePlain(Map<String, String> metaMap, Integer mcounter, ArchivePrepStatus status, String extension) throws IOException {
		String documentid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
		String counter = "" + String.format("%03d", mcounter + 1);
		String path=documentid + "-" + counter + "." + (extension != null ? extension : "");
		SkyLogger.getWflLogger().debug("getFile:" + path);
		return path;
	}
	
	public static List<File> createArchivingFiles(Map<String, String> metaMap, Email email) throws Exception {
		Integer mcounter = 0;
		List<File> outputfiles = new LinkedList<>();
		boolean whitePaper = false;
		
		String documentid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
		String direction = metaMap.get(TagMatchDefinitions.MX_DIRECTION);
		String logPreafix = "createArchivingFile:" + direction + ":" + documentid + ":";
		
		List<ArchivingAttachment> aat = new LinkedList<>();
		for (Attachment attachment : email.getAttachments()) {
			SkyLogger.getWflLogger().info(logPreafix + " attachment:" + attachment.getFilename());
			aat.add(new ArchivingAttachment(attachment.getBuffer(), attachment.getFilename(), attachment.getContentType()));
		}
		
		Map<Integer, String> txtbody = new TreeMap<>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
		});
		Map<String, String> header = new LinkedHashMap<>();
		SkyLogger.getWflLogger().info(logPreafix + " Extract EmailHeader");
		header = extractHeaderLines(email, header);
		
		if (email.getDocumentContainer() != null) {
			CDocumentContainer cont = email.getDocumentContainer();
			for (Object cdoc : cont.getDocuments()) {
				if (cdoc == null) {
					SkyLogger.getWflLogger().info(logPreafix + mcounter + " processing InboundEmail");
					continue;
				}
				if (cdoc instanceof CDocument) {
					CDocument doc = (CDocument) cdoc;
					/*if (doc.getPages() != null && doc.getPages().length > 0) {
						for (CPage page : doc.getPages()) {
							if (page instanceof OCRPage)
								a.setOcrText();
						}
					} else */
					if ( doc.getImages() != null) {
						for (BufferedImage img : doc.getImages()) {
							// File ofile=new File(exportPrefix + "_io" + format.format(count) + ".tif");
							// ImageIO.write(img, "TIFF", ofile);
							try {
								String srcname = getArchFileName(metaMap, outputfiles.size(), ArchivePrepStatus.SRC, "tif");
								SkyLogger.getWflLogger().info(logPreafix + " imgAtt:start " + " " + srcname + ":" + mcounter);
								File srcFile = NttFileUtils.writeDownAsTiff(img, srcname);
								List<File> res = createArchImg(metaMap, srcFile, new LinkedList<File>());
								
								for (File f : res) {
									byte[] imgbytes = FileUtils.readFileToByteArray(f);
									ArchivingAttachment a = new ArchivingAttachment(imgbytes, f.getName());
									a.setContentAtt(true);
									aat.add(a);
									whitePaper = true;
									SkyLogger.getWflLogger().info(logPreafix + " imgAtt: Document F " + mcounter + " "+f.getName());
								}
								
							} catch (Exception e) {
								SkyLogger.getWflLogger().warn(logPreafix + " imgAtt: Document E " + mcounter + " cannot be archived. " + e.getMessage(), e);
							}
						}
					}
				}
				if (cdoc instanceof EmailDocument) {
					EmailDocument doc = (EmailDocument) cdoc;
					
						if (doc.getBody() != null && !doc.getBody().isEmpty()) {
							txtbody.put(doc.getBody().length(), doc.getBody());
						}
						if (doc.getAlternativeBody() != null && !doc.getAlternativeBody().isEmpty()) {
							txtbody.put(doc.getBody().length(), doc.getAlternativeBody());
						}
						header = extractHeaderLines(doc, header);
						
						
					
					if ( cont.getDocument(0) != null && cont.getDocument(0).getPayload() != null) {
						try {
							InputStream src = new ByteArrayInputStream(cont.getDocument(0).getPayload());
							Session mailSession = Session.getDefaultInstance(System.getProperties(), null);
							MimeMessage message = new MimeMessage(mailSession, src);
							txtbody.putAll(getMsgBody(message, logPreafix + mcounter));
							header = extractHeaderLines(message, header);
							aat = getAttFromMsgPart(message, aat, logPreafix + mcounter);
							
						} catch (Exception e) {
							SkyLogger.getWflLogger().warn(logPreafix + " mail: Document E " + mcounter + " cannot be archived. " + e.getMessage(), e);
							String destFileS = getArchFileName(metaMap, outputfiles.size(), ArchivePrepStatus.DST, "msg");
							File msgFile = NttFileUtils.writeFile(cont.getDocument(0).getPayload(), destFileS);
							outputfiles.add(msgFile);
						}
					}
					
				} else if (cdoc instanceof StringDocument) {
					String ct = ((StringDocument) cdoc).getContentAsString();
					if (ct != null && !ct.isEmpty()) {
						txtbody.put(ct.length(), ct);
					}
				}
			}
		}
		if (txtbody.isEmpty() && !whitePaper) {
			SkyLogger.getWflLogger().info(logPreafix + " Extract EmailBody");
			if (email.getBody() != null && !email.getBody().isEmpty()) {
				txtbody.put(email.getBody().length(), email.getBody());
			}
		}
		
		PdfUtils pdfu =  PdfUtils.getInstance();
		String title = " E:" + email.getEmailId() + " DocId:" + documentid;
		if (email instanceof Answer) {
			title = "Sky DMS MX: A:" + ((Answer) email).getId() + title;
		} else if (email instanceof Question) {
			title = "Sky DMS MX: Q:" + ((Question) email).getId() + title;
		}
		//Alternative Wege um PDF für den Text zu generieren
		//List<File> res = createArchImg(metaMap, txtbody.getBytes(java.nio.charset.Charset.forName("UTF-8")), srcname, count);
		//List<File> res = createArchImg(metaMap, cont.getDocument(0).getPayload(), srcname, count);
		//List<File> res = createArchImg(metaMap, body.getBytes(System.getProperty("aspose.charset.encoding", "UTF-8")), srcname, count);
		outputfiles.addAll(pdfu.generatePdfForEmail(metaMap, outputfiles, title, header, txtbody, aat));
		return outputfiles;
	}
	
	private static void addOrReplaceHeader(String hkey, String hvalue, Map<String, String> header) {
		if (!isEmpty(hvalue)) {
			if (hvalue!=null && !hvalue.isEmpty() && hvalue.startsWith("=?")){
				try {
					hvalue=MimeUtility.decodeText(hvalue);
				} catch (UnsupportedEncodingException e) {
					//use org value
				}
			}
			header.put(hkey, hvalue);
		}
	}
	
	private static Map<String, String> extractHeaderLines(Email email, Map<String, String> header) {
		String fdatum;
		if (Answer.class.isAssignableFrom(email.getClass())) {
			Answer answer = (Answer) email;
			long lastActivityTime = answer.getSendTime();
			if (lastActivityTime < 1) {
				lastActivityTime = answer.getEmailDate();
			}
			fdatum = (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")).format(lastActivityTime);
		} else {
			fdatum = email.getReceived();
			if ((fdatum == null || fdatum.isEmpty()) && email.getEmailDate() > 1) {
				fdatum = (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")).format(email.getEmailDate());
			}
		}
		if (fdatum != null && !fdatum.startsWith("01.01.1970")) {
			addOrReplaceHeader("Date", fdatum, header);
		}
		addOrReplaceHeader("From", email.getFrom(), header);
		addOrReplaceHeader("To", email.getTo(), header);
		addOrReplaceHeader("CC", email.getCC(), header);
		addOrReplaceHeader("Subject", email.getSubject(), header);
		return header;
	}
	
	
	private static Map<String, String> extractHeaderLines(EmailDocument doc, Map<String, String> header) {
		addOrReplaceHeader("Date", doc.getEmailDate(), header);
		addOrReplaceHeader("From", doc.getFrom(), header);
		addOrReplaceHeader("To", doc.getTo(), header);
		addOrReplaceHeader("CC", doc.getCC(), header);
		addOrReplaceHeader("Subject", doc.getSubject(), header);
		return header;
	}
	
	private static Map<String, String> extractHeaderLines(MimeMessage message, Map<String, String> header) throws MessagingException {
		if (message!=null && message.getAllHeaders()!=null) {
			Enumeration<Header> headers = message.getAllHeaders();
			Map<String, String> headersM = new HashMap<>();
			while (headers.hasMoreElements()) {
				Header line = headers.nextElement();
				headersM.put(line.getName().toUpperCase(), line.getValue());
			}
			addOrReplaceHeader("Date", headersM.get("DATE"), header);
			addOrReplaceHeader("From", headersM.get("FROM"), header);
			addOrReplaceHeader("To", headersM.get("TO"), header);
			addOrReplaceHeader("CC", headersM.get("CC"), header);
			if (header.get("Subject") == null || header.get("Subject").isEmpty()) {
				//sonnst ggf. Codierungsproblem von Umlauten in Subject
				addOrReplaceHeader("Subject", headersM.get("SUBJECT"), header);
			}
			addOrReplaceHeader("Reply-To", headersM.get("REPLY-TO"), header);
			addOrReplaceHeader("Sender", headersM.get("SENDER"), header);
			addOrReplaceHeader("Receiver", headersM.get("RECEIVER"), header);
		}
		return header;
	}
	
	
	private static List<ArchivingAttachment> getAttFromMsgPart(Object message, List<ArchivingAttachment> aat, String logpreafix) throws MessagingException, IOException {
		if (message instanceof  MimeMessage){
			SkyLogger.getWflLogger().info(logpreafix + " Scanning message");
			return getAttFromMsgPart(((MimeMessage) message).getContent(),aat,logpreafix+"M");
		}else if (message instanceof Multipart) {
			Multipart mcontent = (Multipart) message;
			SkyLogger.getWflLogger().info(logpreafix + " Scanning multipart");
			
			for (int i = 0; i < mcontent.getCount(); i++) {
				try {
					Part spart = mcontent.getBodyPart(i);
					String cfn = spart.getFileName();
					if (cfn!=null && !cfn.isEmpty() && cfn.startsWith("=?")){
						cfn=MimeUtility.decodeText(cfn);
					}
					String cfct = spart.getContentType();
					if (cfn != null && !cfn.isEmpty()) {
						boolean attachmentfound = false;
						for (ArchivingAttachment at : aat) {
							if (at.getFileName().equalsIgnoreCase(cfn)) {
								attachmentfound = true;
								break;
							}
						}
						if (attachmentfound) {
							SkyLogger.getWflLogger().info(logpreafix + " msg attachment:" + cfn + " already recognized. Skipping");
						} else {
							SkyLogger.getWflLogger().info(logpreafix + " add msg attachment:" + cfn);
							InputStream in = spart.getInputStream();
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							IOUtils.copy(in, out);
							IOUtils.closeQuietly(in);
							IOUtils.closeQuietly(out);
							byte[] bytes = out.toByteArray();
							aat.add(new ArchivingAttachment(bytes, cfn, cfct));
						}
					} else {
						aat = getAttFromMsgPart(spart, aat, logpreafix + "l" + i + ":");
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					SkyLogger.getWflLogger().warn(logpreafix + " problems with MPartCounter :" + i);
				}
			}
		} else if (message instanceof Part) {
			SkyLogger.getWflLogger().info(logpreafix + " Scanning part");
			Part content = (Part) message;
			SkyLogger.getWflLogger().debug(logpreafix + " :" + content.getFileName() + " :" + content.getContentType());
			String cfn = content.getFileName();
			if (cfn!=null && !cfn.isEmpty() && cfn.startsWith("=?")){
				cfn=MimeUtility.decodeText(cfn);
			}
			String cfct = content.getContentType();
			if (cfn != null && !cfn.isEmpty()) {
				boolean attachmentfound = false;
				for (ArchivingAttachment at : aat) {
					if (at.getFileName().equalsIgnoreCase(cfn)) {
						attachmentfound = true;
						break;
					}
				}
				if (attachmentfound) {
					SkyLogger.getWflLogger().info(logpreafix + " msg attachment:" + cfn + " already recognized. Skipping");
				} else {
					SkyLogger.getWflLogger().info(logpreafix + " add msg attachment:" + cfn);
					InputStream in = content.getInputStream();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					IOUtils.copy(in, out);
					IOUtils.closeQuietly(in);
					IOUtils.closeQuietly(out);
					byte[] bytes = out.toByteArray();
					aat.add(new ArchivingAttachment(bytes, cfn, cfct));
				}
			} else {
				aat = getAttFromMsgPart(content.getContent(), aat, logpreafix);
			}
		} else {
			SkyLogger.getWflLogger().debug(logpreafix + " Skip part during attachment scanning fn:" + message.getClass().getCanonicalName());
		} return aat;
	}
	
	private static Map<Integer, String> getMsgBody(MimeMessage message, String logpreafix) throws MessagingException, IOException {
		Map<Integer, String> result = new TreeMap<>();
		if (message.getContent() instanceof Multipart) {
			Multipart mcontent = (Multipart) message.getContent();
			for (int i = 0; i < mcontent.getCount(); i++) {
				SkyLogger.getWflLogger().info(logpreafix + "extractTxtFromMsg: processing Multipart:" + i + " ct:" + mcontent.getBodyPart(i).getContentType() + " mt:" + mcontent.getBodyPart(i).getFileName() + " class:" + mcontent.getBodyPart(i).getContent().getClass());
				try {
					Part spart = mcontent.getBodyPart(i);
					if (spart.getFileName()==null || spart.getFileName().isEmpty()) {
						Map<Integer, String> sbody = getBody(spart, logpreafix + "l" + i + ":");
						if (sbody!=null) {
							result.putAll(sbody);
						}
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					SkyLogger.getWflLogger().warn(logpreafix + " problems with MPartCounter :" + i);
				}
			}
		} else if (message.getContent() instanceof Part) {
			Part content = (Part) message.getContent();
			if (content.getFileName()==null || content.getFileName().isEmpty()) {
				result.putAll(getBody(content, logpreafix));
			}
		} else {
			SkyLogger.getWflLogger().debug(logpreafix + " TXT:" + message.getContent());
			String ct = (String) message.getContent();
			if (ct != null && !ct.isEmpty()) {
				result.put(ct.length(), ct);
			}
		}
		return result;
	}
	
	private static Map<Integer, String> getBody(Part content, String logpreafix) throws MessagingException, IOException {
		Map<Integer, String> result = new TreeMap<>();
		if (content == null || content.getSize() < 1) {
			return null;
		} else if (content.isMimeType("multipart/*")) {
			SkyLogger.getWflLogger().info(logpreafix + "extractTxtFromMsg");
			if (content instanceof MimeBodyPart) {
				SkyLogger.getWflLogger().info(logpreafix + "extractTxtFromMsg");
				MimeBodyPart mcontent = (MimeBodyPart) content;
				if (mcontent.getContent() instanceof Part && (mcontent.getFileName()==null || mcontent.getFileName().isEmpty())) {
					result.putAll(getBody((Part) mcontent.getContent(), logpreafix + "bp:"));
				}
			} else {
				Multipart mcontent = (Multipart) content;
				for (int i = 0; i < mcontent.getCount(); i++) {
					SkyLogger.getWflLogger().info(logpreafix + "extractTxtFromMsg: processing Multipart:" + i + " ct:" + mcontent.getBodyPart(i).getContentType() + " class:" + mcontent.getBodyPart(i).getContent().getClass());
					try {
						Part spart = mcontent.getBodyPart(i);
						if (spart.getFileName()==null || spart.getFileName().isEmpty()) {
							result.putAll(getBody(spart, logpreafix + "l" + i + ":"));
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						SkyLogger.getWflLogger().warn(logpreafix + " problems with MPartCounter :" + i);
					}
				}
			}
		} else if (content.isMimeType("text/*")) {
			SkyLogger.getWflLogger().info(logpreafix + " found " + (content.isMimeType("text/html") ? "html" : "txt"));
			String ct = (String) content.getContent();
			if (ct != null && !ct.isEmpty()) {
				result.put(ct.length(), ct);
			}
		}
		return result;
	}
	
	private static boolean isEmpty(String value) {
		return value == null || value.isEmpty() || value.equalsIgnoreCase("noreply@sky.de");
	}

	/*protected static List<File> createArchImg(Map<String, String> metaMap, byte[] bytes, String srcFile, List<File> outputFiles) throws Exception {
		return createArchImg(metaMap, bytes, new File(srcFile), outputFiles);
	}*/
	
	protected static List<File> createArchImg(Map<String, String> metaMap,  File srcFile, List<File> outputFiles) throws Exception {
		String docid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
		String destFileS = getArchFileName(metaMap, outputFiles.size(), ArchivePrepStatus.TMP, null);
		
		String logPrefix = "TiffUtils#" + new Object() {
		}.getClass().getEnclosingMethod().getName() + ": " + docid + ":src:" + srcFile.getAbsolutePath() + ":dst:" + destFileS;
		byte[] bytes = org.apache.commons.io.FileUtils.readFileToByteArray(srcFile);
			//bytes= Files.readAllBytes(srcFile.getAbsolutePath());
		String srcE = NttFileUtils.getFileExtension(srcFile);
		
		if ("tif".contains(srcE)) {
			SkyLogger.getWflLogger().info(logPrefix + " finish:tiff");
			File dstF = NttFileUtils.writeFile(bytes, destFileS + "tif");
			outputFiles.add(dstF);
			return outputFiles;
		} else if ("jpeg".contains(srcE) || "jpg".contains(srcE) || "png".contains(srcE)) {
			try {
				SkyLogger.getWflLogger().info(logPrefix + " start:img:" + srcE);
				File dstF = NttFileUtils.writeDownAsTiff(srcFile, destFileS);
				SkyLogger.getWflLogger().info(logPrefix + " finish:img:" + srcE);
				outputFiles.add(dstF);
				return outputFiles;
			} catch (IOException ei) {
				SkyLogger.getWflLogger().info(logPrefix + " error:img:" + srcE + " unable to convert IMG" + ei.getMessage(), ei);
			}
		} else if (srcE.contains("txt") || srcE.contains("html")) {
			try {
				SkyLogger.getWflLogger().info(logPrefix + " start:" + srcE);
				PdfUtils pdfu =  PdfUtils.getInstance();
				List<ArchivingAttachment> aat = new LinkedList<>();
				//aat.add(new ArchivingAttachment(bytes, srcFile.getName()));
				// count zurück da bei den vorherrigen versuchen nichts geschrieben
				
				String message = "";
				String encoding = null;
				
				try {
					java.io.FileInputStream fis = new java.io.FileInputStream(srcFile);
					
					UniversalDetector detector = new UniversalDetector(null);
					
					int nread;
					byte[] buf = new byte[4096];
					while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
						detector.handleData(buf, 0, nread);
					}
					fis.close();
					detector.dataEnd();
					encoding = detector.getDetectedCharset();
				} catch (Exception ee) {
					SkyLogger.getWflLogger().info(logPrefix + " detector:problem:" + srcE + ":" + ee.getMessage(), ee);
				}
				
				if (encoding != null) {
					message = org.apache.commons.io.FileUtils.readFileToString(srcFile, encoding);
				}
				if (message == null || message.isEmpty()) {
					message = "";
					for (String msg : Files.readAllLines(Paths.get(srcFile.getAbsolutePath()), Charset.defaultCharset())) {
						message += msg + System.lineSeparator();
					}
				}
				
				Map<Integer, String> messages = new TreeMap<>();
				messages.put(message.length(), message);
				SkyLogger.getWflLogger().info(logPrefix + " interim:" + srcE);
				outputFiles = pdfu.generatePdfForEmail(metaMap, outputFiles, "DMS/ITyX:" + docid, new HashMap<String, String>(), messages, aat);
				SkyLogger.getWflLogger().info(logPrefix + " finished:" + srcE);
				return outputFiles;
			} catch (IOException ei) {
				SkyLogger.getWflLogger().info(logPrefix + " error:" + srcE + " unable to convert to pdf" + ei.getMessage(), ei);
			}
		}
		try {
			SkyLogger.getWflLogger().info(logPrefix + " start:others");
			PdfUtils pdfu =  PdfUtils.getInstance();
			List<ArchivingAttachment> aat = new LinkedList<>();
			aat.add(new ArchivingAttachment(bytes, srcFile.getName()));
			// count zurück da bei den vorherrigen versuchen nicht geschrieben
			outputFiles = pdfu.generatePdfForAtt(metaMap, outputFiles, aat);
			SkyLogger.getWflLogger().info(logPrefix + " finished:others");
			
			return outputFiles;
			
		} catch (Exception e) {
			SkyLogger.getWflLogger().info(logPrefix + " error:pdf unable to direct PDF convert " + e.getMessage(), e);
		}
		
		SkyLogger.getWflLogger().info(logPrefix + " start:toPdf");
		ByteAttachment e = new ByteAttachment(bytes, srcFile.getName(), java.nio.file.Files.probeContentType(Paths.get(srcFile.getAbsolutePath())));
		List<ByteAttachment> attachments = new LinkedList<>();
		attachments.add(e);
		EmailContainer ec = new EmailContainer(null, attachments);
		CreatePDFImpl pdfmaschine = CreatePDFImpl.INSTANCE;
		File dstPdf = pdfmaschine.createPDF(destFileS + "pdf", new EmailContainer[]{ec});
		SkyLogger.getWflLogger().info(logPrefix + " finished:toPdf");
		outputFiles.add(dstPdf);
		return outputFiles;
	}
	
	public static boolean isSBS(Map<String, String> docMeta, Email email) {
		boolean isSbsProjectCX = "sbs".equalsIgnoreCase(docMeta.get(TagMatchDefinitions.MX_MASTER));
		boolean isSbsProjectMX;
		Question question = null;
		if (email != null && email instanceof Question) {
			question = (Question) email;
		}
		if (question != null) {
			isSbsProjectMX = TagMatchDefinitions.isSbsProject(question);
			if (isSbsProjectCX != isSbsProjectMX) {
				SkyLogger.getWflLogger().warn("810: Archive/CreateArchFiles: " + question.getDocId() + " : " + question.getId() + " MasterConflict mx:q:" + question.getId() + ":" + isSbsProjectMX + " cx:" + isSbsProjectCX);
				
			}
		} else {
			isSbsProjectMX = false;
		}
		return isSbsProjectCX || isSbsProjectMX;
	}
}
