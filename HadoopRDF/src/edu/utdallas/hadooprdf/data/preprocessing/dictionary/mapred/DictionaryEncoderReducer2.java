/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class DictionaryEncoderReducer2 extends
		Reducer<Text, Text, Text, Text> {
	private Text outputValue;
	
	public DictionaryEncoderReducer2() {
		outputValue = new Text("");
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#reduce(java.lang.Object, java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	protected void reduce(Text key, Iterable<Text> values,
			org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		String id = "";
		List<String> triples = new LinkedList<String> ();
		Iterator<Text> iter = values.iterator();
		while (iter.hasNext()) {
			String value = iter.next().toString();
			if (value.startsWith("I\t")) // Id
				id = value.substring(2);
			else
				triples.add(value.substring(2));
		}
		StringBuffer sb = new StringBuffer();
		for (String triple : triples) {
			String [] splits = triple.split("\\s");
			sb.setLength(0);
			sb.append(splits[0]); 	// The subject
			sb.append('\t');
			sb.append(id);			// The predicate
			sb.append('\t');
			sb.append(splits[1]); 	// The object
			outputValue.set(sb.toString());
			context.write(null, outputValue);
		}
	}

}
