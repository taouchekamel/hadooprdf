/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class BinaryEncoderPredicateSplitterMapper extends
		Mapper<LongWritable, Text, Text, Text> {

	private Text outputKey;
	private Text outputValue;
	
	public BinaryEncoderPredicateSplitterMapper() {
		outputKey = new Text();
		outputValue = new Text();
	}
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#map(java.lang.Object, java.lang.Object, org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		String [] splits = value.toString().split("\\s");
		if (splits.length < 3)
			return;
		outputKey.set(splits[1]);	// Set the predicate as the key
		StringBuilder sb = new StringBuilder(splits[0]);	// Initialize the builder with subject
		sb.append('\t');
		sb.append(splits[2]);	// Append object
		outputValue.set(sb.toString());
		context.write(outputKey, outputValue);
	}

}
