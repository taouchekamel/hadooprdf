package edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.mapred;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import edu.utdallas.hadooprdf.data.SubjectObjectPair;
import edu.utdallas.hadooprdf.data.commons.Constants;
import edu.utdallas.hadooprdf.data.commons.Tags;
import edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.PredicateSplitterByObjectType;
import edu.utdallas.hadooprdf.lib.mapred.io.ByteLongLongWritable;
import edu.utdallas.hadooprdf.lib.util.Pair;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class PredicateSplitterByObjectTypeJob1Reducer extends
		Reducer<LongWritable, ByteLongLongWritable, Text, SubjectObjectPair> {
	private Text outputKey;
	private SubjectObjectPair outputValue;
	private Set<Long> typeList;
	private List<Pair<Long, Long>> subjectPredicateList;
	private String rdfTypePredicate;
	private String fileNamePrefix;

	public PredicateSplitterByObjectTypeJob1Reducer() {
		outputKey = new Text();
		outputValue = new SubjectObjectPair();
		typeList = new HashSet<Long> ();
		subjectPredicateList = new LinkedList<Pair<Long, Long>> ();
		rdfTypePredicate = "";
		fileNamePrefix = "";
	}
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#reduce(java.lang.Object, java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	protected void reduce(LongWritable key, Iterable<ByteLongLongWritable> values,
			org.apache.hadoop.mapreduce.Reducer<LongWritable, ByteLongLongWritable, Text, SubjectObjectPair>.Context context)
			throws IOException, InterruptedException {
		typeList.clear();
		subjectPredicateList.clear();
		
		for (ByteLongLongWritable value : values) {
			if (PredicateSplitterByObjectType.JOB1_TYPE_FLAG == value.getFlag()) {
				typeList.add(value.getData1());
			} else {
				subjectPredicateList.add(new Pair<Long, Long>(value.getData1(), value.getData2()));
			}
		}
		
		if (typeList.size() > 0) {
			for (Long type : typeList) {
				// Output to the type file
				StringBuilder sb = new StringBuilder(fileNamePrefix);
				sb.append(rdfTypePredicate);
				sb.append(Constants.PREDICATE_OBJECT_TYPE_SEPARATOR);
				sb.append(type);
				sb.append('.');
				sb.append(Constants.POS_EXTENSION);
				outputKey.set(sb.toString());
				outputValue.setSubject(key.get());
				outputValue.setObject(0);	// Very important, the SOPOutputFormat will output any object which is not zero!
				context.write(outputKey, outputValue);	// Output to the type file
				// Output to the splitted predicate file
				writePairsToSplittedPredicateFile(key.get(), "" + Constants.PREDICATE_OBJECT_TYPE_SEPARATOR + type, context);
			}
		} else {	// No type information
			writePairsToSplittedPredicateFile(key.get(), "", context);
		}
	}
	
	private void writePairsToSplittedPredicateFile(long object, String separatorAndTypeInfo,
			org.apache.hadoop.mapreduce.Reducer<LongWritable, ByteLongLongWritable, Text, SubjectObjectPair>.Context context) throws IOException, InterruptedException {
		String suffix = separatorAndTypeInfo + '.' + Constants.POS_EXTENSION;
		for (Pair<Long, Long> pair : subjectPredicateList) {
			StringBuffer sb = new StringBuffer(fileNamePrefix);
			sb.append(pair.getSecond().toString());
			sb.append(suffix);
			outputKey.set(sb.toString());	// Output filename
			outputValue.setSubject(pair.getFirst());	// Subject
			outputValue.setObject(object);				// Object
			context.write(outputKey, outputValue);		// Output to the splitted predicate file
		}
	}
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#setup(org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	protected void setup(org.apache.hadoop.mapreduce.Reducer<LongWritable, ByteLongLongWritable, Text, SubjectObjectPair>.Context context)
			throws IOException, InterruptedException {
		super.setup(context);
		rdfTypePredicate = context.getConfiguration().get(Tags.RDF_TYPE_PREDICATE);
		fileNamePrefix = "" + context.getTaskAttemptID().getTaskID().getId() + Constants.PREDICATE_OBJECT_TYPE_SEPARATOR;
	}

}
