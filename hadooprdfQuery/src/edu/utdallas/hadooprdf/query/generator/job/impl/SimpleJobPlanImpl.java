package edu.utdallas.hadooprdf.query.generator.job.impl;

import java.util.HashMap;
import java.util.Map;

import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;

public class SimpleJobPlanImpl implements JobPlan
{
	/** A map of joining variables **/
	private Map<String,TriplePattern> predMap = new HashMap<String,TriplePattern>();
	
	/** The total number of variables expected in the result of the query **/
	private int totalVars = 0;
	
	/** Constructor **/
	public SimpleJobPlanImpl() { }

	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.job.JobPlan#addPredicateBasedTriplePattern(String, TriplePattern)}
	 */	
	public void addPredicateBasedTriplePattern( String pred, TriplePattern tp )
	{
		predMap.put( pred, tp );
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.job.JobPlan#getPredicateBasedTriplePattern(String)}
	 */	
	public TriplePattern getPredicateBasedTriplePattern( String pred )
	{
		return predMap.get( pred );
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
}