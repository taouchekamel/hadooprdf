package edu.utdallas.hadooprdf.conf;

/**
 * The singleton configuration class for the application
 * @author Mohammad Farhan Husain
 *
 */
public class Configuration {
	private static Configuration m_Instance = null;	// The singleton instance
	private int m_iTaskTrackersInCluster;	// Number of reducers in the cluster
	private final org.apache.hadoop.conf.Configuration m_HadoopConfiguration;	// The cluster configuration
	
	/**
	 * The class constructor
	 * @param hadoopConfiguration the m_HadoopConfiguration to set
	 */
	private Configuration(org.apache.hadoop.conf.Configuration hadoopConfiguration) {
		m_HadoopConfiguration = hadoopConfiguration;
		setNumberOfTaskTrackersInCluster(-1);
	}
	
	/**
	 * The factory method
	 * @param hadoopConfiguration the m_HadoopConfiguration to set if the instance has not been created
	 * @return the singleton instance of the class
	 */
	public static Configuration createInstance(org.apache.hadoop.conf.Configuration hadoopConfiguration) {
		if (null == m_Instance)
			m_Instance = new Configuration(hadoopConfiguration);
		return m_Instance;
	}

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
}
