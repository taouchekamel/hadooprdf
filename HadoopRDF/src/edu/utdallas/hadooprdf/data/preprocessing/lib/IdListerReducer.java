/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.lib;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class IdListerReducer extends
		Reducer<Text, Text, Text, Text> {

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#reduce(java.lang.Object, java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	protected void reduce(Text key, Iterable<Text> values,
			org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		String predicate, tmp;
		boolean isPredicate = false;
		predicate = null;
		for (Text value : values) {
			tmp = value.toString();
			if (tmp.charAt(0) == 'P')
				isPredicate = true;
			else
				predicate = tmp.substring(2);
		}
		if (isPredicate && null != predicate)
			context.write(key, new Text(predicate));
	}

}
