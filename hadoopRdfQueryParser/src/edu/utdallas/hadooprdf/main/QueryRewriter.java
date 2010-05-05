package edu.utdallas.hadooprdf.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Node_Literal;
import com.hp.hpl.jena.graph.Node_URI;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.util.NodeFactory;
import com.hp.hpl.jena.vocabulary.RDF;



public class QueryRewriter {
	

	private static ArrayList<Integer> getIndicesOfTriplesTobeChanged (int index, ArrayList<Triple> tripleList) {
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		
		indexList.add(index);
		for (int i = 0; i < tripleList.size(); i++) { 
			if (tripleList.get(i).getPredicate().hashCode() != 
				tripleList.get(index).getPredicate().hashCode()){
				
				if (tripleList.get(i).getObject().hashCode() == 
					tripleList.get(index).getSubject().hashCode()) {
					indexList.add(i);
				}				
			}
		}		
		
		return indexList;		
	}
	
	private static HashMap <Integer, Triple> createTripleListMap (ArrayList<Integer> indices, 
				List<Triple> tripleList) throws Exception {
		HashMap <Integer, Triple> tripleListMap = new HashMap<Integer, Triple>();
	
		tripleListMap.put(indices.get(0), tripleList.get(indices.get(0)));
		
		
		for (int i = 1; i < indices.size(); i++) {					
			String URI = tripleList.get(indices.get(i)).getPredicate().getURI() + 
										"_" + 
										tripleList.get(indices.get(0)).getObject().toString();
			Node predicate =  Node_Literal.createURI(URI);
			
			Triple triple = new Triple (tripleList.get(indices.get(i)).getSubject(), 
										predicate, tripleList.get(indices.get(i)).getObject());
			tripleListMap.put(indices.get(i), triple);			
		}
		
		return tripleListMap;
	}
	
	private static HashMap <Integer, Triple> createTripleMap (int index, Triple triple) {
		
		HashMap <Integer, Triple> tripleMap = new HashMap<Integer, Triple>();

		Model model = ModelFactory.createDefaultModel();
		
		
		String URI = triple.getPredicate().getURI() + "_" + triple.getObject().toString();
		model.createResource(triple.getSubject().toString()).
		addProperty(model.createProperty(URI), triple.getObject().toString());	
		
		Node predicate = Node_Literal.createURI(URI);
		
		
		Triple tTriple = new Triple (triple.getSubject(), predicate, triple.getObject());
		
		tripleMap.put(index, tTriple);
		
		return tripleMap;
	}
	
	private static boolean shouldTripleBePartofQuery (int index, ArrayList<Integer> indicesOfTripleToBeRemoved) {
		
		for (int i = 0; i < indicesOfTripleToBeRemoved.size(); i++) {
			if (index == indicesOfTripleToBeRemoved.get(i))
				return false;
			
		}
		return true;
	}
	private static HadoopElement createQueryElement (HashMap <Integer, Triple>  tripleListMap, 
				ArrayList<Integer> indicesOfTripleToBeRemoved) {
		
		ArrayList<Triple> tripleList = new ArrayList<Triple>();
	
		
		for (int i = 0; i < tripleListMap.size(); i++) {
			if (shouldTripleBePartofQuery (i, indicesOfTripleToBeRemoved) == true) {
				tripleList.add(tripleListMap.get(i));
			}
		}
				
		
		
		System.out.println("Triple List Size -- " + tripleList.size());
		
		HadoopElement element = new HadoopElement (tripleList);
		
		return element;
		
	}
	
	
	
	private static ArrayList<HadoopElement>
		rewriteBasicElement (List<HadoopElement> queryElementList)
										throws Exception {
		
		ArrayList<HadoopElement> hElementList = new ArrayList<HadoopElement>();
		
		//System.out.println(queryElementList.size());
		for (int i = 0; i < queryElementList.size(); i++) {
			HadoopElement element = queryElementList.get(i);
			
			ArrayList<Triple> tripleList = null;
			try {
				tripleList = element.getTriple();
			} catch (NotBasicElementException e) {
				throw new UnhandledElementException (e.getMessage());
			}
			
			HashMap<Integer,Triple> tripleListMap = new HashMap<Integer, Triple>(); 
			ArrayList<Integer> TripleIndexToBeRemoved = new ArrayList<Integer>();

			for (int  j = 0; j < tripleList.size(); j++) 
			{
				Triple triple = tripleList.get(j);				
				
				if ((triple.getPredicate().hasURI(RDF.type.getURI())) && 
						(triple.getSubject().isVariable()) && (triple.getObject().isConcrete())) {
					
					ArrayList<Integer> indices = QueryRewriter.getIndicesOfTriplesTobeChanged(j, tripleList);
										
					if (indices.size() > 1) {
						
						tripleListMap.putAll(QueryRewriter.createTripleListMap(indices, tripleList));
						
						// Remove the first indices from the new Query, since its existence is 
						// depicted by the rest of the indices
						TripleIndexToBeRemoved.add(indices.get(0));		
						
					} else {
						tripleListMap.putAll(QueryRewriter.createTripleMap(j, triple));
					}					
				} else {
					Triple tTriple = null;
					tTriple = tripleListMap.put(j, triple);
					if (tTriple != null) {
						tTriple = tripleListMap.put(j, tTriple);
					}
				}
			}
			// Add Hadoop Element to the ArrayList
			HadoopElement ele = QueryRewriter.createQueryElement(tripleListMap, TripleIndexToBeRemoved);
			hElementList.add(ele);
			
 		}
				
		return hElementList;
	}
	
	public static List<HadoopElement>  rewriteQuery (List<HadoopElement> queryElementList) throws 
								Exception  {			
		return rewriteBasicElement (queryElementList);
	}
	
	public static void main (String [] args) throws Exception {
		String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
				"SELECT ?X ?Y ?Z " +
				"WHERE " +
				"{" +
				"?X rdf:type ub:GraduateStudent ." + 
				"?Y rdf:type ub:University ." + 
				"?Z rdf:type ub:Department ." +
				"?X ub:memberOf ?Z ." +
				"?Z ub:subOrganizationOf ?Y ." +
				"?X ub:undergraduateDegreeFrom ?Y." +
				"}";
		
		ArrayList <HadoopElement> eList = (ArrayList <HadoopElement>)QueryParser.parseQuery(queryString);
		
		
		ArrayList <HadoopElement> eList1 = (ArrayList<HadoopElement>)QueryRewriter.rewriteQuery(eList);
		
		System.out.println("eList -- " + eList.size());
		for (int i = 0; i < eList1.size(); i++) {
			ArrayList<Triple> triple = eList1.get(i).getTriple();
			System.out.println("triple -- " + triple.size());
			for (int j = 0; j < triple.size(); j++) {
				System.out.println("---------------------------------------------------------------");
				System.out.println("Subject -- " + triple.get(j).getSubject().toString());
				System.out.println("Predicate -- " + triple.get(j).getPredicate().toString());
				System.out.println("Object -- " + triple.get(j).getObject().toString());
				System.out.println("---------------------------------------------------------------");
			}		
		}

	}
	
}
