/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import edu.utdallas.hadooprdf.data.commons.Tags;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class PredicateListerMapper extends
		Mapper<LongWritable, Text, Text, Text> {
	private String pathToDictionary;
	private Text outputKey;
	private Text outputValue;
	
	public PredicateListerMapper() {
		pathToDictionary = "";
		outputKey = new Text("");
		outputValue = new Text("");
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#map(java.lang.Object, java.lang.Object, org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		String [] splits = line.split("\\s");
		String pathToParent = ((FileSplit) context.getInputSplit()).getPath().getParent().toString();
		if (pathToParent.endsWith(pathToDictionary)) {
			// Process dictionary data here
			outputKey.set(splits[2]);	// Set the string id as the key
			outputValue.set("S\t" + splits[0]);	// Set the string as the value
		} else {
			// Process predicate list here
			outputKey.set(splits[0]);	// The predicate which is a string id
			outputValue.set("P");
		}
		context.write(outputKey, outputValue);
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void setup(org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		super.setup(context);
		pathToDictionary = context.getConfiguration().get(Tags.PATH_TO_DICTIONARY);
	}

}
