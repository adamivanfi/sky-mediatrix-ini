package com.nttdata.de.ityx.sharedservices.utils;

import com.nttdata.de.lib.logging.SkyLogger;
import org.apache.commons.imaging.*;
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by meinusch on 20.07.15.
 */
public class NttFileUtils {


	public static File createDirIfNotExists(String dir) throws IOException {
		String fullpath=FilenameUtils.getFullPath(dir);
		File sdir=new File(fullpath);
		if (sdir!=null && !sdir.exists()){
			org.apache.commons.io.FileUtils.forceMkdir(sdir);
		}
		return sdir;
	}

	public static void moveFileToDir(String srcDir, String dstDir, String fName) throws IOException {
		String srcFile = srcDir + File.separator + fName;
		String dstFile = dstDir + File.separator + fName;
		moveFileToDir(srcFile, dstFile);
	}

	public static void moveFileToDir(String srcFile, String dstFile) throws IOException {
		SkyLogger.getItyxLogger().debug("moving File: " + srcFile);
		File src = new File(srcFile);
		moveFileToDir(src, dstFile);
	}

	public static void moveFilesToDir(List<File> srcFiles, String dstFile) throws IOException {
		for (File src :srcFiles) {
			moveFileToDir(src, dstFile);
		}
	}
	public static void moveFilesToDir(File[] srcFiles, String dstFile) throws IOException {
		for (File src :srcFiles) {
			moveFileToDir(src, dstFile);
		}
	}

	public static void moveFileToDir(File src, String dst) throws IOException {
		SkyLogger.getItyxLogger().debug("moving File: " + src.getAbsolutePath());

		File dstF= createDirIfNotExists(dst);
		String srcFileName= FilenameUtils.getName(src.getPath());

		Path movedFP = Files.move(Paths.get(src.getAbsolutePath()), Paths.get(dstF.getAbsolutePath() + File.separator+srcFileName), StandardCopyOption.REPLACE_EXISTING);
		//SkyLogger.getItyxLogger().error("moving File Exception: " + src.getAbsolutePath() + " to: "+dst, e);
		//moved = src.renameTo(dst);
	}

	public static File writeDownAsTiff(BufferedImage bi, File dstFile) throws IOException {
		createDirIfNotExists(dstFile.getAbsolutePath());
		Map<String, Object> params = new HashMap<>();
		params.put(ImagingConstants.PARAM_KEY_COMPRESSION, TiffConstants.TIFF_COMPRESSION_CCITT_GROUP_4);
		//params.put(ImagingConstants.PARAM_KEY_COMPRESSION, TiffConstants.TIFF_COMPRESSION_LZW);
		params.put(ImagingConstants.PARAM_KEY_FORMAT, ImageFormats.TIFF);
		params.put(ImagingConstants.PARAM_KEY_PIXEL_DENSITY, PixelDensity.createFromPixelsPerInch(300, 300));
		try{
			Imaging.writeImage(bi, dstFile, ImageFormats.TIFF, params);
		} catch (ImageWriteException ei) {
			throw new IOException(ei.getMessage(), ei.getCause());
		}
		return dstFile;
	}
	public static File writeDownAsTiff(String src, String dst) throws IOException, ImageReadException {
		return writeDownAsTiff(new File(src),new File(dst));
	}
	public static File writeDownAsTiff(File src, String dst) throws IOException, ImageReadException {
		return writeDownAsTiff(src,new File(dst));
	}

	public static File writeDownAsTiff(File src, File dst) throws IOException, ImageReadException {
		BufferedImage bi = Imaging.getBufferedImage(src);
		return writeDownAsTiff(bi,dst);
	}

	public static File writeDownAsTiff(BufferedImage bi, String dst) throws IOException, ImageReadException {
		return writeDownAsTiff(bi,new File(dst));
	}

	public static File writeTiffImageIo(BufferedImage bi, File dstfile)
			throws IOException {

		createDirIfNotExists(dstfile.getAbsolutePath());
		ImageIO.write(bi, "TIFF", dstfile);
		return dstfile;
	}
	public static String getFileExtension(File srcF){
		return FilenameUtils.getExtension(srcF.getAbsolutePath()).toLowerCase();
	}

	/**
	 * @param bytes
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static File writeFile(byte[] bytes, File file)
			throws IOException {

		createDirIfNotExists(file.getAbsolutePath());
		BufferedOutputStream fos = new BufferedOutputStream(
				new FileOutputStream(file));
		fos.write(bytes);
		fos.close();
		return file;
	}
	public static File writeFile(byte[] bytes, String filename)
			throws IOException {
		return writeFile(bytes, new File(filename));
	}


}
