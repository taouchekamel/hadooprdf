package edu.utdallas.hadooprdf.query.jobrunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;

/**
 * A class that implements a generic map-reduce job runner given a query plan
 * @author vaibhav
 *
 */
public class GenericJobRunner 
{
	//TODO: How to get these parameters ?? Query plan ??
	/** The input filename **/
	private String filename = "";
	
	/** A boolean that denotes if there are more jobs to follow **/
	private boolean hasMoreJobs = false;
	
	/** The map for prefixes added from the first job **/
	private static final Map<String,String> hm;
	static
	{
		hm = new HashMap<String,String>();
		
		//This comes from the triple pattern that contains the joining variable for the next job
		hm.put( "M#", "" );
		hm.put( "S#", "" );
	}
	
	/** The job plan for this job **/
	private JobPlan jp = null;
	
	/**
	 * 
	 * @param jp - the job plan for the current job
	 */
	public GenericJobRunner( JobPlan jp )
	{
		this.jp = jp;
	}
	
	/**
	 * The generic mapper class for a SPARQL query
	 * @author vaibhav
	 *
	 */
	public class GenericMapper extends Mapper<Text, Text, Text, Text>
	{
		/**
		 * The setup method for this mapper
		 * @param context - the context 
		 */
		protected void setup( Context context ) { }
		
		/**
		 * The cleanup method for this mapper
		 * @param context - the context
		 */
		protected void cleanup( Context context ) { }
		
		/**
		 * The map method
		 * @param key - the input key
		 * @param value - the input value
		 * @param context - the context
		 */
		protected void map( Text key, Text value, Context context ) throws IOException, InterruptedException
		{
			//Tokenize the value
			StringTokenizer st = new StringTokenizer( value.toString(), "\t" ); 
			
			//First check if the key is an input filename, if it is then do file processing based map
			//Else this maybe a second job, do a prefix based map
			String sPredicate = key.toString();
			
			//TODO: Need to check if the key matches any input filename
			if( sPredicate.equalsIgnoreCase( filename ) )
			{
				//Get the subject
				String sSubject = st.nextToken();
				
				//If file is of a standard predicate such as rdf, rdfs etc, we need to output only the subject since this is all the file contains
				//Else depending on the number of variables in the triple pattern we output subject-subject, subject-object, object-subject or object-object 
				if( sPredicate.contains( "type" ) )
				{
					//TODO: Don't know if there is a use to append the subject after the prefix created
					context.write( new Text( sSubject ), new Text( sPredicate.substring( 5, 6 ) + "#" + sSubject ) );
				}
				else
				{
					TriplePattern tp = jp.getPredicateBasedTriplePattern( sPredicate );
					//TODO: How to generate a unique prefix ??
					//TODO: How to get the join variable and the number of variables in a triple pattern ??
					//TODO: How to get the literal subjects or objects from a query ??
					//If join is on subject and the number of variables in the triple pattern is 2 output ( subject, object )
					if( tp.getJoiningVariable().equalsIgnoreCase( "s" ) )
					{
						if( tp.getNumOfVariables() == 2 )
							context.write( new Text( sSubject ), new Text( sPredicate.substring( 0, 2 ) + "#" + st.nextToken() ) );
						else
						{
							String sObject = st.nextToken();
							
							if( sObject.equalsIgnoreCase( "queries object" ) )
								context.write( new Text( sSubject ), new Text( sPredicate.substring( 0, 2 ) + "#" + sSubject ) );
						}
					}
					else
						if( tp.getJoiningVariable().equalsIgnoreCase( "o" ) )
						{
							if( tp.getNumOfVariables() == 2 )
								context.write( new Text( st.nextToken() ), new Text( sPredicate.substring( 0, 2 ) + "#" + sSubject ) );
							else
							{
								String sObject = st.nextToken();
								
								if( sSubject.equalsIgnoreCase( "queries subject" ) )
									context.write( new Text( sObject ), new Text( sPredicate.substring( 0, 2 ) + "#" + sObject ) );
							}
						}
				}
			}
			else
			{
				//TODO: How to handle multiple prefixes in the same line
				while( st.hasMoreTokens() )
				{
					String token = st.nextToken();
					if( hm.containsKey( token.substring( 0, 2 ) ) )
					{
						context.write( new Text( token.substring( 2 ) ), key );
					}
				}
			}
		}
	}

	/**
	 * The generic reducer class for a SPARQL query
	 * @author vaibhav
	 *
	 */
	public class GenericReducer extends Reducer<Text, Text, Text, Text>
	{
		/**
		 * The setup method for this reducer
		 * @param context - the context 
		 */
		protected void setup( Context context ) { }

		/**
		 * The cleanup method for this reducer
		 * @param context - the context
		 */
		protected void cleanup( Context context ) { }

		/**
		 * The reduce method
		 * @param key - the input key
		 * @param value - the input value
		 * @param context - the context
		 */
		protected void reduce( Text key, Iterable<Text> value, Context context ) throws IOException, InterruptedException
		{
			int count = 0;
            String sValue = "";
            
            //Iterate over all values for a particular key
            Iterator<Text> iter = value.iterator();
            while ( iter.hasNext() ) 
            {
            	if( !hasMoreJobs )
            		count++;
                sValue += iter.next().toString() + '\t';
            }
            
            //TODO: How to find the order of results with the given query, may need rearranging of value here
            //TODO: Sometimes only the key is the result, sometimes the key and part of the value is the result, how to find this out ??
            //TODO: How to get if there are more jobs remaining and the total number of variables in the SELECT clause
            //Write the result
            if( !hasMoreJobs )
            {
            	if( count == jp.getTotalVariables() ) context.write( key, new Text( sValue ) );
            }
            else
            	context.write( key, new Text( sValue ) );		
		}
	}
}