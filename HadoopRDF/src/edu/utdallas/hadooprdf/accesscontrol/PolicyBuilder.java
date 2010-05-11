package edu.utdallas.hadooprdf.accesscontrol;


/**
 *
 * @author arindamkhaled
 */


import com.sun.xacml.Indenter;
import com.sun.xacml.Policy;
import com.sun.xacml.Rule;
import com.sun.xacml.Target;
import com.sun.xacml.TargetMatch;

import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.StringAttribute;

import com.sun.xacml.combine.CombiningAlgFactory;
import com.sun.xacml.combine.OrderedPermitOverridesRuleAlg;
import com.sun.xacml.combine.RuleCombiningAlgorithm;

import com.sun.xacml.cond.Apply;
import com.sun.xacml.cond.Function;
import com.sun.xacml.cond.FunctionFactory;

import com.sun.xacml.ctx.Result;

import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 */
public class PolicyBuilder
{

    /**
     * Simple helper routine that creates a TargetMatch instance.
     *
     * @param type the type of match
     * @param functionId the matching function identifier
     * @param designator the AttributeDesignator used in this match
     * @param value the AttributeValue used in this match
     *
     * @return the matching element
     */

    static int ruleCount = 1;
    public static TargetMatch createTargetMatch(int type, String functionId,
                                                AttributeDesignator designator,
                                                AttributeValue value) {
        try {
            // get the factory that handles Target functions and get an
            // instance of the right function
            FunctionFactory factory = FunctionFactory.getTargetInstance();
            Function function = factory.createFunction(functionId);

            // create the TargetMatch
            return new TargetMatch(type, function, designator, value);
        } catch (Exception e) {
            // note that in this example, we should never hit this case, but
            // in the real world you need to worry about exceptions, especially
            // from the factory
            return null;
        }
    }

    /**
     * Creates the Target used in the Policy. This Target specifies that
     * the Policy applies to any example.com users who are requesting some
     * form of access to server.example.com.
     *
     * @return the target
     *
     * @throws URISyntaxException if there is a problem with any of the URIs
     */
    public static Target createPolicyTarget(String fileName) throws URISyntaxException {
        List resources = new ArrayList();
        // create the Resource section
        List resource = new ArrayList();

        String resourceMatchId =
            "urn:oasis:names:tc:xacml:1.0:function:anyURI-equal";

        URI resourceDesignatorType =
            new URI("http://www.w3.org/2001/XMLSchema#anyURI");
        URI resourceDesignatorId =
            new URI("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
        AttributeDesignator resourceDesignator =
            new AttributeDesignator(AttributeDesignator.RESOURCE_TARGET,
                                    resourceDesignatorType,
                                    resourceDesignatorId, false);

        AnyURIAttribute resourceValue =
            new AnyURIAttribute(new URI(fileName));

        resource.add(createTargetMatch(TargetMatch.RESOURCE, resourceMatchId,
                                       resourceDesignator, resourceValue));

        // put the Subject and Resource sections into their lists
        resources.add(resource);

        // create & return the new Target
        return new Target(null, resources, null);
    }

    /**
     * Creates the Target used in the Condition. This Target specifies that
     * the Condition applies to anyone taking the action commit.
     *
     * @return the target
     *
     * @throws URISyntaxException if there is a problem with any of the URIs
     */
    public static Target createRuleTarget() throws URISyntaxException {
        List actions = new ArrayList();

        // create the Action section
        List action = new ArrayList();

        String actionMatchId =
            "urn:oasis:names:tc:xacml:1.0:function:string-equal";

        URI actionDesignatorType =
            new URI("http://www.w3.org/2001/XMLSchema#string");
        URI actionDesignatorId =
            new URI("urn:oasis:names:tc:xacml:1.0:action:action-id");
        AttributeDesignator actionDesignator =
            new AttributeDesignator(AttributeDesignator.ACTION_TARGET,
                                    actionDesignatorType,
                                    actionDesignatorId, false);

        StringAttribute actionValue = new StringAttribute("read");

        action.add(createTargetMatch(TargetMatch.ACTION, actionMatchId,
                                     actionDesignator, actionValue));

        // put the Action section in the Actions list
        actions.add(action);

        // create & return the new Target
        return new Target(null, null, actions);
    }

    /**
     * Creates the Condition used in the Rule. Note that a Condition is just a
     * special kind of Apply.
     *
     * @return the condition
     *
     * @throws URISyntaxException if there is a problem with any of the URIs
     */
    public static Apply createRuleCondition(String groupName) throws URISyntaxException {
        List conditionArgs = new ArrayList();

        // get the function that the condition uses
        FunctionFactory factory = FunctionFactory.getConditionInstance();
        Function conditionFunction = null;
        try {
            conditionFunction =
                factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:"
                                       + "string-equal");
        } catch (Exception e) {
            // see comment in createTargetMatch()
            return null;
        }

        // now create the apply section that gets the designator value
        List applyArgs = new ArrayList();

        factory = FunctionFactory.getGeneralInstance();
        Function applyFunction = null;
        try {
            applyFunction =
                factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:"
                                       + "string-one-and-only");
        } catch (Exception e) {
            // see comment in createTargetMatch()
            return null;
        }

        URI designatorType =
            new URI("http://www.w3.org/2001/XMLSchema#string");
        URI designatorId =
            new URI("group");
        URI designatorIssuer =
            new URI("akhaled@utdallas.edu");
        AttributeDesignator designator =
            new AttributeDesignator(AttributeDesignator.SUBJECT_TARGET,
                                    designatorType, designatorId, false,
                                    null);
        applyArgs.add(designator);

        Apply apply = new Apply(applyFunction, applyArgs, false);

        // add the new apply element to the list of inputs to the condition
        conditionArgs.add(apply);

        // create an AttributeValue and add it to the input list
        StringAttribute value = new StringAttribute(groupName);
        conditionArgs.add(value);

        // finally, create & return our Condition
        return new Apply(conditionFunction, conditionArgs, true);
    }

    /**
     * Creates the Rule used in the Policy.
     *
     * @return the rule
     *
     * @throws URISyntaxException if there is a problem with any of the URIs
     */
    public static Rule createRule(String groupName) throws URISyntaxException {
        // define the identifier for the rule
        URI ruleId = new URI("ReadRule" + ruleCount);
        ruleCount++;

        // define the effect for the Rule
        int effect = Result.DECISION_PERMIT;

        // get the Target for the rule
        Target target = createRuleTarget();

        // get the Condition for the rule
        Apply condition = createRuleCondition(groupName);

        return new Rule(ruleId, effect, null, target, condition);
    }

    /**
     * Command-line routine that bundles together all the information needed
     * to create a Policy and then encodes the Policy, printing to standard
     * out.
     */
    public static void main(String [] args) throws Exception {
        // define the identifier for the policy
//        System.out.println("gets in!!!!!");

        if (args.length < 2) {
            System.out.println("Usage: fileName groupName [groupNames]");
            System.exit(1);
        }

        String [] groupNames = new String[args.length - 1];

        for (int i = 1; i < args.length; i++)
            groupNames[i-1] = args[i];

        String pID = args[0];
        URI policyId = new URI(pID + "Policy");

        // get the combining algorithm for the policy
        URI combiningAlgId = new URI(OrderedPermitOverridesRuleAlg.algId);
        CombiningAlgFactory factory = CombiningAlgFactory.getInstance();
        RuleCombiningAlgorithm combiningAlg =
            (RuleCombiningAlgorithm)(factory.createAlgorithm(combiningAlgId));

        // add a description for the policy
        String description =
            "There is a " +
            "final fall-through rule that always returns Deny.";

        // create the target for the policy
        Target policyTarget = createPolicyTarget(pID);

        // create a list for the rules and add our rules in order
        List ruleList = new ArrayList();
        
        // create the rules
        for (int i = 1; i < args.length; i++)
        {
            Rule aRule = createRule(groupNames[i - 1]);
            ruleList.add(aRule);
        }

        // create the default, fall-through rule
        Rule defaultRule = new Rule(new URI("FinalRule"), Result.DECISION_DENY,
                                    null, null, null);        
        
        ruleList.add(defaultRule);

        // create the policy
        Policy policy = new Policy(policyId, combiningAlg, description,
                                   policyTarget, ruleList);

        // finally, encode the policy and print it to standard out
        String policyOutFile = pID + "Policy.xml";
        FileOutputStream out = new FileOutputStream(policyOutFile);

        policy.encode(System.out, new Indenter());
        policy.encode(out, new Indenter());
    }

}
