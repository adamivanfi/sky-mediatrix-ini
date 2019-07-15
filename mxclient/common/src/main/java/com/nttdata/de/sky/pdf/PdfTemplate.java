package com.nttdata.de.sky.pdf;

import java.io.Serializable;
import java.util.Properties;

public class PdfTemplate implements Serializable {

    private static final long serialVersionUID = -1590510103824117869L;

    protected String          name;
    protected byte[]          pdf;
    protected byte[]          alernativePdf;
    protected Properties      properties;
    protected boolean         pdfchanged;
    protected int             subproject       = 0;
    protected int             language         = 0;

    public boolean isPdfchanged() {
        return this.pdfchanged;
    }

    public void setPdfchanged(boolean pdfchanged) {
        this.pdfchanged = pdfchanged;
    }

    public String getName() {
        return this.name;
    }

    public byte[] getPdf() {
        return this.pdf;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setSubproject(int subproject) {
        this.subproject = subproject;
    }

    public int getSubproject() {
        return subproject;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getLanguage() {
        return language;
    }

    public byte[] getAlernativePdf() {
        return alernativePdf;
    }

    public void setAlernativePdf(byte[] alernativePdf) {
        this.alernativePdf = alernativePdf;
    }
}
