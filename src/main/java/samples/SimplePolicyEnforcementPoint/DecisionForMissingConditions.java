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

import org.ebayopensource.aegis.Decision;
import org.ebayopensource.aegis.Environment;
import org.ebayopensource.aegis.PolicyDecisionPoint;
import org.ebayopensource.aegis.PolicyEnforcementPoint;
import org.ebayopensource.aegis.Target;


/**
  * This sample uses the single PERMIT policy in policies.json file and
  *  is based on "deny-overrides" algorithm.
  * <p>The policy states:
  * <p> 
  * Users who have <b>manager</b> role are <b>PERMITTED</b> to perform action
  * <b>addItem</b> on  resource <b>http://www.ebay.com/xxx</b> provided
  * they are authenticated at <b>auth.level &gt; 2</b> and authenticated by
  * <b>authn.idp  = EBAY</b>.
  *<p> 
  * It demonstrates the decision returned under the following scenario:
  * <ul>
  *  <li>Subject, Resource, Action match but no authentication info provided  - policy matches, but decision is DENY with Advice on how to authn the user.
  * </ul>
  *
  */
public class DecisionForMissingConditions
{
    static public void main(String[] args) 
    {
        try {
            Properties props = new Properties();
            URL url = ClassLoader.getSystemResource("PDP.properties");
            props.load(url.openStream());

            // Get PDP instance
            PolicyDecisionPoint pdp = PolicyEnforcementPoint.getPDP(props);
            
            System.out.println("===== Scenario 4 - matching policy, no authn ==");
           
            //List<Subject> subjects = new ArrayList<Subject>();
            //Subject sub1 = new Subject("role", "manager");
            //subjects.add(sub1);
            Target target = new Target("web", "http://www.ebay.com/xxx");
            //Action action = new Action("cmd", "CMDL1");
            List<Environment> env4 = new ArrayList<Environment>();

            Decision decision4 = 
                pdp.getPolicyDecision(target, env4);

            int effect = decision4.getType();
            System.out.println("DECISION : "+ decision4.getTypeStr());
            System.out.println("decision = "+decision4);
            System.out.println("==========================================");

        } catch (Exception ex) {
            System.out.println("main:Exception:"+ex);
            ex.printStackTrace();
        }
    }
}
