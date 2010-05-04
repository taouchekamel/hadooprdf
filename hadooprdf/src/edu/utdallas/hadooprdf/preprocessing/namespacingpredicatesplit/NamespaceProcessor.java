package edu.utdallas.hadooprdf.preprocessing.namespacingpredicatesplit;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.metadata.DataSet;
import edu.utdallas.hadooprdf.preprocessing.namespacingpredicatesplit.NamespaceProcessorException;
import edu.utdallas.hadooprdf.preprocessing.namespacingpredicatesplit.PrefixFinder;
import edu.utdallas.hadooprdf.preprocessing.namespacingpredicatesplit.PrefixFinderException;
import edu.utdallas.hadooprdf.preprocessing.namespacingpredicatesplit.PrefixReplacerPredicateSplitter;
import edu.utdallas.hadooprdf.preprocessing.namespacingpredicatesplit.PrefixReplacerPredicateSplitterException;

/**
 * This class runs the whole process of finding prefixes,
 * generating namespaces and replacing prefixes with namespaces
 * @author Mohammad Farhan Husain
 */
public class NamespaceProcessor {
	/**
	 * The data set to work on
	 */
	private DataSet m_DataSet;
	
	/**
	 * The class constructor
	 * @param dataSet the data set to work on
	 */
	public NamespaceProcessor(DataSet dataSet) {
		m_DataSet = dataSet;
	}
	
	public void processDataForNamespace() throws ConfigurationNotInitializedException, NamespaceProcessorException {
		try {
			PrefixFinder pf = new PrefixFinder(m_DataSet);
			if (!pf.findPrefixes())
				throw new NamespaceProcessorException("Namespace could not be processed because PrefixFinder failed");
			PrefixReplacerPredicateSplitter pr = new PrefixReplacerPredicateSplitter(m_DataSet);
			if (!pr.replacePrefixes())
				throw new NamespaceProcessorException("Namespace could not be processed because PrefixReplacer failed");
		} catch (DataFileExtensionNotSetException e) {
			throw new NamespaceProcessorException("Namespace could not be processed because of the data file extension\n" + e.getMessage());
		} catch (PrefixFinderException e) {
			throw new NamespaceProcessorException("Namespace could not be processed\n" + e.getMessage());
		} catch (PrefixReplacerPredicateSplitterException e) {
			throw new NamespaceProcessorException("Namespace could not be processed\n" + e.getMessage());
		}
	}
}
