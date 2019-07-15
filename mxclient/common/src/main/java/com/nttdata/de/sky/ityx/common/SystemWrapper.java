package com.nttdata.de.sky.ityx.common;

import java.io.*;

public class SystemWrapper {
    private static String forceDir(String property, String appName, String subdir) {
        String appHome = System.getProperty(property);
        if (!"".equals(appHome)) {
            appHome += "\\." + appName;
            if (subdir != null && !subdir.equals("")) {
                appHome += "\\" + subdir;
            }
            File appDir = new File(appHome);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
        }

        return appHome;
    }



    public static String getUserAppDir(String appName, String subdir) {
        return forceDir("user.home", appName, subdir);
    }

    public static String getUserAppDir(String appName) {
        return forceDir("user.home", appName, null);
    }

    public static String getTempDir(String appName, String subdir) {
        return forceDir("java.io.tmpdir", appName, subdir);
    }



	/**
	 * @param bytes
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeFile(byte[] bytes, File filename)
			throws IOException {
		BufferedOutputStream fos = new BufferedOutputStream(
				new FileOutputStream(filename));
		fos.write(bytes);
		fos.close();
	}


}
