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
public class DictionaryEncoderMapper3 extends
		Mapper<LongWritable, Text, Text, Text> {
	private String pathToDictionary;
	private Text outputKey;
	private Text outputValue;
	
	public DictionaryEncoderMapper3() {
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
		String pathToParent = ((FileSplit) context.getInputSplit()).getPath().getParent().toString();
		if (pathToParent.endsWith(pathToDictionary)) {
			// Process dictionary data here
			outputKey.set(splits[0]);	// Set the string as the key
			outputValue.set("I\t" + splits[2]);	// The long id
		} else {
			// Process N-Triples data here
			outputKey.set(splits[2]);	// Set the object as the key
			StringBuilder sb = new StringBuilder("T\t");
			sb.append(splits[0]);		// The subject
			sb.append('\t');
			sb.append(splits[1]);		// The predicate
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
		super.setup(context);
		pathToDictionary = context.getConfiguration().get(Tags.PATH_TO_DICTIONARY);
	}

}
