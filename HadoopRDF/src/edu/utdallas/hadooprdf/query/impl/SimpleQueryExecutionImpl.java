package edu.utdallas.hadooprdf.query.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;

import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.metadata.StringIdPairsException;
import edu.utdallas.hadooprdf.lib.util.Utility;
import edu.utdallas.hadooprdf.query.QueryExecution;
import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlan;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGenerator;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGeneratorFactory;
import edu.utdallas.hadooprdf.query.parser.HadoopElement;
import edu.utdallas.hadooprdf.query.parser.Query;
import edu.utdallas.hadooprdf.query.parser.QueryParser;
import edu.utdallas.hadooprdf.query.parser.QueryRewriter;
import edu.utdallas.hadooprdf.query.parser.UnhandledElementException;
import edu.utdallas.hadooprdf.query.parser.HadoopElement.HadoopTriple;

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
	
	/** A list of HadoopElement objects that represent the individual triple patterns in the query **/
	private List<HadoopElement> queryElements = new ArrayList<HadoopElement>();
	
	/** A list of the input filenames associated with a given query **/
	private List<String> inputFilenames = new ArrayList<String>();
	
	private static long numOfRows = 0L, numOfColumns = 0L;
	
	/**
	 * Constructor
	 * @param queryString - the SPARQL query as a string
	 * @param dataset - the DataSet object that will be used by the current QueryExecution
	 * @throws StringIdPairsException 
	 * @throws InterruptedException 
	 */
	public SimpleQueryExecutionImpl( String queryString, DataSet dataset ) throws StringIdPairsException, InterruptedException
	{
		//Assign the DataSet for this QueryExecution
		this.dataset = dataset;

		//Parse the query to get a Query object
		Query q = parseQuery( queryString );
		
		//Generate the prefixes used
		//PrefixNamespaceTree prefixTree = getPrefixNamespaceTree();

		//Get the list of triple patterns as HadoopElements based on the Query and PrefixNamespaceTree
		this.queryElements = rewriteQuery( q, this.dataset );
		
		//Create a QueryPlan
		this.queryPlan = createPlan( queryElements );		
		
		System.out.println( "Finished creating query plan" );		
	}

	/**
	 * A method that returns the variables in the SELECT clause of the query.
	 * This method can only be called after the query is parsed.
	 * @return A list of variables in the SELECT clause
	 * @throws Exception
	 */
	public List<String> getSelectVarsInQuery() throws Exception
	{
		return QueryParser.getVars();
	}
	
	/**
	 * A method that returns the input filenames to be used with the given query
	 * @return A list of the input filenames that will be used by this query
	 * @throws Exception
	 */
	public List<String> getFilenamesForQuery() throws Exception
	{
		//Iterate over all HadoopElements
		Iterator<HadoopElement> iterQElements = queryElements.iterator();
		while( iterQElements.hasNext() )
		{
			HadoopElement trPattern = iterQElements.next();
			
			//Get the triple patterns associated with a HadoopElement
			Iterator<HadoopTriple> iterTriples = trPattern.getTriple().iterator();
			while( iterTriples.hasNext() )
			{
				HadoopTriple triple = iterTriples.next();
				
				//Add all the files associated with a triple pattern to the list
				inputFilenames.addAll( triple.getAssociatedFiles() );
			}
		}
		return inputFilenames;
	}
	
	/**
	 * A method that generates the QueryPlan based on a list of HadoopElements
	 * @param list - the list of HadoopElements
	 * @return the generated QueryPlan
	 * @throws StringIdPairsException 
	 * @throws InterruptedException 
	 */
	public QueryPlan createPlan( List<HadoopElement> list ) throws StringIdPairsException, InterruptedException
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
	private ArrayList<HadoopElement> rewriteQuery( Query q, DataSet dataset )
	{
		ArrayList<HadoopElement> list = null;
		try { list = ( ArrayList<HadoopElement> )QueryRewriter.rewriteQuery( q, dataset ); }
		catch( Exception e ) { e.printStackTrace(); }
		return list;
	}
	
/*	*//**
	 * A method that generates the prefixes based on the given DataSet
	 * @return a PrefixNamespaceTree object
	 *//*
	@SuppressWarnings("unused")
	private PrefixNamespaceTree getPrefixNamespaceTree()
	{
		PrefixNamespaceTree prefixTree = null;
		try { prefixTree = ConfigPrefixTree.getPrefixTree( dataset, 5 ); }		
		catch( ConfigurationNotInitializedException e ) { e.printStackTrace(); }
		catch( IOException e ) { e.printStackTrace(); }
		catch (DataSetException e) { e.printStackTrace(); }
		return prefixTree;
	}
*/	
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
	 * {@link edu.utdallas.hadooprdf.query.QueryExecution#getDataSet()}
	 */
	@Override
	public DataSet getDataSet() 
	{
		return dataset;
	}

	/**
	 * {@link edu.utdallas.hadooprdf.query.QueryExecution#execSelect()}
	 */
	@Override
	public BufferedReader execSelect() 
	{	
		BufferedReader resultReader = null;
		Iterator<JobPlan> iterJobPlans = queryPlan.getJobPlans().iterator();
		while( iterJobPlans.hasNext() )
		{
			//Get the current job plan
			JobPlan jp = iterJobPlans.next();

			//Get the job id
			int jobId = jp.getJobId();
						
			//Get the Hadoop Job
			Job job = jp.getHadoopJob();
			
			try
			{ 
				//Serialize the job plan to a file
				File serializationFile = new File( "/home/vaibhav/Research/HadoopRDF/job.txt" );
				ObjectOutputStream objstream = new ObjectOutputStream( new FileOutputStream( serializationFile ) );
				objstream.writeObject( jp );
				objstream.close();
	        
				//Transfer the file to the hdfs
				org.apache.hadoop.conf.Configuration hadoopConfiguration = job.getConfiguration();
				
				FileSystem fs;
				fs = FileSystem.get( hadoopConfiguration ); 

				if( jobId > 1 )
				{
					fs.copyToLocalFile( new Path( dataset.getPathToTemp(), "test" + ( jobId - 1 ) + "/part-r-00000" ), new Path( "/home/vaibhav/Research/HadoopRDF/job" + ( jobId - 1 ) + "-op.txt" ) );
					fs.moveFromLocalFile( new Path( "/home/vaibhav/Research/HadoopRDF/job" + ( jobId - 1 ) + "-op.txt" ), dataset.getPathToTemp() );
					//fs.delete( new Path( dataset.getPathToTemp(), "test" + ( jobId - 1 ) ), true );
				}
				
				fs.delete( new Path( dataset.getPathToTemp(), "job.txt" ), true );
				fs.copyFromLocalFile( new Path( "/home/vaibhav/Research/HadoopRDF/job.txt" ), dataset.getPathToTemp() );
				serializationFile.delete();
				
				//Run the current job
				job.waitForCompletion( true );
				
				System.out.println( "finished mapreduce job, time = " + System.currentTimeMillis() );
				
				//TODO: Get the output path differently
				if( !jp.getHasMoreJobs() ) 
				{
					if( jobId > 1 )
					{
						for( int i = 2; i <= jobId; i++ )
							fs.delete( new Path( dataset.getPathToTemp(), "job" + ( i - 1 ) + "-op.txt" ), true );
					}
					fs.delete( new Path( dataset.getPathToTemp(), "job.txt" ), true );
					BufferedReader resReader = new BufferedReader( new InputStreamReader( fs.open( new Path( dataset.getPathToTemp(), "test" + jp.getJobId() + "/part-r-00000" ) ) ) );

					//TODO: Add the reverse mapping based on dictionary
					Set<String> usedDictionaryFiles = parseOutputForFiles( resReader );
					resReader.close();
					String[][] arrResults = convertResultToString( usedDictionaryFiles, fs, jp.getJobId() );
					BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( fs.create( new Path( dataset.getPathToTemp(), "results" ) ) ) );
					for( int i = 0; i < numOfRows; i++ )
					{
						String str = "";
						for( int j = 0; j < numOfColumns; j++ )
						{
							str += arrResults[i][j] + "\t";
						}
						writer.write( str + "\n" ); writer.flush();
					}
					writer.close();
					resultReader = new BufferedReader( new InputStreamReader( fs.open( new Path( dataset.getPathToTemp(), "results" ) ) ) );
					
					System.out.println( "finished dictionary based conversion, time = " + System.currentTimeMillis() );
				}
			}
			catch( Exception e ) { e.printStackTrace(); }
		}
		return resultReader;
	}

	/**
	 * 
	 * @param usedDictionaryFiles - the set of dictionary files needed to process the current result
	 * @param fs - the hadoop file system handle
	 * @param jobId - the current job id
	 * @return
	 * @throws IOException
	 */
	private String[][] convertResultToString( Set<String> usedDictionaryFiles, FileSystem fs, int jobId ) throws IOException
	{
		String[][] arrResults = new String[(int) numOfRows][(int) numOfColumns];
		Iterator<String> iterUsedDictionaryFiles = usedDictionaryFiles.iterator();
		while( iterUsedDictionaryFiles.hasNext() )
		{
			BufferedReader reader = new BufferedReader( new InputStreamReader( fs.open( new Path( dataset.getPathToDictionary(), iterUsedDictionaryFiles.next() ) ) ) );
			String str = null;
			while( ( str = reader.readLine() ) != null )
			{
				String[] splitStr = str.split( "\t" );
				long numOfRows = 0L;
				BufferedReader inReader = new BufferedReader( new InputStreamReader( fs.open( new Path( dataset.getPathToTemp(), "test" + jobId + "/part-r-00000" ) ) ) );
				String inStr = null;
				while( ( inStr = inReader.readLine() ) != null )
				{
					numOfRows++;
					long numOfColumns = 0L;
					String[] splitInStr = inStr.split( "\t" );
					for( int i = 0; i < splitInStr.length; i++ )
					{
						numOfColumns++;
						if( splitStr[2].equalsIgnoreCase( splitInStr[i] ) )
						{
							arrResults[(int) (numOfRows - 1)][(int) (numOfColumns - 1)] = splitStr[0];
						}
					}
				}
				inReader.close();
			}
			reader.close();
		}
		return arrResults;
	}
	
	/**
	 * 
	 * @param inReader
	 * @return
	 * @throws IOException
	 */
	private Set<String> parseOutputForFiles( BufferedReader inReader ) throws IOException
	{
		Set<String> usedDictionaryFiles = new HashSet<String>();
		String str = null;
		int reducerBits = Utility.getMaxBitsRequiredToStore( 100 - 1 );
		long mask = 1;
		mask <<= reducerBits; mask--; mask <<= ( Long.SIZE - reducerBits ); 
		while( ( str = inReader.readLine() ) != null )
		{
			numOfRows++;
			String[] splitStr = str.split( "\t" );
			numOfColumns = splitStr.length;
			for( int i = 0; i < splitStr.length; i++ )
			{
				long value = Long.parseLong( splitStr[i] );
				long and = mask & value;
				and >>>= ( Long.SIZE - reducerBits );
				usedDictionaryFiles.add( "part-r-" + getReducerPartForFilename( and ) );
			}
		}
		return usedDictionaryFiles;
	}
	
	/**
	 * A method that converts the reducer id to the required form
	 * @param reducerId - the reducer id as an integer
	 * @return a converted string 0 => 00000
	 */
	private String getReducerPartForFilename( long reducerId )
	{
		String retReducerId = "";
		String strReducerId = "" + reducerId;
		for( int i = 1; i <= ( 5 - strReducerId.length() ); i++ )
		{
			retReducerId += "0";
		}
		return retReducerId + strReducerId;
	}
}
