package edu.utdallas.hadooprdf.generator.plan.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import edu.utdallas.hadooprdf.generator.plan.QueryPlan;
import edu.utdallas.hadooprdf.generator.plan.QueryPlanGenerator;

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
	 */
	public QueryPlan generateQueryPlan( List<HadoopElement> elements )
	{
		//A hashmap that holds each variable and the associated count of times it is found across all triple patterns
		HashMap<String,Integer> hm = new HashMap<String,Integer>();
		
		//Check if all elements share a common variable
		Iterator<HadoopElement> elemIter = elements.iterator();
		while( elemIter.hasNext() )
		{
			//Get each hadoop element
			HadoopElement element = elemIter.next();
			
			
		}
	}
}