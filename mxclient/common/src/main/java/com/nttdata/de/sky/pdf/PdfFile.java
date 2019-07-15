package com.nttdata.de.sky.pdf;

import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.mediatrix.data.Question;

import java.io.Serializable;
import java.util.HashMap;

public class PdfFile implements Serializable {

    private static final long serialVersionUID = -7081002642143419202L;
    
    protected String     body       = "";
    protected int        subproject = 0;
    protected int        language   = 0;
    private boolean    isSbsProject = false;

    private final HashMap<String, Object> parameter;

    public PdfFile(String body, int subproject, int language, boolean isNotSbsProject, HashMap<String, Object> parameter) {
        this.parameter = parameter;
        this.setBody(body);
        this.setSubproject(subproject);
        this.setLanguage(language);
        this.setSbsProject(!isNotSbsProject);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
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

    public HashMap<String, Object> getParameter() {
        return parameter;
    }

    public boolean isSbsProject() {
        return isSbsProject;
    }

    public void setSbsProject(boolean sbsProject) {
        isSbsProject = sbsProject;
    }
}