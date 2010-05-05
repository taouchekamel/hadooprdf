package edu.utdallas.hadooprdf.query.generator.plan.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.job.JobPlanFactory;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlan;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanFactory;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGenerator;
import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;
import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePatternFactory;
import edu.utdallas.hadooprdf.query.jobrunner.GenericJobRunner.GenericMapper;
import edu.utdallas.hadooprdf.query.jobrunner.GenericJobRunner.GenericReducer;
import edu.utdallas.hadooprdf.query.parser.HadoopElement;
import edu.utdallas.hadooprdf.query.parser.NotBasicElementException;
import edu.utdallas.hadooprdf.query.parser.UnhandledElementException;
import edu.utdallas.hadooprdf.query.util.JobParameters;

/**
 * A specific implementation of the query plan generator based on the "elimination count" algorithm
 * @author sharath, vaibhav
 *
 */
@SuppressWarnings("deprecation")
public class SimpleQueryPlanGeneratorImpl implements QueryPlanGenerator
{
	private Map<Integer,String> elimVarCountMap = new HashMap<Integer,String>();
	
	/** A map of triple pattern id's and the corresponding triple pattern that is used in every job **/
	private Map<Integer,TriplePattern> tpMap = new HashMap<Integer,TriplePattern>();
	
	/**
	 * Constructor
	 */
	public SimpleQueryPlanGeneratorImpl() { }
	
	/**
	 * Method that generates the query plan based on the "elimination count" algorithm
	 * @param elements - a list of elements from the QueryParser
	 * @return a QueryPlan object
	 * @throws NotBasicElementException 
	 * @throws UnhandledElementException 
	 * @throws IOException 
	 */
	public QueryPlan generateQueryPlan( List<HadoopElement> elements ) 
	throws UnhandledElementException, NotBasicElementException, IOException
	{
		
		//Create a simple query plan
		QueryPlan qp = QueryPlanFactory.createSimpleQueryPlan();
		
		//Create a list of job plans
		List<JobPlan> jps = new ArrayList<JobPlan>();
		
		//Create the Hadoop configuration based on the path where the various site files are
		Configuration config = getConfiguration( JobParameters.configFileDir );
		
		//Run the heuristic to find out if there is a common variable
		String commonVariable = runHeuristic( elements );
		
		//The case where there is a common variable involves only a single job
		//Else run the "elimination count" algorithm
		if( commonVariable != null )
		{
			//Create a single job plan
			JobPlan jp = JobPlanFactory.createSimpleJobPlan();
		
			//Use the configuration to define a Hadoop Job
			Job currJob = new Job( config, "HeuristicBasedJob" );
			
			//Set the parameters for the job that are fixed in this version
			setJobParameters( currJob );
			
			//Find the position of the joining variable in each triple pattern
			Iterator<HadoopElement> elemIter = elements.iterator();
			while( elemIter.hasNext() )
			{
				//Get each hadoop element
				HadoopElement element = elemIter.next();
				
				//Get the total number of variables
				//TODO: Get the total variables from the query parser
				int totalVars = element.getElement().varsMentioned().size();

				//A counter
				int count = 0;

				//Get triple patterns associated with a hadoop element
				Iterator<Triple> tripleIter = element.getTriple().iterator();
				while( tripleIter.hasNext() )
				{
					//Get each triple pattern
					Triple triple = tripleIter.next();

					//The triple pattern associated with the current counter
					TriplePattern tp = tpMap.get( ++count );

					if( triple.getSubject().toString().equalsIgnoreCase( commonVariable ) )
						tp.setJoiningVariable( "s" );
					else
						tp.setJoiningVariable( "o" );
					
					//Get the predicate, i.e. filename
					String pred = triple.getPredicate().toString();
					
					//Add the filename to the Job object
					FileInputFormat.addInputPath(currJob, new Path( JobParameters.inputHDFSDir + pred ) );
					
					//Add the filenames and their associated prefixes to the triple pattern
					//TODO: get this from the query parser
					tp.setFilenameBasedPrefix( pred, "" );

					//Add the triple pattern to the job plan
					jp.setPredicateBasedTriplePattern( pred, tp );					
				}
				
				//Set the total number of variables expected in the result
				jp.setTotalVariables( totalVars );
			}
			
			//Add the output file to the Job object
			FileOutputFormat.setOutputPath(currJob, new Path( JobParameters.outputHDFSDir + "test" ) );
			
			//Add the Hadoop Job to the JobPlan
			jp.setHadoopJob( currJob );
			
			//Set a flag to denote that there are no more jobs
			jp.setHasMoreJobs( false );
			
			//TODO: Add the input files, output files and file prefixes to the job plan. Also the hadoop specific parameters
			
			//Add the job plan to the list of job plans
			jps.add( jp );
			
			//Add the list of job plans to the query plan
			qp.addJobPlans( jps );
		}
		else
		{
			constructElimCountMap( elements );
		}
		
		return qp;
	}
	
	private void constructElimCountMap( List<HadoopElement> elements ) throws UnhandledElementException, NotBasicElementException
	{
		/** A map of variables and their associated elimination counts **/
		Map<String,Integer> varElimCountMap = new HashMap<String,Integer>();

		//Check if all elements share a common variable
		Iterator<HadoopElement> elemIter = elements.iterator();
		while( elemIter.hasNext() )
		{
			//Get each hadoop element
			HadoopElement element = elemIter.next();
			
			//Get triple patterns associated with a hadoop element
			Iterator<Triple> tripleIter = element.getTriple().iterator();
			while( tripleIter.hasNext() )
			{
				//Get each triple pattern
				Triple triple = tripleIter.next();
				
				//Get the subject and object for that triple pattern
				Node sub = triple.getSubject(), obj = triple.getObject();
				String strSub = sub.toString(), strObj = obj.toString();
				
				//Check if both subject and object are variables
				if( sub.isVariable() && obj.isVariable() )
				{
					//If the hashmap is empty put both subject and object in the hashmap
					if( varElimCountMap.isEmpty() ) { varElimCountMap.put( strSub, new Integer( 1 ) ); varElimCountMap.put( strObj, new Integer( 1 ) ); }
					else
					{
						//If the subject is a variable
						if( sub.isVariable() ) 
						{
							//Get the current elimination count for the current variable
							Integer i = varElimCountMap.get( strSub );
							
							//If the hashmap contains the current variable update its elimination count
							//Else add a new entry in the hashmap
							if( i != null ) varElimCountMap.put( strSub, new Integer( i + 1 ) );
							else varElimCountMap.put( strSub, new Integer( 1 ) );
						}
						if( obj.isVariable() ) 
						{
							//Get the current elimination count for the current variable
							Integer i = varElimCountMap.get( strObj );

							//If the hashmap contains the current variable update its elimination count
							//Else add a new entry in the hashmap
							if( i != null ) varElimCountMap.put( strObj, new Integer( i + 1 ) );
							else varElimCountMap.put( strObj, new Integer( 1 ) );
						}
					}
				}
			}
			
			//Sort the values in the hashmap
			Collection<Integer> values = varElimCountMap.values();
			
			//Get the associated array
			Object[] inArr = values.toArray();
			
			//Sort the values
			Arrays.sort( inArr );
			
			//Iterate over the sorted array
			for( int i = 0; i < inArr.length; i++ )
			{
				String keys = "";
				
				//Check each entry in the variable based hashmap for the value from the sorted array. Combine the keys that have that value.
				Iterator<String> iterKeys =  varElimCountMap.keySet().iterator();
				while( iterKeys.hasNext() )
				{
					String key = iterKeys.next();
					Integer value = varElimCountMap.get( key );
					if( value == inArr[i] ) keys += key + "~";
				}
				
				//If the variable count hashmap is empty add a new entry based on the current value in the sorted array and the keys that have that value
				//Else get the old value from the hashmap and update it
				if( elimVarCountMap.isEmpty() )
					elimVarCountMap.put( ( Integer )inArr[i], keys );
				else
					elimVarCountMap.put( ( Integer )inArr[i], elimVarCountMap.get( ( Integer )inArr[i] ) + keys );
			}
		}
	}
	
	/**
	 * A method that sets the parameters that are fixed for the current Hadoop Job
	 * @param currJob - the current Hadoop Job
	 */
	@SuppressWarnings("unchecked")
	private void setJobParameters( Job currJob )
	{
		//Set the output key and value class
		currJob.setOutputKeyClass( Text.class );
		currJob.setOutputValueClass( Text.class );
		
		//Set the mapper output key and value class
		currJob.setMapOutputKeyClass( Text.class );
		currJob.setMapOutputValueClass( Text.class );
		
		//Set the input and output format classes
		currJob.setInputFormatClass( TextInputFormat.class );
		currJob.setOutputFormatClass( TextOutputFormat.class );
		
		//Set the jar file to be used
		( (JobConf) currJob.getConfiguration() ).setJar( JobParameters.jarFile );
		
		//Set the mapper and reducer classes to be used
		Class<GenericMapper> genericMapper = null;
		Class<GenericReducer> genericReducer = null;
		try
		{
			genericMapper = ( Class<GenericMapper> ) Class.forName( JobParameters.mapperClass );
			genericReducer = ( Class<GenericReducer> ) Class.forName( JobParameters.reducerClass );
		}
		catch( Exception e ) { e.printStackTrace(); }
		currJob.setMapperClass( genericMapper );
		currJob.setReducerClass( genericReducer );
		
		//Set the number of reducers
		currJob.setNumReduceTasks( JobParameters.numOfReducers );
	}
	
	/**
	 * A method that returns a Hadoop Configuration object based on the configuration files
	 * @param path - the directory that contains the configuration files
	 * @return a Hadoop Configuration object
	 */
	private Configuration getConfiguration( String path )
	{
		Configuration config = new Configuration();
		
		config.addResource( path + "core-site.xml" );
		config.addResource( path + "mapred-site.xml" );
		config.addResource( path + "hdfs-site.xml" );
		
		return config;
	}
	
	/**
	 * A method that tests the heuristic of whether all triple patterns have a common variable
	 * @param elements - the triple patterns from the query
	 * @return a null string if there is no common variable, the common variable as a string otherwise
	 * @throws UnhandledElementException
	 * @throws NotBasicElementException
	 */
	private String runHeuristic( List<HadoopElement> elements ) throws UnhandledElementException, NotBasicElementException
	{
		//A hashmap that holds each variable and the associated count of times it is found across all triple patterns
		HashMap<String,Integer> hm = new HashMap<String,Integer>();
		
		//A boolean that denotes if we prematurely abort the construction of the hashmap
		boolean isAborted = false;
		
		//The variable that is common to all triple patterns
		String commonVariable = null;
		
		//Check if all elements share a common variable
		Iterator<HadoopElement> elemIter = elements.iterator();
		while( elemIter.hasNext() )
		{
			//Get each hadoop element
			HadoopElement element = elemIter.next();
			
			//A counter
			int count = 0;
			
			//Get triple patterns associated with a hadoop element
			Iterator<Triple> tripleIter = element.getTriple().iterator();
			while( tripleIter.hasNext() )
			{
				//Get each triple pattern
				Triple triple = tripleIter.next();
				
				//Create a TriplePattern object that will be used in the construction of a JobPlan
				TriplePattern tp = TriplePatternFactory.createSimpleTriplePattern();
			
				//Set the id of the triple pattern
				tp.setTriplePatternId( ++count );
				
				//Get the subject and object for that triple pattern
				Node sub = triple.getSubject(), obj = triple.getObject();
				String strSub = sub.toString(), strObj = obj.toString();
				
				//Check if both subject and object are variables
				if( sub.isVariable() && obj.isVariable() )
				{					
					//Set the number of variables in the triple pattern to two
					tp.setNumOfVariables( 2 );
					
					//For a subject variable, if it is not in the hashmap add it, else increment its count in the hashmap
					if( hm.isEmpty() || hm.get( strSub ) == null ) hm.put( strSub, new Integer( "1" ) );
					else hm.put( strSub, new Integer( hm.get( strSub ).intValue() + 1 ) );

					//For an object variable, if it is not in the hashmap add it, else increment its count in the hashmap
					if( hm.isEmpty() || hm.get( strObj ) == null ) hm.put( strObj, new Integer( "1" ) );
					else hm.put( strObj, new Integer( hm.get( strObj ).intValue() + 1 ) );
				}
				else
				{
					//Set the number of variables in the triple pattern to one
					tp.setNumOfVariables( 1 );

					//If only one of subject or object is a variable then do this
					//If subject does not exist for a single variable in the hashmap then there no common variable, hence break
					//Similarly for object
					if( sub.isVariable() )	
					{
						//Add the object as a literal for the current triple pattern
						tp.setLiteralValue( strObj );
						
						if( !hm.isEmpty() && hm.get( strSub ) == null ) { isAborted = true; break; }
						else 
						{
							if( hm.isEmpty() ) hm.put( strSub, new Integer( 1 ) );
							else hm.put( strSub, new Integer( hm.get( strSub ).intValue() + 1 ) );
						}
					}
					else
					{
						//Add the subject as a literal value for the current triple pattern
						tp.setLiteralValue( strSub );
						
						if( !hm.isEmpty() && hm.get( strObj ) == null ) { isAborted = true; break; }
						else 
						{
							if( hm.isEmpty() ) hm.put( strObj, new Integer( 1 ) );
							else hm.put( strObj, new Integer( hm.get( strObj ).intValue() + 1 ) );						
						}
					}
				}
				
				tpMap.put( new Integer( count ), tp );
			}
			
			//If we have successfully constructed the hashmap then check if the count for any variable in the hashmap
			//equals the number of triple patterns.
			//If it does then we have found a common variable between all triple patterns, return that variable, else return null
			if( !isAborted )
			{
				Iterator<String> keyIter = hm.keySet().iterator();
				while( keyIter.hasNext() )
				{
					String key = keyIter.next();
					if( hm.get( key ).intValue() == element.getTriple().size() ) { commonVariable = key; }
				}
			}
		}
		
		return commonVariable;
	}
}