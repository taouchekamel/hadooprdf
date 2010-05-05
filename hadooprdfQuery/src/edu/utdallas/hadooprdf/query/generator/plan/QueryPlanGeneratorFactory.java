package edu.utdallas.hadooprdf.query.generator.plan;

import edu.utdallas.hadooprdf.query.generator.plan.impl.SimpleQueryPlanGeneratorImpl;

/**
 * A factory class to generate different query plan generators
 * @author sharath, vaibhav
 *
 */
public class QueryPlanGeneratorFactory 
{
	/** A private constructor **/
	private QueryPlanGeneratorFactory() { }
	
	/**
	 * Factory method that returns a QueryPlanGenerator based on the elimination count algorithm
	 * @return a QueryPlanGenerator object that is based on the elimination count algorithm
	 */
	public static QueryPlanGenerator createSimpleQueryPlanGenerator()
	{
		return new SimpleQueryPlanGeneratorImpl();
	}
}