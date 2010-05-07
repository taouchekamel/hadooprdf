package edu.utdallas.hadooprdf.query.generator.plan.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import edu.utdallas.hadooprdf.query.parser.QueryParser;
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
	/** A map of original variables and the associated triple pattern identifiers **/
	private Map<String,String> varOrigTpBasedMap = new HashMap<String,String>();

	/** A map of variables used in a job and the associated triple pattern identifiers **/
	private Map<String,String> varUsedTpBasedMap = new HashMap<String,String>();

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
	 * @throws Exception 
	 */
	public QueryPlan generateQueryPlan( List<HadoopElement> elements ) 
	throws UnhandledElementException, NotBasicElementException, IOException, Exception
	{
		//TODO: Need to check this plan generation with triple patterns of the kind ?x pred ?x, if this case will ever arise.
		
		//Create a simple query plan
		QueryPlan qp = QueryPlanFactory.createSimpleQueryPlan();

		//Create a list of job plans
		List<JobPlan> jps = new ArrayList<JobPlan>();

		//Create the Hadoop configuration based on the path where the various site files are
		Configuration config = getConfiguration( JobParameters.configFileDir );

		//Get the total number of variables
		int totalVars = QueryParser.getNumOfVarsInQuery();

		//The job identifiers
		int jobId = 1;
		
		//Get the total number of triple patterns
		int totalTrPatterns = elements.get( 0 ).getTriple().size();
		
		//Construct the input maps that will be used
		constructInputMaps( elements );
		
		//A list of variables to run the heuristic
		List<String> oldInputVarList = new ArrayList<String>();
		List<String> newInputVarList = new ArrayList<String>();
		
		while( totalTrPatterns > 0 )
		{	
			if( jobId == 1 ) { oldInputVarList = newInputVarList = constructVarList(); }
			else
			{
				newInputVarList = updateVariableList( oldInputVarList );
			}
						
			//Run the heuristic to find out if there is a common variable
			String commonVariable = runHeuristic( newInputVarList );

			//Create a single job plan
			JobPlan jp = JobPlanFactory.createSimpleJobPlan();

			//Set the total number of variables expected in the result
			jp.setTotalVariables( totalVars );

			//The case where there is a common variable involves only a single job
			//Else run the "elimination count" algorithm
			if( commonVariable != null )
			{
				//Use the configuration to define a Hadoop Job
				Job currJob = new Job( config, "HeuristicBasedJob" );

				//Set the parameters for the job that are fixed in this version
				//setJobParameters( currJob );

				//Find the position of the joining variable in each triple pattern
				Iterator<Integer> iterTpMap = tpMap.keySet().iterator();
				while( iterTpMap.hasNext() )
				{
					//Get each triple pattern identifier
					Integer key = iterTpMap.next();

					//Get the corresponding triple pattern
					TriplePattern tp = tpMap.get( key );
					
					//Remove that triple pattern from the 
					tpMap.remove( key );
					
					//Reduce the number of total triple patterns by one
					totalTrPatterns--;

					//Set the value of the joining variable
					tp.setJoiningVariableValue( commonVariable );

					//Set the value of the joining variable for the current triple pattern
					if( tp.getSubjectValue().toString().equalsIgnoreCase( commonVariable ) )
						tp.setJoiningVariable( "s" );
					else
						tp.setJoiningVariable( "o" );

					//Get the predicate, i.e. filename
					String pred = tp.getPredicateValue().toString();

					//Add the filename to the Job object
					FileInputFormat.addInputPath( currJob, new Path( JobParameters.inputHDFSDir + pred ) );

					//Add the filenames and their associated prefixes to the triple pattern
					//TODO: get this from the query parser
					tp.setFilenameBasedPrefix( pred, "" );

					//Add the triple pattern to the job plan
					jp.setPredicateBasedTriplePattern( pred, tp );					
				}

				//Add the output file to the Job object
				FileOutputFormat.setOutputPath(currJob, new Path( JobParameters.outputHDFSDir + "test" ) );

				//Add the Hadoop Job to the JobPlan
				jp.setHadoopJob( currJob );

				//Set a flag to denote that there are no more jobs
				jp.setHasMoreJobs( false );
			}
			else
			{			
				//Use the configuration to define a Hadoop Job
				Job currJob = new Job( config, "Job" + jobId );

				//Set the parameters for the job that are fixed in this version
				//setJobParameters( currJob );
								
				//Get the elimination count based treemap
				Map<Integer,String> elimVarCountMap = constructElimCountMap( newInputVarList );

				//Iterate over each key
				Iterator<Integer> iterElimCount = elimVarCountMap.keySet().iterator();
				while( iterElimCount.hasNext() )
				{
					if( tpMap.size() < 2 ) break;

					//Get each key
					Integer key = iterElimCount.next();

					//Get the corresponding value
					String[] values = elimVarCountMap.get( key ).split( "~" );

					//Iterate over all values
					for( int i = 0; i < values.length; i++ )
					{
						if( tpMap.size() < 2 ) break;
						
						String[] trPatterns = varOrigTpBasedMap.get( values[i] ).split( "~" );
						for( int j = 0; j < trPatterns.length; j++ )
						{
							//Get the triple pattern associated with the current value
							TriplePattern tp = tpMap.get( new Integer( trPatterns[j] ) );
							
							//If the triple pattern is null, it is already used by some other variable, so simply continue
							if( tp == null ) continue;
							
							//Remove that triple pattern from the 
							tpMap.remove( new Integer( trPatterns[j] ) );
							
							//Reduce the number of total triple patterns by one
							totalTrPatterns--;

							//Set the value of the joining variable
							tp.setJoiningVariableValue( values[i] );
							
							if( varUsedTpBasedMap.isEmpty() || varUsedTpBasedMap.get( values[i] ) == null )
								varUsedTpBasedMap.put( values[i], trPatterns[j] + "~" );
							else
								varUsedTpBasedMap.put( values[i], varUsedTpBasedMap.get( values[i] ) + trPatterns[j] + "~" );
							
							//Set the value of the joining variable for the current triple pattern
							if( tp.getSubjectValue().toString().equalsIgnoreCase( values[i] ) )
								tp.setJoiningVariable( "s" );
							else
								tp.setJoiningVariable( "o" );
							
							String pred = tp.getPredicateValue().toString();
							
							//Add the filename to the Job object
							FileInputFormat.addInputPath( currJob, new Path( JobParameters.inputHDFSDir + pred ) );

							//Add the filenames and their associated prefixes to the triple pattern
							//TODO: get this from the query parser
							tp.setFilenameBasedPrefix( pred, "" );

							//Add the triple pattern to the job plan
							jp.setPredicateBasedTriplePattern( pred, tp );					
						}
					}
				}
				
				//Add the output file to the Job object
				FileOutputFormat.setOutputPath(currJob, new Path( JobParameters.outputHDFSDir + "test" ) );

				//Add the Hadoop Job to the JobPlan
				jp.setHadoopJob( currJob );

				//Set the flag for more jobs to true if there are still more triple patterns
				if( totalTrPatterns > 0 ) jp.setHasMoreJobs( true );
			}
			
			//Add the job plan to the list of job plans
			jps.add( jp );

			//Increment the job identifiers
			jobId++;
		}

		//Add the list of job plans to the query plan
		qp.addJobPlans( jps );

		return qp;
	}

	private List<String> updateVariableList( List<String> oldInputVarList )
	{
		List<String> newInputVarList = new ArrayList<String>();
		List<String> eliminatedVars = new ArrayList<String>();
		
		Iterator<String> keys = varOrigTpBasedMap.keySet().iterator();
		while( keys.hasNext() )
		{
			String key = keys.next();
			String valueOrigMap = varOrigTpBasedMap.get( key );
			String valueUsedMap = varUsedTpBasedMap.get( key );
			
			if( valueUsedMap == null ) continue;
			
			if( valueOrigMap.equalsIgnoreCase( valueUsedMap ) ) 
			{ 
				eliminatedVars.add( key ); 
				
				String remainingVars = "";
				Iterator<String> iter = oldInputVarList.iterator();
				while( iter.hasNext() )
				{
					String value = iter.next();
					if( !value.contains( key ) ) continue;
					String[] val = value.split( "~" );
					for( int i = 0; i < val.length; i++ )
					{
						if( val[i].equalsIgnoreCase( key ) ) continue;
						else remainingVars += val[i] + "~";
					}
				}
				newInputVarList.add( remainingVars );
				continue; 
			}
			
			String[] splitValueOrigMap = valueOrigMap.split( "~" );
			String[] splitValueUsedMap = valueUsedMap.split( "~" );
			
			String remainingTps = "", remainingVars = "";
			for( int i = 0; i < splitValueUsedMap.length; i++ )
			{
				String usedValue = splitValueUsedMap[i];				
				
				for( int j = 0; j < splitValueOrigMap.length; j++ )
				{
					String origValue = splitValueOrigMap[j];
					if( origValue.equalsIgnoreCase( usedValue ) || valueUsedMap.contains( origValue ) ) continue;
					else if( !remainingTps.contains( origValue ) ) remainingTps += origValue; 
				}
				
				String vars = oldInputVarList.get( new Integer( usedValue ).intValue() - 1 );
				if( !vars.contains( key ) ) continue;
				String[] var = vars.split( "~" );
				for( int k = 0; k < var.length; k++ )
				{
					if( var[k].equalsIgnoreCase( key ) ) continue;
					else remainingVars += var[k] + "~";
				}
			}
			
			String[] splitRemainingTps = remainingTps.split( "~" );
			for( int x = 0; x < splitRemainingTps.length; x++ )
			{
				String[] remainingVarsFromTps = oldInputVarList.get( new Integer( splitRemainingTps[x] ).intValue() - 1 ).split( "~" );
				for( int y = 0; y < remainingVarsFromTps.length; y++ )
				{
					if( eliminatedVars.contains( remainingVarsFromTps[y] ) ) continue;
					else remainingVars += remainingVarsFromTps[y] + "~";
				}
			}
			newInputVarList.add( remainingVars );
		}
		
		//Add the remaining triple patterns from the triple patterns map
		newInputVarList.addAll( constructVarList() );
		
		return newInputVarList;
	}
	
	private List<String> constructVarList()
	{
		List<String> strInputList = new ArrayList<String>();
		
		Iterator<Integer> iterTpMap = tpMap.keySet().iterator();
		while( iterTpMap.hasNext() )
		{
			Integer key = iterTpMap.next();
			TriplePattern value = tpMap.get( key );
			String vars = "";
			Node sub = value.getSubjectValue(), obj = value.getObjectValue();
			if( sub.isVariable() ) vars += sub.toString() + "~";
			if( obj.isVariable() ) vars += obj.toString() + "~";
			strInputList.add( vars );
		}
		
		return strInputList;
	}
	
	private void constructInputMaps( List<HadoopElement> elements ) throws UnhandledElementException, NotBasicElementException
	{
		//A counter
		int count = 0;

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

				//Create a TriplePattern object that will be used in the construction of a JobPlan
				TriplePattern tp = TriplePatternFactory.createSimpleTriplePattern();

				//Set the id of the triple pattern
				tp.setTriplePatternId( ++count );

				//Get the subject and object for that triple pattern
				Node sub = triple.getSubject(), pred = triple.getPredicate(), obj = triple.getObject();
				String strSub = sub.toString(), strObj = obj.toString();

				//Set the subject, predicate and object for the current triple pattern
				tp.setSubjectValue( sub );
				tp.setPredicateValue( pred );
				tp.setObjectValue( obj );
				
				//Check if both subject and object are variables
				if( sub.isVariable() && obj.isVariable() )
				{					
					//Add entries to the variable-triple pattern identifiers hashmap
					if( varOrigTpBasedMap.isEmpty() ) { varOrigTpBasedMap.put( strSub, "" + count + "~" ); varOrigTpBasedMap.put( strObj, "" + count + "~" ); }
					else
					{
						varOrigTpBasedMap.put( strSub, varOrigTpBasedMap.get( strSub ) + count + "~" );
						varOrigTpBasedMap.put( strObj, varOrigTpBasedMap.get( strObj ) + count + "~" );
					}
					
					//Set the number of variables in the triple pattern to two
					tp.setNumOfVariables( 2 );
				}
				else
				{
					//Set the number of variables in the triple pattern to one
					tp.setNumOfVariables( 1 );

					//If only one of subject or object is a variable then do this
					if( sub.isVariable() )	
					{
						if( varOrigTpBasedMap.isEmpty() || varOrigTpBasedMap.get( strSub ) == null ) varOrigTpBasedMap.put( strSub, "" + count + "~" );
						else varOrigTpBasedMap.put( strSub, varOrigTpBasedMap.get( strSub ) + count + "~" );
						
						//Add the object as a literal for the current triple pattern
						tp.setLiteralValue( strObj );
					}
					else
					{
						if( varOrigTpBasedMap.isEmpty() || varOrigTpBasedMap.get( strSub ) == null ) varOrigTpBasedMap.put( strObj, "" + count + "~" );
						else varOrigTpBasedMap.put( strObj, varOrigTpBasedMap.get( strObj ) + count + "~" );

						//Add the subject as a literal value for the current triple pattern
						tp.setLiteralValue( strSub );
					}
				}

				tpMap.put( new Integer( count ), tp );
			}
		}
	}
	
	private Map<Integer,String> constructElimCountMap( List<String> elements )
	{
		//A map between elimination counts and their associated variables
		Map<Integer,String> elimVarCountMap = new TreeMap<Integer,String>();

		//A map of variables and their associated elimination counts
		Map<String,Integer> varElimCountMap = new HashMap<String,Integer>();

		//Check if all elements share a common variable
		Iterator<String> elemIter = elements.iterator();
		while( elemIter.hasNext() )
		{
			//Get each hadoop element
			String[] vars = elemIter.next().split( "~" );

			if( vars.length == 1 ) continue;
			else
			{
				for( int i = 0; i < vars.length; i++ )
				{
					//Get the current elimination count for the current variable
					Integer j = varElimCountMap.get( vars[i] );

					//If the hashmap contains the current variable update its elimination count
					//Else add a new entry in the hashmap
					if( j != null ) varElimCountMap.put( vars[i], new Integer( j + 1 ) );
					else varElimCountMap.put( vars[i], new Integer( 1 ) );					
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

		return elimVarCountMap;
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
			ClassLoader classLoader = SimpleQueryPlanGeneratorImpl.class.getClassLoader();
			genericMapper = ( Class<GenericMapper> ) classLoader.loadClass( JobParameters.mapperClass ).newInstance();
			genericReducer = ( Class<GenericReducer> ) classLoader.loadClass( JobParameters.reducerClass ).newInstance();
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
	 * A method that tests the heuristic of whether all variables have a common variable
	 * @param elements - the variables from the query
	 * @return a null string if there is no common variable, the common variable as a string otherwise
	 */
	private String runHeuristic( List<String> elements )
	{
		//A hashmap that holds each variable and the associated count of times it is found across all triple patterns
		HashMap<String,Integer> hm = new HashMap<String,Integer>();

		//A boolean that denotes if we prematurely abort the construction of the hashmap
		boolean isAborted = false;

		//The variable that is common to all triple patterns
		String commonVariable = null;

		//Check if all elements share a common variable
		Iterator<String> elemIter = elements.iterator();
		while( elemIter.hasNext() )
		{
			//Get each set of variables
			String[] vars = elemIter.next().split( "~" );

			if( vars.length == 1 )
			{
				if( !hm.isEmpty() && hm.get( vars[0] ) == null ) { isAborted = true; break; }
				else 
				{
					if( hm.isEmpty() ) hm.put( vars[0], new Integer( 1 ) );
					else hm.put( vars[0], new Integer( hm.get( vars[0] ).intValue() + 1 ) );
				}					
			}
			else
			{
				for( int i = 0; i < vars.length; i++ )
				{
					if( hm.isEmpty() || hm.get( vars[i] ) == null ) hm.put( vars[i], new Integer( "1" ) );
					else hm.put( vars[i], new Integer( hm.get( vars[i] ).intValue() + 1 ) );					
				}
			}
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
				if( hm.get( key ).intValue() == elements.size() ) { commonVariable = key; }
			}
		}
		return commonVariable;
	}
}