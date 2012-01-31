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
package org.ebayopensource.aegis.sample.SimplePolicyEnforcementPoint;
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
  * It demonstrates the decision returned under the following scenario:
  * <ul>
  *  <li>Subject, Resource, Action match, authn.level is 5 and authn.idp is missing  - policy matches, decision is DENY
  s
  * </ul>
  *
  */
public class DecisionForPartialConditionMatch2
{
    static public void main(String[] args) 
    {
        try {
            Properties props = new Properties();
            URL url = ClassLoader.getSystemResource("PDP.properties");
            props.load(url.openStream());

            // Get PDP instance
            PolicyDecisionPoint pdp = PolicyEnforcementPoint.getPDP(props);
            
            // Scenario 6 - matching policy authn.level is 4 no idp
            System.out.println("===== Scenario 6 - matching policy, level=4 ==");
            List<Subject> subjects = new ArrayList<Subject>();
            Subject sub1 = new Subject("role", "manager");
            subjects.add(sub1);
            Resource resource = new Resource("web", "http://www.ebay.com/xxx");
            Action craction = new Action("cmd", "createItem");
            Action action = new Action("cmd", "addItem");
            List<Environment> env6 = new ArrayList<Environment>();
            Environment parsession = new Environment("session", "env6");
            parsession.setAttribute("authn.level", new Integer(4));
            env6.add(parsession);
            Decision decision6 = 
                pdp.getPolicyDecision(subjects, resource, action, env6);
            System.out.println("DECISION : "+ decision6.getTypeStr());
            System.out.println("decision="+decision6);
            System.out.println("==========================================");

        } catch (Exception ex) {
            System.out.println("main:Exception:"+ex);
            ex.printStackTrace();
        }
    }
}
