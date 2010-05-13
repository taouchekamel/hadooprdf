package edu.utdallas.hadooprdf.query.jobrunner;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.query.generator.job.JobPlan;

/**
 * The generic reducer class for a SPARQL query
 * @author vaibhav
 *
 */
public class GenericReducer extends Reducer<Text, Text, Text, Text>
{		
	private JobPlan jp = null;
	
	@Override
	protected void setup(Context context) throws IOException,
	InterruptedException 
	{
		try
		{
			org.apache.hadoop.conf.Configuration hadoopConfiguration = context.getConfiguration(); 
			edu.utdallas.hadooprdf.conf.Configuration.createInstance(hadoopConfiguration);

			FileSystem fs = FileSystem.get(hadoopConfiguration); 

			ObjectInputStream objstream = new ObjectInputStream( fs.open( new Path( new DataSet( hadoopConfiguration.get( "dataset" ) ).getPathToTemp(), "job.txt" ) ) );
			this.jp = (JobPlan)objstream.readObject();
			objstream.close();
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
        
        //Iterate over all values for a particular key
        Iterator<Text> iter = value.iterator();
        while ( iter.hasNext() ) 
        {
        	if( !jp.getHasMoreJobs() )
        		count++;
            sValue += iter.next().toString() + '\t';
        }
        
        //TODO: How to find the order of results with the given query, may need rearranging of value here
        //TODO: Sometimes only the key is the result, sometimes the key and part of the value is the result, how to find this out ??
        //Write the result
        if( !jp.getHasMoreJobs() )
        {
        	if( jp.getTotalVariables() == 1 ) 
        	{
        		if( count == jp.getVarTrPatternCount( jp.getJoiningVariablesList().get( 0 ) ) ) 
        			context.write( key, new Text( sValue ) );
        	}
        	else
        	{
        		
        	}
        }
        else
        	context.write( key, new Text( sValue ) );		
	}
}
