package edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.mapred;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import edu.utdallas.hadooprdf.data.commons.Constants;
import edu.utdallas.hadooprdf.data.commons.Tags;

/**
 * The mapper class for PredicateSplitterByObjectType
 * @author Mohammad Farhan Husain
 *
 */
public class PredicateSplitterByObjectTypeJob1Mapper extends
		Mapper<LongWritable, Text, Text, Text> {
	private String m_sRDFTypeFilename;
	private Text m_txtKey;
	private Text m_txtValue;
	
	public PredicateSplitterByObjectTypeJob1Mapper() {
		m_sRDFTypeFilename = null;
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
		String sInputFileName = ((FileSplit) context.getInputSplit()).getPath().getName();
		String sSplits [] = value.toString().split("\\s");
		if (sInputFileName.equals(m_sRDFTypeFilename)) {	// Input file is the rdf:type file
			m_txtKey.set(sSplits[0]);	// Set the subject as the key
			m_txtValue.set("T#" + sSplits[1]);
		}
		else {	// Input file is any predicate other than rdf:type
			m_txtKey.set(sSplits[1]);	// Set the object as the key
			m_txtValue.set("S#" + sSplits[0] + '\t' + sInputFileName.substring(0, sInputFileName.length() - Constants.PS_EXTENSION.length() - 1));
		}
		context.write(m_txtKey, m_txtValue);
	}
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void setup(org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		m_sRDFTypeFilename = context.getConfiguration().get(Tags.RDF_TYPE_FILENAME);
		super.setup(context);
	}
}
