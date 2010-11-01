package edu.utdallas.hadooprdf.query;

import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.metadata.StringIdPairsException;
import edu.utdallas.hadooprdf.query.impl.SimpleQueryExecutionImpl;

/**
 * A factory class to create QueryExecution objects from a query string
 * @author vaibhav
 *
 */
public class QueryExecutionFactory 
{
	/**
	 * A method that constructs a QueryExecution object
	 * @param queryString - the input query as a string
	 * @param dataset - the DataSet for this QueryExecution object
	 * @return a QueryExecution object
	 * @throws StringIdPairsException 
	 * @throws InterruptedException 
	 */
	public static QueryExecution create( String queryString, DataSet dataset ) throws StringIdPairsException, InterruptedException
	{
		return new SimpleQueryExecutionImpl( queryString, dataset );
	}
}
