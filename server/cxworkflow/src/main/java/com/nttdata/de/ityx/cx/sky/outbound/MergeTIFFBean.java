/**
 * 
 */
package com.nttdata.de.ityx.cx.sky.outbound;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.ityx.sharedservices.image.ArchiveUtils;
import com.nttdata.de.ityx.sharedservices.utils.DocContainerUtils;
import com.nttdata.de.ityx.sharedservices.utils.NttFileUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.document.image.ImageDocument;
import de.ityx.contex.impl.document.image.ImagePage;
import de.ityx.contex.interfaces.designer.IBeanState;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contex.interfaces.document.CDocument;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

public class MergeTIFFBean extends AbstractWflBean implements IBeanState {

	private static final String		OUTPUT_TIFF_PARAMETER	= "outputTIFF";
	private static final String		OUTPUT_PDF_PARAMETER	= "outputPDF";
	private Map<Long, List<String>>	cleanMap				= new TreeMap<>();

	@Override
	public void execute(IFlowObject flowObject) throws Exception {
		final String docid = DocContainerUtils.getDocID(flowObject);
		CDocument doc= DocContainerUtils.getDoc(flowObject);
		final String end = ".tif";
		String base_path = (String) flowObject.get(ArchiveUtils.ARCHIVE_BASE_DIR);
		String direction = (String) flowObject.get("MoveFileToArchive_Direction");
		String base_dir = base_path + File.separator + direction + File.separator;
		String src_path = base_dir + "src";
		String tif_path = base_dir + "tif";
		long threadid = Thread.currentThread().getId();
		SkyLogger.getWflLogger().debug("811: Outbound/MergeTIFFBean: "+docid+" path: " + src_path + ", docid: " + docid + ", threadid: " + threadid);
		File dir = new File(src_path);
		if (!(dir.exists() && dir.isDirectory())) {
			SkyLogger.getWflLogger().error("811: Outbound/MergeTIFFBean: "+docid+"  folder " + src_path + " does not exist");
			throw new Exception("811: Outbound/MergeTIFFBean: "+docid+" folder " + src_path + " does not exist");
		}
		if (docid != null && src_path != null) {
			String[] files = new File(src_path).list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					String lname = name.toLowerCase();
					return lname.startsWith(docid.toLowerCase()+"_") && lname.endsWith(".tif");
				}
			});
			SkyLogger.getWflLogger().debug("811: Outbound/MergeTIFFBean: "+docid+" files found: " + files.length);
			SkyLogger.getWflLogger().debug("811: Outbound/MergeTIFFBean: "+docid+" files: " + Arrays.toString(files));
			if (files.length == 0) {
				SkyLogger.getWflLogger().error("811: Outbound/MergeTIFFBean: "+docid+" cannot found input files: " + src_path + "\\" + docid + "_*.tif");
				throw new Exception("811: Outbound/MergeTIFFBean: "+docid+" cannot found input files: " + src_path + "\\" + docid + "_*.tif");
			}
			ImageDocument tifImage = ImageDocument.getInstance(src_path + File.separator + files[0]);
			copyMetadata(doc,tifImage);
			SkyLogger.getWflLogger().debug("811: Outbound/MergeTIFFBean: "+docid+" tifImage: " + tifImage);
			for (int i = 1; i < files.length; i++) {
				String file = src_path + File.separator + files[i];
				SkyLogger.getWflLogger().debug("811: Outbound/MergeTIFFBean: "+docid+" Add file: " + file);
				ImageDocument tmpImage = ImageDocument.getInstance(file);
				copyMetadata(doc,tmpImage);
				for (ImagePage page : tmpImage.getPages()) {
					tmpImage.removePage(page);
					tifImage.addPage(page);
				}
			}
			String outputFileName = tif_path + File.separator + docid + ".tif";
			flowObject.put(OUTPUT_TIFF_PARAMETER, outputFileName);
			String pdf_Path = base_dir + "pdf";
			flowObject.put(OUTPUT_PDF_PARAMETER, pdf_Path);

			SkyLogger.getWflLogger().debug("811: Outbound/MergeTIFFBean: "+docid+" try to save file: " + outputFileName);
			NttFileUtils.writeFile(tifImage.getPayload(), new File(outputFileName));
			/*
			 * File f = new File(outputFileName); if (!f.exists()) {
			 * SkyLogger.getWflLogger
			 * ().error("MergeTiffBean: output file is not found: " +
			 * outputFileName); throw new
			 * RuntimeException("MergeTiffBean: output file is not found: " +
			 * outputFileName); }
			 */
			List<String> cleanData = new ArrayList<>();
			cleanData.add(docid);
			cleanData.add(src_path);
			cleanData.add(tif_path);
			cleanData.add(base_dir + "dst");
			cleanData.addAll(Arrays.asList(files));
			cleanMap.put(threadid, cleanData);
		} else {
			SkyLogger.getWflLogger().error("811: Outbound/MergeTIFFBean: "+docid+" input data are not reliable: path: " + src_path + ", docid: " + docid);
			throw new Exception("811: Outbound/MergeTIFFBean: "+docid+" input data are not reliable: path: " + src_path + ", docid: " + docid);
		}
	}

	@Override
	public void cleanState() {
		long threadid = Thread.currentThread().getId();
		List<String> data = cleanMap.get(threadid);
		String docid = data.get(0);
		String src_path = data.get(1);
		String tif_path = data.get(2);
		String dst_path = data.get(3);
		try {
			NttFileUtils.moveFileToDir(tif_path, dst_path, docid + ".tif");
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 4; i < data.size(); i++) {
			String file = data.get(i);
			try {
				NttFileUtils.moveFileToDir(src_path, dst_path, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}