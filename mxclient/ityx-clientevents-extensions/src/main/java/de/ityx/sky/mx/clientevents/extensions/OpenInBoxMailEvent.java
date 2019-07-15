package de.ityx.sky.mx.clientevents.extensions;

import de.ityx.clientevents.mx.event.EventResult;
import de.ityx.clientevents.mx.event.IMXClientEvent;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.client.dialog.Start;
import de.ityx.mediatrix.client.util.Misc;
import de.ityx.mediatrix.client.util.QuestionActions;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.modules.tools.logger.Log;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class OpenInBoxMailEvent implements IMXClientEvent {

    /**
     * 
     */
    private static final long serialVersionUID = -2317769909932431799L;

    private final int         id;

    protected String          token;

	private String            extra1           = "";

	private String   extra10 = "";

	private String   extra11 = "";

	private String   extra2  = "";

	private String   extra3  = "";

	private String   extra4  = "";

	private String   extra5  = "";

	private String   extra6  = "";

	private String   extra7  = "";

	private String   extra8  = "";

	private String   extra9  = "";

    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    public OpenInBoxMailEvent(String idstring) {
        this.id = Integer.parseInt(idstring);
    }

    public OpenInBoxMailEvent(int idstring) {
        this.id = idstring;
    }

    @Override
    public EventResult doAction() {
        if (Misc.istOperatordialog()) {
            return EventResult.ERROR(this.token);
        }
        Runnable act = new Runnable() {
            @Override
            public void run() {
                //
                // Klick auf Mediatrix-Client  Toolbar-Button "Mail-Inbox..." simulieren
                //
                Start start = Start.getInstance();
                Question frage = null;
                try {
                    frage = API.getClientAPI().getQuestionAPI().load(OpenInBoxMailEvent.this.id);
                }
                catch(NumberFormatException e) {
                    Log.logerror(e, false);
                }
                if (frage != null) {
                    QuestionActions.openMailInbox(start, start, frage);
                }
            }
        };

        try {
            SwingUtilities.invokeAndWait(act);
        }
        catch(InterruptedException | InvocationTargetException e) {
            Log.logerror(e, false);
        }

		return EventResult.OK(this.token);
    }

	public String getExtra1() {
		return extra1;
	}

	public void setExtra1(String extra1) {
		this.extra1 = extra1;
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
}
