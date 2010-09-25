package edu.utdallas.hadooprdf.data.preprocessing;

import java.io.IOException;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.commons.Constants.SerializationFormat;
import edu.utdallas.hadooprdf.data.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.metadata.PredicateIdPairsException;
import edu.utdallas.hadooprdf.data.preprocessing.dictionary.DictionaryCreator;
import edu.utdallas.hadooprdf.data.preprocessing.dictionary.DictionaryCreatorException;
import edu.utdallas.hadooprdf.data.preprocessing.dictionary.DictionaryEncoder;
import edu.utdallas.hadooprdf.data.preprocessing.dictionary.DictionaryEncoderException;
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
	private DataSet dataSet;
	private SerializationFormat originalFormat;
	/**
	 * The class constructor
	 * @param dataSet the data set to preprocess
	 */
	public Preprocessor(DataSet dataSet, SerializationFormat originalFormat) {
		this.dataSet = dataSet;
		this.originalFormat = originalFormat;
	}
	public void preprocess() throws PreprocessorException {
		try {
			if (SerializationFormat.NTRIPLES != originalFormat) {
				ConvertToNTriples c = new ConvertToNTriples(originalFormat, dataSet);
				c.doConversion();
			}
			DictionaryCreator dc = new DictionaryCreator(dataSet);
			dc.createDictionary();
			DictionaryEncoder de = new DictionaryEncoder(dataSet);
			de.dictionaryEncode();
			PredicateSplitterByObjectType posbot = new PredicateSplitterByObjectType(dataSet);
			posbot.splitPredicateByObjectType();
		} catch (IOException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		} catch (DataFileExtensionNotSetException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		} catch (ConversionToNTriplesException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		} catch (ConfigurationNotInitializedException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		} catch (PredicateSplitterByObjectTypeException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		} catch (PredicateIdPairsException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		} catch (DictionaryCreatorException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		} catch (DictionaryEncoderException e) {
			throw new PreprocessorException("Preprocessing failed because\n" + e.getMessage());
		}
	}
}
