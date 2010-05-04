package edu.utdallas.hadooprdf.query.generator.plan.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

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
		String commonVariable = runHeuristic( elements );
	}
	
	/**
	 * 
	 * @param elements - the triple patterns from the query
	 * @return a null string if there is no common variable, the common variable as a string otherwise
	 * @throws UnhandledElementException
	 * @throws NotBasicElementException
	 */
	private String runHeuristic( List<HadoopElement> elements ) throws UnhandledElementException, NotBasicElementException
	{
		//A hashmap that holds each variable and the associated count of times it is found across all triple patterns
		HashMap<String,Integer> hm = new HashMap<String,Integer>();
		
		//A boolean that denotes if we prematurely abort the construction of the hashmap
		boolean isAborted = false;
		
		//The variable that is common to all triple patterns
		String commonVariable = null;
		
		//Check if all elements share a common variable
		Iterator<HadoopElement> elemIter = elements.iterator();
		while( elemIter.hasNext() )
		{
			//Get each hadoop element
			HadoopElement element = elemIter.next();
			
			//Get triple patterns associated with a hadoop element
			Iterator<Triple> tripleIter = element.getTriple().iterator();
			while( tripleIter.hasNext() )
			{
				//Get each triple pattern
				Triple triple = tripleIter.next();
				
				//Get the subject and object for that triple pattern
				Node sub = triple.getSubject(), obj = triple.getObject();
				String strSub = sub.toString(), strObj = obj.toString();
				
				//Check if both subject and object are variables
				if( sub.isVariable() && obj.isVariable() )
				{
					//For a subject variable, if it is not in the hashmap add it, else increment its count in the hashmap
					if( hm.isEmpty() || hm.get( strSub ) == null ) hm.put( strSub, new Integer( "1" ) );
					else hm.put( strSub, new Integer( hm.get( strSub ).intValue() + 1 ) );

					//For an object variable, if it is not in the hashmap add it, else increment its count in the hashmap
					if( hm.isEmpty() || hm.get( strObj ) == null ) hm.put( strObj, new Integer( "1" ) );
					else hm.put( strObj, new Integer( hm.get( strObj ).intValue() + 1 ) );
				}
				else
				{
					//If only one of subject or object is a variable then do this
					//If subject does not exist for a single variable in the hashmap then there no common variable, hence break
					//Similarly for object
					if( sub.isVariable() )	
					{
						if( !hm.isEmpty() && hm.get( strSub ) == null ) { isAborted = true; break; }
						else hm.put( strSub, new Integer( hm.get( strSub ).intValue() + 1 ) );
					}
					else
					{
						if( !hm.isEmpty() && hm.get( strObj ) == null ) { isAborted = true; break; }
						else hm.put( strObj, new Integer( hm.get( strObj ).intValue() + 1 ) );						
					}
				}
			}
			
			//If we have successfully constructed the hashmap then check if the count for any variable in the hashmap
			//equals the number of triple patterns.
			//If it does then we have found a common variable between all triple patterns, return that variable, else return null
			if( !isAborted )
			{
				Iterator<String> keyIter = hm.keySet().iterator();
				while( keyIter.hasNext() )
				{
					String key = keyIter.next();
					if( hm.get( key ).intValue() == element.getTriple().size() ) { commonVariable = key; }
				}
			}
		}
		
		return commonVariable;
	}
}