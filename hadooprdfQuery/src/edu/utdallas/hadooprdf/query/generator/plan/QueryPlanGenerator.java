package edu.utdallas.hadooprdf.query.generator.plan;

import java.io.IOException;
import java.util.List;

import edu.utdallas.hadooprdf.query.parser.HadoopElement;
import edu.utdallas.hadooprdf.query.parser.NotBasicElementException;
import edu.utdallas.hadooprdf.query.parser.UnhandledElementException;

/**
 * An interface that defines the methods needed for the query plan generator algorithm
 * @author sharath, vaibhav
 *
 */
public interface QueryPlanGenerator 
{
	/**
	 * A method that generates a query plan based on a list of hadoop elements
	 * @param elements - the list of hadoop elements formed from parsing the query
	 * @return a QueryPlan object
	 */
	public abstract QueryPlan generateQueryPlan( List<HadoopElement> elements ) throws UnhandledElementException, NotBasicElementException, IOException, Exception;
}