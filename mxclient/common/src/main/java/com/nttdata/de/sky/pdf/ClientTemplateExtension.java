package com.nttdata.de.sky.pdf;

import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.modules.businessrules.DefaultMediatrixClientExtension;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * An extension to load the templates(server).
 */
public class ClientTemplateExtension extends DefaultMediatrixClientExtension implements ILoadTemplate<PdfTemplate> {

    private static ClientTemplateExtension template = null;

    /**
     * Private constructor --> this class  singleton class takes the control of object creation
     */
    public ClientTemplateExtension() {
    }

    public static ClientTemplateExtension getInstance() {
        if (template == null) {
            template = new ClientTemplateExtension();
        }
        return template;
    }

    @Override
    public ArrayList<PdfTemplate> loadTemplates(Connection con) {
        return null;
    }

    public PdfTemplate loadTemplate(int subproject, int language) throws Exception {
        return loadTemplate(subproject, language, null);
    }

    @Override
    public PdfTemplate loadTemplate(int subproject, int language, Connection con) throws Exception {
        List<Object> list = new ArrayList<Object>();
        list.add(subproject);
        list.add(language);
        return (PdfTemplate) API.getClientAPI().getConnectionAPI().exchange("template_loadTemplate", "template_loadTemplate", 0, list);
    }

    @Override
    public byte[] createPdf(PdfFile pdffile, Connection con) throws Exception {
        byte[] pdf = (byte[]) API.getClientAPI().getConnectionAPI().exchange("template_createPdf", "template_createPdf", 0, pdffile);
        if (pdf == null) {
            throw new Exception("The pdf file wasn't created!!");
        }
        return pdf;
    }

}
