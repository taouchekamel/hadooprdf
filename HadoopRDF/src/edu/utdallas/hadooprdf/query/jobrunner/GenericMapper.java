package edu.utdallas.hadooprdf.query.jobrunner;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import edu.utdallas.hadooprdf.data.commons.Constants;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.rdf.uri.prefix.PrefixNamespaceTree;
import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.triplepattern.TriplePattern;

public class GenericMapper extends Mapper<LongWritable, Text, Text, Text>
{
	private JobPlan jp = null;
	private PrefixNamespaceTree prefixTree = null;

	@Override
	protected void setup(Context context) throws IOException,
	InterruptedException {
		try
		{
			org.apache.hadoop.conf.Configuration hadoopConfiguration = context.getConfiguration(); 
			edu.utdallas.hadooprdf.conf.Configuration.createInstance(hadoopConfiguration);
			FileSystem fs = FileSystem.get(hadoopConfiguration);
			DataSet ds = new DataSet( hadoopConfiguration.get( "dataset" ) );
			ObjectInputStream objstream = new ObjectInputStream( fs.open( new Path( ds.getPathToTemp(), "job.txt" ) ) );
			this.jp = (JobPlan)objstream.readObject();
			objstream.close();
			prefixTree = ds.getPrefixNamespaceTree();
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

		//Get the triple pattern associated with a predicate
		TriplePattern tp = jp.getPredicateBasedTriplePattern( sPredicate );

		if( tp.getFilenameBasedPrefix( sPredicate ) != null )
		{
			//Get the subject
			String sSubject = st.nextToken();

			//If file is of a standard predicate such as rdf, rdfs etc, we need to output only the subject since this is all the file contains
			//Else depending on the number of variables in the triple pattern we output subject-subject, subject-object, object-subject or object-object 
			if( sPredicate.startsWith( prefixTree.matchAndReplacePrefix( Constants.RDF_TYPE_URI ) ) )
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