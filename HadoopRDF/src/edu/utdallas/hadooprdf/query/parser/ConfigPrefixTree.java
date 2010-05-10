package edu.utdallas.hadooprdf.query.parser;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.rdf.uri.prefix.PrefixNamespaceTree;

public class ConfigPrefixTree {

	public static PrefixNamespaceTree getPrefixTree (String confgDirPath, String hadoopDfsPath, int clusterId) throws  
										IOException, ConfigurationNotInitializedException {
		
		PrefixNamespaceTree prefixTree = null;
        // Create cluster configuration
        org.apache.hadoop.conf.Configuration hadoopConfiguration = new Configuration();
        hadoopConfiguration.addResource(new Path(confgDirPath + "/core-site.xml"));
        hadoopConfiguration.addResource(new Path(confgDirPath + "/hdfs-site.xml"));
        hadoopConfiguration.addResource(new Path(confgDirPath + "/mapred-site.xml"));
        
        // Create application configuration
        edu.utdallas.hadooprdf.conf.Configuration config =
            edu.utdallas.hadooprdf.conf.Configuration.createInstance(hadoopConfiguration);
        config.setNumberOfTaskTrackersInCluster(clusterId); // 5 for semantic web lab, 10 for SAIAL lab
        try {
            DataSet ds = new DataSet(hadoopDfsPath);
            ds.setOriginalDataFilesExtension("owl");
            prefixTree = ds.getPrefixNamespaceTree(); // This is the object to use to replace prefixes with namespaces
        } catch (IOException e) {
            System.err.println("IOException occurred while testing PrefixFinder.findPrefixes\n" + e.getMessage());
            throw e; 
        } catch (ConfigurationNotInitializedException e) {
            System.err.println("ConfigurationNotInitializedException occurred while testing PrefixFinder.findPrefixes\n" + e.getMessage());
            throw e;
        }		
		
		return prefixTree;
	}
}
