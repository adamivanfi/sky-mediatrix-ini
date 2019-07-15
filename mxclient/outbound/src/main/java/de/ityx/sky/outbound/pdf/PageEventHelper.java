package de.ityx.sky.outbound.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.nttdata.de.sky.archive.CustomLine;
import de.ityx.mediatrix.modules.tools.logger.Log;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Properties;

public class PageEventHelper extends PdfPageEventHelper {

    private static final int                    HEAD_UP          = 720;

    private static final float                  HEAD_LEFT        = 360;

    /** The template for the letter. */
    protected PdfImportedPage                   paper;

    protected PdfImportedPage                   alternativePaper = null;

    /** The layer to which the template will be added. */
    protected PdfLayer                          not_printed;

    private final Properties                    prop;

    private final BaseFont                      bf;

    private final BaseFont                      bd;
    private final BaseFont                      wd;

    private final Connection                    con;

    private final Properties                    variables;

    private final BaseFont                      bi;

    private int                                 page             = 0;

    private final com.nttdata.de.sky.pdf.PdfTemplate template;

    protected com.itextpdf.text.pdf.PdfTemplate total;

    protected PdfContentByte                    cb;

    private final HashMap<String, Object>       parameter;

    private BaseFont                            arialFont;

    public PageEventHelper(com.nttdata.de.sky.pdf.PdfTemplate pdft, BaseFont bd, BaseFont bf,
            BaseFont bi, BaseFont wd, Connection con, Properties variables,
            HashMap<String, Object> parameter, BaseFont arialFont) {
        this.template = pdft;
        this.parameter = parameter;
        this.prop = pdft.getProperties();
        this.bf = bf;
        this.bd = bd;
        this.bi = bi;
        this.wd = wd;
        this.con = con;
        this.variables = variables;
        this.arialFont = arialFont;
    }

    /*
     * @see com.lowagie.text.pdf.PdfPageEvent#onOpenDocument(com.lowagie.text.pdf.PdfWriter,
     *      com.lowagie.text.Document)
     */
    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        Log.message("onOpenDocument");
        try {
            PdfReader reader = new PdfReader(this.template.getPdf());// "/econophone.pdf"));
            this.paper = writer.getImportedPage(reader, 1);
            if (this.template.getAlernativePdf() != null) {
                PdfReader alternativeReader = new PdfReader(this.template.getAlernativePdf());// "/econophone.pdf"));
                this.alternativePaper = writer.getImportedPage(alternativeReader, 1);
            }
            this.not_printed = new PdfLayer("template", writer);
            this.not_printed.setOnPanel(false);
            this.not_printed.setPrint("Print", "true"
                    .equalsIgnoreCase(this.prop.getProperty(
                            "templatepdf.print", "true")));
            this.cb = writer.getDirectContent();

            this.total = cb.createTemplate(100, 100);
            this.total.setBoundingBox(new Rectangle(-20, -20, 200, 200));

        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }

    /*
     * @see com.lowagie.text.pdf.PdfPageEvent#onStartPage(com.lowagie.text.pdf.PdfWriter,
     *      com.lowagie.text.Document)
     */
    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        if (page == 0) {
            Log.message("onStartPage");
            PdfContentByte cb = writer.getDirectContent();
            cb.beginLayer(this.not_printed);
            cb.addTemplate(this.paper, 0, 0);
            cb.endLayer();
            // Fusszeile auf JEDER Seite:
            try {
                PdfContentByte imp = writer.getDirectContent();
                imp.beginText();
                String footerfirst = "footer";
                if (this.prop.getProperty("footer.first", "false").equals(
                        "true")) {
                    footerfirst = "footer.1st";
                }
                if (this.prop.getProperty("footer.oneverypage", "false")
                        .equals("true")) {
                    LetterMaker.printBlock(this.bd, this.bf, this.bi, this.wd,
                            this.con, this.template, this.prop, imp,
                            page == 1 ? footerfirst : "footer", this.variables);
                }
                imp.endText();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        else {
            if (alternativePaper != null) {
                Log.message("other pages");
                PdfContentByte cb = writer.getDirectContent();
                cb.beginLayer(this.not_printed);
                cb.addTemplate(this.alternativePaper, 0, 0);
                cb.endLayer();
                // Fusszeile auf JEDER Seite:
                try {
                    PdfContentByte imp = writer.getDirectContent();
                    imp.beginText();
                    String footerfirst = "footer";
                    if (this.prop.getProperty("footer.first.2", "false").equals(
                            "true")) {
                        footerfirst = "footer.1st";
                    }
                    if (this.prop.getProperty("footer.oneverypage.2", "false")
                            .equals("true")) {
                        LetterMaker.printBlock(this.bd, this.bf, this.bi, this.wd,
                                this.con, this.template, this.prop, imp,
                                page == 1 ? footerfirst : "footer", this.variables);
                    }
                    imp.endText();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ++page;
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        CustomLine cl = (CustomLine) parameter.get(CustomLine.class.getName());
        cl.setTotalPages((page - 1) + "");
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte imp = writer.getDirectContent();
        imp.saveState();
        imp.beginText();
        imp.setTextMatrix(5.669291304f, 827.826771632f); // 2mm,5mm from left
                                                         // upper corner at
                                                         // 72 DPI
        CustomLine cl = (CustomLine) parameter.get(CustomLine.class.getName());
        cl.setPageNumber(writer.getPageNumber() + "");
        imp.setFontAndSize(arialFont, 5f);
        imp.setColorFill(BaseColor.WHITE);
        imp.showText(cl.toString());
        imp.setColorFill(BaseColor.BLACK);
        imp.endText();
        imp.restoreState();

        if (document.getPageNumber() > 1) {
            String customerid = (String) parameter.get("CustomerID");
            if (customerid != null) {
                imp = writer.getDirectContent();
                imp.saveState();
                imp.beginText();
                imp.setTextMatrix(HEAD_LEFT, HEAD_UP);
                imp.setFontAndSize(bf, 11f);
                imp.setColorFill(BaseColor.BLACK);
                if(template.getSubproject()== LetterMaker.SUBPROJ_SU_RA_KORR){
                    imp.showText("Kundennummer: " + customerid);
                }else{
                    imp.showText("Ihre Kundennummer: " + customerid);
                }

                //final String customerText = ((template.getSubproject()== LetterMaker.SUBPROJ_SU_RA_KORR) ? "Kundennummer: " : "Ihre Kundennummer: ") + customerid;
                //imp.showText(customerText);
                imp.endText();
                imp.restoreState();
            }
        }

        imp = writer.getDirectContent();
        imp.saveState();
        imp.beginText();
        imp.setTextMatrix(566.651543308f, 14.173228346f);
        imp.setFontAndSize(arialFont, 5f);
        imp.setColorFill(BaseColor.BLACK);
        imp.showText("Seite " + cl.getPageNumber());
        imp.endText();
        imp.restoreState();
    }
}
