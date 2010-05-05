/**
 * 
 */
package edu.utdallas.hadooprdf.preprocessing.predicateobjectsplit.mapred;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class PredicateSplitterByObjectTypeJob2Mapper extends
		Mapper<LongWritable, Text, Text, Text> {
	private Text m_txtKey;
	private Text m_txtValue;
	/**
	 * The class constructor
	 */
	public PredicateSplitterByObjectTypeJob2Mapper() {
		m_txtKey = new Text();
		m_txtValue = new Text();
	}
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#map(java.lang.Object, java.lang.Object, org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		String sValue = value.toString();
		int index = sValue.indexOf('\t');
		m_txtKey.set(sValue.substring(0, index));
		m_txtValue.set(sValue.substring(index + 1));
		context.write(m_txtKey, m_txtValue);
	}

}
