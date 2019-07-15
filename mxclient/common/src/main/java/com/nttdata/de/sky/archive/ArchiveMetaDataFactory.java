/**
 * 
 */
package com.nttdata.de.sky.archive;

import com.nttdata.de.lib.logging.SkyLogger;

import java.sql.Connection;
import java.util.Map;

/**
 * Factory class
 * 
 * @author DHIFLM
 * 
 */
public class ArchiveMetaDataFactory {

	public static synchronized AbstractArchiveMetaData getInstance(String className) {
		AbstractArchiveMetaData ret=null;
		try {
			ret = (AbstractArchiveMetaData) Class.forName(className).newInstance();
			return ret;
		} catch (Exception e) {
                     String logPrefix = AbstractArchiveMetaData.class.getName() + "#" + new Object() { }.getClass().getEnclosingMethod().getName();
                     SkyLogger.getMediatrixLogger().error(logPrefix + "ArchiveMetaDataFactory cannot instantiate:"+className);
         
		    e.printStackTrace();
                    try{
			ret = new AbstractArchiveMetaData()  {
				@Override
                                public Map<String, String> collectMetadata(Connection con, Object source, Map<String, String> srcMap)  throws ClassCastException,NoSuchMethodException{
                                	throw new NoSuchMethodException("Abstract method is not implemented.");
				}
			};
                        } catch (Exception ex) {
                            SkyLogger.getMediatrixLogger().error(logPrefix + "ArchiveMetaDataFactory cannot create AnonymousClass after instantiate of:"+className,ex);
                        }
		}
		return ret;
	}
}
