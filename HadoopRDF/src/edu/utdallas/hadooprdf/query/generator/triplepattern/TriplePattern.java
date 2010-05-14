package edu.utdallas.hadooprdf.query.generator.triplepattern;

import com.hp.hpl.jena.graph.Node;

/**
 * An interface for different triple patterns that are used in a job plan
 * @author vaibhav
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
	
	/**
	 * A method that sets for every triple pattern, if it has one a literal value
	 * @param literal - the literal value if present in a triple pattern
	 */
	public void setLiteralValue( String literal ) ;
	
	/**
	 * A method that returns the literal value associated with the current triple pattern
	 * @return - the literal value associated with this triple pattern
	 */
	public String getLiteralValue() ;

	/**
	 * A method that sets the value of the subject for this triple pattern
	 * @param pred - the subject for this triple pattern
	 */
	public void setSubjectValue( Node sub ) ;
	
	/**
	 * A method that returns the subject in the current triple pattern
	 * @return the subject for the current triple pattern
	 */
	public Node getSubjectValue() ;
	
	
	/**
	 * A method that sets the value of the predicate for this triple pattern
	 * @param pred - the predicate for this triple pattern
	 */
	public void setPredicateValue( Node pred ) ;
	
	/**
	 * A method that returns the predicate in the current triple pattern
	 * @return the predicate for the current triple pattern
	 */
	public Node getPredicateValue() ;
	
	/**
	 * A method that sets the value of the object for this triple pattern
	 * @param pred - the object for this triple pattern
	 */
	public void setObjectValue( Node obj ) ;
	
	/**
	 * A method that returns the object in the current triple pattern
	 * @return the object for the current triple pattern
	 */
	public Node getObjectValue() ;
	
	/**
	 * A method that sets the value of the joining variable in the current triple pattern
	 * @param val - the value of the joining variable in this triple pattern
	 */
	public void setJoiningVariableValue( String val ) ;
	
	/**
	 * A method that returns the value of the joining variable in the current triple pattern
	 * @return the value of the joining variable in this triple pattern
	 */
	public String getJoiningVariableValue() ;
	
	/**
	 * A method that sets the value of the second variable in a triple pattern if there exists one
	 * @param value - the value of the second variable in a triple pattern if there exists one
	 */
	public void setSecondVariableValue( String value ) ;
	
	/**
	 * A method that returns the value of the second variable if there exits one
	 * @return the value of the second variable
	 */
	public String getSecondVariableValue() ;
}