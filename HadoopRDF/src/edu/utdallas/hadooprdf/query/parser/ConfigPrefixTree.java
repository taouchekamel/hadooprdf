package edu.utdallas.hadooprdf.query.parser;

import java.io.IOException;

import edu.utdallas.hadooprdf.conf.Configuration;
import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.metadata.DataSetException;
import edu.utdallas.hadooprdf.data.rdf.uri.prefix.PrefixNamespaceTree;

public class ConfigPrefixTree 
{
	public static PrefixNamespaceTree getPrefixTree( DataSet ds, int clusterId ) 
	throws IOException, ConfigurationNotInitializedException, DataSetException 
	{	
		PrefixNamespaceTree prefixTree = null;
        
        // Create application configuration
        Configuration config = Configuration.getInstance();
        config.setNumberOfTaskTrackersInCluster( clusterId ); // 5 for semantic web lab, 10 for SAIAL lab
        ds.setOriginalDataFilesExtension("owl");
		prefixTree = ds.getPrefixNamespaceTree(); // This is the object to use to replace prefixes with namespaces		
		
		return prefixTree;
	}
}
