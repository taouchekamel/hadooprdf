package edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.mapred;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import edu.utdallas.hadooprdf.data.preprocessing.lib.NamespacePrefixParser;
import edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.PrefixFinder;
import edu.utdallas.hadooprdf.data.rdf.uri.prefix.URIPrefixConsolidator;

/**
 * The reducer class for PrefixFinder
 * @author Mohammad Farhan Husain
 * @see PrefixFinder
 */
public class PrefixFinderReducer extends
		Reducer<IntWritable, Text, Text, Text> {
	/**
	 * The consolidator
	 */
	private URIPrefixConsolidator m_URIPrefixConsolidator;	
	/**
	 * The class constructor
	 */
	public PrefixFinderReducer() {
		m_URIPrefixConsolidator = new URIPrefixConsolidator("p");
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#reduce(java.lang.Object, java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	protected void reduce(IntWritable key, Iterable<Text> values,
			org.apache.hadoop.mapreduce.Reducer<IntWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		Iterator<Text> iter = values.iterator();
		while (iter.hasNext()) {
			String sValue = iter.next().toString();
			System.out.println("Prefix Parser : " + sValue);
			if(!sValue.equals("{}")){
				NamespacePrefixParser npp = new NamespacePrefixParser(sValue);
				NamespacePrefixParser.NamespacePrefix [] np = npp.getNamespacePrefixes();
				for (int i = 0; i < np.length; i++) {
					String namespace = np[i].getNamespace().substring(np[i].getNamespace().indexOf('_') + 1);
					m_URIPrefixConsolidator.addPrefixAndReplacementString(np[i].getPrefix(), namespace);
				}
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#cleanup(org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	protected void cleanup(org.apache.hadoop.mapreduce.Reducer<IntWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		context.write(new Text(), new Text(m_URIPrefixConsolidator.getLongestCommonPrefixes().toString()));
	}
}
