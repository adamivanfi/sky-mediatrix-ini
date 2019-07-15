package de.ityx.sky.mx.clientevents.ws;

import com.nttdata.de.sky.ityx.common.ExtendedNewMailFrame;
import de.ityx.clientevents.mx.event.EventResult;
import de.ityx.clientevents.mx.event.EventResult.Status;
import de.ityx.clientevents.mx.event.IMXClientEvent;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.data.Question;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ExtendedNewMailEvent implements IMXClientEvent {

    /**
     * 
     */
    private static final long serialVersionUID = -8403086337462969058L;

    private final int         id;                                      // Teilprojekt

    private String            extra1           = null;

    private String            extra2           = null;

    private String            extra3           = null;

    private String            extra4           = null;

    private String            extra5           = null;

    private String            extra6           = null;

    private String            extra7           = null;

    private String            extra8           = null;

    private String            extra9           = null;

    private String            extra10          = null;

    private String            extra11          = null;

    protected String          token;

    public String getExtra1() {
        return extra1;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public String getExtra3() {
        return extra3;
    }

    public void setExtra3(String extra3) {
        this.extra3 = extra3;
    }

    public String getExtra4() {
        return extra4;
    }

    public void setExtra4(String extra4) {
        this.extra4 = extra4;
    }

    public String getExtra5() {
        return extra5;
    }

    public void setExtra5(String extra5) {
        this.extra5 = extra5;
    }

    public String getExtra6() {
        return extra6;
    }

    public void setExtra6(String extra6) {
        this.extra6 = extra6;
    }

    public String getExtra7() {
        return extra7;
    }

    public void setExtra7(String extra7) {
        this.extra7 = extra7;
    }

    public String getExtra8() {
        return extra8;
    }

    public void setExtra8(String extra8) {
        this.extra8 = extra8;
    }

    public String getExtra9() {
        return extra9;
    }

    public void setExtra9(String extra9) {
        this.extra9 = extra9;
    }

    public String getExtra10() {
        return extra10;
    }

    public void setExtra10(String extra10) {
        this.extra10 = extra10;
    }

    public String getExtra11() {
        return extra11;
    }

    public void setExtra11(String extra11) {
        this.extra11 = extra11;
    }

    @Override
    public String getToken() {
        // TODO Auto-generated method stub
        return this.token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    public ExtendedNewMailEvent(String idstring) {
        if (idstring != null && !"".equals(idstring)) {
            this.id = Integer.parseInt(idstring);
        }
        else {
            this.id = 0;
        }
    }

    public ExtendedNewMailEvent(int idstring) {
        this.id = idstring;
    }

    public ExtendedNewMailEvent() {
        this.id = 0;
    }

    @Override
    public EventResult doAction() {
        try {
            List<JFrame> frames = null;
            if (Repository.hasObject(Repository.NEWMAILFRAME)) {
                frames = (List<JFrame>) Repository.getObject(Repository.NEWMAILFRAME);
            }

            if (frames == null || frames.isEmpty())
            {
                Question question = new Question();
                if (getExtra1() != null) {
                    question.setExtra1(getExtra1());
                }
                if (getExtra2() != null) {
                    question.setExtra2(getExtra2());
                }
                if (getExtra3() != null) {
                    question.setExtra3(getExtra3());
                }
                if (getExtra4() != null) {
                    question.setExtra4(getExtra4());
                }
                if (getExtra5() != null) {
                    question.setExtra5(getExtra5());
                }
                if (getExtra6() != null) {
                    question.setExtra6(getExtra6());
                }
                if (getExtra7() != null) {
                    question.setExtra7(getExtra7());
                }
                if (getExtra8() != null) {
                    question.setExtra8(getExtra8());
                }
                if (getExtra9() != null) {
                    question.setExtra9(getExtra9());
                }
                if (getExtra10() != null) {
                    question.setExtra10(getExtra10());
                }
                if (getExtra11() != null) {
                    question.setExtra11(getExtra11());
                }
                ExtendedNewMailFrame newMail = new ExtendedNewMailFrame(question);

                newMail.setVisible(true);
                newMail.setDefaultFocus();
                newMail.setState(java.awt.Frame.ICONIFIED);
                newMail.setState(java.awt.Frame.NORMAL);
                newMail.toFront();
                newMail.repaint();
                //newMail.toFront();
                newMail.setLocationRelativeTo((Component) Repository.getObject(Repository.MAINWINDOW));

                if (question.getProjectId() == 0) {
                    newMail.initProjektID();
                }
                if (ExtendedNewMailEvent.this.id > 0) {
                    newMail.setselectedTeilprojekt(ExtendedNewMailEvent.this.id);
                }
            }
            else {
                return new EventResult(token, "", Status.ERROR, "FALSE");
            }
        }

        catch(Exception e) {
            e.printStackTrace();
            return EventResult.OK(this.token, "FALSE");
        }

        return EventResult.OK(this.token, "TRUE");
    }
}
