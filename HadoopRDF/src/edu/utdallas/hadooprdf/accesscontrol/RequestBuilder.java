package edu.utdallas.hadooprdf.accesscontrol;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author arindamkhaled
 */

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.Indenter;

import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.StringAttribute;

import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Subject;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;

public class RequestBuilder
{

    /**
     * Sets up the Subject section of the request. This Request only has
     * one Subject section, and it uses the default category. To create a
     * Subject with a different category, you simply specify the category
     * when you construct the Subject object.
     *
     * @return a Set of Subject instances for inclusion in a Request
     *
     * @throws URISyntaxException if there is a problem with a URI
     */
    public static Set setupSubjects(String userName, String groupName) throws URISyntaxException {
        HashSet attributes = new HashSet();

        // setup the id and value for the requesting subject
        URI subjectId =
            new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
        StringAttribute value =
            new StringAttribute(userName);

        // create the subject section with two attributes, the first with
        // the subject's identity...
        attributes.add(new Attribute(subjectId, null, null, value));
        // ...and the second with the subject's group membership
        attributes.add(new Attribute(new URI("group"),
                                     "admin@users.example.com", null,
                                     new StringAttribute(groupName)));

        // bundle the attributes in a Subject with the default category
        HashSet subjects = new HashSet();
        subjects.add(new Subject(attributes));

        return subjects;
    }

    /**
     * Creates a Resource specifying the resource-id, a required attribute.
     *
     * @return a Set of Attributes for inclusion in a Request
     *
     * @throws URISyntaxException if there is a problem with a URI
     */
    public static Set setupResource(String resourceName) throws URISyntaxException {
        HashSet resource = new HashSet();

        // the resource being requested
        AnyURIAttribute value =
            new AnyURIAttribute(new URI(resourceName));

        // create the resource using a standard, required identifier for
        // the resource being requested
        resource.add(new Attribute(new URI(EvaluationCtx.RESOURCE_ID),
                                   null, null, value));

        return resource;
    }

    /**
     * Creates an Action specifying the action-id, an optional attribute.
     *
     * @return a Set of Attributes for inclusion in a Request
     *
     * @throws URISyntaxException if there is a problem with a URI
     */
    public static Set setupAction() throws URISyntaxException {
        HashSet action = new HashSet();

        // this is a standard URI that can optionally be used to specify
        // the action being requested
        URI actionId =
            new URI("urn:oasis:names:tc:xacml:1.0:action:action-id");

        // create the action
        action.add(new Attribute(actionId, null, null,
                                 new StringAttribute("read")));

        return action;
    }

    //XMLFile: contains the groups and their members
    //User: the user requesting
    //file: the requested resource
    public void run(String XMLFile, String user, String file, ArrayList <String> outFileNames) throws FileNotFoundException, URISyntaxException
    {
        ExtractGroupNames gp = new ExtractGroupNames(XMLFile, user);
        gp.run();
        ArrayList groups = gp.getGroups();
//        System.out.println(groups.size());

        Date Ts = new Date();
        String rs = Double.toString(Ts.getTime());
//        System.out.println(rs);

        for(int i = 0; i < groups.size(); i++)
        {
            String requestOutFile = groups.get(i) + rs + "Request.xml";
            outFileNames.add(requestOutFile);
            FileOutputStream out = new FileOutputStream(requestOutFile);

            RequestCtx request =
            new RequestCtx(setupSubjects(user,(String) groups.get(i)), setupResource(file),
                           setupAction(), new HashSet());

            request.encode(out, new Indenter());
        }
    }

    /**
     * Command-line interface that creates a new Request by invoking the
     * static methods in this class. The Request has no Environment section.
     */
    public static void main(String [] args) throws Exception {

    	if (args.length != 2) {
            System.out.println("Usage: file user");
            System.exit(1);
        }

    	String file = args[0];
    	String user = args[1];
//    	String group = args[2];

        // create the new Request...note that the Environment must be specified
        // using a valid Set, even if that Set is empty

        RequestBuilder r = new RequestBuilder();
        ArrayList <String> outNames = new ArrayList();
        r.run("groups.xml", user, file, outNames);
    }

}
