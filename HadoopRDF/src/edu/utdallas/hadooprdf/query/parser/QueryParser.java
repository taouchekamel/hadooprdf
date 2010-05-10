package edu.utdallas.hadooprdf.query.parser;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
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
	public static List <HadoopElement> parseQuery (String queryString) throws UnhandledElementException {
		
		Query query = QueryFactory.create(queryString);
		noOfVaraiables = query.getResultVars().size();
		resVars = query.getResultVars();
		
		ElementGroup elementGrp = (ElementGroup)query.getQueryPattern();
			
		ArrayList <HadoopElement> eList = null;
		try {
			eList = parseQueryTree (elementGrp);
		} catch (UnhandledElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		return eList;
	}
}
