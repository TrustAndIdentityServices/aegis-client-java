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
package org.ebayopensource.aegis.mock;

import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.aegis.Assertion;
import org.ebayopensource.aegis.Effect;
import org.ebayopensource.aegis.Expression;
import org.ebayopensource.aegis.Policy;
import org.ebayopensource.aegis.PolicyStore;
import org.ebayopensource.aegis.Rule;
import org.ebayopensource.aegis.Target;

/**
  * Mock for a single policy :
  * <code>
  * Policy :
  *  {
  *     Subjects : { ANY_OF { Subject : {  type:"role", name : "manager"} } },
  *     Resources : { Resource : {  type:"web", name : "http://www.ebay.com/xxx"}},
  *     Actions : { Action : {  type:"webcmd", name : "addItem"}},
  *     Conditions : { ANY_OF { type : authn, name : authncondition1, ( authn.level > 2 ) AND ( authn.idp = EBAY )}},
  *     effect=PERMIT
  *  }
  * </code>
  */
public class MockOnePolicyDataStore implements PolicyStore
{
    public Policy getPolicy(String id) 
    {
        return null;
    }
    public List<Policy> getAllPolicies()
    {
        //Expression<Subject> subjects = new Expression<Subject>();
        //Subject sub1 = new Subject("role", "manager");
        //subjects.add(sub1);

        List<Target> targets = new ArrayList<Target>();
        Target res1 = new Target("web","http://www.ebay.com/xxx" );
        targets.add(res1);

        Target action1 = new Target("webcmd", "addItem");
        targets.add(action1);

        Effect effect = new Effect(Effect.PERMIT);

        Expression<Assertion> assertions = new Expression<Assertion>();
        Assertion a1 = new Assertion("authn", "authncondition1");
        a1.setCExpr("authn.level", Assertion.OP_GT,  new Integer(2));
        Assertion a2 = new Assertion("authn", "authncondition2");
        a2.setCExpr("authn.idp", Assertion.OP_EQ,  "EBAY");

        assertions.add(a1);
        assertions.add(a2);
        
        Rule r = new Rule("rule1", "desc", assertions);
        Expression<Rule> rexp = new Expression<Rule>();
        rexp.add(r);

        Policy pol1 = new Policy("samplePolicy", "Sample Policy", targets, rexp, effect);

        List<Policy> policies = new ArrayList<Policy>();
        policies.add(pol1);
        return policies;
    }
    public void createPolicy(Policy policy)
    {
    }
    public void updatePolicy(Policy policy)
    {
    }
    public void deletePolicy(Policy policy)
    {
    }
}
