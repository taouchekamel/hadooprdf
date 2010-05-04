package edu.utdallas.hadooprdf.preprocessing.namespacing.mapred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import edu.utdallas.hadooprdf.commons.Tags;
import edu.utdallas.hadooprdf.preprocessing.lib.NamespacePrefixParser;
import edu.utdallas.hadooprdf.rdf.uri.prefix.PrefixNamespaceTree;

/**
 * The mapper class for prefix replacer job
 * @author Mohammad Farhan Husain
 *
 */
public class PrefixReplacerMapper extends
		Mapper<LongWritable, Text, Text, Text> {
	private Text m_txtKey;
	private Text m_txtValue;
	private PrefixNamespaceTree m_PrefixNamespaceTree;
	/**
	 * The class constructor
	 */
	public PrefixReplacerMapper() {
		m_txtKey = new Text();
		m_txtValue = new Text();
		m_PrefixNamespaceTree = null;
	}
	@Override
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		String sInputFileName = ((FileSplit) context.getInputSplit()).getPath().getName();
		m_txtKey.set(sInputFileName);
		// Split the value
		String sValue = value.toString();
		String [] sElements = sValue.split("\\s");
		if (4 != sElements.length)	// Invalid triple, I should find a way to generate an error.
			return;				//I could not throw any exception other that the two specified in the method signature.
		StringBuffer sbValue = new StringBuffer();
		for (int i = 0; i < 3; i++) {
			String sElement = sElements[i];
			int iLength = sElement.length();
			if (sElement.charAt(0) == '<' && sElement.charAt(iLength - 1) == '>') {	// Check if it is a URI
				String sNewElement = m_PrefixNamespaceTree.matchAndReplacePrefix(sElement.substring(1, iLength - 1));
				if (null != sNewElement)
					sElement = sNewElement;
			}
			sbValue.append(sElement);
			if (i != 2)
				sbValue.append(' ');
		}
		m_txtValue.set(sbValue.toString());
		context.write(m_txtKey, m_txtValue);
	}
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@Override
	protected void setup(org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		FileSystem fs = FileSystem.get(context.getConfiguration());
		FSDataInputStream fsdis = fs.open(new Path(context.getConfiguration().get(Tags.PATH_TO_PREFIX_FILE)));
		BufferedReader br = new BufferedReader(new InputStreamReader(fsdis));
		NamespacePrefixParser npp = new NamespacePrefixParser(br.readLine());
		m_PrefixNamespaceTree = new PrefixNamespaceTree(npp.getNamespacePrefixes());
	}

}
