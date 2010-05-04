package edu.utdallas.hadooprdf.preprocessing.namespacing;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.metadata.DataSet;

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
			PrefixReplacer pr = new PrefixReplacer(m_DataSet);
			if (!pr.replacePrefixes())
				throw new NamespaceProcessorException("Namespace could not be processed because PrefixReplacer failed");
		} catch (DataFileExtensionNotSetException e) {
			throw new NamespaceProcessorException("Namespace could not be processed because of the data file extension\n" + e.getMessage());
		} catch (PrefixFinderException e) {
			throw new NamespaceProcessorException("Namespace could not be processed\n" + e.getMessage());
		} catch (PrefixReplacerException e) {
			throw new NamespaceProcessorException("Namespace could not be processed\n" + e.getMessage());
		}
	}
}
