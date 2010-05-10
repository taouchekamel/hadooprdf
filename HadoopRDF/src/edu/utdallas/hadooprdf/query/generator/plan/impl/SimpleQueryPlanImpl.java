package edu.utdallas.hadooprdf.query.generator.plan.impl;

import java.util.ArrayList;
import java.util.List;

import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlan;

/**
 * A specific implementation of the query plan for the current version
 * @author sharath, vaibhav
 *
 */
public class SimpleQueryPlanImpl implements QueryPlan
{
	/** The list of job plans that make up this query plan **/
	private List<JobPlan> jobPlans = new ArrayList<JobPlan>();
	
	/** A blank constructor **/
	public SimpleQueryPlanImpl() { }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.plan.QueryPlan#addJobPlans(List)}
	 */
	public void addJobPlans( List<JobPlan> jobPlans )
	{
		this.jobPlans = jobPlans;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.plan.QueryPlan#getJobPlans()}
	 */
	public List<JobPlan> getJobPlans()
	{
		return jobPlans;
	}
}
