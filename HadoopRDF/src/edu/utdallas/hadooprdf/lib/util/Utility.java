package edu.utdallas.hadooprdf.lib.util;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import edu.utdallas.hadooprdf.data.commons.Constants;

/**
 * A utility class having miscellaneous methods
 * @author Mohammad Farhan Husain
 *
 */
public class Utility 
{
	/**
	 * A static method to convert a given predicate to a filename using POS extension
	 * @param sPredicate the predicate to convert
	 * @return the filename
	 */
	public static String convertPredicateToFilename(String sPredicate) 
	{
		return convertPredicateToFilename(sPredicate, Constants.POS_EXTENSION);
	}
	
	/**
	 * A static method to convert a given predicate to a filename
	 * @param sPredicate the predicate to convert
	 * @param sExtension the extension in the filename
	 * @return the filename
	 */
	public static String convertPredicateToFilename(String sPredicate, String sExtension) 
	{
		char [] chPredicateFilename = sPredicate.toCharArray();
		for (int i = 0; i < chPredicateFilename.length; i++)
			if (!(Character.isLetterOrDigit(chPredicateFilename[i]) || '#' == chPredicateFilename[i]))
				chPredicateFilename[i] = '_';
		StringBuilder sbReturn = new StringBuilder();
		sbReturn.append(chPredicateFilename);
		sbReturn.append('.');
		sbReturn.append(sExtension);
		return sbReturn.toString();
	}
	
	/**
	 * Creates a directory in HDFS
	 * @param hadoopConfiguration the Hadoop Configuration of the cluster
	 * @param path the path to the directory
	 * @throws IOException
	 */
	public static void createDirectory(org.apache.hadoop.conf.Configuration hadoopConfiguration, Path path) throws IOException {
		FileSystem.get(hadoopConfiguration).mkdirs(path);
	}
	/**
	 * Create a directory in HDFS if it does not already exist
	 * @param hadoopConfiguration the Hadoop Configuration of the cluster
	 * @param path the path to the directory
	 * @throws IOException
	 */
	public static void createDirectoryIfNotExists(org.apache.hadoop.conf.Configuration hadoopConfiguration, Path path) throws IOException {
		if (!FileSystem.get(hadoopConfiguration).exists(path))
			createDirectory(hadoopConfiguration, path);
	}
	/**
	 * A static method to remove "#" from given String and replace it with "$"
	 * @param pFileName input String containing #
	 * @return String replaced with $
	 */
	public static String replaceHash(String pFileName) 
	{
		if(pFileName != null){
			return pFileName.replace("#", "$");
		} else {
			return null;
		}
	}
	/**
	 * The method determines the number of bits required to store
	 * any number less than or equal to <i>n</i> in binary
	 * @param n the maximum value for which number of bits needs to be determined
	 * @return number of bits requred to store
	 */
	public static int getMaxBitsRequiredToStore(int n) {
		int i;
		for (i = 0; n != 0; n >>= 1, i++);
		return i;
	}
	
	/**
	 * Checks if a string is empty
	 * @param s the string to check
	 * @return true if it is null or has zero length, otherwise false
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
}
