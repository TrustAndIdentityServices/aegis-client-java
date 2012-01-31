/*******************************************************************************
 * 
 *  Copyright (c) 2006-2012 eBay Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
*******************************************************************************/
package samples.SimplePolicyEnforcementPoint;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.ebayopensource.aegis.Action;
import org.ebayopensource.aegis.Decision;
import org.ebayopensource.aegis.Environment;
import org.ebayopensource.aegis.PolicyDecisionPoint;
import org.ebayopensource.aegis.PolicyEnforcementPoint;
import org.ebayopensource.aegis.Resource;
import org.ebayopensource.aegis.Subject;

/**
  * This sample uses the single PERMIT policy in policies.json file and
  *  is based on "deny-overrides" algorithm.
  * <p>The policy states:
  * <p> 
  * Users who have <b>manager</b> role are <b>PERMITTED</b> to perorm action
  * <b>addItem</b> on  resource <b>http://www.ebay.com/xxx</b> provided
  * they are authenticated at <b>authlevel &gt; 2</b> and auhenticated by
  * <b>authn.idp  = EBAY</b>.
  *<p> 
  * It demonstrates the decision returned under the  following scenario:
  * <ul>
  *  <li>Subject, Resource, Action match, authn.level is 1  - policy matches, but decision is DENY with Advice on how to authn the user.
  * </ul>
  *
  */
public class DecisionForPartialConditionMatch1
{
    static public void main(String[] args) 
    {
        try {
            Properties props = new Properties();
            URL url = ClassLoader.getSystemResource("PDP.properties");
            props.load(url.openStream());

            // Get PDP instance
            PolicyDecisionPoint pdp = PolicyEnforcementPoint.getPDP(props);
            
            // Scenarios 5 - Policy match - authn at level 1

            List<Subject> subjects = new ArrayList<Subject>();
            Subject sub1 = new Subject("role", "manager");
            subjects.add(sub1);
            System.out.println("===== Scenario 5 - matching policy, authn.level=1 ==");
            Resource resource = new Resource("web", "http://www.ebay.com/xxx");
            Action action = new Action("cmd", "addItem");
            List<Environment> env5 = new ArrayList<Environment>();
            Environment envsession = new Environment("session", "env5");
            envsession.setAttribute("authn.level", new Integer(1));
            env5.add(envsession);
            Decision decision5 = 
                pdp.getPolicyDecision(subjects, resource, action, env5);
            System.out.println("DECISION : "+ decision5.getTypeStr());
            System.out.println("decision="+decision5);

            System.out.println("==========================================");

        } catch (Exception ex) {
            System.out.println("main:Exception:"+ex);
            ex.printStackTrace();
        }
    }
}
