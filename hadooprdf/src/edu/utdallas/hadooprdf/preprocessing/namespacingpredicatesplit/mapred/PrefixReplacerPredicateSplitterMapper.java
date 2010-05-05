package edu.utdallas.hadooprdf.preprocessing.namespacingpredicatesplit.mapred;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import edu.utdallas.hadooprdf.commons.Constants;
import edu.utdallas.hadooprdf.commons.Tags;
import edu.utdallas.hadooprdf.lib.util.Utility;
import edu.utdallas.hadooprdf.rdf.uri.prefix.PrefixNamespaceTree;

/**
 * The mapper class for prefix replacer job
 * @author Mohammad Farhan Husain
 *
 */
public class PrefixReplacerPredicateSplitterMapper extends
		Mapper<LongWritable, Text, Text, Text> {
	private Text m_txtKey;
	private Text m_txtValue;
	private PrefixNamespaceTree m_PrefixNamespaceTree;
	/**
	 * The class constructor
	 */
	public PrefixReplacerPredicateSplitterMapper() {
		m_txtKey = new Text();
		m_txtValue = new Text();
		m_PrefixNamespaceTree = null;
	}
	/**
	 * It replaces the prefixes and splits data according to predicates
	 */
	@Override
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		//String sInputFileName = ((FileSplit) context.getInputSplit()).getPath().getName();
		// Split the value
		String sValue = value.toString();
		String [] sElements = sValue.split("\\s");
		if (4 != sElements.length)	// Invalid triple, I should find a way to generate an error.
			return;				//I could not throw any exception other that the two specified in the method signature.
		StringBuffer sbValue = new StringBuffer();
		// Subject
		sbValue.append(matchAndReplacePrefix(sElements[0]));
		sbValue.append('\t');
		// Predicate
		m_txtKey.set(Utility.convertPredicateToFilename(matchAndReplacePrefix(sElements[1]), Constants.PS_EXTENSION));
		// Object
		sbValue.append(matchAndReplacePrefix(sElements[2]));
		m_txtValue.set(sbValue.toString());
		// Output
		context.write(m_txtKey, m_txtValue);
	}
	/**
	 * Tries to match to a prefix and replaces it with a namespace if possible
	 * @param sElement the string to match prefix against
	 * @return the new string in case a match is found, otherwise returns sElement
	 */
	private String matchAndReplacePrefix(String sElement) {
		int iLength = sElement.length();
		String sNewElement = m_PrefixNamespaceTree.matchAndReplacePrefix(sElement.substring(1, iLength - 1));
		return (null == sNewElement) ? sElement : sNewElement;
	}
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void setup(org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		m_PrefixNamespaceTree = Utility.getPrefixNamespaceTreeForDataSet(context.getConfiguration(),
				new Path(context.getConfiguration().get(Tags.PATH_TO_PREFIX_FILE)));
	}

}
