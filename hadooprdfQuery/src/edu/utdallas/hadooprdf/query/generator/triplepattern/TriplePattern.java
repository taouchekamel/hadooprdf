package edu.utdallas.hadooprdf.query.generator.triplepattern;

/**
 * An interface for different triple patterns that are used in a job plan
 * @author sharath, vaibhav
 *
 */
public interface TriplePattern 
{
	public void setTriplePatternId( int id ) ;
	
	public int getTriplePatternId() ;
	
	public void setJoiningVariable( String var ) ;
	
	public String getJoiningVariable() ;
	
	public void setNumOfVariables( int numOfVar ) ;
	
	public int getNumOfVariables() ;
	
	public void setTotalVariables( int totalVars ) ;
	
	public int getTotalVariables() ;
}
