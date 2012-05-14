<%@ page import="java.util.*, java.io.*, org.ebayopensource.aegis.*" %>
<h1>Aegis Sample Web Application : Authenticate User</h1>
<%
String AEGIS_BASE_DIR=null;
try {
    // Ensuring things are setup - this is just for convenience of executing this sample
    // In real situations things should be pre-wired in the platform.
    AEGIS_BASE_DIR=request.getParameter("basedir");
    if (AEGIS_BASE_DIR == null) {
        //Redirect to index.jsp if things are not setup as yet..
        response.sendRedirect("index.jsp");
    }

    // BEGIN
    if (request.getMethod().equals("GET")) {
        // Display Form
       String currdate =  dateString(0);
       // Values for convenience dates are different times before now
       String dts = "-500mts :"+ dateString(-500) + "; -1000mts :"+ dateString(-1000) +
                    "; -2000mts  : "+dateString(-2000)+ "; -30000mts : "+dateString(-30000)+
                    "; -52000mts  : "+dateString(-52000)+ "; -60000mts : "+dateString(-60000);

%>
        <form method="POST">
          <table>
            <tr><td>Userid : (E.g. userA1, userB2) </td><td><input name="userid" type="text" value="userA1" /></td></tr> 
            <tr><td>CreateDate (Format: yyyyMMddHHmmssZ) :</td><td><input size="25" name="crdate" type="text" value="<%=currdate%>"/><%=dts%></td></tr> 
            <tr><td>Confirmed User :</td><td><select name="confirmed"><option>CONFIRMED</option><option>UNCONFIRMED</option></select></td></tr> 
            <tr><td>IDP :</td><td><input name="idp" type="text" value="EBAY"/></td></tr> 
            <tr><td>Token Type :</td><td><input name="tokentype" type="text" value="EBAY_COOKIE"/></td></tr> 
            <tr><td>Session Mechanism :</td><td><select name="browsersession"><option>BROWSER_SESSION</option><option></option></select></td></tr> 
            <tr><td span=2><input type="submit"/></td></tr>
          <table>
        </form>
<%        
        return;
    }
    // Form POST
    // get the return url
    String returl = request.getParameter("returl");
    // Get session params
    String userid = request.getParameter("userid");
    String crdate = request.getParameter("crdate");
    String confirmed = request.getParameter("confirmed");
    String idp = request.getParameter("idp");
    String tokentype = request.getParameter("tokentype");
    String browsersession = request.getParameter("browsersession");
    // Establish a Session - the sample implements a simple HTTP Cookie based
    // Format : <userid>:<datecreated>:<confirmedflag>:<idp>:<tokentype>:<browsersession>
    String sessionVal = userid+":"+crdate+":"+confirmed+":"+idp+":"+tokentype+":"+browsersession;
    Cookie sessionCookie = new Cookie("USERSESSION", sessionVal);
    response.addCookie(sessionCookie);
    if (returl != null)
        response.sendRedirect(returl+"?basedir="+AEGIS_BASE_DIR);
%>
<h2>Hello : <%=userid%></h2>
<%
} catch (Exception ex) {
%>
Error : <%=ex%>
<%
} 
%>
<%!
String dateString(long dur)
{
    java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMddHHmmssZ");
    long currTime = System.currentTimeMillis();
    long t = currTime + (dur*60*1000);
    Date d = new Date(t);
    return df.format(d);
}
%>
