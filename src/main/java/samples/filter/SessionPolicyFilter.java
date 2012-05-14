package samples.filter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import org.ebayopensource.aegis.*;

public final class SessionPolicyFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException 
    {
    }
    public void destroy() 
    {
    }
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
      throws IOException, ServletException
    {
        try {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            // Check if Aegis has been setup : AEGIS_BASE_DIR
            String AEGIS_BASE_DIR=request.getParameter("basedir");
            if (AEGIS_BASE_DIR == null)
               response.sendRedirect("index.jsp");

            // Check if we have a webcmd to protect
            String webcmd = request.getParameter("webcmd");
            // if null : nothing to protect, fall thru to app
            if (webcmd != null) {
                // Check if access is allowed
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
                String userid = getUserIdFromSession((HttpServletRequest) request);
                Object session = getSession((HttpServletRequest) request);
                Environment env = new Environment("", "env");
                if (session != null)
                    env.setAttribute("USERSESSION", getSession(request));
                if (userid != null)
                    env.setAttribute("userid", userid);
            
                // Step 4 : Get Decision from PDP by passing in target and environment
                Decision decision = pdp.getPolicyDecision( resource, env);
                int type = decision.getType(); // PERMIT or DENY
                // if PERMIT = allow access 
                if (type != Decision.EFFECT_PERMIT) {
                    // Decision is a DENY - if authentication advise, redirect to login
                    // Else show Access Denied message.
                    List<Advice> advices = decision.getAdvices();
                    if (advices != null) {
                        for (Advice advice : advices) {
                            ArrayList<CExpr> arr = advice.getAllExpr();
                            for (CExpr exp : arr ) {
                                if ("Authenticated".equals(exp.id_))
                                    response.sendRedirect("login.jsp");
                            }
                        }
                    }                
                    // Access Denied
                    PrintWriter o = response.getWriter();
                    o.println("<html><body>");
                    o.println("<h2>Filter : Access Denied for webcmd : "+webcmd+" User:"+userid+"</h2>");
                    o.println("<br><a href=\"PEPFilterApp.jsp?basedir="+AEGIS_BASE_DIR+"\" >Try another webcmd</a>");
                    o.println("</body></html>");
                    return;
                }
          }
          chain.doFilter(req, res);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

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
}
