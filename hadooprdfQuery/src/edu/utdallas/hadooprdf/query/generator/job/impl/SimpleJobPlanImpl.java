package edu.utdallas.hadooprdf.query.generator.job.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.mapreduce.Job;

import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;

/**
 * A simple implementation of the job plan interface
 * @author sharath, vaibhav
 *
 */
public class SimpleJobPlanImpl implements JobPlan
{	
	/** A map of joining variables **/
	private Map<String,TriplePattern> predTrPatternMap = new HashMap<String,TriplePattern>();
	
	/** The total number of variables expected in the result of the query **/
	private int totalVars = 0;
	
	/** The Hadoop Job object used by the current plan **/
	private Job currJob = null;
	
	/** Constructor **/
	public SimpleJobPlanImpl() { }

	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.job.JobPlan#addPredicateBasedTriplePattern(String, TriplePattern)}
	 */	
	public void setPredicateBasedTriplePattern( String pred, TriplePattern tp )
	{
		predTrPatternMap.put( pred, tp );
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.job.JobPlan#getPredicateBasedTriplePattern(String)}
	 */	
	public TriplePattern getPredicateBasedTriplePattern( String pred )
	{
		return predTrPatternMap.get( pred );
	}
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.job.JobPlan#setTotalVariables(int)}
	 */
	public void setTotalVariables( int totalVars )
	{
		this.totalVars = totalVars;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.job.JobPlan#getTotalVariables()}
	 */
	public int getTotalVariables()
	{
		return totalVars;
	}

	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.job.JobPlan#setHadoopJob(Job)}
	 */
	public void setHadoopJob( Job currJob )
	{
		this.currJob = currJob;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.job.JobPlan#getHadoopJob()}
	 */
	public Job getHadoopJob()
	{
		return currJob;
	}
}