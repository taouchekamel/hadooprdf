/**
 * 
 */
package edu.utdallas.hadooprdf.data.metadata.summarystatistics;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class SummaryStatisticsTest {
	private edu.utdallas.hadooprdf.conf.Configuration config;

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
	 * Test method for {@link edu.utdallas.hadooprdf.data.metadata.summarystatistics.SummaryStatistics#persist()}.
	 * @throws ConfigurationNotInitializedException 
	 * @throws IOException 
	 * @throws SummaryStatisticsException 
	 */
	@Test
	public void testPersist() throws IOException, ConfigurationNotInitializedException, SummaryStatisticsException {
		DataSet ds = new DataSet("/user/farhan/hadooprdf/LUBM1");
		ds.setOriginalDataFilesExtension("owl");
		org.apache.hadoop.conf.Configuration hadoopConfiguration =
			new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
		SummaryStatistics ss = new SummaryStatistics(hadoopConfiguration, ds.getPathToSummaryStatistics(), true);
		ss.addFileStatistics("fname", "fpath", 1622, 47, 100);
		ss.persist();
		FileSystem fs = FileSystem.get(hadoopConfiguration);
		assertTrue(fs.exists(ds.getPathToSummaryStatistics()));
	}

	/**
	 * Test method for {@link edu.utdallas.hadooprdf.data.metadata.summarystatistics.SummaryStatistics#getAllFileStatistics()}.
	 * @throws ConfigurationNotInitializedException 
	 * @throws IOException 
	 * @throws SummaryStatisticsException 
	 */
	@Test
	public void testGetAllFileStatistics() throws IOException, ConfigurationNotInitializedException, SummaryStatisticsException {
		DataSet ds = new DataSet("/user/farhan/hadooprdf/LUBM1");
		ds.setOriginalDataFilesExtension("owl");
		org.apache.hadoop.conf.Configuration hadoopConfiguration =
			new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
		SummaryStatistics ss = new SummaryStatistics(hadoopConfiguration, ds.getPathToSummaryStatistics(), true);
		Collection<FileStatistics> fstats = ss.getAllFileStatistics();
		for (FileStatistics fstat : fstats)
			System.out.println(fstat);
	}

}
