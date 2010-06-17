package edu.utdallas.hadooprdf.query.jobrunner;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
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
	private JobPlan jp = null;
	private PrefixNamespaceTree prefixTree = null;
	
	@Override
	protected void setup(Context context) throws IOException,
	InterruptedException 
	{
		try
		{
			org.apache.hadoop.conf.Configuration hadoopConfiguration = context.getConfiguration(); 
			FileSystem fs = FileSystem.get( hadoopConfiguration );
			DataSet ds = new DataSet( new Path( hadoopConfiguration.get( "dataset" ) ), hadoopConfiguration );
			ObjectInputStream objstream = new ObjectInputStream( fs.open( new Path( ds.getPathToTemp(), "job.txt" ) ) );
			this.jp = (JobPlan)objstream.readObject();
			objstream.close();
			prefixTree = ds.getPrefixNamespaceTree();
		}
		catch( Exception e ) { throw new InterruptedException(e.getMessage()); }//e.printStackTrace(); }
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
		int count = 0;
        String sValue = "";
        List<String> trPatternNos = new ArrayList<String>();
        
        //Iterate over all values for a particular key
        Iterator<Text> iter = value.iterator();
        while ( iter.hasNext() ) 
        {
        	String val = "";
        	if( !jp.getHasMoreJobs() )
        	{
        		String remVal = "";
            	String[] valSplit = iter.next().toString().split( "#" );
            	for( int i = 1; i < valSplit.length; i++ ) 
            	{
            		if( i == ( valSplit.length - 1 ) ) remVal += valSplit[i];
            		else remVal += valSplit[i] + "#";
            	}
            	val = valSplit[0].split( "~" )[0] + "#" + remVal; 
            	if( jp.getJobId() == 1 && !trPatternNos.contains( valSplit[0].split( "~" )[1] ) ) { count++; trPatternNos.add( valSplit[0].split( "~" )[1] ); }
            	else
            		if( jp.getJobId() > 1 ) count++;
        	}
        	else val = iter.next().toString();
            sValue += val + '\t';
        }
        
		//Check if all values are same
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
        		if( count == jp.getVarTrPatternCount( jp.getJoiningVariablesList().get( 0 ) ) )
        		{
        			String keyString = key.toString();
        			String[] splitKey = keyString.split( "#" );
        			String prefixKey = ""; if( splitKey.length > 2 ) prefixKey = splitKey[1] + "#";
        			String namespaceKey = prefixTree.matchAndReplaceNamespace( prefixKey );
        			if( splitKey.length > 2 ) context.write( new Text( namespaceKey + splitKey[2] ), new Text( "" ) );
        			else context.write( new Text( namespaceKey + splitKey[1] ), new Text( "" ) );
        		}
        		else
        			if( count > jp.getVarTrPatternCount( jp.getJoiningVariablesList().get( 0 ) ) )
        			{
        				if( isValueSame )
        				{
        					if( jp.getSelectClauseVarList().contains( key.toString().split( "#" )[0] ) )
        					{
        						String keyString = key.toString();
        						String[] splitKey = keyString.split( "#" );
        						String prefixKey = ""; if( splitKey.length > 2 ) prefixKey = splitKey[1] + "#";
        						String namespaceKey = prefixTree.matchAndReplaceNamespace( prefixKey );
        						if( splitKey.length > 2 ) context.write( new Text( namespaceKey + splitKey[2] ), new Text( "" ) );
        						else context.write( new Text( namespaceKey + splitKey[1] ), new Text( "" ) );
        					}
        				}
        				else
        				if( key.toString().split( "#" )[0].equalsIgnoreCase( jp.getJoiningVariablesList().get( 0 ).substring( 1 ) ) )
        				{
        					String[] valueSplit = sValue.split( "\t" );
        					for( int i = 0; i < valueSplit.length; i++ )
        					{
        						if( !valueSplit[i].contains( jp.getJoiningVariablesList().get( 0 ).substring( 1 ) ) )
        						{
        		        			String[] splitVal = valueSplit[i].split( "#" );
        		        			String prefixVal = ""; if( splitVal.length > 2 ) prefixVal = splitVal[1] + "#";
        		        			String namespaceVal = prefixTree.matchAndReplaceNamespace( prefixVal );
        		        			if( splitVal.length > 2 ) context.write( new Text( namespaceVal + splitVal[2] ), new Text( "" ) );
        		        			else context.write( new Text( namespaceVal + splitVal[1] ), new Text( "" ) );
        						}
        					}
        				}
        			}
        	}
        	else
        	{
    			if( jp.getJoiningVariablesList().size() == 1  )
    			{
    				List<String> listSelectVars = jp.getSelectClauseVarList();
    				int i = 0;
    				for( i = 0; i < listSelectVars.size(); i++ )
    				{
    					if( sValue.contains( listSelectVars.get( i ) ) ) continue;
    					else break;
    				}
    				if( i == listSelectVars.size() )
    				{
        				int countOfTps = jp.getVarTrPatternCount( jp.getJoiningVariablesList().get( 0 ) );
        				Iterator<String> iterVars = jp.getSelectClauseVarList().iterator();
        				Map<String,String> vars = new TreeMap<String,String>();
        				while( iterVars.hasNext() )
        				{
        					String variable = iterVars.next();
        					if( count == countOfTps ) vars.put( variable, variable );
        					else if( count > countOfTps ) vars.put( variable, variable + "~" );
        				}
        			
        				String keyString = key.toString();
        				String[] splitKey = keyString.split( "#" );
        				String varKey = splitKey[0];
        				String prefixKey = ""; if( splitKey.length > 2 ) prefixKey = splitKey[1] + "#";
        				String namespaceKey = prefixTree.matchAndReplaceNamespace( prefixKey );
        				if( splitKey.length > 2 ) 
        				{
        					if( count == countOfTps ) vars.put( varKey, namespaceKey + splitKey[2] );
        					else if( count > countOfTps ) vars.put( varKey, vars.get( varKey ) + namespaceKey + splitKey[2] + "~" );
        				}
        				else 
        				{
        					if( count == countOfTps ) vars.put( varKey, namespaceKey + splitKey[1] );
        					else if( count > countOfTps ) vars.put( varKey, vars.get( varKey ) + namespaceKey + splitKey[1] + "~" );        			
        				}
        				
        				String[] splitRes = sValue.split( "\t" );
        				for( int j = 0; j < splitRes.length; j++ )
        				{
        					//if( keyString.equalsIgnoreCase( splitRes[j] ) ) continue;
        					String[] splitValueRes = splitRes[j].split( "#" );
        					String varValueRes = splitValueRes[0];
        					String prefixValueRes = ""; if( splitValueRes.length > 2 ) prefixValueRes = splitValueRes[1] + "#";
        					String namespaceValueRes = null;
        					if( varKey.equalsIgnoreCase( varValueRes ) ) continue;
        					else namespaceValueRes = prefixTree.matchAndReplaceNamespace( prefixValueRes );
        					if( splitValueRes.length > 2 ) 
        					{
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
        						if( count == jp.getVarTrPatternCount( jp.getJoiningVariablesList().get( 0 ) ) )
        							vars.put( varValueRes, namespaceValueRes + splitValueRes[1] );
        						else
        							if( count > countOfTps )
        							{
        								vars.put( varKey, vars.get( varKey ) + namespaceKey + splitKey[1] + "~" );
        								vars.put( varValueRes, vars.get( varValueRes ) + namespaceValueRes + splitValueRes[1] + "~" );
        							}
        					}
        				}
        				
						if( count == countOfTps )
						{
							String resultInOrder = ""; 
	        				Iterator<String> iterMap = vars.values().iterator();
	        				while( iterMap.hasNext() )
	        				{
	        					resultInOrder += iterMap.next() + "\t";
	        				}
	        				context.write( new Text( resultInOrder ), new Text( "" ) );
						}
						else
						if( count > countOfTps )
						{	
							int length = vars.values().iterator().next().split( "~" ).length;
							String result = null;
					loop:	for( int j = 1; j < length; j++ )
							{
								String resultInOrder = "";
								Iterator<String> iterKeys = vars.keySet().iterator();
								while( iterKeys.hasNext() )
								{
									String[] splitResFromMap = vars.get( iterKeys.next() ).split( "~" );
									if( splitResFromMap.length < ( j + 1 ) ) continue loop;
									else resultInOrder += splitResFromMap[j] + "\t";
								}
								if( j == 1 ) result = resultInOrder;
		        				if( j == 1 || !result.equalsIgnoreCase( resultInOrder ) ) context.write( new Text( resultInOrder ), new Text( "" ) );
							}
						}
        			}
        		}
    			else
    			{
    				String[] splitVal = sValue.split( "\t" );
    				if( sValue.contains( "m&" ) && splitVal.length > 1 )
    				{
         				Iterator<String> iterVars = jp.getSelectClauseVarList().iterator();
        				Map<String,String> vars = new TreeMap<String,String>();
        				while( iterVars.hasNext() )
        				{
        					String variable = iterVars.next();
        					vars.put( variable, variable + "~" );
        				}

        				String keyString = key.toString();
        				String[] splitKeyIntoVars = keyString.split( "~" );
        				for( int i = 0; i < splitKeyIntoVars.length; i++ )
        				{
            				String[] splitKey = splitKeyIntoVars[i].split( "#" );
            				String varKey = splitKey[0];
            				String prefixKey = ""; if( splitKey.length > 2 ) prefixKey = splitKey[1] + "#";
            				String namespaceKey = prefixTree.matchAndReplaceNamespace( prefixKey );
            				if( splitKey.length > 2 ) 
            					vars.put( varKey, vars.get( varKey ) + namespaceKey + splitKey[2] + "~" );
            				else 
            					vars.put( varKey, vars.get( varKey ) + namespaceKey + splitKey[1] + "~" );
        				}

        				for( int i = 0; i < splitVal.length; i++ )
    					{
    						if( splitVal[i].equalsIgnoreCase( "m&#" ) ) continue;
        					String[] splitValueRes = splitVal[i].split( "#" );
        					String varValueRes = splitValueRes[0];
        					String prefixValueRes = ""; if( splitValueRes.length > 2 ) prefixValueRes = splitValueRes[1] + "#";
        					String namespaceValueRes = prefixTree.matchAndReplaceNamespace( prefixValueRes );
        					if( splitValueRes.length > 2 ) 
								vars.put( varValueRes, vars.get( varValueRes ) + namespaceValueRes + splitValueRes[2] + "~" );
        					else 
								vars.put( varValueRes, vars.get( varValueRes ) + namespaceValueRes + splitValueRes[1] + "~" );
    					}
    					
						String resultInOrder = "";
						Iterator<String> iterKeys = vars.keySet().iterator();
						while( iterKeys.hasNext() )
						{
							String keyVarMap = iterKeys.next();
							if( jp.getJoiningVariablesList().contains( "?" + keyVarMap ) ) continue;
							String[] splitValue = vars.get( keyVarMap ).split( "~" );
							for( int i = 1; i < splitValue.length; i++ )
							{
								String joinVarValues = "";
								Iterator<String> iterDuplKeys = vars.keySet().iterator();
								while( iterDuplKeys.hasNext() )
								{
									String duplKeyVarMap = iterDuplKeys.next();
									if( !jp.getJoiningVariablesList().contains( "?" + duplKeyVarMap ) ) continue;
									joinVarValues += vars.get( duplKeyVarMap ).split( "~" )[1] + "\t";
								}
								resultInOrder += splitValue[i] + "\t" + joinVarValues;
								context.write( new Text( resultInOrder ), new Text( "" ) );
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