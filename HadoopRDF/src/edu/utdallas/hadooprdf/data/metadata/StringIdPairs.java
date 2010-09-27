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
 * @author Mohammad Farhan Husain
 *
 */
public abstract class StringIdPairs {
	/**
	 * A map from id to string
	 */
	protected Map<Long, String> idToStringMap;
	/**
	 * A map from string to id
	 */
	protected Map<String, Long> stringToIdMap;
	
	/**
	 * The class constructor
	 * @param dataSet the data set object containing the path to string list
	 * @throws StringIdPairsException
	 */
	public StringIdPairs(org.apache.hadoop.conf.Configuration hadoopConfiguration, Path stringList) throws StringIdPairsException {
		idToStringMap = new HashMap<Long, String> ();
		stringToIdMap = new HashMap<String, Long> ();
		parseStringListFile(hadoopConfiguration, stringList);
	}

	/**
	 * Parse the string list file and populate the maps
	 * @param hadoopConfiguration the hadoop configuration needed to access file system
	 * @param predicateList the path to string list file
	 * @throws StringIdPairsException
	 */
	private void parseStringListFile(org.apache.hadoop.conf.Configuration hadoopConfiguration, Path stringList) throws StringIdPairsException {
		try {
			DataInputStream dis = FileSystem.get(hadoopConfiguration).open(stringList);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			String line;
			while (null != (line = br.readLine())) {
				String [] splits = line.split("\\s");
				Long id = Long.parseLong(splits[0]);
				idToStringMap.put(id, splits[1]);
				stringToIdMap.put(splits[1], id);
			}
			br.close();
			dis.close();
		} catch (IOException e) {
			throw new StringIdPairsException("IOException occurred while trying to parse string list file:\n" + e.getMessage());
		} catch (NumberFormatException e) {
			throw new StringIdPairsException("NumberFormatException occurred while trying to parse string list file:\n" + e.getMessage());
		}

	}
}
