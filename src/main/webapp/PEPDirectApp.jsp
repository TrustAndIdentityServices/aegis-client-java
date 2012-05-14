<%@ page import="java.util.*, java.io.*, org.ebayopensource.aegis.*" %>
<h1>Aegis Sample Web Application : PEPDirect</h1>
<%
String AEGIS_BASE_DIR=null;
try {
    // Ensuring things are setup - this is just for convenience of executing this sample
    // In real situations things should be pre-wired in the platform.
    AEGIS_BASE_DIR=request.getParameter("basedir");
    if (AEGIS_BASE_DIR == null) {
        //Redirect to index.jsp if things are not setup as yet..
%>
<a href="index.jsp">basedir is null</a>
<%
        return;
        //response.sendRedirect("index.jsp");
    }

    // BEGIN
    // Establish a Session - the sample implements a simple HTTP Cookie based
    // session.
    // Check if session already exists - if not establish one via login.jsp
    // Alternative flow : You may use Aegis  Advices to check if authn is needed at all.
    if (getSession(request) ==  null)  {
        response.sendRedirect("login.jsp?"+"basedir="+AEGIS_BASE_DIR+"&returl=PEPDirectApp.jsp");
    }

    // We have a session : say hello to user
    String userid = getUserIdFromSession(request);
%>
<h2>Hello, <%=userid%></h2>
<%
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
    } else {
%>
<h2>webcmd : <%=webcmd%></h2>
<%

        // Check if user is allowed to execute the web command

        // STEP 1 : get a PDP instance - you can cache this instance

        String pdpPropertiesFile = AEGIS_BASE_DIR+"/PDPSessionPolicy.properties";
        // Get a PDP instance by passing it bootstrap info via :
        // (1) Load Properties or (2) Passing properties file name
        //
        // Method 1
        Properties props = new Properties();
        FileInputStream in = new FileInputStream(pdpPropertiesFile);
        props.load(in);
        in.close();
        PolicyDecisionPoint pdp = PolicyEnforcementPoint.getPDP(props);
        // Method 2 : 
        //PolicyDecisionPoint pdp = PolicyEnforcementPoint.getPDP(pdpPropertiesFile);

        // Step 2 : Identify Target - the protected resource
        Target resource = new Target("webcmd", webcmd);

        // Step 3 : Build Environment
        Environment env = new Environment("", "env");
        env.setAttribute("USERSESSION", getSession(request));
        env.setAttribute("userid", userid);
    
        // Step 4 : Get Decision from PDP by passing in target and environment
        Decision decision = pdp.getPolicyDecision( resource, env);
        String type = decision.getTypeStr(); // PERMIT or DENY
        List<Advice> advices = decision.getAdvices();
%>
<br>Effect :<%=type%>
<br>Complete Decision object : <%=decision.toString()%>
<%
        // Step 5 : Execute Obligations
        if (decision.getObligations() != null) {
            // Follow obligations if specified : eg to log the access
        }

        // Step 6 : [Optional] if DENY,  parse Advices to facilitate further action to 
        //          potentially PERMIT access (eg authenticate at different level);

    }
%>
<br>
<br>
<br><a href="PEPDirectApp.jsp?basedir=<%=AEGIS_BASE_DIR%>">Try another Command</a>
<br><a href="login.jsp?basedir=<%=AEGIS_BASE_DIR%>&returl=PEPDirectApp.jsp">Change Session</a>
<br><a href="index.jsp?basedir=<%=AEGIS_BASE_DIR%>">Sample Home</a>
<%
} catch (Exception ex) {
%>
Error : <%=ex%>
<%
} 
%>

<%!
/**
 * The code below this line should typicaly be in a Session api
 * It has been added inline for this sample for better clarity.
 * Our Session is in a simple cookie called USERSESSION. Format:
 *   <userid>:<datecreated>:<confirmedflag>:<idp>:<tokentype>:<browsersession>
*/
String getSession(HttpServletRequest req) throws Exception 
{
     Cookie[] cks = req.getCookies();
     String session = null;
     for (Cookie ck : cks) {
         if (ck.getName().equals("USERSESSION")) {
             if (ck.getValue() != null && ck.getValue().length() > 0)
                 session = ck.getValue();
             break;
         }
     }
     return session;
}
String getUserIdFromSession( HttpServletRequest req) throws Exception
{
     String session = getSession(req);
     if (session == null)
         return null;
     return session.substring(0, session.indexOf(":"));
}

%>
