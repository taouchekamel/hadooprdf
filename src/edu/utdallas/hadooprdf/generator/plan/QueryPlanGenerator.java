package edu.utdallas.hadooprdf.generator.plan;

import java.util.List;

/**
 * An interface that defines the methods needed for the query plan generator algorithm
 * @author sharath, vaibhav
 *
 */
public interface QueryPlanGenerator 
{
	/**
	 * A method that generates a query plan based on a list of hadoop elements
	 * @param elements - the list of hadoop elements formed from parsing the query
	 * @return a QueryPlan object
	 */
	public abstract QueryPlan generateQueryPlan( List<HadoopElement> elements ) ;
}
