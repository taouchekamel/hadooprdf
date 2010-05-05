package edu.utdallas.hadooprdf.query.generator.job.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;

public class SimpleJobPlanImpl implements JobPlan
{
	/** A map of joining variables **/
	private Map<String,List<TriplePattern>> varMap = new HashMap<String,List<TriplePattern>>();
	
	/** Constructor **/
	public SimpleJobPlanImpl() { }

	public void addJoiningVariable( String var, List<TriplePattern> tps )
	{
		varMap.put( var, tps );
	}
}
