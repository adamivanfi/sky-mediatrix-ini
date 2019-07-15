<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />
<%@ page import="de.ityx.contex.version.Product" %>
<%
	String language = request.getParameter("language");
	if (language == null) {
	    language=request.getLocale().getLanguage();
	}
    java.io.InputStream stream = new java.net.URL(request.getScheme(), request.getServerName(), request.getServerPort(), "/lang/index_" + language +".properties").openStream();
	java.util.ResourceBundle bundle = new java.util.PropertyResourceBundle(stream);
	org.apache.commons.io.IOUtils.closeQuietly(stream);
%>
<%
	Object opAttr = session.getAttribute("operator");
	de.ityx.mediatrix.data.Operator operator;
	if (opAttr != null) {
		operator = (de.ityx.mediatrix.data.Operator) opAttr;
	} else {
  	  operator = null;
	}
%>

<%! 
java.lang.management.MemoryUsage memUsage = java.lang.management.ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Mediatrix</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="robots" content="index,follow" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<script src="js/jquery.js" ></script>
<link rel="shortcut icon" href="favicon.ico" />
<style type="text/css">
@import url("media/css/main.css");
</style>
<!--[if IE]>
<style type="text/css">
@import url("media/css/msie.css");
</style>
<![endif]-->
<script src="jquery.js"></script>
<script>
	function displayHealthCheck() {
		$('#loading-image').show();
		var X=new XMLHttpRequest, data="/health_check";
 		X.open('GET', "/health_check", false );
  		X.send('');
		$('#loading-image').hide();		
		if (X.responseText == "failed") {
			location = "login.jsp";
		} else {
			document.getElementById("health_check").innerHTML=X.responseText;
		}
	}
	
	function logout() {
		var X=new XMLHttpRequest, data="/logout";
 		X.open('GET', "/logout", false );
  		X.send('');
	}
jQuery(document).ready(function(){
	/*language*/
	if(<%="\"" + language + "\""%> =="de") {
		$("#lang-en").removeClass('lang-current');
		$("#lang-de").addClass('lang-current');
	} else {
		$("#lang-de").removeClass('lang-current');
		$("#lang-en").addClass('lang-current');
	}
	
	
	<% if(operator!=null){%>
		/*$("#start-login").text("Currently logged in as: " + "<%=operator.getLogin()%>");
		$("#start-login").removeClass();
		$("#start-login").removeAttr("href");*/
		$("#start-login").text("Logout");
		$("#start-login").attr("href", "/logout");
	<%}%>
});
</script>
</head>
<body>
	<div id="page">
		<div id="head">
			<h1><%=bundle.getString("welcome")%></h1>
			<a id="logo-ityx" href="http://www.ityx.eu" title="ityx.eu"></a>
			<ul id="lang-select">
   				<li id="lang-label"><%=bundle.getString("language")%></li>
   				<li id="lang-de">                     <a class="lang-de" href="index.jsp?language=de" title="deutsch"></a></li>
   				<li id="lang-en" class="lang-current"><a class="lang-en" href="index.jsp?language=en" title="english"></a></li>
 			</ul> <!-- #lang-select -->
			<!-- #logo-ityx -->
		</div>
		<!-- #head -->

		<div id="content">
			<div class="start">
				<h1><%=bundle.getString("start")%></h1>

				<p>
					<%=bundle.getString("start2")%>
				</p>

				<a id="start-client" class="button1" href="/mediatrix/jnlp/mediatrix.jnlp"><%=bundle.getString("start-client")%></a>
				<!-- #start-client -->
				<a id="start-login" class="button1" href="/login.jsp?language=<%=language%>">Adminstrator-Login</a>

				<table id="server-info" cellpadding="0" cellspacing="0" border="0">
					<tbody>
						<tr class="row1">
							<td>Version</td>
							<td><%=Product.getProduct().getVersion()%></td>
						</tr>
						<!-- .row1 -->
						<tr class="row2">
							<td>Build</td>
							<td><%=Product.getProduct().getBuildNumber()%></td>
						</tr>
						<!-- .row2 -->
						<tr class="row1">
							<td>Max-RAM</td>
							<td><%=getUsedRAM()%> / <%=getMaxRAM()%></td>
						</tr>
						<!-- .row1 -->
						<tr class="row2">
							<td>Harddisk</td>
							<td><%=getFreeDiscSpace()%></td>
						</tr>
						<!-- .row2 -->
						<tr class="row1">
							<td>Java</td>
							<td><%=System.getProperty("java.version")%></td>
						</tr>
						<!-- .row1 -->
						<tr class="row2">
							<td>CPU</td>
							<td><%=System.getenv("PROCESSOR_IDENTIFIER") != null ? System.getenv("PROCESSOR_IDENTIFIER") : "not available"%></td>
						</tr>
						<tr class="row1">
							<td>Load Average</td>
							<td><%=getLoadAverage()%></td>
						</tr>
						<tr class="row2">
							<td>JVM-Uptime</td>
							<td><%=getUptime()%></td>
						</tr>
					</tbody>
				</table>
				<!-- #server-info -->
			</div>
			<!-- .start -->
			<%!
    public String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
%>

			<%!
    public String getLoadAverage() {
	return String.valueOf(java.lang.management.ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
    }
%>

			<%!
    public String getUptime() {
	long uptime = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
	long uptimeDays = java.util.concurrent.TimeUnit.DAYS.convert(uptime, java.util.concurrent.TimeUnit.MILLISECONDS);
	return String.valueOf(uptimeDays) + " day(s)";
    }
%>

			<%!
    public String getFreeDiscSpace() {
        try {
            java.io.File f = new java.io.File("/");
            return humanReadableByteCount(f.getUsableSpace(), true);
        }
        catch(Exception e) {
            return e.toString();
        }
    }
%>

			<%!
	public String getMaxRAM() {
	long maxMemory = memUsage.getMax();
	return humanReadableByteCount(maxMemory, true);
   }
%>

			<%!
	public String getUsedRAM() {
	long usedMemory = memUsage.getUsed();
	return humanReadableByteCount(usedMemory, true);
   }
%>

			<%!
	public String getFreeRAM() {
    	/* This will return Long.MAX_VALUE if there is no preset limit */
    	long maxMemory = Runtime.getRuntime().freeMemory();
    	/* Maximum amount of memory the JVM will attempt to use */
		if(maxMemory == Long.MAX_VALUE) {
			return "not available";
		} else {
			return humanReadableByteCount(maxMemory, true);
		}
   }
%>
			<div id="modules-help">
				<h2><%=bundle.getString("helpmodules")%></h2>
				<ul>
					<li class="first-child"><h3>Health-Check</h3>
						<div id="health_check"></div> <img id="loading-image" src="media/img/load.gif" alt="Loading..." style="display: none;" />
						<a class="bullet1" onclick="displayHealthCheck()" style="cursor: pointer;">Details</a>
					</li>
					<li><h3><%=bundle.getString("performanceanalysis")%></h3>
						<a class="bullet1" href="">Details</a>
						<div id="performance_analyse"></div></li>
					<li><h3><%=bundle.getString("performanceoptimization")%></h3>
						<a class="bullet1" href="">Details</a>
						<div id="performance_optimierung"></div></li>
				</ul>
			</div>
			<!-- #modules-help -->

			<div id="product-info">
				<h2><%=bundle.getString("productinformation")%></h2>
				<ul>
					<li class="first-child"><a
						href="http://www.ityx.de/loesungen/email-response-management.html"><img
							src="media/img/product/product-response.png" width="130"
							height="57" alt="Response" title="" /></a></li>
					<!-- .first-child -->
					<li><a href="http://www.ityx.de/loesungen/social-crm.html"><img
							src="media/img/product/product-comcrawler.png" width="171"
							height="57" alt="Comcrawler" title="" /></a></li>
					<li><a
						href="http://www.ityx.de/loesungen/knowledge-management.html"><img
							src="media/img/product/product-knowledge.png" width="162"
							height="57" alt="Knowledge" title="" /></a></li>
					<li><a
						href="http://www.ityx.de/loesungen/digitale-poststelle-digital-mail.html"><img
							src="media/img/product/product-mailroom.png" width="162"
							height="57" alt="Mailroom" title="" /></a></li>
					<li><a
						href="http://www.ityx.de/loesungen/automatische-datenerfassung-screen-scraping.html"><img
							src="media/img/product/product-virtual_agent.png" width="206"
							height="57" alt="Virtual Agent" title="" /></a></li>
					<li><a
						href="http://www.ityx.de/loesungen/web-self-service-faq-software.html"><img
							src="media/img/product/product-self_service.png" width="162"
							height="57" alt="Self Service" title="" /></a></li>
				</ul>
			</div>
			<!-- #product-info -->

			<ul id="documentation">
				<li class="first-child"><a href="/docs/mediatrix-api/">Mediatrix-API</a></li>
				<!-- .first-child -->
				<li><a href="/docs/contex-api/">Contex-API</a></li>
			</ul>
			<!-- #documentation -->
		</div>
		<!-- #content -->
	</div>
	<!-- #page -->
</body>
</html>