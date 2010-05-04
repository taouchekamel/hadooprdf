package edu.utdallas.hadooprdf.lib.util;

import edu.utdallas.hadooprdf.commons.Constants;

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
}
