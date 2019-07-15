package de.ityx.sky.mx.clientevents.extensions;

import de.ityx.clientevents.mx.event.EventResult;
import de.ityx.clientevents.mx.event.IMXClientEvent;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.client.dialog.AcceptDialog;
import de.ityx.mediatrix.client.dialog.Start;
import de.ityx.mediatrix.client.dialog.operate.QuestionAnswer;
import de.ityx.mediatrix.client.dialog.operate.WaitQueue;
import de.ityx.mediatrix.client.dialog.singlemode.CustomerDataUpdate;
import de.ityx.mediatrix.client.dialog.singlemode.QuestionTablePanel;
import de.ityx.mediatrix.client.dialog.util.MediaTrixSplitPane;
import de.ityx.mediatrix.client.dialog.util.split.SplitScreenLeerLinks;
import de.ityx.mediatrix.client.dialog.util.split.SplitScreenLeerRechts;
import de.ityx.mediatrix.client.util.Misc;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.modules.tools.logger.Log;

import javax.swing.*;

import java.lang.reflect.InvocationTargetException;

public class OpenOpModusMailEvent implements IMXClientEvent {

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

    public OpenOpModusMailEvent() {
        this.id = -1;
    }

    public OpenOpModusMailEvent(String idstring) {
        this.id = Integer.parseInt(idstring);
    }

    public OpenOpModusMailEvent(int idstring) {
        this.id = idstring;
    }

    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }



    @Override
    public EventResult doAction() {
        QuestionAnswer fa = (QuestionAnswer) Repository.getObject(Repository.OPERATORMODE);
        
            if (fa != null) {
            AcceptDialog acdlg = new AcceptDialog(Start.getInstance());
            switch (acdlg.getAction()) {
                case AcceptDialog.STORE:
                    fa.storeQuestionAnswer(true);
                    if (fa.isGespeichert()) {
                        Repository.removeObject(Repository.QUESTIONANSWER);
                    }
                    break;
                case AcceptDialog.DISCARD:
                    Repository.removeObject(Repository.QUESTIONANSWER);
                    break;

                case AcceptDialog.CANCEL:
                    // Nur der Vollständigkeithalber übernommen aus der 1.3 hier passiert NICHTS
                    break;
            }
        }
        if (!Repository.hasObject(Repository.OPERATORMODEWAITLOOP)) {
            Log.loginfo("No WaitLoop");
            return EventResult.ERROR(this.token);
        }
        Runnable act = new Runnable() {
            @Override
            public void run() {
                if (OpenOpModusMailEvent.this.id != -1) {
                    Log.loginfo("OpmodusMailEvent:> Lade Frage mit ID " + OpenOpModusMailEvent.this.id);
                    Question frage = API.getClientAPI().getQuestionAPI().load(OpenOpModusMailEvent.this.id);

                    //Nicht erledigte Frage versuchen zu sperren: Wenn die Sperre nicht erfolgreich gesetzt wurde, dann einfach weiter machen
                    if (frage != null) {
                        if (!frage.getStatus().equals(Question.S_COMPLETED) && !API.getClientAPI().getQuestionAPI().setLock(frage)) {
                            return;
                        }
                        else {
                            WaitQueue ws = (WaitQueue) Repository.getObject(Repository.OPERATORMODEWAITLOOP);
                            if (ws == null) {
                                ws = new WaitQueue();
                            }

                            ws.setProcessQuestion(true);
                            //Die nächste Frage erst dann anzeigen, wenn die aktuelle Frage nicht gerade beantwortet wird
                            if (!Repository.hasObject(Repository.QUESTIONANSWER)) {
                                Log.loginfo("Waitqueue show Question " + frage.getId());
                                // CDRMIG-10 das QuestionAnswerObject muss hier null sein, sonst geht es nicht
                                WaitQueue.showFrage(frage, ws, null);
                            }
                        }
                    }
                }
                else {
                    MediaTrixSplitPane splitScreen;
                    final QuestionTablePanel frtb;

                    Repository.removeObject(Repository.OPERATORMODE);
                    Repository.removeObject(Repository.QUESTION_LOCK);
                    Repository.removeObject(Repository.THEQUESTION);
                    Repository.removeObject(Repository.THEANSWER);
                    Repository.removeObject(Repository.QUESTIONANSWER);
                    Repository.removeObject("ITREEBROWSER");
                    Repository.removeObject(Repository.OPERATORMODEWAITLOOP);

                    splitScreen = Start.getInstance().getSplitScreen();
                    CustomerDataUpdate.setCustomerData(null, false);

                    // Op.modus einstellen:
                    splitScreen
                            .setLeftComponent((SplitScreenLeerLinks) Repository.getObject(Repository.SPLITSCREENEMPTYLEFT));
                    splitScreen
                            .setRightComponent(SplitScreenLeerRechts.getInstance());
                    Misc.setWaitQueue();
                    // naechste Frage in Queue

                    if (Repository.hasObject(Repository.QUESTIONTABLEPANEL)) {
                        frtb = (QuestionTablePanel) Repository.getObject(Repository.QUESTIONTABLEPANEL);
                        frtb.performRefresh();
                    }
                }
            }

        };

        try {
            SwingUtilities.invokeAndWait(act);
        }
        catch(InterruptedException e) {
            Log.logerror(e, false);
        }
        catch(InvocationTargetException e) {
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
