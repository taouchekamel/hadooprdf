package edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.NamespaceProcessorPredicateSplitterException;
import edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.PrefixFinder;
import edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.PrefixFinderException;
import edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.PrefixReplacerPredicateSplitter;
import edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.PrefixReplacerPredicateSplitterException;

/**
 * This class runs the whole process of finding prefixes,
 * generating namespaces and replacing prefixes with namespaces
 * @author Mohammad Farhan Husain
 */
public class NamespaceProcessorPredicateSplitter {
	/**
	 * The data set to work on
	 */
	private DataSet m_DataSet;
	
	/**
	 * The class constructor
	 * @param dataSet the data set to work on
	 */
	public NamespaceProcessorPredicateSplitter(DataSet dataSet) {
		m_DataSet = dataSet;
	}
	
	/**
	 * The method which finds and replaces prefixes with namespaces and also splits data according to predicates
	 * @throws ConfigurationNotInitializedException
	 * @throws NamespaceProcessorPredicateSplitterException
	 */
	public void processDataForNamespacePredicateSplit() throws ConfigurationNotInitializedException, NamespaceProcessorPredicateSplitterException {
		try {
			PrefixFinder pf = new PrefixFinder(m_DataSet);
			pf.findPrefixes();
			PrefixReplacerPredicateSplitter pr = new PrefixReplacerPredicateSplitter(m_DataSet);
			pr.replacePrefixesAndSplitByPredicate();
		} catch (DataFileExtensionNotSetException e) {
			throw new NamespaceProcessorPredicateSplitterException("Namespace could not be processed because of the data file extension\n" + e.getMessage());
		} catch (PrefixFinderException e) {
			throw new NamespaceProcessorPredicateSplitterException("Namespace could not be processed\n" + e.getMessage());
		} catch (PrefixReplacerPredicateSplitterException e) {
			throw new NamespaceProcessorPredicateSplitterException("Namespace could not be processed\n" + e.getMessage());
		}
	}
}
