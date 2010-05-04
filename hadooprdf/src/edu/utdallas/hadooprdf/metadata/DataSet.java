package edu.utdallas.hadooprdf.metadata;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;

/**
 * This class contains metadata about a dataset
 * @author Mohammad Farhan Husain
 *
 */
public class DataSet {
	/**
	 * Extension of original data files
	 */
	private String m_sOriginalDataFilesExtension;
	/**
	 * The root path of the data set
	 */
	private Path m_DataSetRoot;
	/**
	 * Path to the original data, which can be in any format
	 */
	private Path m_PathToOriginalData;
	/**
	 * Path to data in NTriples format and later using namespaces
	 */
	private Path m_PathToNTriplesData;
	/**
	 * Path to PS data
	 */
	private Path m_PathToPSData;
	/**
	 * Path to POS data
	 */
	private Path m_PathToPOSData;
	/**
	 * Path to metadata of the data set
	 */
	private Path m_PathToMetaData;
	/**
	 * Path to temporary directory
	 */
	private Path m_PathToTemp;
	/**
	 * Path to prefix file
	 */
	private Path m_PathToPrefixFile;
	
	/**
	 * Class constructor
	 * @param sDataSetRoot the root path of the data set as a String
	 * @param sOriginalDataFilesExtension the extension of original data files in lower case
	 * @throws ConfigurationNotInitializedException
	 * @throws IOException 
	 */
	public DataSet(String sDataSetRoot) throws IOException, ConfigurationNotInitializedException {
		this(new Path(sDataSetRoot));
	}

	/**
	 * Class constructor
	 * @param dataSetRoot the root path of the data set
	 * @throws ConfigurationNotInitializedException
	 * @throws IOException 
	 */
	public DataSet(Path dataSetRoot) throws IOException, ConfigurationNotInitializedException {
		m_sOriginalDataFilesExtension = null;
		m_DataSetRoot = dataSetRoot;
		m_PathToOriginalData = new Path(m_DataSetRoot, "Original");
		m_PathToNTriplesData = new Path(m_DataSetRoot, "NTriples");
		m_PathToPSData = new Path(m_DataSetRoot, "PS");
		m_PathToPOSData = new Path(m_DataSetRoot, "POS");
		m_PathToMetaData = new Path(m_DataSetRoot, "metadata");
		createMetaDataDirectory();
		m_PathToPrefixFile = new Path(m_PathToMetaData, "prefixes");
		m_PathToTemp = new Path(m_DataSetRoot, "tmp");
	}
	
	/**
	 * Creates the meta data directory
	 * @throws IOException
	 * @throws ConfigurationNotInitializedException
	 */
	private void createMetaDataDirectory() throws IOException, ConfigurationNotInitializedException {
		FileSystem.get(edu.utdallas.hadooprdf.conf.Configuration.getInstance().getHadoopConfiguration()).mkdirs(m_PathToMetaData);
	}
	
	/**
	 * @return the m_DataSetRoot
	 */
	public Path getDataSetRoot() {
		return m_DataSetRoot;
	}

	/**
	 * @return the m_PathToOriginalData
	 */
	public Path getPathToOriginalData() {
		return m_PathToOriginalData;
	}

	/**
	 * @return the m_PathToNTriplesData
	 */
	public Path getPathToNTriplesData() {
		return m_PathToNTriplesData;
	}

	/**
	 * @return the m_PathToPSData
	 */
	public Path getPathToPSData() {
		return m_PathToPSData;
	}

	/**
	 * @return the m_PathToPOSData
	 */
	public Path getPathToPOSData() {
		return m_PathToPOSData;
	}

	/**
	 * @return the m_PathToMetaData
	 */
	public Path getPathToMetaData() {
		return m_PathToMetaData;
	}

	/**
	 * @return the m_PathToTemp
	 */
	public Path getPathToTemp() {
		return m_PathToTemp;
	}
	/**
	 * @return the m_PathToPrefixFile
	 */
	public Path getPathToPrefixFile() {
		return m_PathToPrefixFile;
	}

	/**
	 * @param sDataFilesExtension the extension of the original data files
	 */
	public void setOriginalDataFilesExtension(String sDataFilesExtension) {
		m_sOriginalDataFilesExtension = sDataFilesExtension;
	}
	/**
	 * @return the m_sOriginalDataFilesExtension
	 * @throws DataFileExtensionNotSetException 
	 */
	public String getOriginalDataFilesExtension() throws DataFileExtensionNotSetException {
		if (null == m_sOriginalDataFilesExtension) throw new DataFileExtensionNotSetException("Extension of original data files is not set");
		return m_sOriginalDataFilesExtension;
	}
}
