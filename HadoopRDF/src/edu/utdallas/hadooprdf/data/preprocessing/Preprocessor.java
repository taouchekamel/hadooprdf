package edu.utdallas.hadooprdf.data.preprocessing;

import java.io.IOException;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.commons.Constants.SerializationFormat;
import edu.utdallas.hadooprdf.data.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.NamespaceProcessorPredicateSplitter;
import edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.NamespaceProcessorPredicateSplitterException;
import edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.PredicateSplitterByObjectType;
import edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.PredicateSplitterByObjectTypeException;
import edu.utdallas.hadooprdf.data.preprocessing.serialization.ConversionToNTriplesException;
import edu.utdallas.hadooprdf.data.preprocessing.serialization.ConvertToNTriples;

/**
 * The class which preprocesses the a data set
 * @author Mohammad Farhan Husain
 *
 */
public class Preprocessor {
	private DataSet m_DataSet;
	private SerializationFormat m_OriginalFormat;
	/**
	 * The class constructor
	 * @param dataSet the data set to preprocess
	 */
	public Preprocessor(DataSet dataSet, SerializationFormat originalFormat) {
		m_DataSet = dataSet;
		m_OriginalFormat = originalFormat;
	}
	public void preprocess() throws PreprocessorException {
		try {
			if (SerializationFormat.NTRIPLES != m_OriginalFormat) {
				ConvertToNTriples c = new ConvertToNTriples(m_OriginalFormat, m_DataSet);
				c.doConversion();
			}
			NamespaceProcessorPredicateSplitter npps = new NamespaceProcessorPredicateSplitter(m_DataSet);
			npps.processDataForNamespacePredicateSplit();
			PredicateSplitterByObjectType posbot = new PredicateSplitterByObjectType(m_DataSet);
			posbot.splitPredicateByObjectType();
		} catch (IOException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		} catch (DataFileExtensionNotSetException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		} catch (ConversionToNTriplesException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		} catch (ConfigurationNotInitializedException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		} catch (NamespaceProcessorPredicateSplitterException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		} catch (PredicateSplitterByObjectTypeException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		}
	}
}
