/**
 * 
 */
package edu.utdallas.hadooprdf.data.metadata;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * A class containing two maps from/to predicate string to/from id
 * @author Mohammad Farhan Husain
 *
 */
public class PredicateIdPairs {
	/**
	 * A map from id to predicate string
	 */
	private Map<Long, String> idToPredicateMap;
	/**
	 * A map from predicate string to id
	 */
	private Map<String, Long> predicateToIdMap;
	
	/**
	 * The class constructor
	 * @param dataSet the data set object containing the path to predicate list
	 * @throws PredicateIdPairsException
	 */
	public PredicateIdPairs(final DataSet dataSet) throws PredicateIdPairsException {
		idToPredicateMap = new HashMap<Long, String> ();
		predicateToIdMap = new HashMap<String, Long> ();
		parsePredicateListFile(dataSet.getHadoopConfiguration(), dataSet.getPathToPredicateList());
	}
	
	/**
	 * Parse the predicate list file and populate the maps
	 * @param hadoopConfiguration the hadoop configuration needed to access file system
	 * @param predicateList the path to predicate list file
	 * @throws PredicateIdPairsException
	 */
	private void parsePredicateListFile(org.apache.hadoop.conf.Configuration hadoopConfiguration, Path predicateList) throws PredicateIdPairsException {
		try {
			DataInputStream dis = FileSystem.get(hadoopConfiguration).open(predicateList);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			String line;
			while (null != (line = br.readLine())) {
				String [] splits = line.split("\\s");
				Long id = Long.parseLong(splits[0]);
				idToPredicateMap.put(id, splits[1]);
				predicateToIdMap.put(splits[1], id);
			}
			br.close();
			dis.close();
		} catch (IOException e) {
			throw new PredicateIdPairsException("IOException occurred while trying to parse predicate list file:\n" + e.getMessage());
		} catch (NumberFormatException e) {
			throw new PredicateIdPairsException("NumberFormatException occurred while trying to parse predicate list file:\n" + e.getMessage());
		}

	}
	
	/**
	 * Gets the id for the predicate
	 * @param predicate the predicate for which id is to be retrieved
	 * @return the id or null if no id exists for the predicate
	 */
	public Long getId(String predicate) {
		return predicateToIdMap.get(predicate);
	}
	
	/**
	 * Gets the predicate for the id
	 * @param id the id for which predicate is to be retrieved
	 * @return the predicate or null if no predicate exists for the id
	 */
	public String getPredicate(long id) {
		return idToPredicateMap.get(id);
	}
}
