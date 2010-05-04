package edu.utdallas.hadooprdf.query.generator.plan.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;

import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGenerator;
import edu.utdallas.hadooprdf.query.parser.HadoopElement;
import edu.utdallas.hadooprdf.query.parser.NotBasicElementException;
import edu.utdallas.hadooprdf.query.parser.UnhandledElementException;

/**
 * A specific implementation of the query plan generator based on the "elimination count" algorithm
 * @author sharath, vaibhav
 *
 */
public class QueryPlanGeneratorImpl implements QueryPlanGenerator
{
	/**
	 * Constructor
	 */
	public QueryPlanGeneratorImpl() { }
	
	/**
	 * Method that generates the query plan based on the "elimination count" algorithm
	 * @param elements - a list of elements from the QueryParser
	 * @return a QueryPlan object
	 * @throws NotBasicElementException 
	 * @throws UnhandledElementException 
	 */
	public void generateQueryPlan( List<HadoopElement> elements ) throws UnhandledElementException, NotBasicElementException
	{
		//A hashmap that holds each variable and the associated count of times it is found across all triple patterns
		HashMap<String,Integer> hm = new HashMap<String,Integer>();
		
		//A boolean that denotes if there is a common variable
		boolean hasCommonVariable = false;
		
		//The variable that is common to all triple patterns
		String commonVariable = null;
		
		//Check if all elements share a common variable
		Iterator<HadoopElement> elemIter = elements.iterator();
		while( elemIter.hasNext() )
		{
			//Get each hadoop element
			HadoopElement element = elemIter.next();
			
			Set<Var> vars = element.getElement().varsMentioned();
			
			Iterator<Triple> tripleIter = element.getTriple().iterator();
			while( tripleIter.hasNext() )
			{
				Triple triple = tripleIter.next();
				Node sub = triple.getSubject(), obj = triple.getObject();
				String strSub = sub.toString(), strObj = obj.toString();
				
				if( sub.isVariable() && obj.isVariable() )
				{
					if( hm.get( strSub ) == null ) hm.put( strSub, new Integer( "1" ) );
					else hm.put( strSub, new Integer( hm.get( strSub ).intValue() + 1 ) );

					if( hm.get( strObj ) == null ) hm.put( strObj, new Integer( "1" ) );
					else hm.put( strObj, new Integer( hm.get( strObj ).intValue() + 1 ) );
				}
				else
				{
					if( sub.isVariable() )	
					{
						if( hm.get( strSub ) == null ) break;
						else hm.put( strSub, new Integer( hm.get( strSub ).intValue() + 1 ) );
					}
					else
					{
						if( hm.get( strObj ) == null ) break;
						else hm.put( strObj, new Integer( hm.get( strObj ).intValue() + 1 ) );						
					}
				}
			}
			
			Iterator<String> keyIter = hm.keySet().iterator();
			while( keyIter.hasNext() )
			{
				String key = keyIter.next();
				if( hm.get( key ).intValue() == element.getTriple().size() ) { hasCommonVariable = true; commonVariable = key; }
			}
		}
	}
}