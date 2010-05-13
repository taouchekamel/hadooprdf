package edu.utdallas.hadooprdf.lib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.commons.Constants;
import edu.utdallas.hadooprdf.data.preprocessing.lib.NamespacePrefixParser;
import edu.utdallas.hadooprdf.data.rdf.uri.prefix.PrefixNamespaceTree;

/**
 * A utility class having miscellaneous methods
 * @author Mohammad Farhan Husain
 *
 */
public class Utility {
	/**
	 * A static method to convert a given predicate to a filename using POS extension
	 * @param sPredicate the predicate to convert
	 * @return the filename
	 */
	public static String convertPredicateToFilename(String sPredicate) {
		return convertPredicateToFilename(sPredicate, Constants.POS_EXTENSION);
	}
	/**
	 * A static method to convert a given predicate to a filename
	 * @param sPredicate the predicate to convert
	 * @param sExtension the extension in the filename
	 * @return the filename
	 */
	public static String convertPredicateToFilename(String sPredicate, String sExtension) {
		char [] chPredicateFilename = sPredicate.toCharArray();
		for (int i = 0; i < chPredicateFilename.length; i++)
			if (!(Character.isLetterOrDigit(chPredicateFilename[i]) || '#' == chPredicateFilename[i]))
				chPredicateFilename[i] = '_';
		StringBuffer sbReturn = new StringBuffer();
		sbReturn.append(chPredicateFilename);
		sbReturn.append('.');
		sbReturn.append(sExtension);
		return sbReturn.toString();
	}
	public static PrefixNamespaceTree getPrefixNamespaceTreeForDataSet(org.apache.hadoop.conf.Configuration hadoopConfiguration,
			Path pathToPrefixFile) throws IOException {
		FileSystem fs = FileSystem.get(hadoopConfiguration);
		FSDataInputStream fsdis = fs.open(pathToPrefixFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fsdis));
		NamespacePrefixParser npp = new NamespacePrefixParser(br.readLine());
		br.close();
		fsdis.close();
		return new PrefixNamespaceTree(npp.getNamespacePrefixes());
	}
	/**
	 * Creates a directory in HDFS
	 * @throws IOException
	 * @throws ConfigurationNotInitializedException
	 */
	public static void createDirectory(org.apache.hadoop.conf.Configuration hadoopConfiguration, Path path) throws IOException {
		FileSystem.get(hadoopConfiguration).mkdirs(path);
	}
}
