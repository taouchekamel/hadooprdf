package edu.utdallas.hadooprdf.query.generator.triplepattern.impl;

import java.io.Serializable;
import com.hp.hpl.jena.graph.Node;

import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;

/**
 * A simple implementation of the triple pattern
 * @author vaibhav
 *
 */
public class SimpleTriplePatternImpl implements TriplePattern, Serializable
{
	/**	The serial version UID **/
	private static final long serialVersionUID = 5892746847161727087L;

	/** The triple pattern id from the query **/
	private int tpId = 0;
	
	/** The number of variables in the triple pattern **/
	private int tpNumOfVars = 0;
	
	/** The joining variable in this triple pattern **/
	private String tpJoiningVar = null;
	
	/** A literal value, if present, in this triple pattern **/
	private String literal = null;
	
	/** The predicate in this triple pattern **/
	private transient Node subject = null, predicate = null, object = null;
	
	/** The value of the joining variable in the current triple pattern **/
	private String joiningVarValue = null;
	
	/** The value of the second variable in a triple pattern when there are two variables in a triple pattern **/
	private String secondVarValue = null;
	
	/** An identifier for the parent triple pattern from the SPARQL query **/
	private int parentTpId = 0;
	
	/** Constructor **/
	public SimpleTriplePatternImpl() { }
	
	@Override
	public String toString()
	{
		return tpId + " " + tpNumOfVars + " " + tpJoiningVar + " " + literal +
		" " + subject.toString() + " " + predicate.toString() + " " + object.toString() + " " + joiningVarValue + 
		" " + secondVarValue + " " + parentTpId;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setTriplePatternId(int)}
	 */
	public void setTriplePatternId( int id ) { this.tpId = id; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getTriplePatternId()}
	 */
	public int getTriplePatternId() { return tpId; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setNumOfVariables(int)}
	 */
	public void setNumOfVariables( int numOfVars ) { this.tpNumOfVars = numOfVars; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getNumOfVariables()}
	 */
	public int getNumOfVariables() { return tpNumOfVars; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setJoiningVariable(String)}
	 */
	public void setJoiningVariable( String joiningVar ) { this.tpJoiningVar = joiningVar; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getJoiningVariable()}
	 */
	public String getJoiningVariable() { return tpJoiningVar; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setLiteralValue(String)}
	 */
	public void setLiteralValue( String value ) { this.literal = value; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getLiteralValue()}
	 */
	public String getLiteralValue() { return literal; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setSubjectValue(Node)}
	 */
	public void setSubjectValue( Node sub ) { this.subject = sub; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getSubjectValue()}
	 */
	public Node getSubjectValue() { return subject; }

	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setPredicateValue(Node)}
	 */
	public void setPredicateValue( Node pred ) { this.predicate = pred; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getPredicateValue()}
	 */
	public Node getPredicateValue() { return predicate; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setPredicateValue(String)}
	 */
	public void setObjectValue( Node obj ) { this.object = obj; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getPredicateValue()}
	 */
	public Node getObjectValue() { return object; }

	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setJoiningVariableValue(String)}
	 */
	public void setJoiningVariableValue( String value ) { this.joiningVarValue = value; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getJoiningVariableValue()}
	 */
	public String getJoiningVariableValue() { return joiningVarValue; }

	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setSecondVariableValue(String)}
	 */
	public void setSecondVariableValue( String value ) { this.secondVarValue = value; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getSecondVariableValue()}
	 */
	public String getSecondVariableValue() { return secondVarValue; }

	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setParentTriplePatternId(int)}
	 */
	public void setParentTriplePatternId( int parentId ) { this.parentTpId = parentId; }
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getParentTriplePatternId()}
	 */
	public int getParentTriplePatternId() { return parentTpId; }
}