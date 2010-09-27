package edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.test;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.metadata.StringIdPairsException;
import edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.PredicateSplitterByObjectType;
import edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.PredicateSplitterByObjectTypeException;

public class PredicateSplitterByObjectTypeTest {
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

	@Test
	public void testSplitPredicateByObjectType() {
		try {
			DataSet ds = new DataSet("/user/farhan/hadooprdf/LUBM1");
			ds.setOriginalDataFilesExtension("owl");
			PredicateSplitterByObjectType psbot = new PredicateSplitterByObjectType(ds);
			psbot.splitPredicateByObjectType();
		} catch (IOException e) {
			System.err.println("IOException occurred while testing PrefixFinder.findPrefixes\n" + e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		} catch (ConfigurationNotInitializedException e) {
			System.err.println("ConfigurationNotInitializedException occurred while testing PrefixFinder.findPrefixes\n" + e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		} catch (DataFileExtensionNotSetException e) {
			System.err.println("DataFileExtensionNotSetException occurred while testing PrefixFinder.findPrefixes\n" + e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		} catch (PredicateSplitterByObjectTypeException e) {
			System.err.println("PrefixFinderException occurred while testing PrefixFinder.findPrefixes\n" + e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		} catch (StringIdPairsException e) {
			System.err.println("PredicateIdPairsException occurred while testing PrefixFinder.findPrefixes\n" + e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
