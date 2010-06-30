package edu.utdallas.hadooprdf.query.test;
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.*;

import edu.utdallas.hadooprdf.lib.util.JobParameters;

public class NormalHadoopQuery 
{
	public static void main(String[] args) throws Exception 
	{
		Configuration config = new Configuration();
		config.addResource( new Path( JobParameters.configFileDir + "/core-site.xml" ) );
		config.addResource( new Path( JobParameters.configFileDir + "/mapred-site.xml" ) );
		config.addResource( new Path( JobParameters.configFileDir + "/hdfs-site.xml" ) );
		edu.utdallas.hadooprdf.conf.Configuration.createInstance( config, "/user/pankil/hadooprdf" );
		
		//Job job = new Job( hadoopConfiguration, "jena-job" );
		JobConf conf = new JobConf(config,NormalHadoopQuery.class);
		//conf.setJobName("wordcount");
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		conf.setMapperClass( Map.class );
		conf.setReducerClass( Reduce.class );

		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		FileInputFormat.setInputPaths(conf, new Path("/user/pankil/hadooprdf/data/DBPEDIA/tmp/jena_test.txt"));
		FileOutputFormat.setOutputPath(conf, new Path("/user/pankil/hadooprdf/data/DBPEDIA/tmp/jena-test-op"));

		JobClient.runJob(conf);
	}
	
	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> 
	{
	      public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
	      {
	    	  String[] splitTab = value.toString().split( "\t" );
	    	  output.collect( new Text( splitTab[0] ), new Text( "hi" ) ); 
	      }
    }
			
    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> 
    {
	      public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
	      {
	    	  int count = 0; String val = "";
	    	  while( values.hasNext() ) { count++; val += values.next().toString() + "\t"; }
	    	  if( count > 1 ) output.collect( key, new Text( "" ) );
	      }
	}	
}
