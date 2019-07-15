package de.ityx.sky.outbound.extensions.template;

import com.nttdata.de.sky.archive.BaseUtils;
import com.nttdata.de.sky.pdf.ILoadTemplate;
import com.nttdata.de.sky.pdf.PdfFile;
import com.nttdata.de.sky.pdf.PdfTemplate;
import de.ityx.base.Global;
import de.ityx.mediatrix.data.Language;
import de.ityx.mediatrix.modules.businessrules.DefaultMediatrixExtension;
import de.ityx.mediatrix.modules.businessrules.MXExtension;
import de.ityx.mediatrix.modules.tools.logger.Log;
import de.ityx.sky.outbound.common.PdfUtils;
import de.ityx.sky.outbound.pdf.LetterMaker;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Properties;

/**
 * An extension to load the templates(server).
 */
@MXExtension(
        prefix = "template"
)
public class ServerTemplateExtension extends DefaultMediatrixExtension
        implements ILoadTemplate<PdfTemplate> {

    private static ServerTemplateExtension template ;


    /**
     * Singleton class therefore private constructor
     */

    public ServerTemplateExtension() {

    }

    public static  ServerTemplateExtension getInstance() {

        if (ServerTemplateExtension.template == null) {
            ServerTemplateExtension.template = new ServerTemplateExtension();
        }
        return ServerTemplateExtension.template;
    }

    @Override
    public ArrayList<PdfTemplate> loadTemplates(Connection con)
            throws Exception {
        return null;
    }

    @Override
    public PdfTemplate loadTemplate(int subproject, int language, Connection con)
            throws Exception {
        PdfTemplate subprojectTemplate = new PdfTemplate();

        try {
            File file = new File(Global.getProperty("sky.template.path", "")
                    + File.separator + subproject + "_"
                    + Language.getLanguageShort(language) + ".pdf");
            // File file = new File(Global.getProperty("sky.template.path", "")
            // + File.separator + "Template_" +
            // Language.getLanguageShort(language) + ".pdf");
            Log.message("using template: " + file.getAbsoluteFile());
            byte[] content = BaseUtils.readFile(file);
            subprojectTemplate.setPdf(content);
        }
        catch(Exception e) {
            Log.message("template not found: " + subproject + "_" + language);
            subprojectTemplate = PdfUtils.createEmptyTemplate();
        }
        try {
            File file = new File(Global.getProperty("sky.template.path", "")
                    + File.separator + subproject + "_"
                    + Language.getLanguageShort(language) + "_alternative.pdf");
            // File file = new File(Global.getProperty("sky.template.path", "")
            // + File.separator + "Template_" +
            // Language.getLanguageShort(language) + ".pdf");
            Log.message("using alternative template: " + file.getAbsoluteFile());
            byte[] content = BaseUtils.readFile(file);
            subprojectTemplate.setAlernativePdf(content);
        }
        catch(Exception e) {
            Log.message("alternative template not found: " + subproject + "_" + language);
        }
        subprojectTemplate.setSubproject(subproject);
        subprojectTemplate.setLanguage(language);
        subprojectTemplate.setProperties(new Properties());

        return subprojectTemplate;
    }

    public PdfTemplate loadTemplate(PdfFile pdf, Connection con) throws Exception {
        PdfTemplate subprojectTemplate = new PdfTemplate();
        int subproject = pdf.getSubproject();
        String country = (String) pdf.getParameter().get("CustomerCountry");
        country = country == null || country.trim().length() == 0 ? "de" : country;
        String sbsPrefix = pdf.isSbsProject() ? "Sbs" : "";
        if (country.equals("DEUTSCHLAND")) {
            country = "de";
        }
        else if (country.equals("ÖSTERREICH")) {
            country = "at";
        }
        try {
            File file = new File(Global.getProperty("sky.template.path", "") + File.separator + subproject + "_" + country + ".pdf");
            if (!file.exists()) {
                file = new File(Global.getProperty("sky.template.path", "") + File.separator + sbsPrefix + "Template" + "_" + country + ".pdf");
            }
            System.err.println("using template: " + file.getAbsoluteFile());
            byte[] content = BaseUtils.readFile(file);
            subprojectTemplate.setPdf(content);
        }
        catch(Exception e) {
            Log.message("template not found: " + subproject + "_" + country);
            subprojectTemplate = PdfUtils.createEmptyTemplate();
        }
        try {
            File file = new File(Global.getProperty("sky.template.path", "") + File.separator + subproject + "_" + country + "_alternative.pdf");
            if (!file.exists()) {
                file = new File(Global.getProperty("sky.template.path", "") + File.separator + sbsPrefix + "Template" + "_" + country + "_alternative.pdf");
            }
            System.err.println("using alternative template: " + file.getAbsoluteFile());
            byte[] content = BaseUtils.readFile(file);
            subprojectTemplate.setAlernativePdf(content);
        }
        catch(Exception e) {
            Log.message("alternative template not found: " + subproject + "_" + country);
        }
        subprojectTemplate.setSubproject(subproject);
        subprojectTemplate.setLanguage(pdf.getLanguage());
        subprojectTemplate.setProperties(new Properties());

        return subprojectTemplate;
    }

    @Override
    public byte[] createPdf(PdfFile pdf, Connection con) throws Exception {
        PdfTemplate template = null;
        if (pdf.getSubproject() != 0) {
            // template = loadTemplate(pdf.getSubproject(), pdf.getLanguage(), con);
            template = loadTemplate(pdf, con);
            return new LetterMaker().generateLetter(con, template, new Properties(), pdf.getBody(), pdf.getParameter());
        }

        return new byte[]{};
    }

    @Override
    public String getMethodPrefix() {
        return "template";
    }
}
