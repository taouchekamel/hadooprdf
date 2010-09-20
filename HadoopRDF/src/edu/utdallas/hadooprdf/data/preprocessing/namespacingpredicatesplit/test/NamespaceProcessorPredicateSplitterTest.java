/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.test;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.NamespaceProcessorPredicateSplitter;
import edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.NamespaceProcessorPredicateSplitterException;

/**
 * @author hadoop
 *
 */
public class NamespaceProcessorPredicateSplitterTest {
	private edu.utdallas.hadooprdf.conf.Configuration config;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// Create cluster configuration
		org.apache.hadoop.conf.Configuration hadoopConfiguration = new Configuration();
		String sConfDirectoryPath = "conf/SAIALLabCluster";
		//String sConfDirectoryPath = "conf/SemanticWebLabCluster";
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/core-site.xml"));
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/hdfs-site.xml"));
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/mapred-site.xml"));
		// Create application configuration
		config = edu.utdallas.hadooprdf.conf.Configuration.createInstance(hadoopConfiguration, "/user/test1/hadooprdf");
		config.setNumberOfTaskTrackersInCluster(10); // 5 for semantic web lab, 10 for SAIAL lab
	}

	/**
	 * Test method for {@link edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.NamespaceProcessorPredicateSplitter#processDataForNamespacePredicateSplit()}.
	 */
	@Test
	public void testProcessDataForNamespacePredicateSplit() {
		DataSet ds;
		try {
			ds = new DataSet("/user/test1/hadooprdf/data/LUBM_1-100");
			ds.setOriginalDataFilesExtension("owl");
			NamespaceProcessorPredicateSplitter npps = new NamespaceProcessorPredicateSplitter(ds);
			npps.processDataForNamespacePredicateSplit();
		} catch (IOException e) {
			System.err.println("IOException occurred while testing NamespaceProcessorPredicateSplitter.processDataForNamespacePredicateSplit\n" + e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		} catch (ConfigurationNotInitializedException e) {
			System.err.println("ConfigurationNotInitializedException occurred while testing NamespaceProcessorPredicateSplitter.processDataForNamespacePredicateSplit\n" + e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		} catch (NamespaceProcessorPredicateSplitterException e) {
			System.err.println("NamespaceProcessorPredicateSplitterException occurred while testing NamespaceProcessorPredicateSplitter.processDataForNamespacePredicateSplit\n" + e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
