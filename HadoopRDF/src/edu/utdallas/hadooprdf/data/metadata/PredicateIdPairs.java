/**
 * 
 */
package edu.utdallas.hadooprdf.data.metadata;

import java.util.Set;

/**
 * A class containing two maps from/to predicate string to/from id
 * @author Mohammad Farhan Husain
 *
 */
public class PredicateIdPairs extends StringIdPairs {
	
	/**
	 * The class constructor
	 * @param dataSet the data set object containing the path to predicate list
	 * @throws StringIdPairsException
	 */
	public PredicateIdPairs(final DataSet dataSet) throws StringIdPairsException {
		super(dataSet.getHadoopConfiguration(), dataSet.getPathToPredicateList());
	}
	
	/**
	 * Gets the id for the predicate
	 * @param predicate the predicate for which id is to be retrieved
	 * @return the id or null if no id exists for the predicate
	 */
	public Long getId(String predicate) {
		return stringToIdMap.get(predicate);
	}
	
	/**
	 * Gets the predicate for the id
	 * @param id the id for which predicate is to be retrieved
	 * @return the predicate or null if no predicate exists for the id
	 */
	public String getPredicate(long id) {
		return idToStringMap.get(id);
	}
	
	public Set<Long> getPredicateIds() {
		return idToStringMap.keySet();
	}
	
	public Set<String> getPredicateStrings() {
		return stringToIdMap.keySet();
	}
}
