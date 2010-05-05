package edu.utdallas.hadooprdf.query.generator.job;

import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;

/**
 * An interface for job plans that defines variables and methods needed in any job plan
 * @author sharath, vaibhav
 *
 */
public interface JobPlan 
{
	/**
	 * A method that stores the association between a predicate and its TriplePattern
	 * @param pred - the predicate of every triple pattern
	 * @param tp - the associated TriplePattern object
	 */
	public void addPredicateBasedTriplePattern( String pred, TriplePattern tp ) ;
	
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
}