package edu.utdallas.hadooprdf.lib.util;

import edu.utdallas.hadooprdf.query.jobrunner.GenericMapper;
import edu.utdallas.hadooprdf.query.jobrunner.GenericReducer;

/**
 * A utility class that defines the various parameters needed in a Hadoop Job 
 * @author sharath, vaibhav
 *
 */
public class JobParameters 
{
	/** The directory that stores the various hadoop configuration files **/
	public static final String configFileDir = "/home/hadoop/sharath/hadooprdf/hadooprdf/conf/SemanticWebLabCluster";
	
	/** The location of the jar file that will be used in a Hadoop Job **/
	public static final String jarFile = "/home/hadoop/HadoopRDF.jar";
	
	/** The generic mapper class used in any Hadoop Job **/
	public static final Class<GenericMapper> mapperClass = edu.utdallas.hadooprdf.query.jobrunner.GenericMapper.class;

	/** The generic mapper class used in any Hadoop Job **/
	public static final Class<GenericReducer> reducerClass = edu.utdallas.hadooprdf.query.jobrunner.GenericReducer.class;
	
	/** The number of mappers to be used in a Hadoop Job **/
	public static final int numOfMappers = 10;

	/** The number of reducers to be used in a Hadoop Job **/
	public static final int numOfReducers = 1;	
}
