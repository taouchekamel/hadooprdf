package edu.utdallas.hadooprdf.query.generator.job;

import edu.utdallas.hadooprdf.query.generator.job.impl.SimpleJobPlanImpl;

/**
 * A factory class that creates different implementations of job plans
 * @author vaibhav
 *
 */
public class JobPlanFactory 
{
	/** A private constructor **/
	private JobPlanFactory() { }
	
	/**
	 * A method that return a simple job plan used in the current version
	 * @return a simple JobPlan object
	 */
	public static JobPlan createSimpleJobPlan()
	{
		return new SimpleJobPlanImpl();
	}
}
