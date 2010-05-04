package edu.utdallas.hadooprdf.preprocessing.namespacing.mapred;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import edu.utdallas.hadooprdf.preprocessing.namespacing.PrefixFinder;
import edu.utdallas.hadooprdf.rdf.uri.prefix.InvalidURIException;
import edu.utdallas.hadooprdf.rdf.uri.prefix.URIPrefixTree;

/**
 * The mapper class for PrefixFinder
 * @author Mohammad Farhan Husain
 * @see PrefixFinder
 */
public class PrefixFinderMapper extends
		Mapper<LongWritable, Text, IntWritable, Text> {
	/**
	 * The output key, the value is set to 1 so that all the pairs goes to one reducer
	 */
	private IntWritable m_Key;
	/**
	 * The URIPrefixTree
	 */
	private URIPrefixTree m_URIPrefixTree;
	/**
	 * The class constructor
	 */
	public PrefixFinderMapper() {
		m_Key = new IntWritable(1);
		m_URIPrefixTree = null;
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#map(java.lang.Object, java.lang.Object, org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, IntWritable, Text>.Context context)
			throws IOException, InterruptedException {
		String [] sElements = value.toString().split("\\s");
		if (4 != sElements.length)	// Invalid triple, I should find a way to generate an error.
			return;				//I could not throw any exception other that the two specified in the method signature.
		for (int i = 0; i < 3; i++) {
			String sElement = sElements[i];
			int iLength = sElement.length();
			if (sElement.charAt(0) == '<' && sElement.charAt(iLength - 1) == '>') {	// Check if it is a URI
				try {
					m_URIPrefixTree.addURI(sElement.substring(1, sElement.length() - 1));
				} catch (InvalidURIException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#cleanup(org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void cleanup(org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, IntWritable, Text>.Context context)
			throws IOException, InterruptedException {
		context.write(m_Key, new Text(m_URIPrefixTree.getLongestCommonPrefixes().toString()));
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void setup(org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, IntWritable, Text>.Context context)
			throws IOException, InterruptedException {
		m_URIPrefixTree = new URIPrefixTree("" + context.getTaskAttemptID().getTaskID().getId() + '_');
	}

}
