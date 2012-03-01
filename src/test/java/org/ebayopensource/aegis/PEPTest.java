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
package org.ebayopensource.aegis;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.ebayopensource.aegis.Decision;
import org.ebayopensource.aegis.Environment;
import org.ebayopensource.aegis.PolicyDecisionPoint;
import org.ebayopensource.aegis.PolicyEnforcementPoint;
import org.ebayopensource.aegis.Target;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
  * These tests are based on a single PERMIT policy in policies.json file and
  *  is based on "deny-overrides" algorithm.
  * <p>The policy states:
  * <p> 
  * Users who have <b>manager</b> role are <b>PERMITTED</b> to perorm action
  * <b>addItem</b> on  resource <b>http://www.ebay.com/xxx</b> provided
  * they are authenticated at <b>authlevel &gt; 2</b> and auhenticated by
  * <b>authn.idp  = EBAY</b>.
  *<p> 
  * Different decisions returned under the  following scenarios are tested:
  * <ul>
  *  <li>Target does not match - falls back to DENY:
  *  <li>Target does not match - policy does not match, falls back to DENY:
  *  <li>Target does not match - policy does not match, falls back to DENY:
  *  <li>Target match but no authentication info provided  - policy matches, but decision is DENY with Advice on how to authn the user.
  *  <li>Target match, authn.level is 1  - policy matches, but decision is DENY with Advice on how to authn the user.
  *  <li>Target match, authn.level is 4 and authn.idp is missing  - policy matches, decision is DENY
  *  <li>TArget match, authn.level is 4 and authn.idp is EBAY  - policy matches, decision is PERMIT
  s
  * </ul>
  *
  */
public class PEPTest
{
    private PolicyDecisionPoint pdp = null;
    @BeforeClass
    public static void oneTimeSetUp() {
        // one-time initialization code        
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
    }

    @Before
    public void setUp() {
        try {
            Properties props = new Properties();
            URL url = ClassLoader.getSystemResource("PDP.properties");
            props.load(url.openStream());

            // Get PDP instance
            pdp = PolicyEnforcementPoint.getPDP(props);
        } catch (Exception ex) {
            System.out.println("main:Exception:"+ex);
            ex.printStackTrace();
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testPEPScenario123() {
        // Scenarios 1,2,3 - if Subject, Resource and Actions done match
        //List<Subject> emplsubjects = new ArrayList<Subject>();
        //Subject emprole = new Subject("role", "employee");
        //emplsubjects.add(emprole);
        Target yyytarget = new Target("web", "http://www.ebay.com/yyy");
        //Action craction = new Action("cmd", "createItem");
        List<Environment> env1 = new ArrayList<Environment>();

        Decision decision1 = 
            pdp.getPolicyDecision(yyytarget, env1);

        int effect = decision1.getType();
        assertEquals(effect, Decision.EFFECT_DENY);
    }

    @Test
    public void testPEPScenario4() {
        //List<Subject> subjects = new ArrayList<Subject>();
        //Subject sub1 = new Subject("role", "manager");
        //subjects.add(sub1);
        Target target = new Target("web", "http://www.ebay.com/xxx");
        //Action action = new Action("cmd", "addItem");
        List<Environment> env4 = new ArrayList<Environment>();
        Decision decision4 = 
            pdp.getPolicyDecision(target, env4);

        int effect = decision4.getType();
        assertEquals(effect, Decision.EFFECT_DENY);

    }

    @Test
    public void testPEPScenario5() {

        //List<Subject> subjects = new ArrayList<Subject>();
        //Subject sub1 = new Subject("role", "manager");
        //subjects.add(sub1);
        List<Environment> env5 = new ArrayList<Environment>();
        Environment envsession = new Environment("session", "env5");
        envsession.setAttribute("authn.level", "1");
        env5.add(envsession);
        Target target = new Target("web", "http://www.ebay.com/xxx");
        //Action action = new Action("cmd", "addItem");
        Decision decision5 = 
            pdp.getPolicyDecision(target, env5);
        int effect = decision5.getType();
        assertEquals(effect, Decision.EFFECT_DENY);
    }
    @Test
    public void testPEPScenario6() {
        //List<Subject> subjects = new ArrayList<Subject>();
        //Subject sub1 = new Subject("role", "manager");
        //subjects.add(sub1);
        List<Environment> env6 = new ArrayList<Environment>();
        Environment parsession = new Environment("session", "env6");
        parsession.setAttribute("authn.level", "4");
        env6.add(parsession);
        Target target = new Target("web", "http://www.ebay.com/xxx");
        //Action action = new Action("cmd", "addItem");
        Decision decision6 = 
            pdp.getPolicyDecision(target, env6);
        int effect = decision6.getType();
        assertEquals(effect, Decision.EFFECT_DENY);
     }
    @Test
    public void testPEPScenario7() {

        //List<Subject> subjects = new ArrayList<Subject>();
        //Subject sub1 = new Subject("role", "manager");
        //subjects.add(sub1);
        List<Environment> env7 = new ArrayList<Environment>();
        Environment goodsession = new Environment("session", "env7");
        goodsession.setAttribute("authn.level", "4");
        goodsession.setAttribute("authn.idp", "EBAY");
        env7.add(goodsession);
        Target target = new Target("web", "http://www.ebay.com/xxx");
        //Action action = new Action("cmd", "addItem");
        Decision decision7 = 
            pdp.getPolicyDecision(target, env7);
        int effect = decision7.getType();
        assertEquals(effect, Decision.EFFECT_PERMIT);
    }
}
