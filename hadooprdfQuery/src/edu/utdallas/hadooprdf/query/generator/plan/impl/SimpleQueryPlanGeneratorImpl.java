package edu.utdallas.hadooprdf.query.generator.plan.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.job.JobPlanFactory;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlan;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanFactory;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGenerator;
import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;
import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePatternFactory;
import edu.utdallas.hadooprdf.query.parser.HadoopElement;
import edu.utdallas.hadooprdf.query.parser.NotBasicElementException;
import edu.utdallas.hadooprdf.query.parser.UnhandledElementException;

/**
 * A specific implementation of the query plan generator based on the "elimination count" algorithm
 * @author sharath, vaibhav
 *
 */
public class SimpleQueryPlanGeneratorImpl implements QueryPlanGenerator
{
	/** A map of triple pattern id's and the corresponding triple pattern that is used in every job **/
	private Map<Integer,TriplePattern> tpMap = new HashMap<Integer,TriplePattern>();
	
	/**
	 * Constructor
	 */
	public SimpleQueryPlanGeneratorImpl() { }
	
	/**
	 * Method that generates the query plan based on the "elimination count" algorithm
	 * @param elements - a list of elements from the QueryParser
	 * @return a QueryPlan object
	 * @throws NotBasicElementException 
	 * @throws UnhandledElementException 
	 */
	public QueryPlan generateQueryPlan( List<HadoopElement> elements ) throws UnhandledElementException, NotBasicElementException
	{
		//Create a simple query plan
		QueryPlan qp = QueryPlanFactory.createSimpleQueryPlan();
		
		//Create a list of job plans
		List<JobPlan> jps = new ArrayList<JobPlan>();
		
		//Run the heuristic to find out if there is a common variable
		String commonVariable = runHeuristic( elements );
		
		//The case where there is a common variable involves only a single job
		//Else run the "elimination count" algorithm
		if( commonVariable != null )
		{
			//Create a single job plan
			JobPlan jp = JobPlanFactory.createSimpleJobPlan();
			
			//Find the position of the joining variable in each triple pattern
			Iterator<HadoopElement> elemIter = elements.iterator();
			while( elemIter.hasNext() )
			{
				//Get each hadoop element
				HadoopElement element = elemIter.next();
				
				//Get the total number of variables
				int totalVars = element.getElement().varsMentioned().size();

				//A counter
				int count = 0;

				//Get triple patterns associated with a hadoop element
				Iterator<Triple> tripleIter = element.getTriple().iterator();
				while( tripleIter.hasNext() )
				{
					//Get each triple pattern
					Triple triple = tripleIter.next();

					//The triple pattern associated with the current counter
					TriplePattern tp = tpMap.get( ++count );

					if( triple.getSubject().toString().equalsIgnoreCase( commonVariable ) )
						tp.setJoiningVariable( "s" );
					else
						tp.setJoiningVariable( "o" );
										
					//Add the triple pattern to the job plan
					jp.addPredicateBasedTriplePattern( triple.getPredicate().toString(), tp );
				}
				
				//Set the total number of variables expected in the result
				jp.setTotalVariables( totalVars );
			}
			
			//TODO: Add the input files, output files and file prefixes to the job plan. Also the hadoop specific parameters
			
			//Add the job plan to the list of job plans
			jps.add( jp );
			
			//Add the list of job plans to the query plan
			qp.addJobPlans( jps );
		}
		
		return qp;
	}
	
	/**
	 * A method that tests the heuristic of whether all triple patterns have a common variable
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
			
			//A counter
			int count = 0;
			
			//Get triple patterns associated with a hadoop element
			Iterator<Triple> tripleIter = element.getTriple().iterator();
			while( tripleIter.hasNext() )
			{
				//Get each triple pattern
				Triple triple = tripleIter.next();
				
				//Create a TriplePattern object that will be used in the construction of a JobPlan
				TriplePattern tp = TriplePatternFactory.createSimpleTriplePattern();
			
				//Set the id of the triple pattern
				tp.setTriplePatternId( ++count );
				
				//Get the subject and object for that triple pattern
				Node sub = triple.getSubject(), obj = triple.getObject();
				String strSub = sub.toString(), strObj = obj.toString();
				
				//Check if both subject and object are variables
				if( sub.isVariable() && obj.isVariable() )
				{
					//Set the number of variables in the triple pattern to two
					tp.setNumOfVariables( 2 );
					
					//For a subject variable, if it is not in the hashmap add it, else increment its count in the hashmap
					if( hm.isEmpty() || hm.get( strSub ) == null ) hm.put( strSub, new Integer( "1" ) );
					else hm.put( strSub, new Integer( hm.get( strSub ).intValue() + 1 ) );

					//For an object variable, if it is not in the hashmap add it, else increment its count in the hashmap
					if( hm.isEmpty() || hm.get( strObj ) == null ) hm.put( strObj, new Integer( "1" ) );
					else hm.put( strObj, new Integer( hm.get( strObj ).intValue() + 1 ) );
				}
				else
				{
					//Set the number of variables in the triple pattern to one
					tp.setNumOfVariables( 1 );

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
				
				tpMap.put( new Integer( count ), tp );
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