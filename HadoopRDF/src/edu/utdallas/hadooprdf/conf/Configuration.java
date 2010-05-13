package edu.utdallas.hadooprdf.conf;

import java.io.IOException;

import edu.utdallas.hadooprdf.data.metadata.DataStore;
import edu.utdallas.hadooprdf.data.metadata.DataStoreException;

/**
 * The singleton configuration class for the application
 * @author Mohammad Farhan Husain
 *
 */
public class Configuration {
	private static Configuration m_Instance = null;	// The singleton instance
	private int m_iTaskTrackersInCluster;	// Number of reducers in the cluster
	private final org.apache.hadoop.conf.Configuration m_HadoopConfiguration;	// The cluster configuration
	private DataStore m_DataStore;
	/**
	 * The class constructor
	 * @param hadoopConfiguration the m_HadoopConfiguration to set
	 * @throws ConfigurationException 
	 */
	private Configuration(org.apache.hadoop.conf.Configuration hadoopConfiguration, String sPathToStorageRoot) throws ConfigurationException {
		m_HadoopConfiguration = hadoopConfiguration;
		try {
			m_DataStore = new DataStore(sPathToStorageRoot, hadoopConfiguration);
		} catch (DataStoreException e) {
			throw new ConfigurationException("Configuration could not be initialized because\n" + e.getMessage());
		}
		setNumberOfTaskTrackersInCluster(-1);
	}
	
	/**
	 * The factory method
	 * @param hadoopConfiguration the m_HadoopConfiguration to set if the instance has not been created
	 * @return the singleton instance of the class
	 * @throws IOException
	 */
	public static Configuration createInstance(org.apache.hadoop.conf.Configuration hadoopConfiguration,
			String sPathToStorageRoot) throws ConfigurationException {
		if (null == m_Instance)
			m_Instance = new Configuration(hadoopConfiguration, sPathToStorageRoot);
		return m_Instance;
	}
	/**
	 * The method to get the already created singleton instance
	 * @return the singleton instance
	 * @throws ConfigurationNotInitializedException
	 */
	public static Configuration getInstance() throws ConfigurationNotInitializedException {
		if (null == m_Instance) throw new ConfigurationNotInitializedException("The singleton instance of the Configuration class is not instantiated");
		return m_Instance;
	}
	/**
	 * @return number of reducers in the cluster
	 */
	public int getNumberOfTaskTrackersInCluster() {
		return m_iTaskTrackersInCluster;
	}
	/**
	 * @param iTaskTrackersInCluster the m_iTaskTrackersInCluster to set
	 */
	public void setNumberOfTaskTrackersInCluster(int iTaskTrackersInCluster) {
		m_iTaskTrackersInCluster = iTaskTrackersInCluster;
	}
	/**
	 * @return the m_HadoopConfiguration
	 */
	public org.apache.hadoop.conf.Configuration getHadoopConfiguration() {
		return m_HadoopConfiguration;
	}
	/**
	 * @return the m_DataStore
	 */
	public DataStore getDataStore() {
		return m_DataStore;
	}
}
