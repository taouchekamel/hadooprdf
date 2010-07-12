package edu.utdallas.hadooprdf.query.jobrunner;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.rdf.uri.prefix.PrefixNamespaceTree;
import edu.utdallas.hadooprdf.query.generator.job.JobPlan;

/**
 * The generic reducer class for a SPARQL query
 * @author vaibhav
 *
 */
public class GenericReducer extends Reducer<Text, Text, Text, Text>
{
	/** The job plan object **/
	private JobPlan jp = null;
	
	/** The prefix namespace tree based on a given DataSet **/
	private PrefixNamespaceTree prefixTree = null;

	@Override
	protected void setup(Context context) throws IOException,
	InterruptedException 
	{
		try
		{
			//Get the Hadoop configuration object
			org.apache.hadoop.conf.Configuration hadoopConfiguration = context.getConfiguration();
			
			//Get a filesystem handle
			FileSystem fs = FileSystem.get( hadoopConfiguration );
			
			//Get the current DataSet
			DataSet ds = new DataSet( new Path( hadoopConfiguration.get( "dataset" ) ), hadoopConfiguration );
			
			//Read in the job plan for the current job
			ObjectInputStream objstream = new ObjectInputStream( fs.open( new Path( ds.getPathToTemp(), "job.txt" ) ) );
			this.jp = (JobPlan)objstream.readObject();
			objstream.close();
			
			//Get the prefix namespace tree based on the current DataSet
			prefixTree = ds.getPrefixNamespaceTree();
		}
		catch( Exception e ) { throw new InterruptedException( e.getMessage() ); }
	}

	/**
	 * A method that outputs a given value
	 * @param value - the input key as a string
	 * @param context - the context
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void outputValue( String value, Context context ) throws IOException, InterruptedException
	{
		//Split the value on #
		String[] splitValue = value.toString().split( "#" );
		
		//Get the prefix part of the value if it exists, like X#0_0# => prefix = 0_0 
		String prefixValue = ""; if( splitValue.length > 2 ) prefixValue = splitValue[1] + "#";
		
		//Get the namespace for the prefix if exists, like 0_0 => namespace = http://abc/
		String namespaceValue = prefixTree.matchAndReplaceNamespace( prefixValue );
		
		//Output the value for the variable
		if( splitValue.length > 2 ) context.write( new Text( namespaceValue + splitValue[2] ), new Text( "" ) );
		else context.write( new Text( namespaceValue + splitValue[1] ), new Text( "" ) );		
	}
	
	/**
	 * The reduce method
	 * @param key - the input key
	 * @param value - the input value
	 * @param context - the context
	 */
	@Override
	public void reduce( Text key, Iterable<Text> value, Context context ) throws IOException, InterruptedException
	{
		//A count of values for a given key
		int count = 0;
		
		//A union string containing all the values for a given key
		String sValue = "";
		
		//A map between a triple pattern identifier and strings in the values part that belong to that identifier
		Map<String,String> trPatternValues = new HashMap<String,String>();

		//Iterate over all values for a particular key
		Iterator<Text> iter = value.iterator();
		while ( iter.hasNext() ) 
		{	
			//A temporary string, val = X#0_0#string for the input X~1#0_0#string
			String val = "";
			
			//Do this only if there are no more jobs
			if( !jp.getHasMoreJobs() )
			{
				//The string without the variable part, remVal = 0_0#string for the input X~1#0_0#string
				String remVal = "";
				
				//One value for the given key
				String inpVal = iter.next().toString();
				
				//Split the value on # and get remVal
				String[] valSplit = inpVal.split( "#" );
				for( int i = 1; i < valSplit.length; i++ ) 
				{
					if( i == ( valSplit.length - 1 ) ) remVal += valSplit[i];
					else remVal += valSplit[i] + "#";
				}
				val = valSplit[0].split( "~" )[0] + "#" + remVal; 
				
				//If the job does not have multiple variables in the key &&
				//   the value contains a variable and triple pattern identifier no, X~1 &&
				//   this is the first job
				if( !inpVal.contains( "m&" ) && valSplit[0].split( "~" ).length > 1 && jp.getJobId() == 1 )
				{
					//Get the triple pattern identifier
					String k = valSplit[0].split( "~" )[1];
					
					//If the map is empty or the above triple pattern is not in the map, insert it in the map and increment the count
					//Else if the value for the current triple pattern does not contain the current value from the input append it in the map
					if( trPatternValues.isEmpty() || trPatternValues.get( k ) == null ) { trPatternValues.put( k, val ); count++; }
					else 
					{
						String v = trPatternValues.get( k );
						if( !v.contains( val ) ) { trPatternValues.put( k, v + " " + val ); count++; }
					}
				}
				else
					if( jp.getJobId() > 1 ) count++;
				//            	if( jp.getJobId() == 1 && !inpVal.contains( "m&" ) && !trPatternNos.contains( valSplit[0].split( "~" )[1] ) ) { count++; trPatternNos.add( valSplit[0].split( "~" )[1] ); }
				//          	else
				//        		if( jp.getJobId() > 1 ) count++;
			}
			else val = iter.next().toString();
			sValue += val + '\t';
		}

		//Check if all values for a given key are the same
		boolean isValueSame = false; String sameVal = ""; int c = 0;
		String[] valSplit = sValue.split( "\t" );
		for( c = 0; c < valSplit.length; c++ )
		{
			String var = valSplit[c].split( "#" )[0];
			if( c == 0 ) sameVal = var;
			else if( sameVal.equalsIgnoreCase( var ) ) continue;
			else break;
		}
		if( c == valSplit.length ) isValueSame = true;

		//Write the result
		if( !jp.getHasMoreJobs() )
		{
			//Single variable in the query, e.g. ?X
			//or multiple variables e.g. ?X ?Y
			if( jp.getTotalVariables() == 1 ) 
			{    
				//Get the expected number of triple patterns for the joining variable
				int countOfTps = jp.getVarTrPatternCount( jp.getJoiningVariablesList().get( 0 ) );
				
				//If the count of values for a key matches the total number of triple patterns for the joining variable, 
				//   simply output the key since there is only variable in the SELECT clause of the query
				//Else If count of values for a key is greater than the total triple patterns for the for the joining variables 
				if( count == countOfTps )
					outputValue( key.toString(), context );
				else
					if( count > countOfTps )
					{
						//If all values for a given key are same
						//Else if the variable in the key matches the joining variable
						if( isValueSame )
						{
							//If the variable in the key matches the variable in the SELECT clause list, output the key
							if( jp.getSelectClauseVarList().contains( key.toString().split( "#" )[0] ) )
								outputValue( key.toString(), context );
						}
						else
							if( key.toString().split( "#" )[0].equalsIgnoreCase( jp.getJoiningVariablesList().get( 0 ).substring( 1 ) ) )
							{
								//Split the values on \t
								String[] valueSplit = sValue.split( "\t" );
								
								//For every value other than ones containing the joining variable output that value
								for( int i = 0; i < valueSplit.length; i++ )
								{
									if( !valueSplit[i].contains( jp.getJoiningVariablesList().get( 0 ).substring( 1 ) ) )
									{
										outputValue( valueSplit[i], context );
									}
								}
							}
					}
			}
			else
			{
				//If there is a single joining variable
				//or multiple joining variables
				if( jp.getJoiningVariablesList().size() == 1  )
				{
					//Get the variables in the SELECT clause
					List<String> listSelectVars = jp.getSelectClauseVarList();
					
					//Check if all variables in the SELECT clause are part of the values for a given key
					int i = 0;
					for( i = 0; i < listSelectVars.size(); i++ )
					{
						if( sValue.contains( listSelectVars.get( i ) ) ) continue;
						else break;
					}
					
					//If the count matches the number of variables in the SELECT clause proceed
					if( i == listSelectVars.size() )
					{
						//Get the expected number of triple patterns for the joining variable
						int countOfTps = jp.getVarTrPatternCount( jp.getJoiningVariablesList().get( 0 ) );

						/** Set an initial value for every variable found in the SELECT clause **/
						//An iterator over all SELECT clause variables
						Iterator<String> iterVars = listSelectVars.iterator();
						
						//A map between variables and the corresponding value from the values for a given key
						Map<String,String> vars = new TreeMap<String,String>();
						while( iterVars.hasNext() )
						{
							//Initially put a dummy variable in the map for every variable
							String variable = iterVars.next();
							if( count == countOfTps ) vars.put( variable, variable );
							else if( count > countOfTps ) vars.put( variable, variable + "~" );
						}

						//Split the key on #
						String[] splitKey = key.toString().split( "#" );

						//Get the variable part in the key, like X
						String varKey = splitKey[0];
						
						//Get the prefix associated with the key if it exists, like X#0_0# => prefix = 0_0
						String prefixKey = ""; if( splitKey.length > 2 ) prefixKey = splitKey[1] + "#";
						
						//Get the namespace for the prefix if exists, like 0_0 => namespace = http://abc/
						String namespaceKey = prefixTree.matchAndReplaceNamespace( prefixKey );
						
						//If the prefix part exists
						//Else it does not exist
						if( splitKey.length > 2 ) 
						{
							//If count of values == expected triple patterns add the value to the map for the appropriate variable
							//Else if count > expected count append the value for the variable
							if( count == countOfTps ) vars.put( varKey, namespaceKey + splitKey[2] );
							else if( count > countOfTps ) vars.put( varKey, vars.get( varKey ) + namespaceKey + splitKey[2] + "~" );
						}
						else 
						{
							//If count of values == expected triple patterns add the value to the map for the appropriate variable
							//Else if count > expected count append the value for the variable
							if( count == countOfTps ) vars.put( varKey, namespaceKey + splitKey[1] );
							else if( count > countOfTps ) vars.put( varKey, vars.get( varKey ) + namespaceKey + splitKey[1] + "~" );        			
						}

						//Split the value part for a given key
						String[] splitRes = sValue.split( "\t" );
						for( int j = 0; j < splitRes.length; j++ )
						{
							//if( keyString.equalsIgnoreCase( splitRes[j] ) ) continue;
							//Split the value on #
							String[] splitValueRes = splitRes[j].split( "#" );

							//Get the variable part in the value, like X
							String varValueRes = splitValueRes[0];
							
							//Get the prefix associated with the value if it exists, like X#0_0# => prefix = 0_0
							String prefixValueRes = ""; if( splitValueRes.length > 2 ) prefixValueRes = splitValueRes[1] + "#";
							
							//The namespace associated with a prefix if it exists
							String namespaceValueRes = null;
							
							//If the variable in the key part matches the variable in the value part simply continue
							//else get the appropriate namespace if it exists
							if( varKey.equalsIgnoreCase( varValueRes ) ) continue;
							else namespaceValueRes = prefixTree.matchAndReplaceNamespace( prefixValueRes );

							//If the prefix part exists
							//Else it does not exist
							if( splitValueRes.length > 2 ) 
							{
								//If count of values == expected triple patterns add the value to the map for the appropriate variable
								//Else if count > expected count append the value for the variable
								if( count == countOfTps )
									vars.put( varValueRes, namespaceValueRes + splitValueRes[2] );
								else
									if( count > countOfTps )
									{
										vars.put( varKey, vars.get( varKey ) + namespaceKey + splitKey[2] + "~" );
										vars.put( varValueRes, vars.get( varValueRes ) + namespaceValueRes + splitValueRes[2] + "~" );
									}	
							}
							else 
							{
								//If count of values == expected triple patterns add the value to the map for the appropriate variable
								//Else if count > expected count append the value for the variable
								if( count == countOfTps )
									vars.put( varValueRes, namespaceValueRes + splitValueRes[1] );
								else
									if( count > countOfTps )
									{
										vars.put( varKey, vars.get( varKey ) + namespaceKey + splitKey[1] + "~" );
										vars.put( varValueRes, vars.get( varValueRes ) + namespaceValueRes + splitValueRes[1] + "~" );
									}
							}
						}

						//Output the results in order
						if( count == countOfTps )
						{
							//The result string containing results in order
							String resultInOrder = ""; 
							
							//Iterate over all values in the map
							Iterator<String> iterMap = vars.values().iterator();
							
							//Get the results for every key in the map
							while( iterMap.hasNext() ) { resultInOrder += iterMap.next() + "\t"; }
							
							//Output the result
							context.write( new Text( resultInOrder ), new Text( "" ) );
						}
						else
							if( count > countOfTps )
							{	
								//Get the maximum length of values for a given variable, also keep track if all variables have the same length
								int length = 0, sameLen = 0;
								
								//Iterate over all values in the map
								Iterator<String> iterMapVal = vars.values().iterator();
								while( iterMapVal.hasNext() )
								{
									//Get the length of values for a variable
									int mapValLen = iterMapVal.next().split( "~" ).length;
									
									//Set the length to the longest length available for any variable
									//Also keep track whether all variables share the same length
									if( mapValLen > length ) { length = mapValLen; sameLen++; }
									else if( mapValLen == length ) sameLen++;
								}

								//int length = vars.values().iterator().next().split( "~" ).length;

								//A temporary variable that keeps track that we do not repeat results 
								String result = null;
						loop:	for( int j = 1; j < length; j++ )
								{
									//The result string containing results in order
									String resultInOrder = "";
									
									//Iterate over all keys in the map
									Iterator<String> iterKeys = vars.keySet().iterator();
									while( iterKeys.hasNext() )
									{
										//Split the values associated with a variable
										String[] splitResFromMap = vars.get( iterKeys.next() ).split( "~" );
										
										//TODO: Don't know if this is general enough ?
										//If the length of values is less than current length
										//Else append the value of the variable in the output
										if( splitResFromMap.length < ( j + 1 ) ) 
										{
											//continue loop;
											//If all variables share the same length continue to the next iteration
											//Else use the same value for the variable in all results 
											if( sameLen == jp.getSelectClauseVarList().size() ) continue loop;
											else resultInOrder += splitResFromMap[j-1] + "\t";
										}
										else resultInOrder += splitResFromMap[j] + "\t";
									}
									
									//Output the result making sure that we do not repeat results
									if( j == 1 ) result = resultInOrder;
									if( j == 1 || !result.equalsIgnoreCase( resultInOrder ) ) context.write( new Text( resultInOrder ), new Text( "" ) );
								}
							}
					}
				}
				else
				{
					//Split the values for a given key on \t
					String[] splitVal = sValue.split( "\t" );

					//if( sValue.contains( "m&" ) && splitVal.length > 1 )
					//If the values for a given key are not same and they contain m& (this denotes multiple joining variables)
					//Else the values are same and contain m&
					if( !isValueSame && sValue.contains( "m&" )  )
					{
						//Iterate over all variables in the SELECT clause
						Iterator<String> iterVars = jp.getSelectClauseVarList().iterator();

						//A map between variables and the corresponding value from the values for a given key
						Map<String,String> vars = new TreeMap<String,String>();
						while( iterVars.hasNext() )
						{
							//Initially put a dummy variable in the map for every variable
							String variable = iterVars.next();
							vars.put( variable, variable + "~" );
						}

						//Iterate over all variables in the key
						String[] splitKeyIntoVars = key.toString().split( "~" );
						for( int i = 0; i < splitKeyIntoVars.length; i++ )
						{
							//Split the key on #
							String[] splitKey = splitKeyIntoVars[i].split( "#" );

							//Get the variable part in the key, like X
							String varKey = splitKey[0];
							
							//Get the prefix associated with the key if it exists, like X#0_0# => prefix = 0_0
							String prefixKey = ""; if( splitKey.length > 2 ) prefixKey = splitKey[1] + "#";
							
							//Get the namespace for the prefix if exists, like 0_0 => namespace = http://abc/
							String namespaceKey = prefixTree.matchAndReplaceNamespace( prefixKey );

							//If the prefix part exists
							//Else it does not exist: add the key to the map for its associated variable
							if( splitKey.length > 2 ) 
								vars.put( varKey, vars.get( varKey ) + namespaceKey + splitKey[2] + "~" );
							else 
								vars.put( varKey, vars.get( varKey ) + namespaceKey + splitKey[1] + "~" );
						}

						for( int i = 0; i < splitVal.length; i++ )
						{
							//If the value contains m&# continue
							if( splitVal[i].equalsIgnoreCase( "m&#" ) ) continue;

							//Split the value on #
							String[] splitValueRes = splitVal[i].split( "#" );

							//Get the variable part in the value, like X
							String varValueRes = splitValueRes[0];

							//Get the prefix associated with the value if it exists, like X#0_0# => prefix = 0_0
							String prefixValueRes = ""; if( splitValueRes.length > 2 ) prefixValueRes = splitValueRes[1] + "#";

							//The namespace associated with a prefix if it exists
							String namespaceValueRes = prefixTree.matchAndReplaceNamespace( prefixValueRes );
							
							//Add the value to the map with its assoicated variable
							if( splitValueRes.length > 2 ) 
								vars.put( varValueRes, vars.get( varValueRes ) + namespaceValueRes + splitValueRes[2] + "~" );
							else 
								vars.put( varValueRes, vars.get( varValueRes ) + namespaceValueRes + splitValueRes[1] + "~" );
						}

						//Variable for outputting results in order
						String resultInOrder = "";
						
						//Iterate over all keys in the map
						Iterator<String> iterKeys = vars.keySet().iterator();
						while( iterKeys.hasNext() )
						{
							//The key from the map
							String keyVarMap = iterKeys.next();
							
							//If the key is in the joining variable simply continue
							if( jp.getJoiningVariablesList().contains( "?" + keyVarMap ) ) continue;
							
							//Get the values for a given key from the map
							String[] splitValue = vars.get( keyVarMap ).split( "~" );
							for( int i = 1; i < splitValue.length; i++ )
							{
								//The joining variables in this job
								String joinVarValues = "";
								
								//Iterate over all keys from the map again
								Iterator<String> iterDuplKeys = vars.keySet().iterator();
								while( iterDuplKeys.hasNext() )
								{
									//Get a key from the map
									String duplKeyVarMap = iterDuplKeys.next();
									
									//If the joining variables list does not contain the current key continue
									//Else get the joining variables for this job
									if( !jp.getJoiningVariablesList().contains( "?" + duplKeyVarMap ) ) continue;
									joinVarValues += vars.get( duplKeyVarMap ).split( "~" )[1] + "\t";
								}
								
								//Set the results in order and output them
								resultInOrder += splitValue[i] + "\t" + joinVarValues;
								context.write( new Text( resultInOrder ), new Text( "" ) );
							}
						}
					}
					else
					{
						//If all values are same and equal to m&
						if( splitVal.length >= 1 && sameVal.contains( "m&" ) )
						{
							//Split the key on ~
							String[] keySplit = key.toString().split( "~" );
							
							//Only if all output variables are in the key continue
							if( jp.getSelectClauseVarList().size() == keySplit.length )
							{
								String result = "";
								for( int i = 0; i < keySplit.length; i++ )
								{
									//Split each part of the key on #
									String[] splitKey = keySplit[i].split( "#" );

									//Split the key on #
									String prefixKey = ""; if( splitKey.length > 2 ) prefixKey = splitKey[1] + "#";
								
									//Get the namespace for the prefix if exists, like 0_0 => namespace = http://abc/
									String namespaceKey = prefixTree.matchAndReplaceNamespace( prefixKey );
								
									//Set the result based on the presence of a prefix
									if( splitKey.length > 2 )
										result += namespaceKey + splitKey[2] + "\t";
									else 
										result += namespaceKey + splitKey[1] + "\t";
								}
							
								//Output the same result for the number of times m&# is present in the value 
								for( int j = 0; j < valSplit.length; j++ )
									context.write( new Text( result ), new Text( "" ) );
							}
						}
					}
				}
			}
		}
		else
			context.write( key, new Text( sValue ) );		
	}
}