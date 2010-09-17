/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class DictionaryCreatorMapper extends
		Mapper<LongWritable, Text, Text, IntWritable> {
	private Text outputKey;
	private IntWritable one;
	
	public DictionaryCreatorMapper() {
		outputKey = new Text();
		one = new IntWritable(1);
	}

	@Override
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		String [] splits = value.toString().split("\\s");
		if (splits.length != 4)
			return;
		for (int i = 0; i < 3; i++) {
			outputKey.set(splits[i]);
			context.write(outputKey, one);
		}
	}
}
