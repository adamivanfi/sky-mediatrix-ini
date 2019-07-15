package com.nttdata.de.sky.ityx.contex.importChannel;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.interfaces.designer.IFlowObject;
import de.ityx.contexdesigner.exflow.states.FlowObject;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Import description
 * 
 * 
 * Flow configuration
 * ##importScan## = BASE_DIR/scanner1
 * ##workScan## = BASE_DIR/tmp/scanner1
 * ##errorScan## = BASE_DIR/err/scanner1
 * ##destScan## = BASE_DIR/dst/scanner1
 * 
 * 
 * 
 * 
 * BASE_DIR/scanner1/WGE00000001/WGE00000001/00000007/IDX.DAT		<- searched for by process
 * BASE_DIR/scanner1/WGE00000001/WGE00000001/00000007/IDX.TXT		<- searched for by process (possibly created by external job?)
 * BASE_DIR/scanner1/WGE00000001/WGE00000001/00000007/<doc1>.TIF
 * BASE_DIR/scanner1/WGE00000001/WGE00000001/00000007/<doc2>.TIF
 * 
 * Import process monitors
 * BASE_DIR/scanner1
 * 
 * Import process moves file to temp work directory
 * BASE_DIR/tmp/scanner1 (##workScan##)
 * The import process creates the same sub directories as in BASE_DIR/scanner1
 * BASE_DIR/tmp/scanner1/WGE00000001/WGE00000001/00000007/IDX.TXT
 * 
 * The subdirectory can be extracted from the doc.uri by removing the first ##workScan## characters from the idx.txt parent folder
 * BASE_DIR/tmp/scanner1/WGE00000001/WGE00000001/00000007  (/IDX.TXT)
 *                      /WGE00000001/WGE00000001/00000007  (/IDX.TXT)
 * This can be used to construct all TIF filenames from the content of the idx.txt
 * 
 * 
 * Cleanup
 * TODO: Cleanup (moving files back, etc.)
 * 
 * Completion
 * TODO: Completion (moving all files, etc.)
 * 
 * @author VOGTDA
 *
 */


public class LetterImportBeanTest {
	
	private String tempDir = System.getProperty("java.io.tmpdir");

	@Test
	public void testExecutePositive() {
		LetterImportBean bean = new LetterImportBean();
		
		IFlowObject flowObject = new FlowObject(); 
		
		// Testing uses a dedicated temp directory
		String baseDir = ".";
		URL base = this.getClass().getResource("/DMSftp");
		if (base != null) {
			baseDir = new File(base.getFile().replaceAll("%20", " ")).getAbsolutePath();
		} else {
			fail("No test data available");
		}
		
		// These values have to be configured in the flow that is using the bean
		
		// This is the input folder. It used to contruct filenames
		flowObject.put("importScan", baseDir + File.separator+"scanner1");
		
		if(!tempDir.endsWith(File.separator)) {
			tempDir+=File.separator;
		}
		flowObject.put("workScan", tempDir+ "tmp"+File.separator+"scanner1");	
		flowObject.put("errorScan", tempDir + "err"+File.separator+"scanner1");
		flowObject.put("destScan", tempDir + "dst"+File.separator+"scanner1");
		
		// This is the idx file for this test case
		File testIdxFile = new File(baseDir + File.separator+"scanner1"+File.separator+"WGE00000001"+File.separator+"00000007"+File.separator+"IDX.TXT");
		File destIdxFile = new File(tempDir + File.separator+"tmp"+File.separator+"scanner1"+File.separator+"WGE00000001"+File.separator+"00000007"+File.separator+"IDX.TXT");
		if(!testIdxFile.renameTo(destIdxFile)){
			try {
				FileUtils.copyFile(testIdxFile, destIdxFile);
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}
				
		flowObject.put("doc.uri", destIdxFile.getAbsolutePath());
		
		try {
			bean.execute(flowObject);
		} catch (Exception e) {
			fail("Exception: "+e.getMessage());
		}
		
		// The bean will have to create a document and trigger a process. This can not be unit tested.

		// Test that the files have been successfully moved to tmp
		// TODO: Will we move the image files to the target directory already?
		assertTrue(new File(tempDir+"tmp"+File.separator+"scanner1"+File.separator+"WGE00000001"+File.separator+"00000007"+File.separator+"00000001.TIF").exists());
		assertFalse(new File(baseDir + File.separator+"WGE00000001"+File.separator+"00000007"+File.separator+"00000001.TIF").exists());
		assertTrue(new File(tempDir+"tmp"+File.separator+"scanner1"+File.separator+"WGE00000001"+File.separator+"00000007"+File.separator+"00000002.TIF").exists());
		assertFalse(new File(baseDir + File.separator+"WGE00000001"+File.separator+"00000007"+File.separator+"00000002.TIF").exists());
		
		String cleanUpDir = tempDir + "tmp"+ File.separator +"scanner1";
		try {
			FileUtils.moveFile(new File(tempDir + "tmp" + File.separator + "scanner1" + File.separator + "WGE00000001" + File.separator + "00000007" + File.separator + "00000001.TIF"), new File(baseDir + File.separator + "scanner1" + File.separator + "WGE00000001" + File.separator + "00000007" + File.separator + "00000001.TIF"));
			FileUtils.moveFile(new File(tempDir + "tmp" + File.separator + "scanner1" + File.separator + "WGE00000001" + File.separator + "00000007" + File.separator + "00000002.TIF"), new File(baseDir + File.separator + "scanner1" + File.separator + "WGE00000001" + File.separator + "00000007" + File.separator + "00000002.TIF"));
			
			assertFalse(new File(tempDir+"tmp"+File.separator+"scanner1"+File.separator+"WGE00000001"+File.separator+"00000007"+File.separator+"00000001.TIF").exists());
			assertTrue(new File(baseDir + File.separator+"scanner1"+File.separator+"WGE00000001"+File.separator+"00000007"+File.separator+"00000001.TIF").exists());
			assertFalse(new File(tempDir+"tmp"+File.separator+"scanner1"+File.separator+"WGE00000001"+File.separator+"00000007"+File.separator+"00000002.TIF").exists());
			assertTrue(new File(baseDir + File.separator+"scanner1"+File.separator+"WGE00000001"+File.separator+"00000007"+File.separator+"00000002.TIF").exists());
			
			FileUtils.deleteDirectory(new File(cleanUpDir));
			
			SkyLogger.getTestLogger().debug("Restored directory: "+baseDir);
			SkyLogger.getTestLogger().debug("Removed directory: "+cleanUpDir);
		} catch (IOException e) {
			fail("Unable to restore direcotry "+baseDir+" - cleaning up might be necessary.\nRemove "+tempDir+File.separator+"tmp and cleanup "+baseDir+"\n"+e.getMessage());
		}
	}
	
	@After
	@Before
	public void cleanUp() {
		try {
			FileUtils.deleteDirectory(new File(tempDir + "tmp"));
		} catch (IOException e) {
			fail("Exception: "+e.getMessage());
		}
	}

}
