/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import edu.utdallas.hadooprdf.data.preprocessing.dictionary.DictionaryPrefixTree;
import edu.utdallas.hadooprdf.lib.util.Utility;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class DictionaryCreatorReducer extends
		Reducer<Text, IntWritable, Text, LongWritable> {
	private long startingId;
	private DictionaryPrefixTree dictionary;
	
	public DictionaryCreatorReducer() {
		startingId = 0;
		dictionary = null;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#setup(org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
		startingId = context.getTaskAttemptID().getTaskID().getId();	// Set to reducer ID
		startingId <<= Long.SIZE - Utility.getMaxBitsRequiredToStore(context.getNumReduceTasks() - 1);
		dictionary = new DictionaryPrefixTree(++startingId);
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#reduce(java.lang.Object, java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			org.apache.hadoop.mapreduce.Reducer<Text, IntWritable, Text, LongWritable>.Context context)
			throws IOException, InterruptedException {
		dictionary.addString(key.toString());
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#cleanup(org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	protected void cleanup(org.apache.hadoop.mapreduce.Reducer<Text, IntWritable, Text, LongWritable>.Context context)
			throws IOException, InterruptedException {
		super.cleanup(context);
		dictionary.writeTreeToReducerContext(context);
	}
}
