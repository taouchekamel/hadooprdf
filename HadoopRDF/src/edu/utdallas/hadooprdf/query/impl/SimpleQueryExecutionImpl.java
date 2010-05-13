package edu.utdallas.hadooprdf.query.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;

import edu.utdallas.hadooprdf.conf.ConfigurationException;
import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.rdf.uri.prefix.PrefixNamespaceTree;
import edu.utdallas.hadooprdf.lib.util.JobParameters;
import edu.utdallas.hadooprdf.query.QueryExecution;
import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlan;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGenerator;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGeneratorFactory;
import edu.utdallas.hadooprdf.query.parser.ConfigPrefixTree;
import edu.utdallas.hadooprdf.query.parser.HadoopElement;
import edu.utdallas.hadooprdf.query.parser.Query;
import edu.utdallas.hadooprdf.query.parser.QueryParser;
import edu.utdallas.hadooprdf.query.parser.QueryRewriter;
import edu.utdallas.hadooprdf.query.parser.UnhandledElementException;

/**
 * A simple implementation of the QueryExecution interface
 * @author vaibhav
 *
 */
public class SimpleQueryExecutionImpl implements QueryExecution
{
	/** The QueryPlan that will be used for the current execution of a SPARQL query **/
	private QueryPlan queryPlan = null;
	
	/** The DataSet object used by the current QueryExecution object **/
	private DataSet dataset = null;
	
	/**
	 * Constructor
	 * @param queryString - the SPARQL query as a string
	 * @param dataset - the DataSet object to be used by the current QueryExecution
	 */
	public SimpleQueryExecutionImpl( String queryString, DataSet dataset )
	{
		//Assign this QueryExecution the given DataSet
		this.dataset = dataset;

		//Parse the query to get a Query object
		Query q = parseQuery( queryString );
		
		//Generate the prefixes used
		PrefixNamespaceTree prefixTree = getPrefixNamespaceTree();
		
		//Get the list of triple patterns as HadoopElements based on the Query and PrefixNamespaceTree
		ArrayList<HadoopElement> list = rewriteQuery( q, prefixTree, this.dataset );
		
		//Create a QueryPlan
		this.queryPlan = createPlan( list );		
	}
	
	/**
	 * A method that generates the QueryPlan based on a list of HadoopElements
	 * @param list - the list of HadoopElements
	 * @return the generated QueryPlan
	 */
	public QueryPlan createPlan( List<HadoopElement> list )
	{
		QueryPlanGenerator qpgen = QueryPlanGeneratorFactory.createSimpleQueryPlanGenerator( dataset );
		try { queryPlan = qpgen.generateQueryPlan( list ); }
		catch( Exception e ) { e.printStackTrace(); }
		return queryPlan;
	}
	
	/**
	 * A method for rewriting the Query given the PrefixNamespaceTree
	 * @param q - the Query object
	 * @param prefixTree - the PrefixNamespaceTree object
	 * @return a list of HadoopElements
	 */
	private ArrayList<HadoopElement> rewriteQuery( Query q, PrefixNamespaceTree prefixTree, DataSet dataset )
	{
		ArrayList<HadoopElement> list = null;
		try { list = ( ArrayList<HadoopElement> )QueryRewriter.rewriteQuery( q, prefixTree, dataset ); }
		catch( Exception e ) { e.printStackTrace(); }
		return list;
	}
	
	/**
	 * A method that generates the prefixes based on the given DataSet
	 * @return a PrefixNamespaceTree object
	 */
	private PrefixNamespaceTree getPrefixNamespaceTree()
	{
		PrefixNamespaceTree prefixTree = null;
		try { prefixTree = ConfigPrefixTree.getPrefixTree( JobParameters.configFileDir, dataset.toString(), 5); }		
		catch( ConfigurationNotInitializedException e ) { e.printStackTrace(); }
		catch( IOException e ) { e.printStackTrace(); }
		catch (ConfigurationException e) { e.printStackTrace();	}
		return prefixTree;
	}
	
	/**
	 * A method that parses the given SPARQL query to generate a Query object
	 * @param queryString - the given SPARQL query as a string
	 * @return a Query object representing the SPARQL query
	 */
	private Query parseQuery( String queryString )
	{
		Query query = null;
		try { query = QueryParser.parseQuery( queryString ); }
		catch( UnhandledElementException e ) { e.printStackTrace(); }
		return query;
	}
	
	/**
	 * {@link edu.utdallas.hadooprdf.query.QueryExecution#execSelect()}
	 */
	@Override
	public String execSelect() 
	{
		//TODO: Get this path differently
		String opPath = "/home/hadoop/output.txt";
		Iterator<JobPlan> iterJobPlans = queryPlan.getJobPlans().iterator();
		while( iterJobPlans.hasNext() )
		{
			//Get the current job plan
			JobPlan jp = iterJobPlans.next();

			//Get the Hadoop Job
			Job job = jp.getHadoopJob();
			
			try
			{ 
				//Serialize the job plan to a file
				ObjectOutputStream objstream = new ObjectOutputStream(new FileOutputStream("/home/hadoop/job.txt"));
				objstream.writeObject(jp);
				objstream.close();
	        
				//Transfer the file to the hdfs
				org.apache.hadoop.conf.Configuration hadoopConfiguration = job.getConfiguration();
				
				FileSystem fs;
				fs = FileSystem.get(hadoopConfiguration); 
				
				fs.delete( new Path( dataset.getPathToTemp(), "job.txt" ), true );
				fs.copyFromLocalFile( new Path( "/home/hadoop/job.txt" ), dataset.getPathToTemp() );
				
				//Run the current job
				job.waitForCompletion( true );
				
				//TODO: Get the output path differently
				if( !jp.getHasMoreJobs() ) fs.copyToLocalFile( new Path( dataset.getPathToTemp(), "test" + jp.getJobId() + "part-r-00000" ), new Path( "/home/hadoop/result.txt" ) );
			}
			catch( Exception e ) { e.printStackTrace(); }
		}
		return opPath;
	}

	/**
	 * {@link edu.utdallas.hadooprdf.query.QueryExecution#getDataSet()}
	 */
	@Override
	public DataSet getDataSet() 
	{
		return dataset;
	}
}
