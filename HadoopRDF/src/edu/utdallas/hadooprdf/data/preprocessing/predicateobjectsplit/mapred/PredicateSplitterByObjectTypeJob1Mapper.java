package edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.mapred;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import edu.utdallas.hadooprdf.data.SubjectObjectPair;
import edu.utdallas.hadooprdf.data.commons.Constants;
import edu.utdallas.hadooprdf.data.commons.Tags;
import edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.PredicateSplitterByObjectType;
import edu.utdallas.hadooprdf.lib.mapred.io.ByteLongLongWritable;

/**
 * The mapper class for PredicateSplitterByObjectType
 * @author Mohammad Farhan Husain
 *
 */
public class PredicateSplitterByObjectTypeJob1Mapper extends
		Mapper<LongWritable, SubjectObjectPair, LongWritable, ByteLongLongWritable> {
	private long rdfTypePredicate;
	private LongWritable outputKey;
	private ByteLongLongWritable outputValue;
	private int extensionLength;
	
	public PredicateSplitterByObjectTypeJob1Mapper() {
		rdfTypePredicate = 0;
		outputKey = new LongWritable();
		outputValue = new ByteLongLongWritable();
		extensionLength = Constants.PS_EXTENSION.length() + 1;
	}
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#map(java.lang.Object, java.lang.Object, org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void map(LongWritable key, SubjectObjectPair value,
			org.apache.hadoop.mapreduce.Mapper<LongWritable, SubjectObjectPair, LongWritable, ByteLongLongWritable>.Context context)
			throws IOException, InterruptedException {
		String inputFileName = ((FileSplit) context.getInputSplit()).getPath().getName();
		final long predicate = Long.parseLong(inputFileName.substring(0, inputFileName.length() - extensionLength));
		if (predicate == rdfTypePredicate) {	// Input file is the rdf:type file
			outputKey.set(value.getSubject());	// Set the subject as the key
			outputValue.setFlag(PredicateSplitterByObjectType.JOB1_TYPE_FLAG);
			outputValue.setData1(value.getObject());
		} else {	// Input file is any predicate other than rdf:type
			outputKey.set(value.getObject());	// Set the object as the key
			outputValue.setFlag(PredicateSplitterByObjectType.JOB1_TRIPLE_FLAG);
			outputValue.setData1(value.getSubject());
			outputValue.setData2(predicate);
		}
		context.write(outputKey, outputValue);
	}
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void setup(org.apache.hadoop.mapreduce.Mapper<LongWritable, SubjectObjectPair, LongWritable, ByteLongLongWritable>.Context context)
			throws IOException, InterruptedException {
		rdfTypePredicate = Long.parseLong(context.getConfiguration().get(Tags.RDF_TYPE_PREDICATE));
		super.setup(context);
	}
}
