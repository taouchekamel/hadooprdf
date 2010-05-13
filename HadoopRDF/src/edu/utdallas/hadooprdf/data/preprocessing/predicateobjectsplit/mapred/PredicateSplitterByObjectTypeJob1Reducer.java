package edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.mapred;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import edu.utdallas.hadooprdf.data.commons.Constants;
import edu.utdallas.hadooprdf.data.commons.Tags;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class PredicateSplitterByObjectTypeJob1Reducer extends
		Reducer<Text, Text, Text, Text> {
	private class SubjectPredicatePair {
		public String sSubject;
		public String sPredicate;
	}
	private Text m_txtKey;
	private Text m_txtValue;
	private String m_sRDFTypeFilenameWithoutExtension;
	private List<SubjectPredicatePair> m_lstSubjectPredicatePair;

	public PredicateSplitterByObjectTypeJob1Reducer() {
		m_txtKey = new Text();
		m_txtValue = new Text();
		m_sRDFTypeFilenameWithoutExtension = null;
		m_lstSubjectPredicatePair = new LinkedList<SubjectPredicatePair> ();
	}
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#reduce(java.lang.Object, java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	protected void reduce(Text key, Iterable<Text> values,
			org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		String sKey = key.toString();
		String sType = null;
		m_lstSubjectPredicatePair.clear();
		Iterator<Text> iter = values.iterator();
		while (iter.hasNext()) {
			String sValue = iter.next().toString();
			String sSplits [] = sValue.split("\\s");
			if (sSplits[0].startsWith("T#"))
				sType = sSplits[0].substring(2);	// Get the type
			else {
				SubjectPredicatePair spp = new SubjectPredicatePair();
				spp.sSubject = sSplits[0].substring(2);
				spp.sPredicate = sSplits[1];
				m_lstSubjectPredicatePair.add(spp);
			}
		}
		String sTypeInfo = "";
		if (null != sType) {
			sTypeInfo = "_" + sType;
			// Output to the type file
			m_txtKey.set(m_sRDFTypeFilenameWithoutExtension + Constants.PREDICATE_OBJECT_TYPE_SEPARATOR + sType + '.' + Constants.POS_EXTENSION);
			m_txtValue.set(sKey);
			context.write(m_txtKey, m_txtValue);
		}
		Iterator<SubjectPredicatePair> iter1 = m_lstSubjectPredicatePair.iterator();
		while (iter1.hasNext()) {
			SubjectPredicatePair spp = iter1.next();
			m_txtKey.set(spp.sPredicate + sTypeInfo + '.' + Constants.POS_EXTENSION);	// Output filename
			m_txtValue.set(spp.sSubject + '\t' + sKey);	// Subject and Object
			context.write(m_txtKey, m_txtValue);
		}
	}
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#setup(org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	protected void setup(org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		m_sRDFTypeFilenameWithoutExtension = context.getConfiguration().get(Tags.RDF_TYPE_FILENAME);
		m_sRDFTypeFilenameWithoutExtension = m_sRDFTypeFilenameWithoutExtension.substring(0,
				m_sRDFTypeFilenameWithoutExtension.length() - Constants.PS_EXTENSION.length() - 1);
		super.setup(context);
	}

}
