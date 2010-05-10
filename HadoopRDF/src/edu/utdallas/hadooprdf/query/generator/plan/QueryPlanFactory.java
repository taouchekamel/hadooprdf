package edu.utdallas.hadooprdf.query.generator.plan;

import edu.utdallas.hadooprdf.query.generator.plan.impl.SimpleQueryPlanImpl;

/**
 * A factory class for different query plan implementations
 * @author sharath, vaibhav
 *
 */
public class QueryPlanFactory 
{
	/** A private constructor **/
	private QueryPlanFactory() { }
	
	/**
	 * A method that returns a simple QueryPlan object
	 * @return a QueryPlan that just abstracts different job plans
 	 */
	public static QueryPlan createSimpleQueryPlan()
	{
		return new SimpleQueryPlanImpl() ;
	}
}
