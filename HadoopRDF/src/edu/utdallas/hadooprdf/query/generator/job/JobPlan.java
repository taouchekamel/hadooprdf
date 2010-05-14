package edu.utdallas.hadooprdf.query.generator.job;

import java.util.List;

import org.apache.hadoop.mapreduce.Job;

import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;

/**
 * An interface for job plans that defines variables and methods needed in any job plan
 * @author vaibhav
 *
 */
public interface JobPlan 
{
	/**
	 * A method that sets a job identifier for the current job in a JobPlan
	 * @param jobId - the job identifier for the current job
	 */
	public void setJobId( int jobId ) ;
	
	/**
	 * A method that returns the job identifier for the current job
	 * @return the job identifier for the current job
	 */
	public int getJobId() ;
	
	/**
	 * A method that stores the association between a predicate and its TriplePattern
	 * @param pred - the predicate of every triple pattern
	 * @param tp - the associated TriplePattern object
	 */
	public void setPredicateBasedTriplePattern( String pred, TriplePattern tp ) ;
	
	/**
	 * A method that returns the TriplePattern associated with a predicate
	 * @param pred - the predicate to be searched
	 * @return the associated TriplePattern object
	 */
	public TriplePattern getPredicateBasedTriplePattern( String pred ) ;
	
	/**
	 * A method that sets the total number of variables expected in the result
	 * @param totalVars - the total number of variables expected in the result of the SPARQL query
	 */
	public void setTotalVariables( int totalVars ) ;
	
	/**
	 * A method that returns the total number of variables that are expected in the result
	 * @return the total number of variables in the SPARQL query
	 */
	public int getTotalVariables() ;
	
	/**
	 * A method that sets the Hadoop Job object to be used by the current job plan
	 * @param currJob - the Hadoop Job object
	 */
	public void setHadoopJob( Job currJob ) ;
	
	/**
	 * A method that returns the Hadoop Job object used by the current job plan
	 * @return - the Hadoop Job used by the current job plan
	 */
	public Job getHadoopJob() ;
	
	/**
	 * A method that sets whether there are any more jobs to follow after the current one
	 * @param hasMoreJobs - true iff there are more Hadoop jobs to follow, false otherwise
	 */
	public void setHasMoreJobs( boolean hasMoreJobs ) ;
	
	/**
	 * A method that returns true if there are more jobs to follow, false otherwise
	 * @return true iff there are more Hadoop jobs to follow, false otherwise
	 */
	public boolean getHasMoreJobs() ;
	
	/**
	 * A method that adds the association between a variable and the number of triple patterns that contain the variable
	 * to a map
	 * @param var - a variable from the SPARQL query
	 * @param count - the number of triple patterns that contain the given variable
	 */
	public void setVarTrPatternCount( String var, Integer count ) ;
	
	/**
	 * A method that returns the number of triple patterns that contain the given variable
	 * @param var - the variable for which the number of triple patterns is desired
	 * @return the number of triple patterns that cotain the given variable
	 */
	public Integer getVarTrPatternCount( String var ) ;
	
	/**
	 * A method that adds the given variable to a list of joining variables
	 * @param var - a given variable 
	 */
	public void addVarToJoiningVariables( String var ) ;
	
	/**
	 * A method that returns the list of joining variables
	 * @return the list that contains all joining variables for a job
	 */
	public List<String> getJoiningVariablesList() ;
	
	/**
	 * A method that sets the list of variables in the SELECT clause
	 * @param listVars - a list of variables in the SELECT clause of the SPARQL query
	 */
	public void setSelectClauseVarList( List<String> listVars ) ;
	
	/**
	 * A method that returns the list of variables in the SELECT clause of the SPARQL query
	 * @return a list containing variables in the SELECT clause
	 */
	public List<String> getSelectClauseVarList() ;
}