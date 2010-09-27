/**
 * 
 */
package edu.utdallas.hadooprdf.data.metadata;

import java.util.Set;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class TypeIdPairs extends StringIdPairs {
	
	/**
	 * The class constructor
	 * @param dataSet the data set object containing the path to type list
	 * @throws StringIdPairsException
	 */
	public TypeIdPairs(final DataSet dataSet) throws StringIdPairsException {
		super(dataSet.getHadoopConfiguration(), dataSet.getPathToTypeList());
	}
	
	/**
	 * Gets the id for the Type
	 * @param type the type for which id is to be retrieved
	 * @return the id or null if no id exists for the type
	 */
	public Long getId(String type) {
		return stringToIdMap.get(type);
	}
	
	/**
	 * Gets the type for the id
	 * @param id the id for which type is to be retrieved
	 * @return the type or null if no type exists for the id
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
