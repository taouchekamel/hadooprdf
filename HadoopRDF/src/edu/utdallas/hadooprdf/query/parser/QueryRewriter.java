package edu.utdallas.hadooprdf.query.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Node_Literal;
import com.hp.hpl.jena.graph.Node_URI;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.utdallas.hadooprdf.query.parser.HadoopElement.HadoopTriple;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.rdf.uri.prefix.PrefixNamespaceTree;

public class QueryRewriter 
{
	private static ArrayList<Integer> getIndicesOfTriplesTobeChanged (int index, ArrayList<HadoopElement.HadoopTriple> tripleList) 
	{
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		
		indexList.add(index);
		for (int i = 0; i < tripleList.size(); i++) 
		{ 
			if (tripleList.get(i).getPredicate().hashCode() != tripleList.get(index).getPredicate().hashCode())
			{	
				if (tripleList.get(i).getObject().hashCode() ==	tripleList.get(index).getSubject().hashCode()) 
					indexList.add(i);
			}
		}				
		return indexList;		
	}
	
	private static HashMap<Integer,HadoopElement.HadoopTriple> createTripleListMap(ArrayList<Integer> indices, 
	List<HadoopElement.HadoopTriple> tripleList, PrefixNamespaceTree prefixTree, FileListGenerator filesGen) 
	throws Exception 
	{
		HashMap <Integer, HadoopElement.HadoopTriple> tripleListMap = new HashMap<Integer, HadoopElement.HadoopTriple>();
		tripleListMap.put(indices.get(0), getHadoopTriple( tripleList.get(indices.get( 0 ) ), prefixTree ) );
		
		for (int i = 1; i < indices.size(); i++) 
		{					
			String URI = tripleList.get(indices.get(i)).getPredicate().getURI() + "_";
			URI += prefixTree.matchAndReplacePrefix(tripleList.get(indices.get(0)).getObject().toString());										
			Node predicate =  Node_Literal.createURI(URI);
			
			HadoopElement.HadoopTriple triple = getHadoopTriple( new HadoopElement.HadoopTriple (tripleList.get(indices.get(i)).getSubject(), predicate, tripleList.get(indices.get(i)).getObject() ), prefixTree );
			
			//try { triple = convertPredicateToPrefix (triple,prefixTree);} catch (Exception e) {throw e;}
			
			String classAssociated =  tripleList.get(indices.get(i)).getPredicate().getURI();
			int index1 = classAssociated.lastIndexOf("#");
			classAssociated = classAssociated.substring (index1, classAssociated.length ());
			ArrayList<String> files = fetchAssociatedFilesForTriple(triple.getObject().toString(), classAssociated, triple.getPredicate().getURI (), filesGen);
			if( FileListGenerator.isInverse ) triple = getHadoopTriple( new HadoopElement.HadoopTriple( tripleList.get(indices.get(i)).getObject(), predicate, tripleList.get(indices.get(i)).getSubject() ), prefixTree );
			triple.addFiles (files);
			tripleListMap.put(indices.get(i), triple);			
		}	
		return tripleListMap;
	}
	
	private static HashMap <Integer, HadoopElement.HadoopTriple> createTripleMap (int index, HadoopTriple triple, 
	PrefixNamespaceTree prefixTree, FileListGenerator filesGen) 
	throws Exception 
	{	
		HashMap <Integer, HadoopElement.HadoopTriple> tripleMap = new HashMap<Integer, HadoopElement.HadoopTriple>();			
		String URI = triple.getPredicate().getURI() + "_";
		URI += prefixTree.matchAndReplacePrefix(triple.getObject().toString());
		Node predicate = Node_URI.createURI(URI);
		
		// Not required - Will not be using object since it is part of the predicate URI already.
		URI = prefixTree.matchAndReplacePrefix(triple.getObject().toString());
		Node object = Node_URI.createURI(URI);
		
		HadoopElement.HadoopTriple tTriple = getHadoopTriple( new HadoopElement.HadoopTriple (triple.getSubject(), predicate, object), prefixTree );
		//try { tTriple = convertPredicateToPrefix (tTriple,prefixTree);} catch (Exception e) {throw e;}	
		
		String classAssociated =  tTriple.getPredicate().getURI();
		int index1 = classAssociated.lastIndexOf("#");
		classAssociated = classAssociated.substring (index1, classAssociated.length ());
		ArrayList<String> files = fetchAssociatedFilesForTriple(triple.getObject().toString(), classAssociated, tTriple.getPredicate().getURI (), filesGen);
		if( FileListGenerator.isInverse ) triple = getHadoopTriple( new HadoopElement.HadoopTriple( triple.getObject(), predicate, triple.getSubject() ), prefixTree );
		tTriple.addFiles (files);
		tripleMap.put(index, tTriple);
		tTriple = null;
		return tripleMap;
	}
	
	private static boolean shouldTripleBePartofQuery (int index, ArrayList<Integer> indicesOfTripleToBeRemoved) 
	{	
		for (int i = 0; i < indicesOfTripleToBeRemoved.size(); i++) 
		{
			if (index == indicesOfTripleToBeRemoved.get(i))
				return false;	
		}
		return true;
	}
	
	private static HadoopElement createQueryElement (HashMap <Integer, HadoopElement.HadoopTriple>  tripleListMap, ArrayList<Integer> indicesOfTripleToBeRemoved) 
	throws Exception 
	{	
		ArrayList<HadoopElement.HadoopTriple> tripleList = new ArrayList<HadoopElement.HadoopTriple>();		
		for (int i = 0; i < tripleListMap.size(); i++) 
		{
			if (shouldTripleBePartofQuery (i, indicesOfTripleToBeRemoved) == true) 
			{
				HadoopElement.HadoopTriple triple = (HadoopElement.HadoopTriple)tripleListMap.get(i);				
				tripleList.add(triple);
			}
		}			
		HadoopElement element = new HadoopElement (tripleList);
		return element;
	}
	
	private static HadoopElement.HadoopTriple getHadoopTriple( HadoopElement.HadoopTriple triple, PrefixNamespaceTree prefixTree )
	{
		Node sub = triple.getSubject(), pred = triple.getPredicate(), obj = triple.getObject();
		if( !sub.isVariable() ) 
		{
			String repSub = prefixTree.matchAndReplacePrefix( sub.toString() );
			if( repSub != null && !sub.toString().equalsIgnoreCase( repSub ) )
			{
				if( sub.isURI() ) sub = Node_URI.createURI( repSub );
				else if( sub.isLiteral() ) sub = Node.createLiteral( repSub );
			}
		}
		if( !pred.isVariable() ) 
		{
			String repPred = prefixTree.matchAndReplacePrefix( pred.toString() );
			if( repPred != null && !pred.toString().equalsIgnoreCase( repPred ) )
			{
				if( pred.isURI() ) pred = Node_URI.createURI( repPred );
				else if( pred.isLiteral() ) pred = Node.createLiteral( repPred );
			}
		}
		if( !obj.isVariable() ) 
		{
			String repObj = prefixTree.matchAndReplacePrefix( obj.toString() );
			if( repObj!= null && !obj.toString().equalsIgnoreCase( repObj ) )
			{
				if( obj.isURI() ) obj = Node_URI.createURI( repObj );
				else if( obj.isLiteral() ) obj = Node.createLiteral( repObj );
			}
		}
		return new HadoopElement.HadoopTriple( sub, pred, obj );
	}
	
	private static ArrayList<HadoopElement> rewriteBasicElement (List<HadoopElement> queryElementList, PrefixNamespaceTree prefixTree, FileListGenerator filesGen)
	throws Exception 
	{	
		ArrayList<HadoopElement> hElementList = new ArrayList<HadoopElement>();	
		for (int i = 0; i < queryElementList.size(); i++) 
		{
			HadoopElement element = queryElementList.get(i);	
			ArrayList<HadoopElement.HadoopTriple> tripleList = null;
			
			try { tripleList = element.getTriple(); } 
			catch (NotBasicElementException e) { throw new UnhandledElementException (e.getMessage()); }
			
			HashMap<Integer,HadoopElement.HadoopTriple> tripleListMap = new HashMap<Integer, HadoopElement.HadoopTriple>(); 
			ArrayList<Integer> TripleIndexToBeRemoved = new ArrayList<Integer>();

			for (int  j = 0; j < tripleList.size(); j++) 
			{
				HadoopElement.HadoopTriple triple = tripleList.get(j);				
				
				if ((triple.getPredicate().hasURI(RDF.type.getURI())) && (triple.getSubject().isVariable()) && (triple.getObject().isConcrete())) 
				{	
					ArrayList<Integer> indices = QueryRewriter.getIndicesOfTriplesTobeChanged(j, tripleList);						
					if (indices.size() > 1) 
					{
						try { tripleListMap.putAll(QueryRewriter.createTripleListMap(indices, tripleList, prefixTree, filesGen)); } 
						catch ( Exception e ) { throw e;}
						
						// Remove the first indices from the new Query, since its existence is 
						// depicted by the rest of the indices
						TripleIndexToBeRemoved.add(indices.get(0));		
					} 
					else 
					{
						try { tripleListMap.putAll(QueryRewriter.createTripleMap(j, triple, prefixTree, filesGen)); } 
						catch ( Exception e ) { throw e; }
					}					
				} 
				//else if( filesGen.isPredicateTransitive( triple.getPredicate().getLocalName() ) )
				//{
					
				//}
				else
				{
					String tURI = prefixTree.matchAndReplacePrefix(triple.getPredicate().getURI());
					String classAssociated =  triple.getPredicate().getURI();
					int index1 = classAssociated.lastIndexOf("#");
					classAssociated = classAssociated.substring (index1, classAssociated.length ());
					ArrayList<String> files = fetchAssociatedFilesForTriple(triple.getObject().toString(), classAssociated, tURI, filesGen);

					triple = QueryRewriter.getHadoopTriple( triple, prefixTree );
					triple.addFiles (files);
					
					//tripleListMap.put(j, triple);
					HadoopElement.HadoopTriple tTriple = null;
					tTriple = tripleListMap.put(j, triple);					
					if (tTriple != null) tTriple = tripleListMap.put(j, tTriple);
				}
			}
			// Add Hadoop Element to the ArrayList
			HadoopElement ele = QueryRewriter.createQueryElement(tripleListMap, TripleIndexToBeRemoved);
			hElementList.add(ele);			
 		}
				
		return hElementList;
	}
	
	private static ArrayList<String> fetchAssociatedFilesForTriple (String uri, String classAssociated, String prefix, FileListGenerator filelistgen)  
	{		
		return (ArrayList<String>) filelistgen.getFilesAssociatedWithTriple(uri, classAssociated, prefix);
	}
	
	public static List<HadoopElement> rewriteQuery( Query query, PrefixNamespaceTree prefixTree, DataSet dataset ) 
	throws Exception  
	{			
		FileListGenerator fileListGenerator = null;
		try { fileListGenerator = new FileListGenerator ( query, dataset );	} 
		catch (Exception e) { throw e; }
		
		ArrayList<HadoopElement> tQueryElementList = (ArrayList<HadoopElement>)query.getElements();
		try { tQueryElementList = rewriteBasicElement (tQueryElementList, prefixTree, fileListGenerator); } 
		catch (Exception e) { throw e;}
		return tQueryElementList;
	}		
}