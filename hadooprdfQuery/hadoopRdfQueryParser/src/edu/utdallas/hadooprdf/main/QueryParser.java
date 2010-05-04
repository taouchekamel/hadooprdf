package edu.utdallas.hadooprdf.main;
import java.util.ArrayList;
import java.util.List;

//import com.hp.hpl.jena.graph.query.Element;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;

public class QueryParser {

	private static ArrayList <HadoopElement> processElement (Element e) throws UnhandledElementException {
		
		ArrayList <HadoopElement> eList = new ArrayList<HadoopElement>();
		
		if (e instanceof ElementTriplesBlock) {
			
			HadoopElement hElement = new HadoopElement(e);
			eList.add(hElement);
			
		} else {
			throw new UnhandledElementException("Currently this type of element is not handled");			
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
	public static ArrayList <HadoopElement> parseQuery (String queryString) throws UnhandledElementException {
		
		Query query = QueryFactory.create(queryString);
		
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
	
	public static void main (String [] args) throws UnhandledElementException, NotBasicElementException {
		
		String queryString = 
			"PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
			"SELECT ?url " +
			"WHERE {" +
			"      ?contributor foaf:name ?y . " +
			"      ?y foaf:weblog ?url . " +
			"      }";
		
		ArrayList <HadoopElement> eList = QueryParser.parseQuery(queryString);
		
		for (int i = 0; i < eList.size(); i++) {
			HadoopElement element = eList.get(i);
			ArrayList<Triple> tripleList = element.getTriple();
			for (int j = 0; j < tripleList.size(); j++) {
				Triple triple = tripleList.get(j);
				System.out.println("-------------------------------------------------------");
				System.out.println("Subject -- " + triple.getSubject().toString());
				System.out.println("Predicate -- "  + triple.getPredicate().toString());
				System.out.println("Object -- " + triple.getObject().toString());
				System.out.println("-------------------------------------------------------");
				
			}
		}
	}
	
}
