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
	InterruptedException 
	{
		try
		{
			org.apache.hadoop.conf.Configuration hadoopConfiguration = context.getConfiguration(); 
			FileSystem fs = FileSystem.get(hadoopConfiguration);
			DataSet ds = new DataSet( new Path( hadoopConfiguration.get( "dataset" ) ), hadoopConfiguration );
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

		//Count of tokens
		int countOfTokens = st.countTokens();
		
		//First check if the key is an input filename, if it is then do file processing based map
		//Else this maybe a second job, do a prefix based map
		String sPredicate = ((FileSplit) context.getInputSplit()).getPath().getName();

		//Get the triple pattern associated with a predicate
		TriplePattern tp = jp.getPredicateBasedTriplePattern( sPredicate );

		if( !sPredicate.equalsIgnoreCase( "job" + ( jp.getJobId() - 1 ) + "-op.txt" ) )
		{
			//Get the subject
			String sSubject = st.nextToken();

			//Get the joining variable value
			String joiningVariableValue = null;
			if( !tp.getJoiningVariable().equalsIgnoreCase( "so" ) ) joiningVariableValue = tp.getJoiningVariableValue().substring( 1 );
			
			//If file is of a standard predicate such as rdf, rdfs etc, we need to output only the subject since this is all the file contains
			//Else depending on the number of variables in the triple pattern we output subject-subject, subject-object, object-subject or object-object 
			if( sPredicate.startsWith( prefixTree.matchAndReplacePrefix( Constants.RDF_TYPE_URI ) ) )
			{
				//context.write( new Text( sSubject ), new Text( tp.getFilenameBasedPrefix( sPredicate ) + sSubject ) );
				context.write( new Text( joiningVariableValue + "#" + sSubject ), new Text( joiningVariableValue + "~" + tp.getParentTriplePatternId() + "#" + sSubject ) );
			}
			else
			{
				//If join is on subject and the number of variables in the triple pattern is 2 output ( subject, object )
				if( tp.getJoiningVariable().equalsIgnoreCase( "s" ) )
				{
					if( tp.getNumOfVariables() == 2 )
						context.write( new Text( joiningVariableValue + "#" + sSubject ), new Text( tp.getSecondVariableValue().substring( 1 ) + "~" + tp.getParentTriplePatternId() + "#" + st.nextToken() ) );
					else
					{
						String sObject = st.nextToken();

						if( sObject.equalsIgnoreCase( tp.getLiteralValue() ) )
							context.write( new Text( joiningVariableValue + "#" + sSubject ), new Text( joiningVariableValue + "~" + tp.getParentTriplePatternId() + "#" + sSubject ) );
					}
				}
				else
					if( tp.getJoiningVariable().equalsIgnoreCase( "o" ) )
					{
						if( tp.getNumOfVariables() == 2 )
							context.write( new Text( joiningVariableValue + "#" + st.nextToken() ), new Text( tp.getSecondVariableValue().substring( 1 ) + "~" + tp.getParentTriplePatternId() + "#" + sSubject ) );
						else
						{
							String sObject = st.nextToken();

							if( sSubject.equalsIgnoreCase( tp.getLiteralValue() ) )
								context.write( new Text( joiningVariableValue + "#" + sObject ), new Text( joiningVariableValue + "~" + tp.getParentTriplePatternId() + "#" + sObject ) );
						}
					}
					else
					{
						String sObject = st.nextToken();
						if( tp.getJoiningVariableValue().contains( "s" ) )
						{
							context.write( new Text( tp.getJoiningVariableValue().substring( 2 ) + "#" + sSubject + "~" + tp.getSecondVariableValue().substring( 2 ) + "#" + sObject ), 
									       new Text( "m&" ) );
						}
						else
						{
							context.write( new Text( tp.getJoiningVariableValue().substring( 2 ) + "#" + sObject + "~" + tp.getSecondVariableValue().substring( 2 ) + "#" + sSubject ), 
								       new Text( "m&" ) );							
						}
					}
			}
		}
		else
		{
			int count = 0;
			String keyVal = "";
			
			if( jp.getJoiningVariablesList().size() == 1 )
			{
				while( st.hasMoreTokens() )
				{
					String token = st.nextToken();
					if( ++count == 1 ) { keyVal = token; continue; }
					String[] tokenSplit = token.split( "#" );
					if( ( countOfTokens - 1 ) >= jp.getVarTrPatternCount( "?" + tokenSplit[0].split( "~" )[0] ) && jp.getJoiningVariablesList().contains( "?" + tokenSplit[0].split( "~" )[0] ) )
					{
						String remToken = "";
						for( int i = 1; i < tokenSplit.length; i++ )
						{
							if( i == ( tokenSplit.length - 1 ) ) remToken += tokenSplit[i];
							else remToken += tokenSplit[i] + "#";
						}
						context.write( new Text( tokenSplit[0].split( "~" )[0] + "#" + remToken ), new Text( keyVal ) );
					}
					else
						if( ( countOfTokens - 1 ) == jp.getVarTrPatternCount( "?" + tokenSplit[0].split( "~" )[0] ) && jp.getSelectClauseVarList().contains( tokenSplit[0].split( "~" )[0] ) )
							context.write( new Text( keyVal ), new Text( token ) );
						else if( ( countOfTokens - 1 ) <= jp.getVarTrPatternCount( "?" + keyVal.split( "#" )[0] ) && jp.getSelectClauseVarList().contains( tokenSplit[0].split( "~" )[0] ) )
							context.write( new Text( keyVal ), new Text( token ) );
				}
			}
			else
			{
				st = null;
				String[] splitVar = value.toString().split( "\t" );
				keyVal = splitVar[0];
				String firstJoinVarValue = "";
				String secondJoinVarValue = "";
				for( int i = 1; i < splitVar.length; i++ )
				{
					String remVarValue = "";
					String[] splitVarValue = splitVar[i].split( "#" );
					for( int j = 1; j < splitVarValue.length; j++ )
					{
						if( j == ( splitVarValue.length - 1 ) ) remVarValue += splitVarValue[j];
						else remVarValue += splitVarValue[j] + "#";
					}
					String newSplitVarValue = splitVarValue[0].split( "~" )[0] + "#" + remVarValue;
					if( splitVar[i].equalsIgnoreCase( keyVal ) ) continue;
					if( !newSplitVarValue.split( "#" )[0].equalsIgnoreCase( jp.getJoiningVariablesList().get( 0 ).substring( 1 ) ) )
						secondJoinVarValue += newSplitVarValue + "\t";
					else
						firstJoinVarValue += newSplitVarValue + "\t";
				}
                String[] splitFirstVarValue = firstJoinVarValue.split( "\t" );
                String[] splitSecondVarValue = secondJoinVarValue.split( "\t" );
                for( int x = 0; x < splitFirstVarValue.length; x++ )
                {
                        for( int y = 0; y < splitSecondVarValue.length; y++ )
                        {
                                context.write( new Text( splitFirstVarValue[x] + "~" + splitSecondVarValue[y] ), new Text( keyVal ) );
                        }
                }
			}
		}
	}
}