package edu.utdallas.hadooprdf.query.generator.plan;

import java.util.List;

import edu.utdallas.hadooprdf.query.generator.job.JobPlan;

/**
 * An interface for query plans. There's not much to it so far, its just an abstraction
 * to the job plans 
 * @author sharath, vaibhav
 *
 */
public interface QueryPlan 
{
	/**
	 * A method to add the list of job plans to the current query plan
	 * @param jobPlans - the list of job plans
	 */
	public void addJobPlans( List<JobPlan> jobPlans ) ;

	/**
	 * A method that returns a list of the job plans that make up this query plan
	 * @return the list of job plans that belong to this query plan
	 */
	public List<JobPlan> getJobPlans() ;
}
