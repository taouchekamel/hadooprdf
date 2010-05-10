package edu.utdallas.hadooprdf.data.lib.mapred.serialization.conversion;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/**
 * The mapper class for converting RDF data from one serialization format
 * to other.
 * @author Mohammad Farhan Husain
 *
 */
public class ConversionMapper extends Mapper<LongWritable, Text, Text, Text> {
	private Text m_txtKey;
	private Text m_txtValue;
	
	/**
	 * The class constructor
	 */
	public ConversionMapper() {
		m_txtKey = new Text();
		m_txtValue = new Text();
	}
	@Override
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		String sInputFileName = ((FileSplit) context.getInputSplit()).getPath().getName();
		m_txtKey.set(sInputFileName);
		// Prepend the line number and a tab character to the value
		m_txtValue.set(key.toString() + '\t' + value);
		context.write(m_txtKey, m_txtValue);
	}
}
