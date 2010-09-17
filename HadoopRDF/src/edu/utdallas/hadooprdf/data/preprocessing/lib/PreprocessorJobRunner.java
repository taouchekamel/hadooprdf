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
	protected DataSet m_DataSet;
	/**
	 * The input directory path, all the files of this directory will be read.
	 */
	protected Path m_InputDirectoryPath;
	/**
	 * Input files extension in lower case
	 */
	protected String m_sInputFilesExtension;
	/**
	 * The output directory path
	 */
	protected Path m_OutputDirectoryPath;
	/**
	 * The number of reducers to be used for the job
	 */
	protected int m_iNumberOfReducers;
	/**
	 * The class constructor
	 * @param dataSet the m_DataSet to set
	 * @throws DataFileExtensionNotSetException
	 */
	public PreprocessorJobRunner(DataSet dataSet) throws DataFileExtensionNotSetException {
		m_DataSet = dataSet;
		m_sInputFilesExtension = dataSet.getOriginalDataFilesExtension();
		setNumberOfReducers(-1);
	}
	/**
	 * @return the m_iNumberOfReducers
	 */
	public int getNumberOfReducers() {
		return m_iNumberOfReducers;
	}
	/**
	 * @param iNumberOfReducers the m_iNumberOfReducers to set
	 */
	public void setNumberOfReducers(int iNumberOfReducers) {
		m_iNumberOfReducers = iNumberOfReducers;
	}
	/**
	 * @return the m_sInputFilesExtension
	 */
	public String getInputFilesExtension() {
		return m_sInputFilesExtension;
	}
	/**
	 * @param mSInputFilesExtension the m_sInputFilesExtension to set
	 */
	public void setInputFilesExtension(String inputFilesExtension) {
		m_sInputFilesExtension = inputFilesExtension;
	}
}
