<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

<%

	String language = request.getParameter("language");
	if (language == null) {
	    language="en";
	}
	Object tempTime = session.getAttribute("deniedTime");
	int deniedLoginTime = 0;
	if (tempTime != null) {
        	deniedLoginTime = (Integer) session.getAttribute("deniedTime");
        }

    java.io.InputStream stream = new java.net.URL(request.getScheme(), request.getServerName(), request.getServerPort(), "/lang/login_" + language +".properties").openStream();
	java.util.ResourceBundle bundle = new java.util.PropertyResourceBundle(stream);
	System.out.println(bundle.getString("login-denied"));
	org.apache.commons.io.IOUtils.closeQuietly(stream);
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Mediatrix</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="robots" content="index,follow" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<link rel="shortcut icon" href="favicon.ico" />
<style type="text/css">
@import url("media/css/main.css");
</style>
<!--[if IE]>
<style type="text/css">
@import url("media/css/msie.css");
</style>
<![endif]-->
<script src="js/jquery.js" ></script>
<script src="js/rusha.js" ></script>
<script src="js/utf.js" ></script>
<script src="js/base64.js" ></script>
<link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
<link href="js/jquery-ui.css" rel="stylesheet" type="text/css"/>
<script src="js/jquery-ui.min.js"></script>

<script>
	function markLoginFailed() {
		$("#form-error-message").show();
		$("#login-name").addClass('form-error');
		$("#login-name").focus();
	}

jQuery(document).ready(function(){
	/*language*/
	if(<%="\"" + language + "\""%> =="en") {
		$("#lang-de").removeClass('lang-current');
		$("#lang-en").addClass('lang-current');
	} else {
		$("#lang-en").removeClass('lang-current');
		$("#lang-de").addClass('lang-current');
	}
	$("#form-error-message").text("<%=bundle.getString("error")%>");
	

  $("#login-name").focus();
  $("#submitButton").click(function(){
 	if(trim($("#login-name").val())=="")
        {
          alert("Login empty");
	  $("#form-error-message").show();	
	  $("#login-name").addClass('form-error');
          $("#login-name").focus();
          return false;
        }
        else if(trim($("#login-pass").val())=="")
        {
          alert("password empty");
	  $("#form-error-message").show();
	  $("#login-name").addClass('form-error');
          $("#login-pass").focus();
          return false;
        } else {
		$("#form-login").submit();
	}
   });
  
  $('#form-login').submit(function(){  
	 var ciphertext = cSHA1($("#login-pass").val()); 
	 var encodedData = base64.encode(ciphertext);
	  $("#login-pass").val(encodedData);
	  return true;
  });
  
  function cSHA1(m){
	  return (new Rusha).digest(utf16to8(m));
	}
  
  $("#login-name").keyup(function(event){
    if(event.keyCode == 13){
    	jQuery(this).blur();
	$("#submitButton").focus().click();
	return false;
    }
  });
  $("#login-pass").keyup(function(event){
    if(event.keyCode == 13){
    	jQuery(this).blur();
    	
	$("#submitButton").focus().click();
	return false;
    }
  });

    	function trim(s) {
        	return s.replace( /^\s*/, "" ).replace( /\s*$/, "" );
    	}

$(function() {
    var time = <%=deniedLoginTime%>;
    if (time > 0) {
	$("#login-name").addClass("form-error");
	$("#form-error-message").removeAttr("style");
	$("#progessbar").removeAttr("style");
        $("#submitButton").hide();
    	var sec = 100 / time;
    	var pGress = setInterval(function() {
        	var pVal = $('#progressbar').progressbar('option', 'value');
        	var pCnt = !isNaN(pVal) ? (pVal + sec) : 0;
        	if (pCnt > 100) {
            		clearInterval(pGress);
			$('#progressbar').progressbar({value: 0});
			/*$("#progessbar").hide();*/
			$("#progressbar").attr("style","display: none;")
			$("#submitButton").show();
			$('#progressbar_display').hide();
        	} else {
			$('progressbar_display').show();
            		$('#progressbar').progressbar({value: pCnt});
			$('#progressbar_display').text(<%="\"" + bundle.getString("login-denied") + " \""%> + time-- + <%= "\" " + bundle.getString("second") + "\""%>);
       		 }
    	},1000);
    } else if (time == -1) {
	/*login denied*/
	alert('Login denied!');
    }
	/*$('#progressbar').hide();*/
});

});


</script>
</head>
<body>
<div id="page">
 <div id="head">
  <h1><%=bundle.getString("welcome")%></h1>
  <a id="logo-ityx" href="http://www.ityx.eu" title="ityx.eu"></a><!-- #logo-ityx -->
			<ul id="lang-select">
   				<li><%=bundle.getString("language")%></li>
   				<li id="lang-de">                     <a class="lang-de" href="login.jsp?language=de" title="deutsch"></a></li>
   				<li id="lang-en" class="lang-current"><a class="lang-en" href="login.jsp?language=en" title="english"></a></li>
 			</ul> <!-- #lang-select -->
 </div><!-- #head -->

 <div id="content">
  <div class="login">
   <h1>Login</h1>
   <p>
   <%=bundle.getString("additional")%>
   </p>

   <form name= "form-login" id="form-login" method="post" action="/login?language=<%=language %>">
    <fieldset>
     <legend>Login</legend>
     <table cellpadding="0" cellspacing="0" border="0">
      <tbody>
       <tr>
        <td class="label"><label for="login-name"><%=bundle.getString("user")%></label></td>
        <td class="input"><input id="login-name" name="login-name" class="form-text" type="text" value="" /></td><!-- .input -->
       </tr>
       <tr>
        <td class="label"><label for="login-pass"><%=bundle.getString("pass")%></label></td><!-- .label -->
        <td class="input"><input id="login-pass" name="login-pass" class="form-text" type="password" /></td><!-- .input -->
       </tr>
       <tr>
        <td>&nbsp;</td>
        <!-- Option: Fehlermeldung  -->
        <td id="form-error-message" class="form-error-message" style="display: none;"><%=bundle.getString("error")%></td>
        <!-- Option: keine Fehlermeldung -->
        <td>&nbsp;</td>
       </tr>
       <tr>
       	<td class="submit" >
		<a class="button2" id="submitButton" href="javascript:;"><%=bundle.getString("send")%></a></td>
        	<!--<td class="register"><a class="bullet1" href="">Zur Registrierung</a></td><!-- .register -->
		<td>
		<div>
			<div id="progressbar" class="progressbar"></div>
			<p id="progressbar_display"></p>		
		</div>
		</td>
       </tr>
      </tbody>
     </table>
   </fieldset>
   </form>

  </div><!-- .login -->

  <div id="product-info">
   <h2><%=bundle.getString("productinformation")%></h2>
   <ul>
    <li class="first-child"><a href="http://www.ityx.de/loesungen/email-response-management.html"><img src="media/img/product/product-response.png" width="130" height="57" alt="Response" title="" /></a></li><!-- .first-child -->
    <li><a href="http://www.ityx.de/loesungen/social-crm.html"><img src="media/img/product/product-comcrawler.png" width="171" height="57" alt="Comcrawler" title="" /></a></li>
    <li><a href="http://www.ityx.de/loesungen/knowledge-management.html"><img src="media/img/product/product-knowledge.png" width="162" height="57" alt="Knowledge" title="" /></a></li>
    <li><a href="http://www.ityx.de/loesungen/digitale-poststelle-digital-mail.html"><img src="media/img/product/product-mailroom.png" width="162" height="57" alt="Mailroom" title="" /></a></li>
    <li><a href="http://www.ityx.de/loesungen/automatische-datenerfassung-screen-scraping.html"><img src="media/img/product/product-virtual_agent.png" width="206" height="57" alt="Virtual Agent" title="" /></a></li>
    <li><a href="http://www.ityx.de/loesungen/web-self-service-faq-software.html"><img src="media/img/product/product-self_service.png" width="162" height="57" alt="Self Service" title="" /></a></li>
   </ul>
  </div><!-- #product-info -->

  <ul id="documentation">
   <li class="first-child"><a href="/docs/mediatrix-api/">Mediatrix-API</a></li><!-- .first-child -->
   <li><a href="/docs/contex-api/">Contex-API</a></li>
  </ul><!-- #documentation -->
 </div><!-- #content -->
</div><!-- #page -->
</body>
</html>