package edu.utdallas.hadooprdf.query.generator.triplepattern;

/**
 * An interface for different triple patterns that are used in a job plan
 * @author sharath, vaibhav
 *
 */
public interface TriplePattern 
{
	/**
	 * A method to set the identifier for the current triple pattern
	 * @param id - the identifier of this triple pattern
	 */
	public void setTriplePatternId( int id ) ;
	
	/**
	 * A method that returns the current triple pattern's identifier
	 * @return the current triple pattern's identifier
	 */
	public int getTriplePatternId() ;
	
	/**
	 * A method that sets the position of the joining variable in the current triple pattern
	 * @param var - the position of the joining variable, "s" or "o"
	 */
	public void setJoiningVariable( String var ) ;
	
	/**
	 * A method that returns the position of the joining variable in the current triple pattern
	 * @return the position of the joining variable in the current triple pattern 
	 */
	public String getJoiningVariable() ;
	
	/**
	 * A method that sets the number of variables in the current triple pattern
	 * @param numOfVar - the number of variables in this triple pattern
	 */
	public void setNumOfVariables( int numOfVar ) ;
	
	/**
	 * A method that returns the number of variables in the current triple pattern
	 * @return the number of variables in the current triple pattern
	 */
	public int getNumOfVariables() ;	
}