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
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import com.hp.hpl.jena.graph.Node;

import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.job.JobPlanFactory;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlan;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanFactory;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGenerator;
import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;
import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePatternFactory;
import edu.utdallas.hadooprdf.query.parser.HadoopElement;
import edu.utdallas.hadooprdf.query.parser.NotBasicElementException;
import edu.utdallas.hadooprdf.query.parser.QueryParser;
import edu.utdallas.hadooprdf.query.parser.UnhandledElementException;
import edu.utdallas.hadooprdf.query.parser.HadoopElement.HadoopTriple;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.lib.util.JobParameters;

/**
 * A specific implementation of the query plan generator based on the "elimination count" algorithm
 * @author vaibhav
 *
 */
public class SimpleQueryPlanGeneratorImpl implements QueryPlanGenerator
{	
	/** A map of original variables and the associated triple pattern identifiers **/
	private Map<String,String> varOrigTpBasedMap = new HashMap<String,String>();

	/** A map of variables used in a job and the associated triple pattern identifiers **/
	private Map<String,String> varUsedTpBasedMap = new HashMap<String,String>();

	/** A map of triple pattern id's and the corresponding triple pattern that is used in every job **/
	private Map<Integer,TriplePattern> tpMap = new HashMap<Integer,TriplePattern>();
	
	/** A list of variables that are eliminated **/
	private List<String> eliminatedVars = new ArrayList<String>();

	/** The DataSet to be used **/
	private DataSet dataset = null;
	
	/** A map between variables and the number of triple patterns they are found in **/
	private Map<String,Integer> varTpCountMap = new HashMap<String,Integer>();
	
	/**Variable for testing **/
	private int countOfJobs = 0;
	
	/**
	 * Constructor
	 */
	public SimpleQueryPlanGeneratorImpl( DataSet dataset ) 
	{ 
		this.dataset = dataset;
	}

	/**
	 * A method that generates the query plan based on the "elimination count" algorithm
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
		List<JobPlan> jpList = new ArrayList<JobPlan>();

		//Create the Hadoop configuration based on the path where the various site files are
		Configuration hadoopConfig = getConfiguration( JobParameters.configFileDir );

		//Set the DataSet for the configuration
		hadoopConfig.set( "dataset", dataset.getDataSetRoot().toString() );
		
		//Get the total number of variables in the SELECT clause
		int totalVarsSelectClause = QueryParser.getNumOfVarsInQuery();

		//Get the variables in the SELECT clause
		List<String> varsSelectClause = QueryParser.getVars();
		
		//The job identifier
		int jobId = 1;
		
		//Construct the input maps that will be used
		constructInputMaps( elements );
		
		//A list of variables to run the elimination count algorithm and the heuristic
		List<String> oldInputVarList = new ArrayList<String>();
		List<String> newInputVarList = new ArrayList<String>();
		
		//Initialize both input lists
		oldInputVarList = newInputVarList = constructVarList();

		while( tpMap.size() > 0 || !newInputVarList.isEmpty() )
		{				
			countOfJobs++;
			
			//Run the heuristic to find out if there is a common variable
			String commonVariable = runHeuristic( newInputVarList );

			//Create a single job plan
			JobPlan jp = JobPlanFactory.createSimpleJobPlan();

			//Set the job id
			jp.setJobId( jobId );
			
			//Set the total number of variables expected in the result
			jp.setTotalVariables( totalVarsSelectClause );
			
			//Set the total variable from the SELECT clause
			jp.setSelectClauseVarList( varsSelectClause );
			
			//The case where there is a common variable involves only a single job
			//Else run the "elimination count" algorithm
			if( !commonVariable.equalsIgnoreCase( "" ) )
			{
				String[] splitCommonVar = commonVariable.split( "~" );

				//Use the configuration to define a Hadoop Job
				Job currJob = new Job( hadoopConfig, "HeuristicBasedJob" );

				//Set the parameters for the job that are fixed in this version
				setJobParameters( currJob );

				//The list of triple patterns to be removed
				List<Integer> tpToBeRemoved = new ArrayList<Integer>();
				
				//Find the position of the joining variable in each triple pattern
				Iterator<Integer> iterTpMap = tpMap.keySet().iterator();
				while( iterTpMap.hasNext() )
				{
					//Get each triple pattern identifier
					Integer tpId = iterTpMap.next();

					//Get the corresponding triple pattern
					TriplePattern tp = tpMap.get( tpId );
					
					//Add the triple pattern to be removed to the list 
					tpToBeRemoved.add( tpId );
					
					//Set the value of the joining variable
					if( splitCommonVar.length == 1 ) 
					{
						tp.setJoiningVariableValue( splitCommonVar[0] );

						//Set the value of the joining variable and the second variable (if it exists) for the current triple pattern
						if( tp.getSubjectValue().toString().equalsIgnoreCase( splitCommonVar[0] ) )
						{ 
							tp.setJoiningVariable( "s" ); 
							if( tp.getObjectValue().isVariable() ) tp.setSecondVariableValue( tp.getObjectValue().toString() ); 
						}
						else
						{
							tp.setJoiningVariable( "o" );
							if( tp.getSubjectValue().isVariable() ) tp.setSecondVariableValue( tp.getSubjectValue().toString() ); 
						}
					}
					else
					{
						tp.setJoiningVariable( "so" );
						
						if( tp.getSubjectValue().toString().equalsIgnoreCase( splitCommonVar[0] ) )
						{
							tp.setJoiningVariableValue( "s" + splitCommonVar[0] ); 
							tp.setSecondVariableValue( "o" + splitCommonVar[1] );
						}
						else
						{
							tp.setJoiningVariableValue( "o" + splitCommonVar[0] ); 
							tp.setSecondVariableValue( "s" + splitCommonVar[1] );							
						}
					}
					
					//Get the predicate, i.e. filename
					String pred = tp.getPredicateValue().toString();

					//Add the filename to the Job object
					FileInputFormat.addInputPath( currJob, new Path( dataset.getPathToPOSData(), pred ) );

					//Add the triple pattern to the job plan
					jp.setPredicateBasedTriplePattern( pred, tp );					
				}

				//Remove the used triple patterns from the map
				for( int i = 0; i < tpToBeRemoved.size(); i++ )
				{
					tpMap.remove( tpToBeRemoved.get( i ) );
				}
				
				//If this is not the first job do the following
				if( jobId > 1 )
				{
					//Add the filename to the Job object as an input file, this input file is the output file from the previous job 
					FileInputFormat.addInputPath( currJob, new Path( dataset.getPathToTemp(), "job" + ( jobId - 1 ) + "-op.txt" ) );
				}

				if( splitCommonVar.length == 1 ) 
				{ 
					//Add the joining variable to the job plan
					jp.addVarToJoiningVariables( splitCommonVar[0] );
					
					//Set the total triple patterns associated with this variable in the job plan
					jp.setVarTrPatternCount( splitCommonVar[0], varTpCountMap.get( splitCommonVar[0] ) );
				}
				else
				{
					for( int i = 0; i < splitCommonVar.length; i++ )
					{
						//Add the joining variable to the job plan
						jp.addVarToJoiningVariables( splitCommonVar[i] );

						//Set the total triple patterns associated with this variable in the job plan
						jp.setVarTrPatternCount( splitCommonVar[i], varTpCountMap.get( splitCommonVar[i] ) );					
					}
				}
				
				//Add the output file to the Job object
				Path opPath = new Path( dataset.getPathToTemp(), "test" + jobId );
				FileSystem fs = FileSystem.get( hadoopConfig );
				fs.delete( opPath, true );
				FileOutputFormat.setOutputPath( currJob, opPath );

				//Add the Hadoop Job to the JobPlan
				jp.setHadoopJob( currJob );

				//Set a flag to denote that there are no more jobs
				jp.setHasMoreJobs( false );
				
				//Update the variables list
				newInputVarList.clear(); oldInputVarList.clear();
			}
			else
			{			
				//Use the configuration to define a Hadoop Job
				Job currJob = new Job( hadoopConfig, "Job" + jobId );

				//Set the parameters for the job that are fixed in this version
				setJobParameters( currJob );
								
				//Get the elimination count based treemap
				Map<Integer,String> elimCountVarMap = constructElimCountMap( newInputVarList );

				//Iterate over each key
				Iterator<Integer> iterElimCount = elimCountVarMap.keySet().iterator();
				while( iterElimCount.hasNext() )
				{
					//If there is only a single triple pattern left it can't be used in any join, hence break
					if( checkIfOneTpLeft() ) break;

					//Get each key
					Integer elimCount = iterElimCount.next();

					//Get the corresponding value
					String[] vars = elimCountVarMap.get( elimCount ).split( "~" );

					//Iterate over all values
					for( int i = 0; i < vars.length; i++ )
					{
						if( checkIfOneTpLeft() ) break;
						
						String[] trPatterns = varOrigTpBasedMap.get( vars[i] ).split( "~" );
						
						//If there is only a single triple pattern associated with a variable, it is not a variable that can
						//be joined on
						if( trPatterns.length == 1 ) continue;
						
						for( int j = 0; j < trPatterns.length; j++ )
						{
							//Get the triple pattern associated with the current value
							TriplePattern tp = tpMap.get( new Integer( trPatterns[j] ) );
							
							//If the triple pattern is null, it is already used by some other variable, so simply continue
							if( tp == null ) continue;
							
							//Remove that triple pattern from the 
							tpMap.remove( new Integer( trPatterns[j] ) );
							
							//Set the value of the joining variable
							tp.setJoiningVariableValue( vars[i] );
							
							//Increment the map of used triple patterns 
							if( varUsedTpBasedMap.isEmpty() || varUsedTpBasedMap.get( vars[i] ) == null )
								varUsedTpBasedMap.put( vars[i], trPatterns[j] + "~" );
							else
								varUsedTpBasedMap.put( vars[i], varUsedTpBasedMap.get( vars[i] ) + trPatterns[j] + "~" );
							
							//Set the value of the joining variable and the second variable (if it exists) for the current triple pattern
							if( tp.getSubjectValue().toString().equalsIgnoreCase( vars[i] ) )
							{
								tp.setJoiningVariable( "s" );
								if( tp.getObjectValue().isVariable() ) tp.setSecondVariableValue( tp.getObjectValue().toString() ); 
							}
							else
							{
								tp.setJoiningVariable( "o" );
								if( tp.getSubjectValue().isVariable() ) tp.setSecondVariableValue( tp.getSubjectValue().toString() ); 
							}
							
							//Get the predicate, i.e. filename
							String pred = tp.getPredicateValue().toString();
							
							//Add the filename to the Job object
							FileInputFormat.addInputPath( currJob, new Path( dataset.getPathToPOSData(), pred ) );

							//Add the triple pattern to the job plan
							jp.setPredicateBasedTriplePattern( pred, tp );					
						}
	
						//Add the joining variable to the job plan
						jp.addVarToJoiningVariables( vars[i] );

						//Set the total triple patterns associated with this variable in the job plan
						jp.setVarTrPatternCount( vars[i], varTpCountMap.get( vars[i] ) );
					}
				}
				
				//If this is not the first job do the following
				if( jobId > 1 )
				{
					//Add the filename to the Job object as an input file, this input file is the output file from the previous job 
					FileInputFormat.addInputPath( currJob, new Path( dataset.getPathToTemp(), "job" + ( jobId - 1 ) + "-op.txt" ) );
				}
				
				//Add the output file to the Job object
				Path opPath = new Path( dataset.getPathToTemp(), "test" + jobId );
				FileSystem fs = FileSystem.get( hadoopConfig );
				fs.delete( opPath, true );
				FileOutputFormat.setOutputPath( currJob, opPath );

				//Add the Hadoop Job to the JobPlan
				jp.setHadoopJob( currJob );

				//Update the variables list
				newInputVarList = updateVariableList( oldInputVarList ); oldInputVarList = newInputVarList;
				
				//Set the flag for more jobs to true if there are still more triple patterns
				if( tpMap.size() > 0 || !newInputVarList.isEmpty() ) jp.setHasMoreJobs( true );
			}
			
			//Add the job plan to the list of job plans
			jpList.add( jp );

			//Increment the job identifiers
			jobId++;			
		}
		
		//Add the list of job plans to the query plan
		qp.addJobPlans( jpList );

		//Return the query plan
		return qp;
	}

	/**
	 * A method that checks if we are left only a single triple pattern from the original SPARQL query
	 * @return true iff one original triple pattern is left, false otherwise
	 */
	private boolean checkIfOneTpLeft()
	{
		boolean isFirstKey = true; int parentTpId = 0, countOfParentTpId = 0;
		Iterator<Integer> keysTpMap = tpMap.keySet().iterator();
		while( keysTpMap.hasNext() )
		{
			TriplePattern tp = tpMap.get( keysTpMap.next() );
			if( isFirstKey ) { isFirstKey = false; parentTpId = tp.getParentTriplePatternId(); countOfParentTpId++; }
			else { if( parentTpId == tp.getParentTriplePatternId() ) countOfParentTpId++; }
		}
		if( countOfParentTpId == tpMap.keySet().size() ) return true;
		else return false;
	}
	
	/**
	 * A method that returns a Hadoop Configuration object based on the configuration files
	 * @param path - the directory that contains the configuration files
	 * @return a Hadoop Configuration object
	 * @throws InterruptedException 
	 */
	private Configuration getConfiguration( String path ) throws InterruptedException
	{
		org.apache.hadoop.conf.Configuration hadoopConfiguration = null;
		try
		{ 
			edu.utdallas.hadooprdf.conf.Configuration config = edu.utdallas.hadooprdf.conf.Configuration.getInstance();
			hadoopConfiguration = new org.apache.hadoop.conf.Configuration( config.getHadoopConfiguration() );

			hadoopConfiguration.addResource( path + "core-site.xml" );
			hadoopConfiguration.addResource( path + "mapred-site.xml" );
			hadoopConfiguration.addResource( path + "hdfs-site.xml" );
		}
		catch( Exception e ) { throw new InterruptedException( e.getMessage() ); }
		return hadoopConfiguration;
	}

	/**
	 * A method that returns an updated variable list based on actual triple patterns for a variable and the triple patterns
	 * that are used for a variable in a job
	 * @param oldInputVarList - the old input variable list based on triple patterns
	 * @return an updated variable list based on original triple patterns for a variable and the triple patterns used in a job 
	 */
	private List<String> updateVariableList( List<String> oldInputVarList )
	{
		//The new list that will be returned
		List<String> newInputVarList = new ArrayList<String>();
				
		//Iterate over all variables
		Iterator<String> iterVariables = varOrigTpBasedMap.keySet().iterator();
		while( iterVariables.hasNext() )
		{
			//Get each variable
			String variable = iterVariables.next();
			
			//Get the original triple patterns and the triple patterns used in a job for the current variable
			String valueOrigMap = varOrigTpBasedMap.get( variable );
			String valueUsedMap = varUsedTpBasedMap.get( variable );
			
			//If no triple pattern for the variable was used in the job simply continue
			if( valueUsedMap == null ) continue;
			
			//This is the case where all triple patterns for a variable were used in a job
			if( valueOrigMap.equalsIgnoreCase( valueUsedMap ) ) 
			{ 
				//Add the variable to the list of eliminated variables
				eliminatedVars.add( variable ); 
				
				//A string to denote the remaining variables from the triple patterns that contain the eliminated variable
				String remainingVars = "";
				
				//Iterate over the old variables list to compute the remaining variables
				Iterator<String> iterOldIpVarList = oldInputVarList.iterator();
				while( iterOldIpVarList.hasNext() )
				{
					//Get the variables for each triple pattern from this list
					String varsInEachTp = iterOldIpVarList.next();
					
					//If the variables list does not contain the current variable simply continue
					if( !varsInEachTp.contains( variable ) ) continue;
					
					//Split the variables list and iterate over it
					String[] vars = varsInEachTp.split( "~" );
					for( int i = 0; i < vars.length; i++ )
					{
						//If the variable == to the current variable simply continue
						//Else add the variable to the remaining variables
						if( vars[i].equalsIgnoreCase( variable ) ) continue;
						else remainingVars += vars[i] + "~";
					}
				}
				
				//Add the remaining variables for the current variable to the new list of variables and then continue to 
				//process the next variable
				newInputVarList.add( remainingVars );
				continue; 
			}
			
			//This is the case where some triple patterns for the variable are used in a job while some remain
			//Get the actual triple patterns and the triple patterns used for the current variable 
			String[] splitValueOrigMap = valueOrigMap.split( "~" );
			String[] splitValueUsedMap = valueUsedMap.split( "~" );
			
			//The string of triple patterns and variables that remain
			String remainingTps = "", remainingVars = "";
			
			//Iterate over all triple patterns that are actually used in a job for the current variable
			for( int i = 0; i < splitValueUsedMap.length; i++ )
			{
				//Get each used triple pattern
				String usedTp = splitValueUsedMap[i];				
				
				//Iterate over all actual triple patterns for the current variable
				for( int j = 0; j < splitValueOrigMap.length; j++ )
				{
					//Get each original triple pattern
					String origTp = splitValueOrigMap[j];
					
					//If the original triple pattern matches the used triple pattern
					//or the used triple patterns list contains the original triple pattern simply continue
					//Else if the remaining triple patterns list does not contain this triple pattern, add it to that list
					if( origTp.equalsIgnoreCase( usedTp ) || valueUsedMap.contains( origTp ) ) continue;
					else if( !remainingTps.contains( origTp ) ) remainingTps += origTp; 
				}
				
				//Get the variables for the used triple pattern from the old variables list
				String varsInEachTp = oldInputVarList.get( new Integer( usedTp ).intValue() - 1 );
				
				//If the variables list does not contain the current variable simply continue				
				if( !varsInEachTp.contains( variable ) ) continue;
				
				//Split the variables list and iterate over it				
				String[] vars = varsInEachTp.split( "~" );
				for( int k = 0; k < vars.length; k++ )
				{
					//If the variable == to the current variable simply continue
					//Else add the variable to the remaining variables
					if( vars[k].equalsIgnoreCase( variable ) ) continue;
					else remainingVars += vars[k] + "~";
				}
			}
			
			//Compute the variables from the triple patterns that were not used in a job for the current variable
			//Iterate over the list of remaining triple patterns
			String[] splitRemainingTps = remainingTps.split( "~" );
			for( int x = 0; x < splitRemainingTps.length; x++ )
			{
				//Get the list of remaining variables for the triple pattern from the old input variable list and iterate over this list
				String[] remainingVarsFromTps = oldInputVarList.get( new Integer( splitRemainingTps[x] ).intValue() - 1 ).split( "~" );
				for( int y = 0; y < remainingVarsFromTps.length; y++ )
				{
					//If the list contains a variable that is already eliminated simply continue
					//Else add the variable to the list of remaining variables
					if( eliminatedVars.contains( remainingVarsFromTps[y] ) ) continue;
					else remainingVars += remainingVarsFromTps[y] + "~";
				}
			}
			
			//Add the remaining variables to the new input variable list
			newInputVarList.add( remainingVars );
		}
		
		//Add the remaining triple patterns from the triple patterns map
		newInputVarList.addAll( constructVarList() );
		
		//Return the new input variables list
		return newInputVarList;
	}
	
	/**
	 * A method that constructs the initial list of input variables from the triple patterns that are part of the 
	 * input SPARQL query
	 * @return the initial list of input variables based on the triple patterns that are part of the SPARQL query
	 */
	private List<String> constructVarList()
	{
		//The list of variables
		List<String> strInputVarsList = new ArrayList<String>();
		
		//An arraylist of the parent triple pattern identifiers
		List<Integer> parentTpIds = new ArrayList<Integer>();
		
		//Iterate over the triple patterns from the input SPARQL query
		Iterator<Integer> iterTpMap = tpMap.keySet().iterator();
		while( iterTpMap.hasNext() )
		{
			//Get the triple pattern identifier
			Integer tpId = iterTpMap.next();
			
			//Get the corresponding triple pattern
			TriplePattern tp = tpMap.get( tpId );
			
			//Get the parent triple pattern identifier
			Integer parentTpId = tp.getParentTriplePatternId();
			
			//If the list contains the parent identifier, then continue else add the id to the list
			if( parentTpIds.contains( parentTpId ) ) continue;
			else parentTpIds.add( parentTpId );
			
			//The string of associated variables from the current triple pattern
			String vars = "";
			
			//Check if the subject and/or object of each triple pattern are variables.
			//If they are simply add them to the variables
			Node sub = tp.getSubjectValue(), obj = tp.getObjectValue();
			if( sub.isVariable() ) vars += sub.toString() + "~";
			if( obj.isVariable() ) vars += obj.toString() + "~";
			
			//Add the variables string to the list of variables
			strInputVarsList.add( vars );
		}
		
		//Return the list of variables
		return strInputVarsList;
	}
	
	/**
	 * A method that constructs the triple pattern and variables map that are used by the algorithm
	 * @param elements - a list of elements from the QueryParser
	 * @throws Exception 
	 */
	private void constructInputMaps( List<HadoopElement> elements ) throws Exception
	{
		//A counter for the triple pattern id
		int count = 0;
		
		//A counter for the parent triple pattern id
		int parentTpId = 0;
		
		//Iterate over all elements
		Iterator<HadoopElement> iterElements = elements.iterator();
		while( iterElements.hasNext() )
		{
			//Get each element
			HadoopElement element = iterElements.next();
			
			//Get triple patterns associated with an element
			Iterator<HadoopTriple> iterTriplePatterns = element.getTriple().iterator();
			while( iterTriplePatterns.hasNext() )
			{
				//Get each triple pattern
				HadoopTriple triple = iterTriplePatterns.next();

				//Increment the parent triple pattern identifier
				parentTpId++;
				
				//Get the files associated with a triple pattern
				Iterator<String> files = triple.getAssociatedFiles().iterator();
				
				//A counter for actual triple patterns, the above counter does not keep track of triple patterns when there
				//are multiple files
				int varTpCount = 0;
				
				//Iterate over the files, creating a TriplePattern for each of them
				while( files.hasNext() )
				{	
					varTpCount++;
					
					//Create a TriplePattern object that will be used by a JobPlan
					TriplePattern tp = TriplePatternFactory.createSimpleTriplePattern();

					//Set the parent triple pattern id for the current triple pattern
					tp.setParentTriplePatternId( parentTpId );
					
					//Set the id of the triple pattern
					tp.setTriplePatternId( ++count );

					//Get the subject and object for that triple pattern
					Node sub = triple.getSubject(), pred = Node.createURI( files.next() ), obj = triple.getObject();
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
							if( varOrigTpBasedMap.get( strSub ) == null ) varOrigTpBasedMap.put( strSub, "" + count + "~" );
							else varOrigTpBasedMap.put( strSub, varOrigTpBasedMap.get( strSub ) + count + "~" );
							
							if( varOrigTpBasedMap.get( strObj ) == null ) varOrigTpBasedMap.put( strObj, "" + count + "~" );
							else varOrigTpBasedMap.put( strObj, varOrigTpBasedMap.get( strObj ) + count + "~" );
						}
					
						//Set the number of variables in the triple pattern to two
						tp.setNumOfVariables( 2 );
												
						//Add an entry to the variable-triple pattern count hashmap
						if( varTpCountMap.isEmpty() ) { varTpCountMap.put( strSub, new Integer( 1 ) ); varTpCountMap.put( strObj, new Integer( 1 ) ); }
						else
							if( varTpCount == 1 )
							{
								if( varTpCountMap.get( strSub ) == null ) varTpCountMap.put( strSub, new Integer( 1 ) );
								else varTpCountMap.put( strSub, new Integer( varTpCountMap.get( strSub ).intValue() + 1 ) );
								
								if( varTpCountMap.get( strObj ) == null ) varTpCountMap.put( strObj, new Integer( 1 ) );
								else varTpCountMap.put( strObj, new Integer( varTpCountMap.get( strObj ).intValue() + 1 ) );
							}
					}
					else
					{
						//Set the number of variables in the triple pattern to one
						tp.setNumOfVariables( 1 );

						//If only one of subject or object is a variable then do this
						if( sub.isVariable() )	
						{
							//Add entries to the variable-triple pattern identifiers hashmap
							if( varOrigTpBasedMap.isEmpty() || varOrigTpBasedMap.get( strSub ) == null ) varOrigTpBasedMap.put( strSub, "" + count + "~" );
							else varOrigTpBasedMap.put( strSub, varOrigTpBasedMap.get( strSub ) + count + "~" );
						
							//Add the object as a literal for the current triple pattern
							tp.setLiteralValue( strObj );
							
							//Add an entry to the variable-triple pattern count hashmap
							if( varTpCountMap.isEmpty() || varTpCountMap.get( strSub ) == null ) { varTpCountMap.put( strSub, new Integer( 1 ) ); }
							else
								if( varTpCount == 1 )
									varTpCountMap.put( strSub, new Integer( varTpCountMap.get( strSub ).intValue() + 1 ) );
						}
						else
						{
							//Add entries to the variable-triple pattern identifiers hashmap
							if( varOrigTpBasedMap.isEmpty() || varOrigTpBasedMap.get( strObj ) == null ) varOrigTpBasedMap.put( strObj, "" + count + "~" );
							else varOrigTpBasedMap.put( strObj, varOrigTpBasedMap.get( strObj ) + count + "~" );

							//Add the subject as a literal value for the current triple pattern
							tp.setLiteralValue( strSub );
							
							//Add an entry to the variable-triple pattern count hashmap
							if( varTpCountMap.isEmpty() || varTpCountMap.get( strObj ) == null ) { varTpCountMap.put( strObj, new Integer( 1 ) ); }
							else
								if( varTpCount == 1 )
									varTpCountMap.put( strObj, new Integer( varTpCountMap.get( strObj ).intValue() + 1 ) );
						}
					}

					//Add the triple pattern to hashmap of all triple patterns indexed by the identifier
					tpMap.put( new Integer( count ), tp );
				}
			}
		}
	}
	
	/**
	 * A method that returns a sorted map of elimination counts and the associated variables
	 * @param inputVarsList - the list of variables
	 * @return a map sorted in increasing order of elimination counts with its associated variables 
	 */
	private Map<Integer,String> constructElimCountMap( List<String> inputVarsList )
	{
		//A map between elimination counts and their associated variables
		Map<Integer,String> elimCountVarMap = new TreeMap<Integer,String>();

		//A map of variables and their associated elimination counts
		Map<String,Integer> varElimCountMap = new HashMap<String,Integer>();

		//Iterate over all variable lists that are part of the input list
		Iterator<String> iterVarsList = inputVarsList.iterator();
		while( iterVarsList.hasNext() )
		{
			//Get each variable list
			String[] vars = iterVarsList.next().split( "~" );

			//If there is only one variable, simply continue
			if( vars.length == 1 ) continue;
			else
			{
				//Iterate over all variables in the variable list
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
			//The list of variables with the same elimination count
			String varsWithSameElimCount = "";

			//Check each entry in the variable based hashmap for the elimination count from the sorted array. 
			//Combine the variables that have that value.
			Iterator<String> iterVars =  varElimCountMap.keySet().iterator();
			while( iterVars.hasNext() )
			{
				String var = iterVars.next();
				Integer elimCount = varElimCountMap.get( var );
				if( elimCount == inArr[i] ) varsWithSameElimCount += var + "~";
			}

			//If the variable count hashmap is empty add a new entry based on the current elimination count in the sorted array 
			//and the variables that have that value
			//Else get the old variables list from the hashmap and update it
			if( elimCountVarMap.isEmpty() || elimCountVarMap.get( ( Integer )inArr[i] ) == null )
				elimCountVarMap.put( ( Integer )inArr[i], varsWithSameElimCount );
			else
				elimCountVarMap.put( ( Integer )inArr[i], elimCountVarMap.get( ( Integer )inArr[i] ) + varsWithSameElimCount );
		}

		//Return the map between elimination counts and variables
		return elimCountVarMap;
	}

	/**
	 * A method that sets the parameters that are fixed for the current Hadoop Job
	 * @param currJob - the current Hadoop Job
	 */
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
		currJob.setJarByClass( this.getClass() );
		
		//Set the mapper and reducer classes to be used
		currJob.setMapperClass( edu.utdallas.hadooprdf.query.jobrunner.GenericMapper.class );
		//if( countOfJobs < 2 ) currJob.setMapperClass( edu.utdallas.hadooprdf.query.jobrunner.GenericMapper.class );
		//if( countOfJobs < 2 ) currJob.setReducerClass( edu.utdallas.hadooprdf.query.jobrunner.GenericReducer.class );
		currJob.setReducerClass( edu.utdallas.hadooprdf.query.jobrunner.GenericReducer.class );
		
		//Set the number of reducers
		currJob.setNumReduceTasks( JobParameters.numOfReducers );
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
		String commonVariable = "";

		//Check if all elements share a common variable
		Iterator<String> elemIter = elements.iterator();
		while( elemIter.hasNext() )
		{
			//Get each set of variables
			String[] vars = elemIter.next().split( "~" );

			//If only one variable exists
			//Else two variables exist
			if( vars.length == 1 )
			{
				//If the hashmap is not empty and this variable does not exist in the hashmap there cannot be a common variable
				//in which case simply abort further inspection and break
				//Else put the variable in the hashmap if it does not exist, if it does simply update its count
				if( !hm.isEmpty() && hm.get( vars[0] ) == null ) { isAborted = true; break; }
				else 
				{
					if( hm.isEmpty() ) hm.put( vars[0], new Integer( 1 ) );
					else hm.put( vars[0], new Integer( hm.get( vars[0] ).intValue() + 1 ) );
				}					
			}
			else
			{
				//Iterate over all variables
				for( int i = 0; i < vars.length; i++ )
				{
					//If both variables are the same do not update the count
					if( i > 0 && vars[i].equalsIgnoreCase( vars[i-1] ) ) continue;
					
					//If the hashmap is empty or this variable is not found add it in the hashmap
					//Else simply update the count of the times the variable was found
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
				if( hm.get( key ).intValue() == elements.size() ) { commonVariable += key + "~"; }
			}
		}
		return commonVariable;
	}
}