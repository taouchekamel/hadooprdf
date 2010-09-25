/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import edu.utdallas.hadooprdf.data.SubjectObjectPair;
import edu.utdallas.hadooprdf.data.commons.Constants;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class BinaryEncoderPredicateSplitterReducer extends
		Reducer<Text, Text, Text, SubjectObjectPair> {

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#reduce(java.lang.Object, java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	protected void reduce(Text key, Iterable<Text> values,
			org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, SubjectObjectPair>.Context context)
			throws IOException, InterruptedException {
		Iterator<Text> iter = values.iterator();
		int index;
		String value;
		long subject, object;
		Text outputKey = new Text(key.toString() +  '.' + Constants.PS_EXTENSION);
		while (iter.hasNext()) {
			value = iter.next().toString();
			index = value.indexOf('\t');
			subject = Long.parseLong(value.substring(0, index));
			object = Long.parseLong(value.substring(index + 1));
			context.write(outputKey, new SubjectObjectPair(subject, object));
		}
	}

}
