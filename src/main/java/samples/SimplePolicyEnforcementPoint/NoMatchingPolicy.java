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
  * Users who have <b>manager</b> role are <b>PERMITTED</b> to perform action
  * <b>addItem</b> on  resource <b>http://www.ebay.com/xxx</b> provided
  * they are authenticated at <b>authlevel &gt; 2</b> and auhenticated by
  * <b>authn.idp  = EBAY</b>.
  *<p> 
  * It demonstrates the different decisions returned under the
  * following 3 scenarios:
  * <ul>
  *  <li>Subject does not match - falls back to DENY:
  *  <li>Resource does not match - policy does not match, falls back to DENY:
  *  <li>Action does not match - policy does not match, falls back to DENY:
  * </ul>
  *
  */
public class NoMAtchingPolicy
{
    static public void main(String[] args) 
    {
        try {
            Properties props = new Properties();
            URL url = ClassLoader.getSystemResource("PDP.properties");
            props.load(url.openStream());

            // Get PDP instance
            PolicyDecisionPoint pdp = PolicyEnforcementPoint.getPDP(props);
            
            // Scenarios 1,2,3 - if Subject, Resource and Actions dont match
            System.out.println("===== Scenarios 1,2,3 - no matching policy ==");
            List<Subject> emplsubjects = new ArrayList<Subject>();
            Subject emprole = new Subject("role", "employee");
            emplsubjects.add(emprole);
            Resource yyyresource = new Resource("web", "http://www.ebay.com/yyy");
            Action craction = new Action("cmd", "createItem");
            List<Environment> env1 = new ArrayList<Environment>();

            Decision decision1 = 
                pdp.getPolicyDecision(emplsubjects, yyyresource, craction, env1);

            int effect = decision1.getType();
            System.out.println("DECISION : "+ decision1.getTypeStr());
            System.out.println("decision = "+decision1);
            System.out.println("==========================================");

        } catch (Exception ex) {
            System.out.println("main:Exception:"+ex);
            ex.printStackTrace();
        }
    }
}
