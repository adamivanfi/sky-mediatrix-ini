package com.nttdata.de.ityx.cx.sky.importChannel;

import com.nttdata.de.ityx.cx.workflow.base.AbstractWflBean;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.impl.designer.state.KeyConfiguration;
import de.ityx.contex.interfaces.designer.IFlowObject;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LetterImportBean extends AbstractWflBean {

	// private static final String IDX_DAT = "IDX.TXT";
	/**
	 * 
	 */
	private static final long serialVersionUID = 962521877474182958L;

	// Contains the list of temporary files.
	private ArrayList<String> tmpFiles = new ArrayList<>();

	private String workScan;

	private String destScan;

	private List<String> checkIndexLine(String line, int lengthOfFilename) {

		String[] values = line.split(";");
		// values[3] contains first number
		// values[4] contains last number

		Integer first = Integer.parseInt(values[3].replaceAll("\"", "").trim());
		Integer last = Integer.parseInt(values[4].replaceAll("\"", "").trim());

		// each filename is running number with the following length
		String format = "%0" + lengthOfFilename + "d";

		List<String> result = new ArrayList<>();

		for (int h1 = 0; h1 <= last - first; h1++) {
			result.add(String.format(format, first + h1) + ".TIF");
		}

		return result;
	}

	private List<String> checkIndexFile(File indexFile, String filename,
			int lengthOfFilename) throws IOException {

		if (!indexFile.exists()) {
			return null;
		}

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(indexFile));
			String line = in.readLine();

			if (line != null) {
				List<String> result = checkIndexLine(line, lengthOfFilename);
				in.close();
				return result;
			}

		} catch (IOException e) {
			if (in!=null) in.close();
			throw e;
		}

		in.close();
		return null;
	}

	public void execute(IFlowObject flowObject) throws Exception {
		tmpFiles.clear();
		String uri = (String) flowObject.get("doc.uri");
		SkyLogger.getItyxLogger().debug("URI: " + uri);

		String importScan = (String) flowObject.get("importScan");
		workScan = (String) flowObject.get("workScan");
		destScan = (String) flowObject.get("destScan");

		String srcFile = importScan + uri.substring(workScan.length());

		String indexPath = new File(srcFile).getParent();

		File indexFile = new File(uri);

		String workDirForIdx = workScan
				+ indexPath.substring(importScan.length());

		int lengthOfSubfolder = indexFile.getParentFile().getName().length();

		SkyLogger.getItyxLogger().debug("IDX: " + indexFile.getAbsolutePath());

		List<String> filenames = checkIndexFile(indexFile,
				new File(uri).getName(), lengthOfSubfolder);

		if (filenames != null) {

			// Moves the files for processing.
			Iterator<String> sit = filenames.iterator();
			while (sit.hasNext()) {
				String fname = sit.next();
				File currentFile = new File(indexPath + File.separator + fname);
				if (!currentFile.exists()) {
					String msg = "Filename " + currentFile
							+ " does not exist";
					SkyLogger.getItyxLogger().error(msg);
					throw new Exception(msg);
				}

				File destFile = new File(workDirForIdx + File.separator + fname);
				SkyLogger.getItyxLogger().debug("Moving file to processing directory: "
						+ destFile.getAbsolutePath());
				FileUtils.copyFile(currentFile, destFile);
				FileUtils.deleteQuietly(currentFile);
				tmpFiles.add(destFile.getCanonicalPath());

				SkyLogger.getItyxLogger().debug("DONE: " + currentFile);
			}

			// // Sets the list of files in the process.
			flowObject.put("validFile", Boolean.TRUE);
			flowObject.put("fileList", new ArrayList<>(tmpFiles));

		} else {
			throw new Exception("Invalid index file");
		}
	}


	@Override
	public KeyConfiguration[] getKeys() {
		return new KeyConfiguration[]{
				new KeyConfiguration("validFile", Boolean.class),
				new KeyConfiguration("fileList", ArrayList.class)
		};
	}

	@Override
	public void cleanState() {
		for (String fname : tmpFiles) {
			try {
				FileUtils.moveFile(new File(fname),
						new File(fname.replace(workScan, destScan)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


}
