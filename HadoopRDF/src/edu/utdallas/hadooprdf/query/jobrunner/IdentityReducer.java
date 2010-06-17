package edu.utdallas.hadooprdf.query.jobrunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class IdentityReducer extends Reducer<Text, Text, Text, Text>
{
	public void reduce( Text key, Iterable<Text> value, Context context ) throws IOException, InterruptedException
	{
        String sValue = "";
        //List<String> trPatternNos = new ArrayList<String>();
        int count = 0;
        
        //Iterate over all values for a particular key
        Iterator<Text> iter = value.iterator();
        while ( iter.hasNext() ) 
        {
        	String val = "";
       		String remVal = "";
           	String[] valSplit = iter.next().toString().split( "#" );
           	for( int i = 1; i < valSplit.length; i++ ) 
           	{
           		if( i == ( valSplit.length - 1 ) ) remVal += valSplit[i];
           		else remVal += valSplit[i] + "#";
           	}
           	val = valSplit[0].split( "~" )[0] + "#" + remVal;
            //if( !trPatternNos.contains( valSplit[0].split( "~" )[1] ) ) { count++; trPatternNos.add( valSplit[0].split( "~" )[1] ); }
           	count++;
            sValue += val + '\t';
        }
    	context.write( key, new Text( sValue + "count = " + count ) );		
	}
}
