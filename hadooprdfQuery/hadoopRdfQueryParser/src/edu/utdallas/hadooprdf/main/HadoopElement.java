package edu.utdallas.hadooprdf.main;

import java.util.ArrayList;

import javax.swing.text.DefaultStyledDocument.ElementBuffer;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;


/* 
 * Currently supports only queries of single graph of the types - Basic, Queries with optional, with Filter, with union
 * 
 *
 */
public class HadoopElement {

	private Element element = null;
	
	public static enum HElementType {
		FILTER, BASIC, OPTIONAL, UNION
	};
	
	private  boolean isFilterElement = false;
	private  boolean isBasicElement = true;
	private  boolean isOptionalElement = false;
	private  boolean isUnionElement = false;
	private  HElementType elementType = HElementType.BASIC;	
	
		
	public HadoopElement (Element element) throws UnhandledElementException {
		this.element = element;
		
		if (element instanceof ElementGroup) {
			
			// logic not correct, fetch each element from the list and check for instance
			if (element instanceof ElementFilter) {
				elementType = HElementType.FILTER;
				isFilterElement = true;
			} else if (element instanceof ElementUnion) {
				elementType = HElementType.UNION;
				isUnionElement = true;
			} else {
				throw new UnhandledElementException("Element of this type is currently not handled");
			}
			
		} else if (element instanceof ElementOptional) {
			isOptionalElement = true;
			elementType = HElementType.OPTIONAL;
		} else if (element instanceof ElementTriplesBlock) {
			elementType = HElementType.BASIC;
			isBasicElement = true;
		} else {
			throw new UnhandledElementException("Element of this type is currently not handled");
		}								
	}
	
	public Element getElement () {
		return this.element;
	}
	
	public ArrayList <Triple> getTriple () throws UnhandledElementException, NotBasicElementException {
		
		ArrayList <Triple> tripleList = new ArrayList<Triple>();
		switch (elementType) {
		case BASIC:
			ElementTriplesBlock tBlock = (ElementTriplesBlock)this.element;
			BasicPattern pattern = tBlock.getPattern();
			
			for (int i = 0; i < pattern.size(); i++) {
				Triple triple = pattern.get(i);
				tripleList.add(triple);
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
		return isFilterElement;
	}
	
	public boolean isBasic () {
		return this.isBasicElement;
	}
	
	public boolean isUnion () {
		return isUnionElement;
	}
	
	public boolean isOptional () {
		return isOptionalElement;
	}
			
}
