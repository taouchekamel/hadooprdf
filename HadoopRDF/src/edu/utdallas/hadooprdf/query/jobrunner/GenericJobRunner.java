package edu.utdallas.hadooprdf.query.jobrunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;

public class GenericJobRunner 
{
	public GenericJobRunner() { }
	
	/**
	 * The generic mapper class for a SPARQL query
	 * @author vaibhav
	 *
	 */
	public class GenericMapper extends Mapper<LongWritable, Text, Text, Text>
	{
		private JobPlan jp = null;
		
		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			try
			{
				org.apache.hadoop.conf.Configuration hadoopConfiguration = context.getConfiguration(); 
			
				FileSystem fs = FileSystem.get(hadoopConfiguration); 
				
				ObjectInputStream objstream = new ObjectInputStream( fs.open( new Path( new DataSet( "/user/farhan/hadooprdf/LUBM1" ).getPathToPOSData(), "job.txt" ) ) );
				this.jp = (JobPlan)objstream.readObject();
				System.out.println( jp.getHasMoreJobs() + " " + jp.getTotalVariables() );
				objstream.close();
			}
			catch( Exception e ) { throw new InterruptedException(e.getMessage()); }//e.printStackTrace(); }
		}
		
		/**
		 * The map method
		 * @param key - the input key
		 * @param value - the input value
		 * @param context - the context
		 */
		@Override
		public void map( LongWritable key, Text value, Context context ) throws IOException, InterruptedException
		{
			//Tokenize the value
			StringTokenizer st = new StringTokenizer( value.toString(), "\t" ); 
			
			//First check if the key is an input filename, if it is then do file processing based map
			//Else this maybe a second job, do a prefix based map
			String sPredicate = ((FileSplit) context.getInputSplit()).getPath().getName();

			System.out.println( sPredicate );
			//Get the triple pattern associated with a predicate
			TriplePattern tp = jp.getPredicateBasedTriplePattern( sPredicate );

			if( tp.getFilenameBasedPrefix( sPredicate ) != null )
			{
				//Get the subject
				String sSubject = st.nextToken();
				
				//If file is of a standard predicate such as rdf, rdfs etc, we need to output only the subject since this is all the file contains
				//Else depending on the number of variables in the triple pattern we output subject-subject, subject-object, object-subject or object-object 
				if( sPredicate.contains( "type" ) )
				{
					context.write( new Text( sSubject ), new Text( tp.getFilenameBasedPrefix( sPredicate ) + sSubject ) );
				}
				else
				{
					//TODO: How to generate a unique prefix ??
					//If join is on subject and the number of variables in the triple pattern is 2 output ( subject, object )
					if( tp.getJoiningVariable().equalsIgnoreCase( "s" ) )
					{
						if( tp.getNumOfVariables() == 2 )
							context.write( new Text( sSubject ), new Text( tp.getFilenameBasedPrefix( sPredicate ) + st.nextToken() ) );
						else
						{
							String sObject = st.nextToken();
							
							if( sObject.equalsIgnoreCase( tp.getLiteralValue() ) )
								context.write( new Text( sSubject ), new Text( tp.getFilenameBasedPrefix( sPredicate ) + sSubject ) );
						}
					}
					else
						if( tp.getJoiningVariable().equalsIgnoreCase( "o" ) )
						{
							if( tp.getNumOfVariables() == 2 )
								context.write( new Text( st.nextToken() ), new Text( tp.getFilenameBasedPrefix( sPredicate ) + sSubject ) );
							else
							{
								String sObject = st.nextToken();
								
								if( sSubject.equalsIgnoreCase( tp.getLiteralValue() ) )
									context.write( new Text( sObject ), new Text( tp.getFilenameBasedPrefix( sPredicate ) + sObject ) );
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
					if( tp.checkIfPrefixExists( token.substring( 0, 2 ) ) )
					{
						context.write( new Text( token.substring( 2 ) ), new Text( key.toString() ) );
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
		private JobPlan jp = null;
		
		public GenericReducer() 
		{ 
			try
			{
				ObjectInputStream objstream = new ObjectInputStream( new FileInputStream( "/user/farhan/hadooprdf/LUBM1/POS/job.txt" ) );
				this.jp = (JobPlan)objstream.readObject();
				System.out.println( jp.getHasMoreJobs() + " " + jp.getTotalVariables() );
				objstream.close();
			}
			catch( Exception e ) { e.printStackTrace(); }			
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
}
