package de.ityx.sky.outbound.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;

import com.itextpdf.text.pdf.*;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Attachment;
import de.ityx.mediatrix.modules.tools.logger.Log;
import de.ityx.mediatrix.util.EmbeddedImageManager;
import de.ityx.sky.outbound.data.Messages;
import de.ityx.utils.HTMLConverter;
import de.ityx.utils.HTMLDetection;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *     This class implements the HTML --> PDF conversion for all PDF formats (Sky/DE, Sky/AT, Sbs/DE, Sbs/AT, RA_KORR)
 *   for "Neues Anschreiben" as for reply as well.
 *     The class uses the obsolete technology "HTMLWorker" that cannot convert <ul><li></li>...<li></li></ul> and
 *   <ol><li></li>...<li></li></ol> tags properly: it loses the bullet points (Aufzählungszeichen). XMLWorker can cope
 *   with the task, but it also has a strongly different font management, that would need a redesign for the class.
 *
 *      Logging level for "Log.logdebug()", Log.loginfo()... etc. is set in ...\conf_winservice\
 *    environment_integration.inc.conf:  set LOG4JLEVEL={DEBUG,INFO,WARN,ERROR,FATAL}
 *    This writes and influences the log files:
 *      - \\MTX\mediatrix\ityxappserver_mtrix.log
 *      - \\MTX\mediatrix\ityxappserver_service.log
 *      - \\MTX\mediatrix\servlet.log
 *
 *    Log mechanism:
 *      conf/jog4j_sky.xml (MtrixAppender/Threshold) ->
 *          service/ityxcommon.inc.conf ({de.ntt.sky.loglevel}) ->
 *              conf_winservice/environment_integration.inc.conf (LOG4JLEVEL) ....[environment_production.inc.conf]
 *
 */
public class LetterMaker {
    private static final float                   LEADING            = 13f;

    EmbeddedImageManager                         mngr               = new EmbeddedImageManager();

    private static float                         LEFT_MARGIN_LETTER = 84.96f;

    private static boolean                       init               = false;

    private static Hashtable<String, File>       fontTemp           = new Hashtable<String, File>();

    HashMap<String, byte[]>                      imageList          = null;

    private static BaseFont                      bf;

    private static final HashMap<String, Object> PROVIDERS          = new HashMap<String, Object>();

    private static final Integer                 HEAD_LEFT          = 450;

    private static final Float                   DELTA              = 12.5f;

    /**
     *   message.properties - being read by "Messages" - eliminates the scope problem raising at System.getProperty for
     *   this module. SU_RA-Korr Subproject-ID for INT and PROD needs to be the same!
     */
    public static final Integer                 SUBPROJ_SU_RA_KORR = Integer.parseInt(Messages.getString("su_ra_korr.subproject"));

    static public class PdfPTableEvents implements PdfPTableEvent {

        @Override
        public void tableLayout(PdfPTable table, float[][] width,
                float[] height, int headerRows, int rowStart,
                PdfContentByte[] canvas) {
            // widths of the different cells of the first row
            float widths[] = width[0];

            PdfContentByte cb = canvas[PdfPTable.TEXTCANVAS];
            cb.saveState();
            // border for the complete table
            cb.setLineWidth(2);
            cb.setRGBColorStroke(0, 0, 0);
            cb.rectangle(widths[0], height[height.length - 1],
                    widths[widths.length - 1] - widths[0], height[0]
                            - height[height.length - 1]);
            cb.stroke();
            cb.restoreState();
        }
    }

    public static class MyFontFactory implements FontProvider {
        @Override
        public Font getFont(String fontname, String encoding, boolean embedded,
                float size, int style, BaseColor color) {
            return new Font(LetterMaker.bf, 11f, style, color);
        }

        @Override
        public boolean isRegistered(String fontname) {
            return false;
        }
    }

    protected void init() {
        if (!LetterMaker.init) {
            try {
                LetterMaker.bf = BaseFont.createFont("SkyText-Regular.otf",
                        BaseFont.CP1252, BaseFont.EMBEDDED);
                LetterMaker.PROVIDERS.put(HTMLWorker.FONT_PROVIDER,
                        new MyFontFactory());

                initFont("arial", "Arial");
                initFont("arialbd", "ArialBold");
                initFont("ariali", "ArialItalic");
                initFont("arialbi", "ArialItalicBold");
                initFont("verdana", "Verdana");
                initFont("verdanai", "VerdanaItalic");
                initFont("verdanabd", "VerdanaBold");
                initFont("winding", "WinDings");
                LetterMaker.init = true;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initFont(String string, String name) throws IOException {
        File fontTem = File.createTempFile(string, ".ttf");
        try {
            fontTem.deleteOnExit();
            FileOutputStream fo = new FileOutputStream(fontTem);
            InputStream in = getClass().getResourceAsStream(
                    "/" + string + ".ttf");
            Log.message(string + ": " + fontTem.getAbsolutePath() + " / " + in);
            byte[] data = new byte[64000];
            int len = in.read(data);
            while (len > 0) {
                fo.write(data, 0, len);
                len = in.read(data);
            }
            in.close();
            fo.close();
            FontFactory.register(fontTem.getAbsolutePath(), name);

        } catch(IOException e) {
            e.printStackTrace();
        }
        LetterMaker.fontTemp.put(string, fontTem);
    }

    public Paragraph doFormats(BaseFont bf, BaseFont bbf, String input,
                               float fontSize) throws Exception {
        Paragraph p = new Paragraph(LEADING);

        List<Element> elements = HTMLWorker.parseToList(
                new StringReader(input), null, LetterMaker.PROVIDERS);
        for (Element element : elements) {
            p.add(element);
        }

        return p;
    }

    public Paragraph getBold(BaseFont bf, BaseFont bbf, Chunk pre,
            String input, float fontSize) throws Exception {
        Paragraph p = new Paragraph(pre);
        Font std = new Font(bf, fontSize);
        Font bld = new Font(bbf, fontSize);// , Font.BOLD);
        Pattern bold = Pattern.compile("<b>([^<]*)</b>");
        Matcher m = bold.matcher(input);
        int pos = 0;
        while (m.find(pos)) {

            p.add(new Phrase(input.substring(pos, m.start()), std));

            p.add(new Phrase(m.group(1), bld));
            pos = m.end();
        }
        if (pos < input.length()) {

            p.add(new Phrase(11, input.substring(pos), std));
        }
        return p;
    }

    /**
     * generates the pdf for the letter.
     * 
     * @param con
     * @param template
     * @param variables
     * @param body
     * @return
     * @throws Exception
     */
    public byte[] generateLetter(Connection con, com.nttdata.de.sky.pdf.PdfTemplate template,
            Properties variables, String body, HashMap<String, Object> parameter)
            throws Exception {
        // Generates the document to get the pages count.
        // The pages count(totalPages) is used to generate the white line for
        // the letter shop(read 4.6.1.2).
        // createDocument(con, template, variables, body, parameter);
        // creates the document and inserts the white space.
        return createDocument(con, template, variables, body, parameter);
    }

    @SuppressWarnings("unchecked")
    private byte[] createDocument(Connection con, com.nttdata.de.sky.pdf.PdfTemplate template,
            Properties variables, String body, HashMap<String, Object> parameter)
            throws Exception {

        Log.loginfo("createDocument(...) entered");
        //Log.message("createDocument(...) entered");


        if (variables == null) {
            variables = new Properties();
        }

        init();

        // Change by Ivanfi NTT-Data, I-211451:
        // System.out.println("LetterMaker.createDocument() has been entered.");

        // Vitalij: Der Text kann Tabulatoren (\t) enthalten. Deshalb werden
        // Tabulatoren durch Leerzeichen ersetzt.
        // Die Funktion "replaceTab" rechnet aus, wieviel Leerzeichen eingefügt
        // werden müssen
        if (!HTMLDetection.isHTML(body)) {
            body = replaceTab(body);
        }

        body = HTMLConverter.getBodyContent(body);
        mngr.parseImgTags(body);

        List<Attachment> attList = new ArrayList<Attachment>();
        List<Attachment> helpAttachmentList = null;

        // Zufka: zuerstmal alle vorhanden Attachments sammeln
        if (parameter.containsKey("embedded_att")) {
            helpAttachmentList = (List<Attachment>) parameter
                    .get("embedded_att");
        }
        else {
            helpAttachmentList = new ArrayList<Attachment>();
            List<Integer> attIds = (List<Integer>) parameter
                    .get("embedded_att_ids");
            if (attIds != null) {
                for (int id : attIds) {
                    Attachment att = API.getServerAPI().getAttachmentAPI()
                            .load(con, id);
                    helpAttachmentList.add(att);
                }
            }
        }

        // Zufka. dann nur die nutzen, deren Name im Text vorkommt und als
        // EmbeddedImage gekennzeichnet sind.
        for (Attachment att : helpAttachmentList) {
            StringBuilder sb = new StringBuilder();
            if (att.getFilename().startsWith(EmbeddedImageManager.PREFIX)
                    && mngr.checkPathInImgSet(att.getFilename(), sb)) {
                if (att.getBuffer() == null) {
                    Attachment temp_att = API.getServerAPI().getAttachmentAPI().load(con, att.getId());
                    if (temp_att != null && temp_att.hasFullBuffer()) {
                        att.setBuffer(temp_att.getBuffer());
                    }
                }
                Attachment tempAtt = new Attachment();
                tempAtt.copy(att);
                tempAtt.setFilename(sb.toString());

                attList.add(tempAtt);
            }
        }
        mngr.addBuffer(attList);
        body = mngr.checkEmbeddedFiles(body, true, false);

        Properties prop = template.getProperties();

        float LEFT_MARGIN = LetterMaker.LEFT_MARGIN_LETTER;
        // Float.parseFloat(prop.getProperty("margin.left",
        // "65.5f"));
        float RIGHT_MARGIN = Float.parseFloat(prop.getProperty(
                "margin.right", "60.0f"));
        float TOP_MARGIN = Float.parseFloat(prop.getProperty(
                "margin.top", "140.0f"));
        // Change by Ivanfi, Incident #183638 - #235775 (02.12.2016):
        float BOTTOM_MARGIN = Float.parseFloat(prop.getProperty(
                "margin.bottom", "100f"));

        Document fax = new Document();
        fax.addTitle("Brief an " + variables.getProperty("[Betreff]"));
        fax.addAuthor("Mediatrix CCS");
        // Put into comment by Ivanfa (double insertion):
        //fax.addTitle("Brief an " + variables.getProperty("[Betreff]"));
        fax.addCreator("Mediatrix CCS");

        fax.setMargins(LEFT_MARGIN, RIGHT_MARGIN, TOP_MARGIN, BOTTOM_MARGIN);

        BaseFont bi = BaseFont.createFont(
                LetterMaker.fontTemp
                        .get(prop.getProperty("font", "verdana").toLowerCase()
                                + "i").getAbsolutePath(), BaseFont.CP1252,
                BaseFont.EMBEDDED);
        // BaseFont bd = BaseFont.createFont(
        // LetterMaker.fontTemp.get(
        // prop.getProperty("font", "verdana").toLowerCase()
        // + "bd").getAbsolutePath(), BaseFont.CP1252,
        // BaseFont.EMBEDDED);
        BaseFont win = BaseFont.createFont(LetterMaker.fontTemp.get("winding")
                .getAbsolutePath(), BaseFont.CP1252, BaseFont.EMBEDDED);
        BaseFont bd = BaseFont.createFont("SkyText-Bold2.otf", BaseFont.CP1252,
                BaseFont.EMBEDDED);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter faxw = PdfWriter.getInstance(fax, out);
        BaseFont arialFont = BaseFont.createFont(
                LetterMaker.fontTemp.get("arial").getAbsolutePath(),
                BaseFont.CP1252, BaseFont.EMBEDDED);
        PageEventHelper pageEventHelper = new PageEventHelper(template, bd,
                LetterMaker.bf, bi, win, con, variables, parameter, arialFont);
        faxw.setPageEvent(pageEventHelper);

        faxw.open();
        fax.open();

        // Ivanfa HTML --> PDF Konversion:
        //Log.message("LetterMaker.createDocument()/adding-image-to-pdf");
        // wd008958.pfad.biz gets connection time out for the request (in browser as well):
        //Image img = Image.getInstance("http://www.sky.de/web/redaktion/static/nl/sky_logo.png");
        // file not found:
        //Image img = Image.getInstance("file:///c:/tmp/img/sky_logo.png");
        // Working code:
        //Image img = Image.getInstance("\\\\wd008958\\mediatrix_testdata\\img\\sky_logo.png");
        //fax.add(img);
        //Log.loginfo("LetterMaker.createDocument()/image-added-successfully");

        PdfContentByte imp = faxw.getDirectContent();

        imp.beginText();
        initHeader(template, LetterMaker.bf, bd, imp, parameter);
        //
        // Zeilen fuer Adressblock per Konfiguration holen und mit aufgeloesten
        // MX
        // ausgeben.
        //
        LetterMaker.printBlock(bd, LetterMaker.bf, bi, win, con, template,
                prop, imp, "address", variables);
        LetterMaker.printBlock(bd, LetterMaker.bf, bi, win, con, template,
                prop, imp, "info", variables);
        LetterMaker.printBlock(bd, LetterMaker.bf, bi, win, con, template,
                prop, imp, "feedback", variables);
        LetterMaker.printBlock(bd, LetterMaker.bf, bi, win, con, template,
                prop, imp, "subject", variables);
        if (prop.getProperty("footer.oneverypage", "false").equals("false")) {
            LetterMaker.printBlock(bd, LetterMaker.bf, bi, win, con, template,
                    prop, imp, "footer", variables);
        }

        imp.endText();
        Font wd = new Font(win, 11);
        float fontSizeEmpty = Float.parseFloat(prop.getProperty(
                "firstpage.emptysize", "18"));
        int empty = (int) Float.parseFloat(prop.getProperty(
                "firstpage.emptylines", "6"));
        String emptyString = "";

        for (int i = 0; i < empty; ++i) {
            emptyString += "\n";
        }
        fax.add(new Paragraph(new Phrase(emptyString, new Font(bd,
                fontSizeEmpty))));

        fillContent(body, fax, LetterMaker.bf, bd, wd, prop);

        fax.close();

        return out.toByteArray();
    }

    /**
     * Creates the header informations
     * 
     * @param bf
     * @param imp
     */
    private void initHeader(com.nttdata.de.sky.pdf.PdfTemplate template, BaseFont bf, BaseFont bd,
            PdfContentByte imp, HashMap<String, Object> parameter)
            throws Exception {
        DateFormat dd = new SimpleDateFormat("dd. MMMMM yyyy");
        String customerid = (String) parameter.get("CustomerID");
        System.err.println("CustomerID = " + customerid!=null?customerid:"");
        float start = 680;
        imp.setFontAndSize(bf, 11);
        // we draw some text on a certain position
        imp.setTextMatrix(LetterMaker.LEFT_MARGIN_LETTER, start);
        // imp.showText(customerid);
        start -= DELTA;
        imp.setTextMatrix(LetterMaker.LEFT_MARGIN_LETTER, start);
        Object firstName = parameter.get("CustomerFirstName");
        Object lastName = parameter.get("CustomerLastName");
        Object copmanyName = parameter.get("SbsCompany");

        imp.showText((firstName!=null?firstName.toString():"") + " " + (lastName!=null?lastName.toString():""));
        System.err.println("CustomerFirstName = " + firstName
                + ", CustomerLastName = " + lastName
                + ", SbsCompany = " + copmanyName
                + ", PDF-SubprojectID = " + template.getSubproject());
        if(copmanyName!=null) {
            start -= DELTA;
            imp.setTextMatrix(LetterMaker.LEFT_MARGIN_LETTER, start);
            imp.showText(copmanyName.toString());
        }
        Object additionalAddress = parameter.get("CustomerAdditionalAddress");
        if(additionalAddress!=null && template.getSubproject()== SUBPROJ_SU_RA_KORR) {
            start -= DELTA;
            imp.setTextMatrix(LetterMaker.LEFT_MARGIN_LETTER, start);
            imp.showText(additionalAddress.toString());
        }
        start -= DELTA;
        imp.setTextMatrix(LetterMaker.LEFT_MARGIN_LETTER, start);
        Object param = parameter.get("CustomerStreet");
        imp.showText(param != null ? param.toString() : "");
        System.err.println("CustomerStreet = " + param);
        start -= DELTA;
        imp.setTextMatrix(LetterMaker.LEFT_MARGIN_LETTER, start);
        Object zipCode = parameter.get("CustomerZipCode");
        Object city = parameter.get("CustomerCity");
        imp.showText((zipCode!=null?zipCode.toString():"") + " " + (city!=null?city.toString():""));
        System.err.println("CustomerZipCode = " + zipCode + ", CustomerCity = "
                + city);
        start -= 2 * DELTA;
        imp.setTextMatrix(LetterMaker.LEFT_MARGIN_LETTER, start);
        param = parameter.get("CustomerCountry");
        System.err.println("CustomerCountry = " + param);
        if (param != null) {
            String country = param.toString();
            if (country.equals("DE")) {
                country = "DEUTSCHLAND";
            }
            else if (country.equals("AT")) {
                country = "\u00D6STERREICH";
            }
            imp.showText(country);
        }

        if (customerid!=null) {
            String kdnrText = template.getSubproject()== SUBPROJ_SU_RA_KORR ? "Kundennummer: " : "Ihre Kundennummer: ";
            start = 700 - DELTA;
            drawKontaktLine(kdnrText, customerid, bf, bd, imp, start, HEAD_LEFT);
            start -= DELTA;
        }
        String loginname = (String) parameter.get("loginname");
//        drawKontaktLine("Unser Zeichen: ", loginname != null ? loginname : "",
//                bf, bd, imp, start, HEAD_LEFT);
        start -= DELTA;
        
        // drawKontaktLine("Unsere Telefonnummer: ", "0180 5 11 00 00", bf, imp,
        // start, HEAD_LEFT);
        start -= 3.25 * DELTA;
        drawKontaktLine("", dd.format(new java.util.Date()), bf, bd, imp,
                start, HEAD_LEFT - 60);
    }

    private void drawKontaktLine(String a, String b, BaseFont bf, BaseFont bd,
            PdfContentByte imp, float start, int HEAD_LEFT) {
        imp.setFontAndSize(bf, 11);
        imp.setTextMatrix(HEAD_LEFT, start);
        imp.showText(b);
        imp.setFontAndSize(bd, 11);
        imp.setTextMatrix(HEAD_LEFT - 110, start);
        imp.showText(a);
    }

    public static void printBlock(BaseFont bd, BaseFont bf, BaseFont bi,
            BaseFont wd, Connection con, com.nttdata.de.sky.pdf.PdfTemplate template, Properties prop,
            PdfContentByte imp, String block, Properties variables) {
        float left = LetterMaker.LEFT_MARGIN_LETTER;
        // Float.parseFloat(prop
        // .getProperty(block + ".left", "65.5f"));
        float start = Float.parseFloat(prop.getProperty(block + ".top", "693"));
        float deltaY = Float.parseFloat(prop.getProperty(block + ".deltaY",
                "15"));
        float deltaX = Float.parseFloat(prop
                .getProperty(block + ".deltaX", "0"));
        int advance = (int) Float.parseFloat(prop.getProperty(block
                + ".advance", "1"));
        boolean bold = prop.getProperty(block + ".bold", "false")
                .equals("true");
        float fontsize = Float.parseFloat(prop.getProperty(block + ".fontsize",
                "11"));
        String[] adrlines = LetterMaker.getBlockLines(con, template, block,
                prop);
        int i = 0;
        float l = left;
        Pattern p = Pattern.compile("(:\\+([0-9]*(\\.[0-9])?))?(:B)?(:I)?:");
        Pattern pc = Pattern.compile(":(#[0-9a-fA-F]{6}):");
        for (String line : adrlines) {
            imp.setFontAndSize(bold || line.startsWith("BB:") ? bd : bf,
                    fontsize);
            Log.message("Write " + line + " at (" + l + "/" + start + ")");
            float rl = l;
            imp.setTextMatrix(rl, start);
            String[] words = line.split(" ");
            for (int wi = 0; wi < words.length; ++wi) {
                float plus = 0;
                boolean bbold = bold || line.startsWith("BB:");
                boolean italic = false;
                Matcher m = p.matcher(words[wi]);
                if (m.find()) {
                    if (m.group(2) != null || m.group(4) != null
                            || m.group(5) != null) {
                        words[wi] = words[wi].substring(1);
                    }
                    if (m.group(2) != null) {
                        plus = Float.parseFloat(m.group(2));
                        words[wi] = words[wi]
                                .substring(m.group(2).length() + 2);
                    }
                    if (m.group(4) != null) {
                        bbold = true;
                        words[wi] = words[wi].substring(2);
                    }
                    if (m.group(5) != null) {
                        italic = true;
                        words[wi] = words[wi].substring(2);
                        Log.message("Italic: " + words[wi]);
                    }
                }
                if (words[wi].trim().equals("*")) {
                    imp.setFontAndSize(wd, fontsize + plus - 1);
                    String tx = " " + (char) 0x6c;
                    float width = imp.getEffectiveStringWidth(" " + tx, false);
                    imp.showText(tx);
                    rl += width;
                }
                else if (words[wi].trim().startsWith(":#")) {
                    Matcher m2 = pc.matcher(words[wi]);
                    if (m2.find()) {
                        words[wi] = words[wi].substring(9);
                        Color c = Color.decode(m2.group(1));
                        imp.setColorFill(new BaseColor(c.getRed(),
                                c.getGreen(), c.getBlue(), c.getAlpha()));
                    }
                    imp.setFontAndSize(bbold ? bd : bf, fontsize + plus);
                    if (italic) {
                        imp.setFontAndSize(bi, fontsize + plus);
                    }
                    String tx = (wi == 0 ? "" : " ")
                            + LetterMaker.replaceMX(words[wi], variables);
                    float width = imp.getEffectiveStringWidth(tx, false);
                    imp.showText(tx);
                    imp.setColorFill(BaseColor.BLACK);
                    rl += width;
                }
                else {
                    imp.setFontAndSize(bbold ? bd : bf, fontsize + plus);
                    if (italic) {
                        imp.setFontAndSize(bi, fontsize + plus);
                    }
                    String tx = (wi == 0 ? "" : " ")
                            + LetterMaker.replaceMX(words[wi], variables);
                    float width = imp.getEffectiveStringWidth(tx, false);
                    Log.message("Write Text: " + tx);
                    imp.showText(tx);
                    rl += width;
                }
                imp.setTextMatrix(rl, start);
            }
            l += deltaX;
            if (++i % advance == 0) {
                start -= deltaY;
                l = left;
            }
        }
    }

    private static String replaceMX(String line, Properties variables) {
        Enumeration<?> ni = variables.propertyNames();
        while (ni.hasMoreElements()) {
            String key = (String) ni.nextElement();
            String reg = "(?i)"
                    + key.replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]");
            Log.message("Replace: " + reg + " with "
                    + variables.getProperty(key, key));
            Log.message("Line-IN:  " + line);
            line = line.replaceAll(reg, variables.getProperty(key, key));
            Log.message("Line-OUT: " + line);
        }
        return line;
    }

    private static String[] getBlockLines(Connection con, com.nttdata.de.sky.pdf.PdfTemplate template,
            String block, Properties prop) {
        return prop.getProperty(block + ".text", "").split("\n|\r\n");
    }

    private void fillContent(String body, PdfPCell fax, BaseFont bf,
            BaseFont bd, Font wd, Properties prop) throws Exception,
            DocumentException {
        // BaseFont bf = BaseFont.createFont(fontTemp.getAbsolutePath(),
        // BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        float fontSize = Float.parseFloat(prop.getProperty("fontsize", "11"));

        String[] ps = body.split("\r?\n\r?\n");
        for (int i = 0; i < ps.length; ++i) {
            Log.message("CELL: --- " + i + " ---\n" + ps[i] + "\n --- ");
            Pattern pat = Pattern.compile("#CHECK\\s(.*)");
            Matcher m = pat.matcher(ps[i]);
            if (m.find()) {
                Log.message("##> Checkbox in CELL");
                Chunk pr = new Chunk("" + (char) 0x6f, wd);
                fax.addElement(getBold(bf, bd, pr, " " + m.group(1), fontSize));
            }
            else {
                Paragraph p = doFormats(bf, bd, "\n" + ps[i], fontSize);
                p.setKeepTogether(true);
                fax.addElement(p);
            }
        }
    }

    /**
     * #WHITELINE The text font color is set to white. The text is visible, if
     * it is selected. #ENDWHITELINE
     * 
     * @param body
     * @param fax
     * @param bf
     * @param bbf
     * @param wd
     * @param prop
     * @throws Exception
     * @throws DocumentException
     */
    private void fillContent(String body, ElementListener fax, BaseFont bf,
            BaseFont bbf, Font wd, Properties prop) throws Exception,
            DocumentException {

        Log.loginfo("fillContent(...) entered");
        Log.message("fillContent(...) entered");

        Float fontSize = Float.parseFloat(prop.getProperty("fontsize", "11"));
        Font bld = new Font(bbf, fontSize);// , Font.BOLD);

        /* Ivanfi, NTT-Data 2017, I-211451. ---- BEGIN */
            // System property "line.separator" doesn't split HTML file properly, that's why it has been deactivated:
            //final String LINE_SEP = System.getProperty("line.separator"); // JIRA, MX-60
            final String LINE_SEP = "(\r\n)|(\n\r)|\r|\n";

            final Pattern listUlBegin = Pattern.compile("<ul>");
            final Pattern listUlEnd = Pattern.compile("</ul>");
            final Pattern listOlBegin = Pattern.compile("<ol>");
            final Pattern listOlEnd = Pattern.compile("</ol>");

            boolean insideUl = false;
            boolean insideOl = false;

            // Insert a new line before and after <ul></ul> and <ol></ol> tags:
            body = body.replace("<ul>", "\n<ul>\n").replace("</ul>", "\n</ul>\n");
            body = body.replace("<ol>", "\n<ol>\n").replace("</ol>", "\n</ol>\n");
            Log.logdebug("NEWLINE to <ul></ul> and <ol></ol> tags added.");

        /* Ivanfi, NTT-Data 2017, I-211451. ---- END   */

        String[] ps = body.split(LINE_SEP);

        // iterate over the elements of the string array:
        for (int i = 0; i < ps.length; ++i) {
            /**
             *   Current section has been created to avoid HTML->PDF formatting problems described in I-211451.
             */
            /* Ivanfi, NTT-Data 2017, I-211451. ---- BEGIN */
            Matcher luB = listUlBegin.matcher(ps[i]);
            Matcher luE = listUlEnd.matcher(ps[i]);
            Matcher loB = listOlBegin.matcher(ps[i]);
            Matcher loE = listOlEnd.matcher(ps[i]);

            boolean lubFound = luB.find();
            boolean lueFound = luE.find();
            boolean lobFound = loB.find();
            boolean loeFound = loE.find();

            Log.logdebug("HTML line to analyse (ps["+i+"]): " + ps[i]);

            if (lubFound && lueFound || lobFound && loeFound) {
                Log.logwarn("HTML contains dense format. PDF-Formatting will fail at line: "+ps[i]);
            } else if (lubFound) {
                insideUl = true;
                Log.logdebug("List-Tag '<ul>' found, '<li>' elements are going to be changed.");
            } else if (lobFound) {
                insideOl = true;
                Log.logdebug("List-Tag '<ol>' found, '<li>' elements are going to be changed.");
            }
            if (insideUl || insideOl) {
                // replace list item-beginning by "-":
                ps[i] = ps[i].replace("<li>", "<li>&nbsp;&nbsp;- ");
                Log.logdebug("HTML line (ps["+i+"]) changed: " + ps[i]);
            }
            if (lueFound) {
                insideUl = false;
                Log.logdebug("List-End-Tag '</ul>' found, closing change modus.");
            } else if (loeFound) {
                insideOl = false;
                Log.logdebug("List-End-Tag '</ol>' found, closing change modus.");
            }
            /* Ivanfi, NTT-Data 2017, I-211451. ---- END   */

            Pattern pat = Pattern.compile("#CHECK\\s(.*)");
            Matcher m = pat.matcher(ps[i]);
            Pattern pat2 = Pattern.compile("#CHECKLINE\\s([^#]*)#(.*)",
                    Pattern.DOTALL);
            Matcher m2 = pat2.matcher(ps[i]);
            if (m.find()) {
                Phrase pr = new Phrase("" + (char) 0x6f, wd);

                PdfPCell cell = new PdfPCell(pr);
                cell.setBorderColor(BaseColor.WHITE);
                // experim --- END
                cell.setVerticalAlignment(Element.ALIGN_TOP);

                PdfPTable table = new PdfPTable(new float[]{0.05f, 0.9f});
                table.setSplitLate(false);// Damit wird verhindert man ,dass die
                                          // erste Seite leer wird!!
                table.setWidthPercentage(100.0f);
                table.addCell(cell);
                PdfPCell cell2 = new PdfPCell(doFormats(bf, bbf, m.group(1),
                        fontSize));
                cell2.setBorderColor(BaseColor.WHITE);
                // ----- END
                cell2.setPaddingTop(3f);
                cell2.setVerticalAlignment(Element.ALIGN_TOP);
                table.addCell(cell2);
                Paragraph p = new Paragraph();
                p.setSpacingBefore(0.05f);
                p.add(table);
                fax.add(p);
            }
            else if (m2.find()) {
                Phrase pr = new Phrase("" + (char) 0x6f, wd);

                PdfPCell cell = new PdfPCell(pr);
                cell.setBorderColor(BaseColor.WHITE);
                // ------------- END
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_TOP);

                PdfPTable table = new PdfPTable(new float[]{0.05f, 0.9f});
                table.setSplitLate(false);// Damit wird verhindert man ,dass die
                                          // erste Seite leer wird!!
                table.setWidthPercentage(100.0f);
                table.addCell(cell);
                PdfPCell cell2 = new PdfPCell(new Phrase(m2.group(1), bld));
                cell2.setBorderColor(BaseColor.WHITE);
                // ----------- END
                cell2.setPaddingTop(5f);
                cell2.setPaddingBottom(5f);
                cell2.setPaddingRight(5f);
                cell2.setVerticalAlignment(Element.ALIGN_TOP);
                table.addCell(cell2);
                table.addCell(getCell(bld, ""));
                String bl = m2.group(2).replaceAll("#", "\n");
                // Log.message("BL: " + bl);
                PdfPCell cell3 = new PdfPCell(doFormats(bf, bbf, bl, fontSize));
                cell3.setBorderColor(BaseColor.WHITE);
                // ------------ END
                cell3.setPaddingTop(5f);
                cell3.setPaddingBottom(5f);
                cell3.setPaddingRight(5f);
                cell3.setVerticalAlignment(Element.ALIGN_TOP);
                table.addCell(cell3);

                table.setTableEvent(new PdfPTableEvents());
                Paragraph p = new Paragraph();
                p.setSpacingBefore(0.05f);
                p.add(table);
                fax.add(p);
            }
            else if (ps[i].startsWith("#WHITELINE")) {
                StringBuffer sb = new StringBuffer();
                int j = i;
                for (j = i + 1; j < ps.length
                        && !ps[j].startsWith("#ENDWHITELINE"); ++j) {
                    sb.append(ps[j]).append("\n\n");
                }
                Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12,
                        BaseColor.WHITE);
                Paragraph p = new Paragraph();
                p.add(new Phrase(sb.toString(), font));
                fax.add(p);
                i = j;
            }
            else if (ps[i].startsWith("#BORDER")) {
                StringBuffer sb = new StringBuffer();
                int j = i;
                for (j = i + 1; j < ps.length
                        && !ps[j].startsWith("#ENDBORDER"); ++j) {
                    sb.append(ps[j]).append("\n\n");
                }
                PdfPTable table = new PdfPTable(new float[]{1.0f});
                table.setSplitLate(false);
                table.setWidthPercentage(100.0f);
                // Log.message("BL: " + bl);
                PdfPCell cell3 = new PdfPCell();
                cell3.setBorderColor(BaseColor.WHITE);
                // ------------ END
                cell3.setPaddingTop(5f);
                cell3.setPaddingBottom(5f);
                cell3.setPaddingRight(5f);
                cell3.setVerticalAlignment(Element.ALIGN_TOP);
                fillContent(sb.toString(), cell3, bf, bbf, wd, prop);
                table.addCell(cell3);

                table.setTableEvent(new PdfPTableEvents());
                Paragraph p = new Paragraph();
                p.setSpacingBefore(0.05f);
                p.add(table);
                fax.add(p);
                i = j;
            }
            else if (ps[i].startsWith("#NEWPAGE")) {
                // Log.message("Newpage");
                if (fax instanceof Document) {
                    ((Document) fax).newPage();
                }
            }
            else {
                Paragraph p = doFormats(bf, bbf, ps[i], fontSize);// "\n" +
                                                                  // ps[i],
                                                                  // fontSize);
                                                                  // //Gem??
                                                                  // JIRA 60
                p.setKeepTogether(false);
                fax.add(p);
                // Next line was in comment. Why?:
                //addImage(ps[i], fax);
            }
        }
    }


    private void addImage(String input, ElementListener fax)
            throws MalformedURLException, IOException, DocumentException {
        Pattern pattern = Pattern.compile("#IMGSTART(.*?)#IMGEND");
        Matcher m = pattern.matcher(input);
        while (m.find()) {
            Image img = Image.getInstance(imageList.get(m.group(1)));
            System.err.println(m.group());
            PdfPTable table = new PdfPTable(2);
            //table.addCell(img);
            table.addCell("TEST");
            fax.add(img);
            break;
        }
    }

    private PdfPCell getCell(Font std, String string) {
        PdfPCell cell2 = new PdfPCell(new Phrase(string, std));
        cell2.setBorderColor(BaseColor.WHITE);
        cell2.setPaddingTop(5f);
        cell2.setPaddingBottom(5f);
        return cell2;
    }

    protected byte[] generatetxtAttach(String body) throws Exception {

        init();
        // float LEFT_MARGIN_LETTER = 65.5f;

        body = HTMLDetection.clearHTML(body);

        Document fax = new Document();
        fax.addTitle("Attachment");
        fax.addAuthor("Mediatrix CCS");
        fax.addTitle("Attachment");
        fax.addCreator("Mediatrix CCS");

        fax.setMargins(LetterMaker.LEFT_MARGIN_LETTER - 2, 60, 90f, 80f);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter faxw = PdfWriter.getInstance(fax, out);

        faxw.open();
        fax.open();

        BaseFont bf = BaseFont.createFont(LetterMaker.fontTemp.get("arial")
                .getAbsolutePath(), BaseFont.CP1252, BaseFont.EMBEDDED);
        BaseFont bd = BaseFont.createFont(LetterMaker.fontTemp.get("arialbd")
                .getAbsolutePath(), BaseFont.CP1252, BaseFont.EMBEDDED);

        faxw.getDirectContent();

        String[] ps = body.split("\r?\n\r?\n");
        for (int i = 0; i < ps.length; ++i) {
            Paragraph p = doFormats(bf, bd, "\n" + ps[i], 11);
            p.setKeepTogether(true);
            fax.add(p);
        }
        fax.close();
        out.flush();
        return out.toByteArray();
    }

    private String replaceTab(String input) {
        String[] lines = input.split(System.getProperty("line.separator"));
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].indexOf('\t') >= 0) {
                StringBuffer thisLine = new StringBuffer();
                String[] columns = lines[i].split("\\t");
                for (int j = 0; j < columns.length; j++) {
                    int neededBlanks = 8 - (columns[j].length() % 8);
                    thisLine.append(columns[j]);
                    thisLine.append("        ".substring(0, neededBlanks)); // 8
                    // Blanks!!
                }
                lines[i] = thisLine.toString();
            }
            StringBuffer lineBuffer = new StringBuffer();
            // Ein einzelnes Blank bleibt erhalten, sonst wird es ein nbsp,
            // damit Zeilenumbrueche funktionieren
            for (int j = 0; j < lines[i].length(); j++) {
                char c = lines[i].charAt(j);
                if (c == ' '
                        && (lines[i].length() - 1 > j
                                && lines[i].charAt(j + 1) == ' ' || j == 0)) {
                    while (j < lines[i].length() && lines[i].charAt(j) == ' ') {
                        lineBuffer.append('\u00a0');
                        j++;
                    }
                    if (j < lines[i].length()) {
                        lineBuffer.append(lines[i].charAt(j));
                    }
                }
                else {
                    lineBuffer.append(c);
                }
            }
            sb.append(HTMLDetection.withoutEndSpaces(lineBuffer.toString()));
            sb.append("\n");
        }

        return sb.toString();
    }

}
