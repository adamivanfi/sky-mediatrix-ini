package com.nttdata.de.sky.pdf;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * loads or creates the pdf templates.
 *
 * @param <T>
 */
public interface ILoadTemplate<T> {
    /**
     * loads the pdf templates.
     * 
     * not in use because the pdf template is generated in the server.
     * 
     * @param con
     * @return
     * @throws Exception
     */
    public ArrayList<T> loadTemplates(Connection con) throws Exception;

    /**
     * load the pdf template.
     * 
     * @param subproject
     * @param language
     * @param con
     * @return
     * @throws Exception
     */
    public T loadTemplate(int subproject, int language, Connection con) throws Exception;

    /**
     * creates the pdf. In the current version the pdf is created in the server and transfers to the client.
     * 
     * @param pdf
     * @param con
     * @return
     * @throws Exception
     */
    public byte[] createPdf(PdfFile pdf, Connection con) throws Exception;
    
}
