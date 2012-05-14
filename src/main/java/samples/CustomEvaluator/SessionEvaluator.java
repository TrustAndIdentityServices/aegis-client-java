package samples.CustomEvaluator;

import org.ebayopensource.aegis.*;
import org.ebayopensource.aegis.debug.Debug;
import org.ebayopensource.aegis.impl.BaseAssertionEvaluator;
import java.net.*; 
import java.text.*; 
import java.util.*; 

/**
  * Session cookie format :<br>
  * <userid>:<authNtime>:<userconfirmed>:<IDP>:<tokentype>:<browsersession>
  * E.g. : userA1:20120513141045-0700:CONFIRMED:EBAY:EBAY_COOKIE:BROWSER_SESSION
  */
public class SessionEvaluator extends BaseAssertionEvaluator
{
    // Shared state - to cache session data in the current policy eval context.
    final public static String SHARED_STATE_SESSION_DATA_KEY = "SHARED_STATE_SESSION_DATA";

    // Session object name in Environment
    final public static String SESSION_ATTR_KEY = "USERSESSION";

    public void initialize(Context context)
    {
        // Check of we have already processed the session cookie as part of this evaluation
        Object sessiondata = context.getSharedState(SHARED_STATE_SESSION_DATA_KEY);
        Debug.message("SessionEvaluator", "initialize:sessiondata="+sessiondata);
        if (sessiondata != null) {
            return;
        }
        // Process USERSESSION cookie
        HashMap<String,Object> data = new HashMap<String,Object>();
        Object stuff = context.getEnvValue(SESSION_ATTR_KEY);
        
        // Start with assuming user is not authenticated
        data.put(SessionEvaluator.AUTHENTICATED, new Boolean(false));
        
        // Session object is passed in as a string (we could have used alternatives like : HttpSession or HttpServetRequest objs
        String val = (String) stuff;
        Debug.message("SessionEvaluator", "initialize :cookie="+val);
        StringTokenizer tok = new StringTokenizer(val, ":");
        if (tok.hasMoreTokens()) {
            // VALID|INVALID
            data.put(SessionEvaluator.AUTHENTICATED, new Boolean(true));

            // Userid
            if (tok.hasMoreTokens()) {
                String s = tok.nextToken(); 
                Debug.message("SessionEvaluator", "initialize :userid="+s);
                data.put(SessionEvaluator.AUTHN_USERID, s);
            }

            // AuthN DateTime
            if (tok.hasMoreTokens()) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssZ");
                    String s = tok.nextToken(); 
                    Debug.message("SessionEvaluator", "initialize :authndt="+s);
                    Date authnDate = df.parse(s); 
                    long authnStart = authnDate.getTime();
                    int i = (int) ((System.currentTimeMillis() - authnStart)/6000); 
                    long xx = (System.currentTimeMillis() - authnStart)/6000;
                    Debug.message("SessionEvaluator", "curr="+System.currentTimeMillis()+" :authnStart="+authnStart+ " i="+i+" l="+xx);
                    Integer dursincelogin = new Integer(i);
                    data.put(SessionEvaluator.TIME_AUTHENTICATED, val);
                    data.put(SessionEvaluator.FRESHNESS_FROM_START, dursincelogin);
                } catch (Exception ex) { 
                    /* Handle error if data cannot be parsed */
                }
            }

            // User confirmed
            if (tok.hasMoreTokens()) {
                String s = tok.nextToken(); 
                Debug.message("SessionEvaluator", "initialize :confirmed="+s);
                if ("CONFIRMED".equals(s))
                    data.put(SessionEvaluator.CONFIRMED_USER, new Boolean(true));
                else
                    data.put(SessionEvaluator.CONFIRMED_USER, new Boolean(false));
            }
 
            // IDP
            if (tok.hasMoreTokens()) {
                String s = tok.nextToken(); 
                Debug.message("SessionEvaluator", "initialize :idp="+s);
                data.put(SessionEvaluator.IDENTITY_PROVIDER, s);
            }
            // Token Type
            if (tok.hasMoreTokens()) {
                String s = tok.nextToken(); 
                Debug.message("SessionEvaluator", "initialize :toktype="+s);
                data.put(SessionEvaluator.TOKEN_TYPE, s);
            }

            // Browser session
            if (tok.hasMoreTokens()) {
                String s = tok.nextToken(); 
		Debug.message("SessionEvaluator", "initialize :browsersess="+s);
                if ("BROWSER_SESSION".equals(s))
                    data.put(SessionEvaluator.BROWSER_SESSION, new Boolean(true));
                else
                    data.put(SessionEvaluator.BROWSER_SESSION, new Boolean(false));
            }
        }
        // Store processes data in shaed state 
        context.setSharedState(SHARED_STATE_SESSION_DATA_KEY, data);
    }
    public Object getValue(String id, Context context)
    {
        HashMap<String,Object> sessiondata = 
            (HashMap<String,Object> ) context.getSharedState(SHARED_STATE_SESSION_DATA_KEY);
        return sessiondata.get(id);
    }
    final public static String AUTHN_USERID = "AuthNUserId"; 
    final public static String AUTHENTICATED = "Authenticated";
    final public static String TIME_AUTHENTICATED = "TimeAuthenticated";
    final public static String TIME_REFRESHED = "TimeRefreshed";
    final public static String FRESHNESS_FROM_START = "FreshnessFromStartTime";
    final public static String FRESHNESS_FROM_LASTREFRESH = "FreshnessFromLastRefresh";
    final public static String HIGH_RISK_CHECK = "HighRiskCheck";
    final public static String BROWSER_SESSION = "BrowserSession";
    final public static String FORCE_AUTHENTICATION = "ForceAuthentication";
    final public static String CONFIRMED_USER = "ConfirmedUser";
    final public static String TOKEN_TYPE = "TokenType";
    final public static String IDENTITY_PROVIDER = "IdentityProvider";
    final public static String BUSINESS_ROLE = "BusinessRole";
}
