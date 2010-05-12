package edu.utdallas.hadooprdf.query.generator.triplepattern.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;

import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;

/**
 * A simple implementation of the triple pattern
 * @author sharath, vaibhav
 *
 */
public class SimpleTriplePatternImpl implements TriplePattern, Serializable
{
	/**	The serial version UID **/
	private static final long serialVersionUID = -5695001996186890719L;

	/** The triple pattern id from the query **/
	private int tpId = 0;
	
	/** The number of variables in the triple pattern **/
	private int tpNumOfVars = 0;
	
	/** The joining variable in this triple pattern **/
	private String tpJoiningVar = null;
	
	/** A map between filenames and their prefixes to be used in a job **/
	private Map<String,String> filenamePrefixMap = new HashMap<String,String>();

	/** A literal value, if present, in this triple pattern **/
	private String literal = null;
	
	/** The predicate in this triple pattern **/
	private transient Node subject = null, predicate = null, object = null;
	
	/** The value of the joining variable in the current triple pattern **/
	private String joiningVarValue = null;
	
	/** Constructor **/
	public SimpleTriplePatternImpl() { }
	
	@Override
	public String toString()
	{
		return tpId + " " + tpNumOfVars + " " + tpJoiningVar + "#" + filenamePrefixMap.toString() + "#" + literal +
		" " + subject.toString() + " " + predicate.toString() + " " + object.toString() + " " + joiningVarValue;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setTriplePatternId(int)}
	 */
	public void setTriplePatternId( int id )
	{
		this.tpId = id;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getTriplePatternId()}
	 */
	public int getTriplePatternId()
	{
		return tpId;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setNumOfVariables(int)}
	 */
	public void setNumOfVariables( int numOfVars )
	{
		this.tpNumOfVars = numOfVars;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getNumOfVariables()}
	 */
	public int getNumOfVariables()
	{
		return tpNumOfVars;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setJoiningVariable(String)}
	 */
	public void setJoiningVariable( String joiningVar )
	{
		this.tpJoiningVar = joiningVar;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getJoiningVariable()}
	 */
	public String getJoiningVariable()
	{
		return tpJoiningVar;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setFilenameBasedPrefix(String, String)}
	 */
	public void setFilenameBasedPrefix( String filename, String prefix )
	{
		filenamePrefixMap.put( filename, prefix );
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getFilenameBasedPrefix(String)}
	 */
	public String getFilenameBasedPrefix( String filename )
	{
		return filenamePrefixMap.get( filename );
	}

	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#checkIfPrefixExists(String)}
	 */
	public boolean checkIfPrefixExists( String prefix )
	{
		return filenamePrefixMap.containsValue( prefix );
	}

	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setLiteralValue(String)}
	 */
	public void setLiteralValue( String value )
	{
		this.literal = value;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getLiteralValue()}
	 */
	public String getLiteralValue()
	{
		return literal;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setSubjectValue(Node)}
	 */
	public void setSubjectValue( Node sub )
	{
		this.subject = sub;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getSubjectValue()}
	 */
	public Node getSubjectValue()
	{
		return subject;
	}

	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setPredicateValue(Node)}
	 */
	public void setPredicateValue( Node pred )
	{
		this.predicate = pred;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getPredicateValue()}
	 */
	public Node getPredicateValue()
	{
		return predicate;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setPredicateValue(String)}
	 */
	public void setObjectValue( Node obj )
	{
		this.object = obj;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getPredicateValue()}
	 */
	public Node getObjectValue()
	{
		return object;
	}

	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#setJoiningVariableValue(String)}
	 */
	public void setJoiningVariableValue( String value )
	{
		this.joiningVarValue = value;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern#getJoiningVariableValue()}
	 */
	public String getJoiningVariableValue()
	{
		return joiningVarValue;
	}
}