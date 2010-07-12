package edu.utdallas.hadooprdf.query.jobrunner;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class IdentityReducer extends Reducer<Text, Text, Text, Text>
{
	public void reduce( Text key, Iterable<Text> value, Context context ) throws IOException, InterruptedException
	{
        String sValue = "";
        int count = 0;
        
        //Iterate over all values for a particular key
        Iterator<Text> iter = value.iterator();
        while ( iter.hasNext() ) 
        {
        	count++;
            sValue += iter.next().toString() + '\t';
        }
    	context.write( key, new Text( sValue + "count = " + count ) );		
	}
}
