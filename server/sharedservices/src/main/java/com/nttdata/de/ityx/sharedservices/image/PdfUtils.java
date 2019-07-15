package com.nttdata.de.ityx.sharedservices.image;

import com.itextpdf.text.*;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.io.FileChannelRandomAccessSource;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.codec.TiffImage;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.nttdata.de.ityx.sharedservices.utils.NttFileUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.base.Global;
import de.ityx.customer.commons.pdf.aspose.AsposeLicenseRegistry;
import de.ityx.customer.commons.pdf.impl.CreatePDFImpl;
import de.ityx.customer.commons.pdf.model.ByteAttachment;
import de.ityx.customer.commons.pdf.model.EmailContainer;
import de.ityx.utils.HTMLDetection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hsmf.MAPIMessage;
import org.elasticsearch.common.collect.Lists;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.*;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nttdata.de.ityx.sharedservices.image.ArchiveUtils.ArchivePrepStatus.DST;

/**
 * Created by meinusch on 20.07.15.
 */
public class PdfUtils {
	
	static BaseFont fontBase;
	Font fontStandard;
	Font fontHeader;
	
	private FontSelector fsStandard;
	
	private static final HashMap<String, Object> HTMLProviders = new HashMap<>();
	
	public static PdfUtils instance = null;
	
	public static synchronized PdfUtils getInstance() {
		if (instance == null) {
			instance = new PdfUtils();
		}
		return instance;
	}
	
	private PdfUtils() {
		try {
			//Font helvetica = new Font(FontFamily.HELVETICA, 12);
			//Font font = new Font(Font.FontFamily.TIMES_ROMAN, 9f, Font.BOLD);
			//"verdana.ttf", "arial.ttf"
			PdfUtils.fontBase = BaseFont.createFont("verdana.ttf", BaseFont.CP1252, BaseFont.EMBEDDED, true, IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("verdana.ttf")), null, false);
			BaseFont fontBaseB = BaseFont.createFont("verdanab.ttf", BaseFont.CP1252, BaseFont.EMBEDDED, true, IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("verdanab.ttf")), null, false);
			
			//BaseFont bf_helv = helvetica.getCalculatedBaseFont(false);
			FontFactory.defaultEmbedding = true;
			
			//BaseFont fontBase = BaseFont.createFont("Helvetica", "UTF-8", BaseFont.EMBEDDED);
			//BaseFont fontBaseB = BaseFont.createFont("Helvetica-Bold", "UTF-8", BaseFont.EMBEDDED);
			fontStandard = new Font(fontBase, 10.0F);
			fontHeader = new Font(fontBaseB, 16.0F, Font.BOLD);

			/*FontFactory.registerDirectory("resources/fonts");
			for (String f : FontFactory.getRegisteredFonts()) {
				document.add(new Paragraph(f, FontFactory.getFont(f, "", BaseFont.EMBEDDED)));
			}
			*/
			//BaseFont baseSerif = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, false);
			//fontHeader = new Font(baseSerif, 14, Font.BOLD);
			
			List<BaseFont> bfList = new ArrayList<>();
			//bfList.add(BaseFont.createFont("Helvetica", BaseFont.IDENTITY_H, BaseFont.EMBEDDED));
			//bfList.add(BaseFont.createFont("Helvetica-Bold", BaseFont.IDENTITY_H, BaseFont.EMBEDDED));
			
			String[] fontPaths = new String[]{
					// quite nice font with wide european support
					"arial.ttf", "arialbd.ttf", "arialbi.ttf", "ariali.ttf", "verdana.ttf", "verdanab.ttf", "verdanabd.ttf", "verdanai.ttf", "verdanaz.ttf", "winding.ttf", "tahoma.ttf", "tahomabd.ttf"};
			
			for (String fontPath : fontPaths) {
				try {
					BaseFont bf = getIdentityFont(fontPath);
					if (bf == null) {
						System.out.println("Font " + fontPath + " does not exist");
					} else {
						bfList.add(bf);
					}
					if (bf != null) {
						SkyLogger.getWflLogger().info("registering:" + fontPath + " >" + bf.getPostscriptFontName());
						FontFactory.register(fontPath, bf.getPostscriptFontName());
					} else {
						SkyLogger.getWflLogger().warn("problem registering:" + fontPath);
					}
					
				} catch (Exception e) {
					System.out.println("Failure when trying to load font " + fontPath);
					e.printStackTrace();
				}
			}
			
			// prepare fsHeader
			FontSelector fsHeader = new FontSelector();
			for (BaseFont baseFont : bfList) {
				Font font = new Font(baseFont, 14, Font.BOLD);
				fsHeader.addFont(font);
			}
			// prepare fsNormal
			fsStandard = new FontSelector();
			for (BaseFont baseFont : bfList) {
				Font font = new Font(baseFont, 9, Font.NORMAL);
				fsStandard.addFont(font);
			}
			HTMLProviders.put(HTMLWorker.FONT_PROVIDER, new PdfFontFactory());
			AsposeLicenseRegistry alr = AsposeLicenseRegistry.getInstance();
			boolean pdfloaded = alr.loadLicenseFromResources();
			
			SkyLogger.getWflLogger().info("PdfUtils PDF-License loaded" + pdfloaded);
		} catch (DocumentException | IOException e) {
			SkyLogger.getWflLogger().error("PdfUtils Problem beim Font-Laden" + e.getMessage(), e);
		}
		
		
	}
	
	private BaseFont getIdentityFont(String path) throws DocumentException, IOException {
		URL fontResource = getClass().getClassLoader().getResource(path);
		if (fontResource == null)
			return null;
		String fontPath = fontResource.toExternalForm();
		if (path.toLowerCase().endsWith(".ttc")) {
			//            String[] ttcNames = BaseFont.enumerateTTCNames(path);
			// first entry
			fontPath = fontPath + ",0";
		}
		BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		baseFont.setSubset(true);
		return baseFont;
	}
	
	public static File createMergedPdf(List<File> files, String docid, String dstPdfFile) throws Exception {
		
		if (files != null && files.size() == 1 && files.get(0) != null && FilenameUtils.getExtension(files.get(0).getAbsolutePath()).equalsIgnoreCase("pdf")) {
			SkyLogger.getWflLogger().info("PdfUtils Outbound/createPDF: Quicky:" + docid + " files: " + files.get(0).getAbsolutePath());
			File dst = new File(dstPdfFile);
			NttFileUtils.createDirIfNotExists(dstPdfFile);
			try {
				Files.copy(files.get(0).toPath(), dst.toPath());
			} catch (Exception e) {
				SkyLogger.getWflLogger().warn("PdfUtils Outbound/createPDF: Exception during Quicky:" + docid + " files: " + files.get(0).getAbsolutePath() + " msg:" + e.getMessage(), e);
				return files.get(0);
			}
			return dst;
		}
		
		SkyLogger.getWflLogger().info("PdfUtils Outbound/createPDF: " + docid + " files: " + org.apache.commons.lang3.StringUtils.join(files, ';'));
		if (files.size() == 0) {
			SkyLogger.getWflLogger().error("PdfUtils Outbound/createPDF: " + docid + " cannot found input files for doc: " + docid);
			throw new Exception("PdfUtils Outbound/createPDF: " + docid + " cannot found input files for doc: " + docid);
		}
		// convert to pdf
		// ITyX Customer.commons Way
		//CreatePDF createPDF = CreatePDFImpl.INSTANCE;
		//createPDF.createPDFFromFiles(dstPdfFile, files);
		//Itext Way
		
		// ** Pre-Schleife: konvertiere alle tiff-Files einzeln zur pdf-Dateien
		List<File> interimfiles = new LinkedList<>();
		try {
			int i = 0;
			for (File inputFile : files) {
				String ext = FilenameUtils.getExtension(inputFile.getAbsolutePath());
				SkyLogger.getWflLogger().info("PdfUtils Outbound/createPDF: " + docid + " PRE processing:" + inputFile.getAbsolutePath());
				if (ext == null || ext.isEmpty()) {
					SkyLogger.getWflLogger().error(docid + "Cannot recoginze Extension for File:" + inputFile.getAbsolutePath());
				}
				
				if (ext.equalsIgnoreCase("tif") || ext.equalsIgnoreCase("tiff")) {
					SkyLogger.getWflLogger().info(docid + "add tiff-File:" + inputFile.getAbsolutePath());
					
					String interimDstName = Global.getProperty("sky.archive.base", System.getProperty("java.io.tmpdir")) + File.separator + "interim" + File.separator + docid + "_" + i + ".pdf";
					Document pdfdoc = new Document(PageSize.A4);
					NttFileUtils.createDirIfNotExists(interimDstName);
					FileOutputStream fos = new FileOutputStream(interimDstName);
					pdfdoc.addTitle(docid);
					pdfdoc.addCreator("DMS ITyX Mediatrix");
					pdfdoc.addCreationDate();
					pdfdoc.open();
					PdfWriter pdfWriter = PdfWriter.getInstance(pdfdoc, fos);
					pdfWriter.setPDFXConformance(PdfWriter.PDFX1A2001);
					pdfWriter.setPdfVersion(PdfWriter.VERSION_1_7);
					pdfWriter.setCompressionLevel(5);
					pdfdoc.open();
					//pdfWriter.createXmpMetadata();
					//pdfWriter.setTagged();
					
					FileChannelRandomAccessSource is = null;
					com.itextpdf.text.pdf.RandomAccessFileOrArray ra = null;
					
					try {
						//is = new FileInputStream(inputFile);
						is = new FileChannelRandomAccessSource(new FileInputStream(inputFile).getChannel());
						ra = new com.itextpdf.text.pdf.RandomAccessFileOrArray(is);
						Image img;
						for (int j = 1; j <= TiffImage.getNumberOfPages(ra); j++) {
							img = TiffImage.getTiffImage(ra, j);
							img = scaleIfNeeded(img);
							SkyLogger.getWflLogger().info(docid + "add tiff-File_inner: " + img.getScaledWidth() + "/" + img.getScaledHeight() + " url:" + img.getUrl());
							if (i != 0) {
								pdfdoc.newPage();
							}
							pdfdoc.add(img);
						}
						i++;
					} catch (Throwable ee) {
						SkyLogger.getWflLogger().error("PdfUtils Outbound/MergeTIFFBean: " + docid + " Problem processing Tif:" + inputFile.getAbsolutePath() + " msg:" + ee.getMessage(), ee);
					} finally {
						try {
							if (pdfdoc != null && pdfdoc.isOpen()) {
								pdfdoc.close();
							}
							if (pdfWriter != null) {
								pdfWriter.flush();
								pdfWriter.close();
							}
							if (ra != null) {
								ra.close();
							}
							if (is != null) {
								is.close();
							}
							if (fos != null) {
								fos.close();
							}
						} catch (Exception eee) {
							SkyLogger.getWflLogger().error(docid + "Trying to close Writer:" + eee.getMessage(), eee);
						}
						interimfiles.add(new File(interimDstName));
					}
				} else { //other than TIFF
					interimfiles.add(inputFile);
				}
			}
		} catch (Exception e) {
			SkyLogger.getWflLogger().error(docid + "Trying to create pdf unsucessfully:" + e.getMessage(), e);
			throw e;
		}
		
		// Haupt-Schleife: Merge PDF's zusammen
		try {
			Document pdfdoc = new Document(PageSize.A4);
			NttFileUtils.createDirIfNotExists(dstPdfFile);
			
			FileOutputStream fos = new FileOutputStream(dstPdfFile);
			pdfdoc.addTitle(docid);
			pdfdoc.addCreator("DMS ITyX Mediatrix");
			pdfdoc.addCreationDate();
			
			//PdfWriter pdfWriter = PdfWriter.getInstance(pdfdoc, fos);
			PdfCopy pdfWriter = new PdfCopy(pdfdoc, fos);
			pdfWriter.setPDFXConformance(PdfWriter.PDFX1A2001);
			pdfWriter.setPdfVersion(PdfWriter.VERSION_1_6);
			pdfWriter.setCompressionLevel(5);
			pdfWriter.createXmpMetadata();
			//pdfWriter.setTagged();
			pdfdoc.open();
			int i = 0;
			List<PdfReader> readers = new LinkedList<>();
			try {
				for (File inputFile : interimfiles) {
					SkyLogger.getWflLogger().info("PdfUtils Outbound/createPDF: " + docid + " processing:" + inputFile.getAbsolutePath());
					
					String ext = FilenameUtils.getExtension(inputFile.getAbsolutePath());
					if (ext == null || ext.isEmpty()) {
						SkyLogger.getWflLogger().error(docid + "Cannot recoginze Extension for File:" + inputFile.getAbsolutePath());
					}
					if (ext.equalsIgnoreCase("pdf")) {
						SkyLogger.getWflLogger().info(docid + " add pdf-File:" + inputFile.getAbsolutePath());
						PdfReader reader = new PdfReader(inputFile.getPath());
						SkyLogger.getWflLogger().info(docid + " add pdf-File:" + inputFile.getAbsolutePath() + " pages:" + reader.getNumberOfPages());
						
						for (int pageN = 1; pageN <= reader.getNumberOfPages(); pageN++) {
							SkyLogger.getWflLogger().info(docid + " add pdf-File:" + inputFile.getAbsolutePath() + " start processing page:" + pageN);
							if (i != 0) {
								pdfdoc.newPage();
							}
							PdfImportedPage page = pdfWriter.getImportedPage(reader, pageN);
							/*Image img = Image.getInstance(page);
							scaleIfNeeded(img);
							pdfdoc.add(img);
							// Alternative Way
							//dc.addImage(img, false);
							*/
							pdfWriter.addPage(page);
							SkyLogger.getWflLogger().info(docid + " add pdf-File:" + inputFile.getAbsolutePath() + " finish processing page:" + pageN);
							readers.add(reader); // hier kann man diesen nicht abschließen, erst nach dem abschluss von Writer
							i++;
						}
					} else {
						SkyLogger.getWflLogger().error("PdfUtils Outbound/MergeTIFFBean: " + docid + " Problem processing File:" + inputFile.getAbsolutePath() + " unkown extension:" + ext);
					}
				}
			} finally {
				try {
					try {
						if (pdfdoc != null && pdfdoc.isOpen()) {
							pdfdoc.close();
						}
						if (pdfWriter != null) {
							pdfWriter.flush();
							pdfWriter.close();
						}
						if (fos != null) {
							fos.close();
						}
					} catch (Exception eee) {
						SkyLogger.getWflLogger().error(docid + "Trying to close Writer:" + eee.getMessage(), eee);
					}
					for (PdfReader reader : readers) {
						//pdfWriter.freeReader(reader);
						reader.close();
					}
				} catch (Exception eee) {
					SkyLogger.getWflLogger().error(docid + "Trying to close Writer:" + eee.getMessage(), eee);
				}
			}
		} catch (Exception e) {
			SkyLogger.getWflLogger().error(docid + "Trying to create pdf unsucessfully:" + e.getMessage(), e);
			throw e;
		}
		return new File(dstPdfFile);
	}
	
	
	private static boolean isOversized(Image img) {
		float margin = 40.0F;
		return (img.getWidth() > PageSize.A4.getWidth() - margin || img.getHeight() > PageSize.A4.getHeight() - margin) ;
	}
	
	private static Image scaleIfNeeded(Image img) {
		float margin = 0F;
		if (img.getWidth() > PageSize.A4.getWidth() - margin || img.getHeight() > PageSize.A4.getHeight() - margin) {
			img.scaleToFit(PageSize.A4.getWidth() - margin, PageSize.A4.getHeight() - margin);
		}
		img.setCompressionLevel(3);
		img.setAbsolutePosition((PageSize.A4.getWidth() - img.getScaledWidth()) / 2.0F, (PageSize.A4.getHeight() - img.getScaledHeight()) / 2.0F);
		return img;
	}
	
	
	private void addPagesFromBytes(Document document, PdfWriter writer, ArchivingAttachment byteAttachment, List<File> outputFilesList, Map<String, String> metaMap) throws DocumentException, IOException {
		byte[] fileBytes = byteAttachment.getBytes();
		String fileName = byteAttachment.getFileName();
		boolean isContenAtt = byteAttachment.isContentAtt();
		//String ext = FilenameUtils.getExtension(fileName).toLowerCase().replaceAll("[\\?=&:]","");
		String ext = byteAttachment.getExtension();
		
		Paragraph att = new Paragraph("", fontStandard);
		att.setKeepTogether(true);
		Chunk c = new Chunk("Attachment: " + fileName + System.lineSeparator(), fontHeader);
		c.setUnderline(0.8F, -1.0F);
		att.add(c);
		
		if (fileBytes == null || fileBytes.length < 1) {
			att.add(new Chunk("Attachment " + fileName + " not readable.\n", fontStandard));
		} else {
			att.add(new Chunk("Size: " + (new DecimalFormat("###,###.##", new DecimalFormatSymbols(Locale.GERMANY))).format(fileBytes.length / 1024L) + "kB\n", fontStandard));
			
			FileChannelRandomAccessSource is = null;
			RandomAccessFileOrArray ra = null;
			try {
				switch (ext) {
					case "png":
					case "bmp":
					case "gif":
					case "jpg":
					case "jpeg":
						Image jpgimg = Image.getInstance(fileBytes);
						document.newPage();
						if (!isContenAtt) {
							document.add(att);
							document.add(new LineSeparator(fontHeader));
						}
						if (isOversized(jpgimg)) {
							if (!isContenAtt) {
								document.newPage();
							}
							jpgimg = scaleIfNeeded(jpgimg);
						}
						
						document.add(jpgimg);
						break;
					
					case "tif":
					case "tiff":
						ra = new RandomAccessFileOrArray(fileBytes);
						document.newPage();
						if (!isContenAtt) {
							document.add(att);
							document.add(new LineSeparator(fontHeader));
						}
						
						for (int j = 1; j <= TiffImage.getNumberOfPages(ra); j++) {
							Image tifimg = TiffImage.getTiffImage(ra, j);
							if (isOversized(tifimg)) {
								if (!isContenAtt) {
									document.newPage();
								}
								tifimg = scaleIfNeeded(tifimg);
							}
							
							String ocrTxt = byteAttachment.getOcrText(j);
							if (ocrTxt != null && !ocrTxt.isEmpty()) {
								try {
									Chunk ocr = new Chunk(tifimg, 0F, 0F);
									ocr.setAccessibleAttribute(PdfName.ALT, new PdfString(ocrTxt));
									document.add(ocr);
								} catch (Exception ee) {
									document.add(tifimg);
								}
							} else {
								document.add(tifimg);
							}
						}
						break;
					case "rtf":
					case "doc":
					case "docx":
						com.aspose.words.Document asposeDoc = new com.aspose.words.Document(new ByteArrayInputStream(fileBytes));
						File tmpf = File.createTempFile(fileName, ".pdf");
						asposeDoc.save(tmpf.getAbsolutePath());
						fileBytes = IOUtils.toByteArray(new FileInputStream(tmpf));
						includePdfSubDocument(document, writer, fileBytes, att);
						try {
							tmpf.delete();
						} catch (Exception e1) {
							SkyLogger.getWflLogger().info("cannot delete tmpfile:" + tmpf.getAbsolutePath());
							throw e1;
						}
						break;
					case "pdf":
						try {
							includePdfSubDocument(document, writer, fileBytes, att);
						} catch (IOException ee) {
							SkyLogger.getWflLogger().info("PDF File cannot be intergrated into Archive-PDF:" + fileName);
							throw ee;
						}
						break;
					case "msg":
						InputStream msgis = new ByteArrayInputStream(fileBytes);
						MAPIMessage msg = new MAPIMessage(msgis);
						fileBytes = msg.getTextBody().getBytes();
						msgis.close();
					
					case "txt":
					/*String txt = new String(fileBytes);
					Paragraph p = new Paragraph(encodeToCP1252(txt), fontStandard);
					document.add(att);
					document.add(new LineSeparator(fontHeader));
					document.add(p);
					break;*/
					case "htm":
					case "html":
						//InputStream htmlis = new ByteArrayInputStream(fileBytes); //Probleme mit malformed Mails
						//XMLWorkerHelper.getInstance().parseXHtml(writer, document, htmlis);
						//HTMLWorker htmlWorker = new HTMLWorker(document); //Probleme mit Fonts
						//htmlWorker.parse(new StringReader(new String(fileBytes)));
						document.newPage();
						if (!isContenAtt) {
							document.add(att);
							document.add(new LineSeparator(fontHeader));
						}
						String emailContent = new String(fileBytes);
						Matcher matcher = Pattern.compile("(?is)<body.*?>(.*)</body>").matcher(emailContent);
						if (matcher.find()) {
							emailContent = matcher.group(1);
						}
						Paragraph htmlp = new Paragraph();
						htmlp.add(System.lineSeparator());
						
						emailContent = emailContent.replaceAll("(?i)</?(a|img)(|\\s+[^>]+)>", "");
						emailContent = emailContent.replaceAll("(?i)src=\"cid:[.@\\-\\.]*\"", "");
						emailContent = emailContent.replaceAll("(?i)\\[cid:[.@\\-\\.]*\\]", "");
						
						List<Element> elements = HTMLWorker.parseToList(new StringReader(emailContent), null, HTMLProviders);
						for (Element element : elements) {
							htmlp.add(element);
						}
						document.newPage();
						document.add(htmlp);
						
						//String htmlclean = stripHtml(new String(fileBytes));
						//fileBytes=htmlclean.getBytes();
						break;
					case "tmp":
						SkyLogger.getWflLogger().info("PdfUtils Outbound/MergeTIFFBean: Ingore TMP-Attachment :" + fileName + " ");
						break;
					default:
						//case "xls":
						//case "xlsx":
						String outputFileS = ArchiveUtils.getArchFileName(metaMap, outputFilesList.size(), DST, "pdf");
						CreatePDFImpl ityxPdfCreator = CreatePDFImpl.INSTANCE;
						ByteAttachment e = new ByteAttachment(fileBytes, fileName, java.nio.file.Files.probeContentType(Paths.get(fileName, new String[0])));
						EmailContainer ec = new EmailContainer(null, Lists.newArrayList(new ByteAttachment[]{e}));
						File outputFile = ityxPdfCreator.createPDF(outputFileS, new EmailContainer[]{ec});
						
						//ityx scheint keine Exceptions nach aussen zu feuern.
						if (outputFile == null || !outputFile.exists() || outputFile.length() < 10) {
							throw new DocumentException("Empty Document");
						} else {
							SkyLogger.getWflLogger().debug("PdfUtils including file:" + fileName + " " + outputFile.length());
							byte[] pdfbytes = FileUtils.readFileToByteArray(outputFile);
							includePdfSubDocument(document, writer, pdfbytes, att);
							//	outputFilesList.add(outputFile);
						}
				}
			} catch (Exception e) {
				if (SkyLogger.getWflLogger().isDebugEnabled()) {
					SkyLogger.getWflLogger().warn("PdfUtils Outbound/MergeTIFFBean: Attachment cannot be converted, attaching as file:" + fileName + " " + e.getMessage(), e);
				} else {
					SkyLogger.getWflLogger().warn("PdfUtils Outbound/MergeTIFFBean: Attachment cannot be converted, attaching as file:" + fileName + " " + e.getMessage());
				}
				document.newPage();
				document.add(att);
				document.add(new LineSeparator(fontHeader));
				Paragraph nka = new Paragraph("Attachment:" + fileName + " konnte nicht konvertiert werden. " + System.lineSeparator(), fontStandard);
				document.add(nka);
				
				String outputFileS = ArchiveUtils.getArchFileName(metaMap, outputFilesList.size(), DST, ext);
				File outputFile = new File(outputFileS);
				
				FileOutputStream fos = null;
				SkyLogger.getWflLogger().warn("PdfUtils Outbound/MergeTIFFBean: ADD Attachment :" + outputFileS + " oldname:" + fileName);
				
				try {
					fos = new FileOutputStream(outputFile);
					fos.write(byteAttachment.getBytes());
					fos.flush();
					fos.close();
					outputFilesList.add(outputFile);
				} finally {
					try {
						if (fos != null) {
							fos.close();
						}
					} catch (Exception eee) {
						SkyLogger.getWflLogger().error("Trying to close Writer:" + eee.getMessage(), eee);
					}
				}
			} finally {
				if (ra != null) {
					ra.close();
				}
				if (is != null) {
					is.close();
				}
			}
		}
	}
	
	private void includePdfSubDocument(Document document, PdfWriter writer, byte[] fileBytes, Paragraph att) throws IOException, DocumentException {
		PdfReader pdfr = new PdfReader(fileBytes);
		document.newPage();
		document.add(att);
		document.add(new LineSeparator(fontHeader));
		if (pdfr.getNumberOfPages() < 1) {
			throw new DocumentException("Empty document cannot be included to main PDF");
		}
		for (int i = 1; i <= pdfr.getNumberOfPages(); ++i) {
			PdfImportedPage page = writer.getImportedPage(pdfr, i);
			Image pageAsImage = Image.getInstance(page);
			
			if (isOversized(pageAsImage)) {
				document.newPage();
				pageAsImage = scaleIfNeeded(pageAsImage);
				document.add(pageAsImage);
			} else {
				//pageAsImage = scaleIfNeeded(pageAsImage, false);
				document.add(pageAsImage);
			}
		}
	}
	
	private String encodeToCP1252(String text_in) {
		final CharBuffer utfEncoded = StandardCharsets.UTF_8.decode(java.nio.ByteBuffer.wrap(text_in.getBytes()));
		final byte[] winEncoded = StandardCharsets.ISO_8859_1.encode(utfEncoded).array();
		//return new String(winEncoded);
		return text_in;
	}
	
	public static String stripHtml(String html) {
		if (html == null)
			return null;
		org.jsoup.nodes.Document document = Jsoup.parse(html);
		org.jsoup.nodes.Document.OutputSettings os = document.outputSettings();//makes html() preserve linebreaks and spacing
		os.prettyPrint(false);
		document.select("br").append("\\n");
		document.select("p").prepend("\\n\\n");
		String s = document.html().replaceAll("\\\\n", "\n");
		return Jsoup.clean(s, "", Whitelist.none());
	}
	
	public List<File> generatePdfForAtt(Map<String, String> metaMap, List<File> outputFiles, List<ArchivingAttachment> aat) throws IOException, DocumentException {
		return generatePdfForEmail(metaMap, outputFiles, "DMS", null, null, aat);
		
	}
	
	public List<File> generatePdfForEmail(Map<String, String> metaMap, List<File> outputFiles, String title, Map<String, String> headers, Map<Integer, String> emailContentList, List<ArchivingAttachment> aat) throws IOException, DocumentException {
		String dstFilename = ArchiveUtils.getArchFileName(metaMap, outputFiles.size(), DST, "pdf");
		String docid = metaMap.get(TagMatchDefinitions.DOCUMENT_ID);
		String logPreafix = "generatePdf:" + docid + ":";
		
		File outputFile = new File(dstFilename);
		List<File> outputFilesList = new LinkedList<>();
		outputFilesList.add(outputFile);
		FileOutputStream fos = new FileOutputStream(dstFilename);
		
		Document pdfdoc = new Document(PageSize.A4);
		PdfWriter pdfWriter = PdfWriter.getInstance(pdfdoc, fos);
		//probleme mit ebedded Fonts die aus dem HtmlContent kommen
		//pdfWriter.setPDFXConformance(PdfWriter.PDFX1A2001); //PdfAConformanceLevel.PDF_A_3B
		
		pdfWriter.setPdfVersion(PdfWriter.VERSION_1_7);
		pdfWriter.setCompressionLevel(5);
		pdfWriter.createXmpMetadata();
		pdfWriter.setTagged();
		pdfWriter.setViewerPreferences(PdfWriter.DisplayDocTitle);
		FileChannelRandomAccessSource is = null;
		RandomAccessFileOrArray ra = null;
		pdfdoc.open();
		pdfdoc.addTitle(title);
		pdfdoc.addSubject(title);
		pdfdoc.addCreator("DMS ITyX Mediatrix");
		pdfdoc.addCreationDate();
		pdfdoc.addLanguage("de-DE");
		
		try {
			//alt, zum chekcen
			//body = new String(((String) part.getBodyPart(j).getContent()).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
			//input = new BufferedReader(new InputStreamReader(args[0], "UTF-8"));
			for (Map.Entry<Integer, String> emailContentE : emailContentList.entrySet()) {
				SkyLogger.getWflLogger().info(logPreafix + "processing: " + emailContentE.getKey() + " of:" + emailContentList.size());
				
				String emailContent = emailContentE.getValue();
				if (emailContent != null && !emailContent.isEmpty()) {
					if (emailContent.trim().length() < 3) {
						SkyLogger.getWflLogger().info(logPreafix + "ommiting empty content block:" + emailContent);
						continue;
					}
					try {
						Paragraph att = new Paragraph("", fontHeader);
						att.setKeepTogether(true);
						att.add(new Chunk("Email", fontHeader));
						String header = "";
						for (Map.Entry<String, String> h : headers.entrySet()) {
							if (h.getValue() != null && !h.getValue().isEmpty()) {
								header += h.getKey() + ": " + h.getValue() + System.lineSeparator();
							}
						}
						Paragraph pt = new Paragraph();
						
						if (!HTMLDetection.isHTML(emailContent)) { //TXT
							SkyLogger.getWflLogger().info(logPreafix + "txt content");
							SkyLogger.getWflLogger().debug(logPreafix + "txt content:" + emailContent);
							pt.add(fsStandard.process(System.lineSeparator() + encodeToCP1252(emailContent)));
							pt.setAlignment(3);
							
						} else { //HTML
							// geht nicht- parsing probleme
							//InputStream htmlis = new ByteArrayInputStream(emailContent.getBytes());
							//XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, pdfdoc, htmlis);
							
							// probleme mit fonts
							//HTMLWorker htmlWorker = new HTMLWorker(pdfdoc);
							//htmlWorker.parse(new StringReader(emailContent));
							//String htmlcontent=  new HTML2TextParser().stripHTML(emailContent);
							//Paragraph pt = new Paragraph(htmlcontent, fontStandard, "UTF-8");
							SkyLogger.getWflLogger().info(logPreafix + "html content:start");
							if (SkyLogger.getWflLogger().isDebugEnabled()) {
								SkyLogger.getWflLogger().debug(logPreafix + "html content: process" + stripHtml(emailContent));
							}
							Matcher matcher = Pattern.compile("(?is)<body.*?>(.*)</body>").matcher(emailContent);
							if (matcher.find()) {
								emailContent = matcher.group(1);
							}
							pt.add(System.lineSeparator());
							
							
							emailContent = emailContent.replaceAll("(?i)</?(a|img)(|\\s+[^>]+)>", "");
							emailContent = emailContent.replaceAll("(?i)src=\"cid:[.@\\-\\.]*\"", "");
							emailContent = emailContent.replaceAll("(?i)\\[cid:[.@\\-\\.]*\\]", "");
							
							List<Element> elements = HTMLWorker.parseToList(new StringReader(emailContent), null, HTMLProviders);
							for (Element element : elements) {
								pt.add(element);
							}
							//Paragraph contentP = new Paragraph();
							//Chunk content = new Chunk(htmlcontent, fontStandard);
							//contentP.add(content);
							SkyLogger.getWflLogger().info(logPreafix + "html content:finish");
							
							//contentP.add(fsStandard.process(System.lineSeparator()+encodeToCP1252(htmlcontent)));
							//contentP.setAlignment(3);
							//pdfdoc.add(contentP);
						}
						// dieser Abschnitt muss am Ende stehen. Wenn es zur Problemen beid er Verarbietung kommt (vor allem bei HTML)
						// ist es besser zum nächsten Text-Abschnitt zu gehen
						pdfdoc.newPage();
						pdfdoc.add(att);
						pdfdoc.add(new LineSeparator(fontHeader));
						pdfdoc.add(new Paragraph(header, fontStandard));
						pdfdoc.add(new LineSeparator(fontHeader));
						pdfdoc.add(pt);
						break;
					} catch (FileNotFoundException e) {
						SkyLogger.getWflLogger().warn(logPreafix + "Embedded images are not supported yet:" + e.getMessage() + emailContent);
						
					} catch (Exception e) {
						SkyLogger.getWflLogger().warn(logPreafix + "Exception during processsing of Message. Will continue with next content. Faulty text: " + (emailContent) + " message:" + e.getMessage(), e);
					}
				}
			}
			
			for (ArchivingAttachment byteAttachment : aat) {
				try {
					SkyLogger.getWflLogger().warn(logPreafix + "processing att: " + (byteAttachment.getFileName()));
					addPagesFromBytes(pdfdoc, pdfWriter, byteAttachment, outputFilesList, metaMap);
				} catch (Exception ae) {
					SkyLogger.getWflLogger().warn(logPreafix + "Exception during processsing of Message. Will continue with next content. Faulty text: " + (byteAttachment.getFileName()) + " message:" + ae.getMessage(), ae);
					
					String ext = byteAttachment.getExtension();
					String outputFileS = ArchiveUtils.getArchFileName(metaMap, outputFiles.size(), DST, ext);
					File attFile = new File(outputFileS);
					
					FileOutputStream attfos = null;
					SkyLogger.getWflLogger().warn("PdfUtils Outbound/MergeTIFFBean: ADD Attachment :" + outputFileS + " oldname:" + byteAttachment.getFileName());
					
					try {
						attfos = new FileOutputStream(attFile);
						attfos.write(byteAttachment.getBytes());
						attfos.flush();
						attfos.close();
						outputFilesList.add(attFile);
					} finally {
						try {
							if (attfos != null) {
								attfos.close();
							}
						} catch (Exception eee) {
							SkyLogger.getWflLogger().error("Trying to close Writer:" + eee.getMessage(), eee);
						}
					}
				}
			}
		} catch (Throwable ee) {
			SkyLogger.getWflLogger().error("PdfUtils Outbound/MergeTIFFBean: " + docid + " Problem gerating PDF msg:" + ee.getMessage(), ee);
		} finally {
			try {
				if (pdfdoc != null && pdfdoc.isOpen()) {
					pdfdoc.close();
				}
				if (pdfWriter != null) {
					pdfWriter.flush();
					pdfWriter.close();
				}
				if (ra != null) {
					ra.close();
				}
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception eee) {
				SkyLogger.getWflLogger().error(docid + "Trying to close Writer:" + eee.getMessage(), eee);
			}
		}
		return outputFilesList;
	}
	
	
	public static class PdfFontFactory implements FontProvider {
		@Override
		public boolean isRegistered(String fontname) {
			return false;
		}
		
		@Override
		public Font getFont(String fontname, String encoding, boolean embedded, float size, int style, BaseColor color) {
			return new Font(fontBase, 10f, style, color);
		}
	}
}
