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
public class DictionaryEncoderMapper1 extends
		Mapper<LongWritable, Text, Text, Text> {
	private String pathToDictionary;
	private Text outputKey;
	private Text outputValue;
	
	public DictionaryEncoderMapper1() {
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
		if (splits.length < 3)	// Something wrong, is there a way to communicate this error other than throwing an IOException
			return;
		//String inputFile = ((FileSplit) context.getInputSplit()).getPath().getName();
		String pathToParent = ((FileSplit) context.getInputSplit()).getPath().getParent().toString();
		//System.out.println("Parent: " + pathToParent + "\nDictionary: " + pathToDictionary);
		outputKey.set(splits[0]);	// The string
		if (pathToParent.endsWith(pathToDictionary)) {
		//if (inputFile.endsWith(".owl")) {
			// Process dictionary data here
			outputValue.set("I\t" + splits[2]);	// The long id
		} else {
			// Process N-Triples data here
			StringBuffer sb = new StringBuffer("T\t");
			sb.append(splits[1]);
			sb.append('\t');
			sb.append(splits[2]);
			outputValue.set(sb.toString());
		}
		context.write(outputKey, outputValue);
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void setup(org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		pathToDictionary = context.getConfiguration().get(Tags.PATH_TO_DICTIONARY);
	}

}
