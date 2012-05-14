<%@ page import="java.util.*, java.io.*" %>
<html>
<body>
<h1>Aegis Sample Web Application : Overview</h1>
<p>This application protects its resources (<b>web commands</b>) based on <b>session policy</b></p>
<p>The sample touches on concerns of five classes of people, so please be aware of what specific tasks lies in which class of users: 
<li> <i>Business Owner</i> : Responsible for describing policies and the vocabulary, as well as their ongoing governance and lifecycle
<li> <i>Policy Administrator</i> : Responsible for expressing policies as Aegis policies
<li> <i>Policy Plugin Developer</i>: Typically a platform/framework developer responsible for Aegis plugins.
<li> <i>Platform/Framework Deployer</i> : Responsible of setting up the appropriate environment for applications to execute under
<li> <i>Application Developer</i> : Responsible for app development and appropriately invoking Aegis PEP.
</p>
<%
String AEGIS_BASE_DIR=null;
try {
    AEGIS_BASE_DIR=request.getParameter("basedir");
    if (AEGIS_BASE_DIR == null) {
        // Default to home directory
        String home = System.getProperty("user.home")+"/aegis";
%>
        <p>Setup Needed : An initial onetime setup is needed to bootstrap this sample - in real world situations, this is part of <i>Platform Deployer</i> tasks.
        <form>
        Enter Aegis Base Directory (any non existant directory to store sample policies, metadata and group info)  : <input name='basedir' type="text" value="<%=home%>" /><br>
        <input type="submit" value="Setup" />
        </p>
<%      return;
    } else {
       setUp(AEGIS_BASE_DIR);
   }
} catch (Exception ex) {
%>
 <p><b>Setup Error : <%=ex%></b></p>
<%
 throw ex;
}
%>
<p>Data for this sample is being read from: <b><code><%=AEGIS_BASE_DIR%> directory</b></code></p>
<p><b>Sample Policies</b> (<code><%=AEGIS_BASE_DIR%>/SessionPolicies.json</code>)</p>
<code>
<%

try {
    BufferedReader istr = new BufferedReader(new FileReader(AEGIS_BASE_DIR+"/SessionPolicies.json"));
    String pol = null;
    while ((pol = istr.readLine()) != null) {
       out.println(pol+"<br><br>");
    }
    istr.close();
} catch (Exception ex) {
    out.println("Could not load Session Policies:"+ex);
    return;
}
%>
</code>
<p><code>webcmdGroup</code> and <code>BusinessRole</code> membership specfied in <code>attrgroups.txt</code> file and <code>groupmember/</code> directory</p>
<p><b>MetaData</b> (<code><%=AEGIS_BASE_DIR%>/MetaData.properties</code> )</p>
<p> The MetaData is authored by a <i>Policy Plugin Developer</i> with input from <i>Business Owners</i>. It encapsulates the vocabulary used to describe policy, facilitates mapping of runtime artifacts (eg attributes and sessions in this sample) and provides the plugin implementation classes.
Following plugins are used in this sample :
<li> <code>FlatFile</code> plugin to evaluate group memberships for <code>webcmdGroup</code> and <code>BusinessRole</code> membership. This plugin is provided as part of core Aegis jar.
<li> custom <code>SessionCookie</code> based assertion plugin for evaluating other session attributes in the policy. This plugin is part of this sample.
</p>

<p><b>Policy Enforcement</b>:
<p>Two PEP mechanisms are demonstrated :
<li>Programmatic - <i>Application developer</i> directly invokes Aegis Policy Enforcement Point API : <a href="PEPDirectApp.jsp?basedir=<%=AEGIS_BASE_DIR%>">PEPDirectApp</a>
<li>Http Servlet Filter provided by the platform invokes Aegis Policy Enforcement Point API : <a href="PEPFilterApp.jsp?basedir=<%=AEGIS_BASE_DIR%>">PEPFilterApp</a>
</p>
</body>
</html>
<%!
void setUp(String AEGIS_BASE_DIR) throws Exception 
{
    // Bootstrapping function toi make the sample work - should not be app developers concern
    File basegrpdir = new File(AEGIS_BASE_DIR+"/groupmembers");
    if (!basegrpdir.exists()) {
        basegrpdir.mkdirs();
        File aegisbasedir = new File(AEGIS_BASE_DIR);
        // Copy PDP props file
        copyFile(aegisbasedir, "PDPSessionPolicy.properties", true);
        // TAG swap %%BASEDIR%%
        
        // Copy policy file
        copyFile(aegisbasedir, "SessionPolicies.json", false);
        // Copy MetaData file
        copyFile(aegisbasedir, "MetaDataSessionPolicy.properties", false);

        // Copy group file and  memberships
        copyFile(aegisbasedir, "attrgroups.txt", false);
        String files[] = { "BR_BUYER.txt", "BR_SELLER.txt", "WCG_L0CMD.txt", "WCG_L1CMD.txt", "WCG_L2CMD.txt" };
        for (String f : files) {
            copyFile(aegisbasedir, "groupmembers/"+f, false);
        }

    }

}
void copyFile(File basedir, String filename, boolean tagswap) throws Exception 
{
        System.out.println("copyFile : basedir="+basedir+" filename="+filename);
        BufferedReader istr = null;
        PrintStream ostr = null;
        istr = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream( filename)));
        ostr = new PrintStream(new File(basedir,filename));
        String line = null;
        while ((line = istr.readLine()) != null)
        {
            if (tagswap) {
                line = line.replaceAll("%%BASEDIR%%", basedir.getAbsolutePath());
            }
            ostr.println(line);
        }
        istr.close();
        ostr.close();
}
%>
