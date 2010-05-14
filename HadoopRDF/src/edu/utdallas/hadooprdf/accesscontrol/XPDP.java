package edu.utdallas.hadooprdf.accesscontrol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.xacml.ConfigurationStore;
import com.sun.xacml.Indenter;
import com.sun.xacml.PDP;
import com.sun.xacml.PDPConfig;
import com.sun.xacml.ParsingException;
import com.sun.xacml.cond.FunctionFactory;
import com.sun.xacml.cond.FunctionFactoryProxy;
import com.sun.xacml.cond.StandardFunctionFactory;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.AttributeFinderModule;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.impl.CurrentEnvModule;
import com.sun.xacml.finder.impl.FilePolicyModule;
import com.sun.xacml.finder.impl.SelectorModule;

/**
 * This is the policy decision point. The resource or file is provided along
 * with the user name This code is modified from Sun's XACML implementation
 * 
 * @author Arindam Khaled
 */
public class XPDP {

	// this is the actual PDP object we'll use for evaluation
	private PDP pdp = null;

	/**
	 * Default constructor. This creates a <code>SimplePDP</code> with a
	 * <code>PDP</code> based on the configuration defined by the runtime
	 * property com.sun.xcaml.PDPConfigFile.
	 */
	public XPDP() throws Exception {
		// load the configuration
		ConfigurationStore store = new ConfigurationStore();

		// use the default factories from the configuration
		store.useDefaultFactories();

		// get the PDP configuration's and setup the PDP
		pdp = new PDP(store.getDefaultPDPConfig());
	}

	/**
	 * Constructor that takes an array of filenames, each of which contains an
	 * XACML policy, and sets up a <code>PDP</code> with access to these
	 * policies only. The <code>PDP</code> is configured programatically to have
	 * only a few specific modules.
	 * 
	 * @param policyFiles
	 *            an arry of filenames that specify policies
	 */
	public XPDP(String[] policyFiles) throws Exception {
		// Create a PolicyFinderModule and initialize it...in this case,
		// we're using the sample FilePolicyModule that is pre-configured
		// with a set of policies from the filesystem
		FilePolicyModule filePolicyModule = new FilePolicyModule();
		for (int i = 0; i < policyFiles.length; i++)
			filePolicyModule.addPolicy(policyFiles[i]);

		// next, setup the PolicyFinder that this PDP will use
		PolicyFinder policyFinder = new PolicyFinder();
		Set<FilePolicyModule> policyModules = new HashSet<FilePolicyModule>();
		policyModules.add(filePolicyModule);
		policyFinder.setModules(policyModules);

		// now setup attribute finder modules for the current date/time and
		// AttributeSelectors (selectors are optional, but this project does
		// support a basic implementation)
		CurrentEnvModule envAttributeModule = new CurrentEnvModule();
		SelectorModule selectorAttributeModule = new SelectorModule();

		// Setup the AttributeFinder just like we setup the PolicyFinder. Note
		// that unlike with the policy finder, the order matters here. See the
		// the javadocs for more details.
		AttributeFinder attributeFinder = new AttributeFinder();
		List<AttributeFinderModule> attributeModules = new ArrayList<AttributeFinderModule>();
		attributeModules.add(envAttributeModule);
		attributeModules.add(selectorAttributeModule);
		attributeFinder.setModules(attributeModules);

		// Try to load the time-in-range function, which is used by several
		// of the examples...see the documentation for this function to
		// understand why it's provided here instead of in the standard
		// code base.
		FunctionFactoryProxy proxy = StandardFunctionFactory
				.getNewFactoryProxy();
		FunctionFactory factory = proxy.getConditionFactory();
		factory.addFunction(new TimeInRangeFunction());
		FunctionFactory.setDefaultFactory(proxy);

		// finally, initialize our pdp
		pdp = new PDP(new PDPConfig(attributeFinder, policyFinder, null));
	}

	/**
	 * Evaluates the given request and returns the Response that the PDP will
	 * hand back to the PEP.
	 * 
	 * @param requestFile
	 *            the name of a file that contains a Request
	 * 
	 * @return the result of the evaluation
	 * 
	 * @throws IOException
	 *             if there is a problem accessing the file
	 * @throws ParsingException
	 *             if the Request is invalid
	 */
	public ResponseCtx evaluate(String requestFile) throws IOException,
			ParsingException {
		// setup the request based on the file
		RequestCtx request = RequestCtx.getInstance(new FileInputStream(
				requestFile));

		// evaluate the request
		return pdp.evaluate(request);
	}

	/**
	 * 
	 * @param XMLGroupFile
	 *            : groups.xml
	 * @param resource
	 *            : the requesting resource
	 * @param pdp
	 *            : this
	 * @param user
	 *            : the requesting user
	 * @param permit
	 *            : The most important element. The permit[0] is set true when
	 *            permit and permit[0] is set false when the access to resource
	 *            is denied.
	 * @throws FileNotFoundException
	 * @throws URISyntaxException
	 * @throws Exception
	 */
	public void run(String XMLGroupFile, String resource, XPDP pdp,
			String user, boolean[] permit) throws FileNotFoundException,
			URISyntaxException, Exception {
		permit[0] = false;
		RequestBuilder r = new RequestBuilder();
		ArrayList<String> outNames = new ArrayList<String>();
		r.run(XMLGroupFile, user, resource, outNames);
		Date Ts = new Date();
		String rs = Double.toString(Ts.getTime());

		for (int i = 0; i < outNames.size() && !permit[0]; i++) {
			// System.out.println(outNames.get(i));
			ResponseCtx response = pdp.evaluate(outNames.get(i));
			String responseOutFile = resource + rs + "Response.xml";
			FileOutputStream out = new FileOutputStream(responseOutFile);
			response.encode(out, new Indenter());

			FileReader fr = new FileReader(responseOutFile);
			BufferedReader br = new BufferedReader(fr);
			String record = new String();
			Pattern p = Pattern.compile("(?:<Decision>)(.*?)(:?</Decision>)");
			Matcher m;

			while ((record = br.readLine()) != null && !permit[0]) {
				m = p.matcher(record);
				while (m.find()) {
					if (m.group(1).toString().equals("permit"))
						;
					permit[0] = true;
				}
			}

			response.encode(System.out, new Indenter());
			File f = new File(responseOutFile);
			if (f.exists())
				f.delete();
		}

		// Deleting all the files
		for (int i = 0; i < outNames.size(); i++) {
			File f = new File(outNames.get(i));
			if (f.exists())
				f.delete();
		}
	}

	/**
	 * 
	 * @param args
	 *            [0] resource; args[1] the requesting user
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			System.out.println("Usage: resource user");
			System.exit(1);
		}

		String resource = args[0];
		String user = args[1];
		String policyFile = resource + "Policy.xml";
		String[] policyFiles = new String[1];
		policyFiles[0] = policyFile;
		XPDP simplePDP = new XPDP(policyFiles);
		boolean[] permit = new boolean[1];

		simplePDP.run("groups.xml", resource, simplePDP, user, permit);
		System.out.println(permit[0]);
	}

}
