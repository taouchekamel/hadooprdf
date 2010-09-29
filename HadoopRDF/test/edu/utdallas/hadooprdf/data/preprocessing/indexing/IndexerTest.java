/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.indexing;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.metadata.StringIdPairsException;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class IndexerTest {
	private edu.utdallas.hadooprdf.conf.Configuration config;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// Create cluster configuration
		org.apache.hadoop.conf.Configuration hadoopConfiguration = new Configuration();
		//String sConfDirectoryPath = "conf/SAIALLabCluster";
		String sConfDirectoryPath = "conf/SemanticWebLabCluster";
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/core-site.xml"));
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/hdfs-site.xml"));
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/mapred-site.xml"));
		// Create application configuration
		config = edu.utdallas.hadooprdf.conf.Configuration.createInstance(hadoopConfiguration, "/user/farhan/hadooprdf/");
		config.setNumberOfTaskTrackersInCluster(5); // 5 for semantic web lab, 10 for SAIAL lab
	}

	/**
	 * Test method for {@link edu.utdallas.hadooprdf.data.preprocessing.indexing.Indexer#index()}.
	 * @throws ConfigurationNotInitializedException 
	 * @throws IOException 
	 * @throws StringIdPairsException 
	 * @throws DataFileExtensionNotSetException 
	 * @throws IndexerException 
	 */
	@Test
	public void testIndex() throws IOException, ConfigurationNotInitializedException, DataFileExtensionNotSetException, StringIdPairsException, IndexerException {
		DataSet ds = new DataSet("/user/farhan/hadooprdf/LUBM1");
		ds.setOriginalDataFilesExtension("owl");
		Indexer indexer = new Indexer(ds);
		indexer.index();
	}

}
