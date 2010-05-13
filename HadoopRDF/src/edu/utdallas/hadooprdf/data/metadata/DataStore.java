package edu.utdallas.hadooprdf.data.metadata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.lib.util.Utility;

/**
 * A meta data class for the whole storage
 * @author Mohammad Farhan Husain
 *
 */
public class DataStore {
	/**
	 * Path to the storage directory
	 */
	private Path m_PathToStorageRoot;
	/**
	 * Path to metadata of the data set
	 */
	private Path m_PathToMetaData;
	/**
	 * Path to data directory
	 */
	private Path m_PathToDataDirectory;
	/**
	 * Path to temporary directory
	 */
	private Path m_PathToTemp;
	/**
	 * A map object containing data set name-metadata pair
	 */
	private Map<String, DataSet> m_DataSetMap;
	/**
	 * The class constructor
	 * @param sPathToStorageRoot a string containing the path to the storage root
	 * @throws ConfigurationNotInitializedException 
	 * @throws DataStoreException 
	 */
	public DataStore(String sPathToStorageRoot, org.apache.hadoop.conf.Configuration hadoopConfiguration) throws DataStoreException {
		this(new Path(sPathToStorageRoot), hadoopConfiguration);
	}
	/**
	 * The class constructor
	 * @param pathToStorageRoot the Path to the storage root
	 * @throws ConfigurationNotInitializedException 
	 * @throws IOException 
	 */
	public DataStore(Path pathToStorageRoot, org.apache.hadoop.conf.Configuration hadoopConfiguration) throws DataStoreException {
		m_PathToStorageRoot = pathToStorageRoot;
		m_PathToMetaData = new Path(m_PathToStorageRoot, "metadata");
		try {
			Utility.createDirectory(hadoopConfiguration, m_PathToMetaData);
		} catch (IOException e) {
			throw new DataStoreException("Meta data directory could not be created because\n" + e.getMessage());
		}
		m_PathToDataDirectory = new Path(m_PathToStorageRoot, "data");
		try {
			Utility.createDirectory(hadoopConfiguration, m_PathToDataDirectory);
		} catch (IOException e) {
			throw new DataStoreException("Data directory could not be created because\n" + e.getMessage());
		}
		try {
			m_DataSetMap = createDataSets(hadoopConfiguration);
		} catch (IOException e) {
			throw new DataStoreException("Could not list data sets because\n" + e.getMessage());
		}
		m_PathToTemp = new Path(m_PathToStorageRoot, "tmp");
	}
	private Map<String, DataSet> createDataSets(org.apache.hadoop.conf.Configuration hadoopConfiguration) throws IOException {
		Map<String, DataSet> dataSetMap = new HashMap<String, DataSet> ();
		FileSystem fs = FileSystem.get(hadoopConfiguration);
		FileStatus files [] = fs.listStatus(m_PathToDataDirectory);
		for (int i = 0; i < files.length; i++)
			if (files[i].isDir())
				dataSetMap.put(files[i].getPath().getName(), new DataSet(files[i].getPath(), hadoopConfiguration));
		return dataSetMap;
	}
	/**
	 * @return the m_PathToStorageRoot
	 */
	public Path getPathToStorageRoot() {
		return m_PathToStorageRoot;
	}
	/**
	 * @return the m_PathToMetaData
	 */
	public Path getPathToMetaData() {
		return m_PathToMetaData;
	}
	/**
	 * @return the m_PathToDataDirectory
	 */
	public Path getPathToDataDirectory() {
		return m_PathToDataDirectory;
	}
	/**
	 * @return the m_PathToTemp
	 */
	public Path getPathToTemp() {
		return m_PathToTemp;
	}
	/**
	 * @return the m_DataSetMap
	 */
	public Map<String, DataSet> getDataSetMap() {
		return m_DataSetMap;
	}
}
