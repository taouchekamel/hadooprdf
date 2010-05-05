package edu.utdallas.hadooprdf.query.generator.job;

import java.util.List;

import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;

/**
 * An interface for job plans that defines variables and methods needed in any job plan
 * @author sharath, vaibhav
 *
 */
public interface JobPlan 
{
	
	public void addJoiningVariable( String var, List<TriplePattern> tps ) ;
}