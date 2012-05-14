<%@ page import="java.util.*, java.io.*, org.ebayopensource.aegis.*" %>
<h1>Aegis Sample Web Application : PEPFilter</h1>
<%
String AEGIS_BASE_DIR=null;
try {
    AEGIS_BASE_DIR=request.getParameter("basedir");
    // get webcommand to be executed -  if not, prompt for one
    String webcmd = request.getParameter("webcmd");
    if (webcmd == null) {
    
%>
<form>
<br>Enter a web command to execute : <input name="webcmd" type="text" value="cmdA1" />
<br>E.g :L0 commands : cmdA1, cmdA2; L1 commands : cmdB1, cmdB2; L2 commands : cmdC1, cmdC2
<br><input type="submit"/>
<br><input type="hidden" name="basedir" value="<%=AEGIS_BASE_DIR%>" value="cmdA1" />
</form>
<%
        return;
    }
%>
<h2>webcmd : <%=webcmd%> IS PERMITTED to be EXECUTED</h2>
<br>
<br>
<br><a href="PEPFilterApp.jsp?basedir=<%=AEGIS_BASE_DIR%>">Try another Command</a>
<br><a href="login.jsp?basedir=<%=AEGIS_BASE_DIR%>&returl=PEPFilterApp.jsp">Change Session</a>
<br><a href="index.jsp?basedir=<%=AEGIS_BASE_DIR%>">Sample Home</a>
<%
} catch (Exception ex) {
%>
Error : <%=ex%>
<%
} 
%>
