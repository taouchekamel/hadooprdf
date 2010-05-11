package edu.utdallas.hadooprdf.main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//import com.hp.hpl.jena.graph.query.Element;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;

public class QueryParser {

	private static int noOfVaraiables = -1;
	private static List <String> resVars = null;
	
	public static int getNumOfVarsInQuery () throws Exception { 
		
		if (noOfVaraiables == -1) {
			throw new Exception ("Query is not parsed yet"); 
		}
		
		return noOfVaraiables;
	}
	
	public static List<String> getVars () throws Exception {
		if (noOfVaraiables == -1) {
			throw new Exception ("Query is not parsed yet"); 
		}
		return resVars;
	}
	
	
	private static ArrayList <HadoopElement> processElement (Element e) throws UnhandledElementException {
		
		ArrayList <HadoopElement> eList = new ArrayList<HadoopElement>();
		
		if (e instanceof ElementTriplesBlock) {
			
			HadoopElement hElement = new HadoopElement(e);
			eList.add(hElement);
			
		} else {
			
			if (e instanceof ElementGroup) {
				//e.
				
			} else {				
				throw new UnhandledElementException("Currently this type of element is not handled");
			}
		}
		
		return eList;
	}
	
	
	private static ArrayList <HadoopElement> parseQueryTree (ElementGroup eGrp) throws UnhandledElementException {
	
		ArrayList <HadoopElement> hdpElementList = new ArrayList<HadoopElement>();		
		
		List<Element> elementList = eGrp.getElements();
		
		for (int i = 0; i < elementList.size(); i++) {
			Element e = elementList.get(i);
			
			ArrayList <HadoopElement> tList = null;
			try {
				tList = QueryParser.processElement(e);
				hdpElementList.addAll(tList);
			} catch (UnhandledElementException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw e1;
			}
		}
		
		return hdpElementList;
	}
	
	private static HashMap <String, String> getNsPrefixMap (Query query) {
		HashMap <String, String> nsPrefixMap = new HashMap<String, String> ();
		PrefixMapping map = query.getPrefixMapping();
		System.out.println (map.toString());
		Set<String> keySet = map.getNsPrefixMap().keySet();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()) {
			String key = it.next();
			String prefix = map.getNsPrefixURI(key);
			nsPrefixMap.put(key, prefix);
		}		
		
		return nsPrefixMap;		
	}
	
	public static edu.utdallas.hadooprdf.main.Query parseQuery (String queryString) throws UnhandledElementException {
		
		Query query = QueryFactory.create(queryString);
		noOfVaraiables = query.getResultVars().size();
		resVars = query.getResultVars();
		
		
		ElementGroup elementGrp = (ElementGroup)query.getQueryPattern();
		HashMap <String, String> prefixMap = getNsPrefixMap (query);
		ArrayList <HadoopElement> eList = null;
		try {
			eList = parseQueryTree (elementGrp);
		} catch (UnhandledElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		edu.utdallas.hadooprdf.main.Query q = new edu.utdallas.hadooprdf.main.Query (eList, prefixMap);
		
		return q;
	}
	
}
