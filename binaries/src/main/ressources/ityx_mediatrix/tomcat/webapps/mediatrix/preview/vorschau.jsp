<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"><html>
<%@ page import="java.sql.*" %>
<%@ page import="java.io.*" %>
<%@ page import="de.ityx.base.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="de.ityx.mediatrix.data.*" %>
<%@ page import="de.ityx.base.dbpooling.*" %>
<%@ page import="de.ityx.mediatrix.server.connect.*" %>
<%@ page import="de.ityx.mediatrix.api.*" %>
<%@ page import="de.ityx.mediatrix.api.server.*" %>
<%@ page import="de.ityx.mediatrix.api.interfaces.textobjects.*" %>
<%@ page import="de.ityx.mediatrix.api.interfaces.*" %>
<%@ page import="de.ityx.base.*" %>
<%@ page import="de.ityx.mediatrix.server.servlet.ERPSServlet" %>

	<%	
try{
  String stTextbaustein = request.getParameter ("textobjectid");
  int textObjectId = Integer.parseInt (stTextbaustein);
  String stHistory = request.getParameter ("historyid");
  String stAktuell = request.getParameter ("aktuell");
  String mandant = request.getParameter ("mandant");
  int historyId = -1;
  boolean aktuell = false;
	
  if (stHistory != null){
  historyId = Integer.parseInt (stHistory);
  }  
   
  if (stAktuell != null){
  aktuell = true;
  } 
 
  if(mandant!= null){
  	UnitDirectory unitDir = UnitDirectory.getInstance();
    UnitInfo unitInfo = unitDir.getUnitInfo();
    unitInfo.setHttpRequest(request);
    unitInfo.setOperatorId(ERPSServlet.getOperatorId(request));
    unitInfo.setUnitName(mandant);
  }
  ISTextObject st = API.getServerAPI().getTextObjectAPI();
  ISProject sp = API.getServerAPI().getProjectAPI();
  IConnectionPool pool = DBConnectionPoolFactory.getPool();
  Connection con = pool.getCon();
  String body = "";
  String title = "";
  String projectname = "Alle Projekte";
  
  if (aktuell == true){
  
    ITextObject tb = st.loadSelfServiceWithIDAktuell(con, textObjectId);
    if(tb!=null)
    {
    	body = tb.getSelfServiceLongDescription();
    	title = tb.getSelfServiceShortDescription();
    	if(tb.getProjectId() > 0){
            projectname = sp.load(con, tb.getProjectId()).getName();
  		}

    }
    else{
      title="Textobject is not active for self service or has no valid history";
    }
  }
    

  else{
  if (historyId > 0){
    TextObject tb = new TextObject();
    tb.setId(textObjectId);
    List v = st.loadHistory (con, tb);
    for(Iterator i=v.iterator();i.hasNext ();){
      History h = (History) i.next ();
      if (h.getVersion () == historyId){
        //Abschnippeln und einfuegen
        body = h.getSelfServiceShortTerm();
        title = h.getSelfServiceLongTerm();
      }
    }
  }
  else{
    //Andere Variante:
    ITextObject tb = st.loadSelfServiceWithID(con, textObjectId);
   if(tb!=null)
    {
    	body = tb.getSelfServiceLongDescription ();
    	title = tb.getSelfServiceShortDescription();
    	if(tb.getProjectId() > 0){
            projectname = sp.load(con, tb.getProjectId()).getName();
  		}

    }
    else{
      title="Textobject is not active for self service or has no valid history";
    }
  }
  }




  if (body != null && body.length () > 0){
    String tmp = body.toLowerCase();
    int index = tmp.indexOf("<body");
    if (index >= 0) {
      body = body.substring(index + "<body".length());
      tmp = tmp.substring(index + "<body".length());
      // Falls Attribute im body-Element drin stehen. Wir
      // schneiden das > getrennt ab:
      index = tmp.indexOf('>');
      body = body.substring(index + 1);
      tmp = tmp.substring(index + 1);
    }
    index = tmp.lastIndexOf("</body>");
    if (index >= 0) {
      body = body.substring(0, index);
    }
    body = body.trim();    
  }
  title = title.replaceAll("Ä", "&Auml;").replaceAll("Ö", "&Ouml;").replaceAll("Ü", "&Uuml;")
  	.replaceAll("ä", "&auml;").replaceAll("ö","&ouml;").replaceAll("ü", "&uuml;")
  	.replaceAll("ß", "&szlig;");
  	
  projectname = projectname.replaceAll("Ä", "&Auml;").replaceAll("Ö", "&Ouml;").replaceAll("Ü", "&Uuml;")
  	.replaceAll("ä", "&auml;").replaceAll("ö","&ouml;").replaceAll("ü", "&uuml;")
  	.replaceAll("ß", "&szlig;");
  	
  pool.releaseCon(con);
%>
<head>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ITyX GmbH SE: <%= title %></title>
<link type="text/css" rel="styleSheet" href="css/ityx/selfservice.css">
<link type="text/css" rel="styleSheet" href="css/ityx/selfservice_advisor.css">
<link href="img/ityx/mediatrix.png" rel="shortcut icon">
</head>
<body>
<center>
<div id="main_doc">
<div id="ityxss_document_bg">
<h3 id="docsubject"><%= title %></h3>
<div id="ityxss_a_doc"><%= body %></div>
<div id="ityxss_rateForm">
<form method="POST" action="" class="rateFormForm">
<input value="SelfServiceAdvisor.rateResult" name="plugin" type="hidden" class="none"><input value="6B65EE32FBE761D0BF84429A63547970" name="sessionid" type="hidden" class="none">
	 	Hat Ihnen diese Antwort geholfen?&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 	<input value="Ja" name="R_YES" class="ityxss_btnsubmit" type="submit" src="img/ityx/btn_senden.gif">&nbsp;&nbsp;&nbsp;&nbsp;
	 	<input value="Nein" name="R_NO" class="ityxss_btnsubmit" type="submit" src="img/ityx/btn_senden.gif"><input value="40009" name="docid" type="hidden" class="none"><input value="" name="master" type="hidden" class="none"><input name="query" type="hidden" class="none" value="">
</form>
</div>
<div id="ityxss_related">
<table width="100%" cellpadding="0" cellspacing="0">
<tr>
<td colspan="4" class="ityxss_form1rel"><span class="ityxss_font1">Verwandte Themen</span></td><td class="ityxss_form1rel">&nbsp;</td>
</tr>
<tr>
<td class="linkbg2a">&nbsp;</td><td class="linkbg2a"><img src="img/ityx/link.gif"></td><td class="linkbg2a"><a target="ityxss_document" class="plainr" href="">Gibt es eine &auml;hnliche Anfrage Nummer eins?</a></td><td class="linkbg2a">&nbsp;</td><td class="linkbg2a">&nbsp;</td>
</tr>
<tr>
<td class="linkbg2a">&nbsp;</td><td class="linkbg2a"><img src="img/ityx/link.gif"></td><td class="linkbg2a"><a target="ityxss_document" class="plainr" href="">Diese Frage wird immer zusammen mit der anderen gelesen</a></td><td class="linkbg2a">&nbsp;</td><td class="linkbg2a">&nbsp;</td>
</tr>
<tr>
<td class="linkbg2a">&nbsp;</td><td class="linkbg2a"><img src="img/ityx/link.gif"></td><td class="linkbg2a"><a target="ityxss_document" class="plainr" href="">Allgemeine Informationen zum Thema Vorschau</a></td><td class="linkbg2a">&nbsp;</td><td class="linkbg2a">&nbsp;</td>
</tr>
<tr>
<td class="linkbg2a">&nbsp;</td><td class="linkbg2a"><img src="img/ityx/link.gif"></td><td class="linkbg2a"><a target="ityxss_document" class="plainr" href="">Haben Sie ausserdem noch interessante Textbausteine?</a></td><td class="linkbg2a">&nbsp;</td><td class="linkbg2a">&nbsp;</td>
</tr>
<tr>
<td class="linkbg2a">&nbsp;</td><td class="linkbg2a"><img src="img/ityx/link.gif"></td><td class="linkbg2a"><a target="ityxss_document" class="plainr" href="">Wo finde ich Downloads zum Thema H&ouml;herversicherung?</a></td><td class="linkbg2a">&nbsp;</td><td class="linkbg2a">&nbsp;</td>
</tr>
</table>
</div>
</div>
</div>
</center>
</body>
</html>
<%
} catch(Exception e) {
  e.printStackTrace();
}%>