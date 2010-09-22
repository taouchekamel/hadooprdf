package edu.utdallas.hadooprdf.data.preprocessing.lib;

import org.apache.hadoop.fs.Path;

import edu.utdallas.hadooprdf.data.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;

/**
 * A parent class for preprocessor job runners
 * @author Mohammad Farhan Husain
 */
public abstract class PreprocessorJobRunner {
	/**
	 * The data set to work on
	 */
	protected DataSet dataSet;
	/**
	 * The input directory path, all the files of this directory will be read.
	 */
	protected Path inputDirectoryPath;
	/**
	 * Input files extension in lower case
	 */
	protected String inputFilesExtension;
	/**
	 * The output directory path
	 */
	protected Path outputDirectoryPath;
	/**
	 * The number of reducers to be used for the job
	 */
	protected int numberOfReducers;
	/**
	 * The class constructor
	 * @param dataSet the m_DataSet to set
	 * @throws DataFileExtensionNotSetException
	 */
	public PreprocessorJobRunner(DataSet dataSet) throws DataFileExtensionNotSetException {
		this.dataSet = dataSet;
		inputFilesExtension = dataSet.getOriginalDataFilesExtension();
		setNumberOfReducers(-1);
	}
	/**
	 * @return the m_iNumberOfReducers
	 */
	public int getNumberOfReducers() {
		return numberOfReducers;
	}
	/**
	 * @param iNumberOfReducers the numberOfReducers to set
	 */
	public void setNumberOfReducers(int iNumberOfReducers) {
		this.numberOfReducers = iNumberOfReducers;
	}
	/**
	 * @return the inputFilesExtension
	 */
	public String getInputFilesExtension() {
		return inputFilesExtension;
	}
	/**
	 * @param mSInputFilesExtension the inputFilesExtension to set
	 */
	public void setInputFilesExtension(String inputFilesExtension) {
		this.inputFilesExtension = inputFilesExtension;
	}
}
