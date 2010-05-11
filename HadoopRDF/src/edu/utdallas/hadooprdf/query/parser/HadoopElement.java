package edu.utdallas.hadooprdf.query.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.text.DefaultStyledDocument.ElementBuffer;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.ElementVisitor;
import com.hp.hpl.jena.sparql.util.NodeIsomorphismMap;


/* 
 * Currently supports only queries of single graph of the types - Basic, Queries with optional, with Filter, with union
 * 
 *
 */
public class HadoopElement  {

	private Element element = null;
	private HashMap <Integer,ArrayList<String>> fileMap = new HashMap ();
	boolean isHadoopElement = false;
	private HashMap <String, String> prefixMap = new HashMap<String, String> ();
	
		
	public static enum HElementType {
		FILTER, BASIC, OPTIONAL, UNION
	};
	
	private  HElementType elementType = HElementType.BASIC;	
	
	public void setPrefixMap (String key, String value) {
		prefixMap.put(key, value);
	}
	
	public String getNsPrefix (String key) {
		return prefixMap.get(key);
	}
	
	public Set<String> getNsPrefixKey () {
		return prefixMap.keySet();
	}
	
	public HadoopElement (List<HadoopTriple> triplesList) throws Exception {

		elementType = HElementType.BASIC;			
		ElementTriplesBlock bElement = new ElementTriplesBlock();
		for (int i = 0; i < triplesList.size(); i++) {
			
			bElement.addTriple(triplesList.get(i));	
			fileMap.put(i, (ArrayList<String>) triplesList.get(i).getAssociatedFiles());			
		}	
		this.element = bElement;
		isHadoopElement = true;
		
	}
		
	public HadoopElement (Element element) throws UnhandledElementException {
		this.element = element;
		
		if (element instanceof ElementGroup) {		
			throw new UnhandledElementException("Element of this type is currently not handled");			
		} else if (element instanceof ElementOptional) {
			elementType = HElementType.OPTIONAL;
		} else if (element instanceof ElementTriplesBlock) {
			elementType = HElementType.BASIC;
		} else {
			throw new UnhandledElementException("Element of this type is currently not handled");
		}								
	}
	
	public Element getElement () {
		return this.element;
	}
	
	public ArrayList <HadoopElement.HadoopTriple> getTriple () throws Exception {
		ArrayList <HadoopElement.HadoopTriple> tripleList =  new ArrayList<HadoopElement.HadoopTriple>();
		switch (elementType) {
		case BASIC:
			ElementTriplesBlock tBlock = (ElementTriplesBlock)this.element;
			BasicPattern pattern = tBlock.getPattern();
			
			for (int i = 0; i < pattern.size(); i++) {
				Triple triple = pattern.get(i);
				HadoopTriple tTriple = new HadoopTriple (triple.getSubject(), triple.getPredicate(), triple.getObject());
				if  (isHadoopElement == true)
					tTriple.addFiles(this.fileMap.get(i));
				tripleList.add(tTriple);
			}
			
			break;
		case OPTIONAL:
			ElementOptional opt = (ElementOptional)this.element;
			ElementGroup optGrpElement = (ElementGroup)opt.getOptionalElement();
			ArrayList<Element> optTElementList = (ArrayList<Element>) optGrpElement.getElements();
			
			for (int i = 0; i < optTElementList.size(); i++) {
				ElementTriplesBlock tOptBlock = (ElementTriplesBlock)optTElementList.get(i);
				BasicPattern optPattern = tOptBlock.getPattern();							
				for (int j = 0; j < optPattern.size(); j++) {
					// TBD
				}									
			}			
			break;
		default:
			throw new NotBasicElementException("Use getTriplelist to fetch Triples from Non Basic Elements. ");
		}
		return tripleList;
	}
	
	public static ArrayList <Triple> getTripleList (HadoopElement hElement) {
		
		return null;
	}
	
	public HElementType elementType () {
		return elementType;
	}
	
	public boolean hasFilters () {
		if (this.elementType == HElementType.FILTER)
			return true;
		
		return false;
	}
	
	public boolean isBasic () {
		if (this.elementType == HElementType.BASIC)
			return true;
		
		return false;
	}
	
	public boolean isUnion () {
		if (this.elementType == HElementType.UNION)
			return true;
		
		return false;
	}
	
	public boolean isOptional () {
		if (this.elementType == HElementType.OPTIONAL)
			return true;
		
		return false;
	}
			
	/**
	 * 
	 * @author Sharath
	 *
	 */
	
	/* 
	 * HadoopElement is associated with triple list and each triple in the list is associated with files on which the query
	 * execution unit  is dependent. This class defines a structure for the associated files 
	 */
	public static class HadoopTriple extends Triple {
		ArrayList <String> filesAssociated = new ArrayList<String>();
		
		public HadoopTriple (Node subject, Node predicate, Node object) {
			super (subject, predicate, object);
		}

		public HadoopTriple (Node subject, Node predicate, Node object, List<String> filesAssociated) {
			super (subject, predicate, object);
			this.filesAssociated.addAll(filesAssociated);
		}		
		
		public void addFile (String filePath) {
			filesAssociated.add(filePath);
		}
		
		public void addFiles (List<String> filesPath) {
			
			filesAssociated.addAll(filesPath);
		}
	
		public List<String> getAssociatedFiles () throws Exception {
			
			if (filesAssociated.size() == 0) {
				throw new Exception ("No associated files associated");
			}
			
			return filesAssociated; 
		}
	}
}
