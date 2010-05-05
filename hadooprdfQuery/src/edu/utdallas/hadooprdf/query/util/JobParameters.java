package edu.utdallas.hadooprdf.query.util;

/**
 * A utility class that defines the various parameters needed in a Hadoop Job 
 * @author sharath, vaibhav
 *
 */
public class JobParameters 
{
	/** The directory that stores the various hadoop configuration files **/
	public static final String configFileDir = "/home/vaibhav/pkg/hadoop-0.20.2/conf/";
	
	/** The location of the jar file that will be used in a Hadoop Job **/
	public static final String jarFile = "/home/vaibhav/HadoopRDF/HadoopRDF.jar";
	
	/** The generic mapper class used in any Hadoop Job **/
	public static final String mapperClass = "edu.utdallas.hadooprdf.query.jobrunner.GenericJobRunner.GenericMapper";

	/** The generic mapper class used in any Hadoop Job **/
	public static final String reducerClass = "edu.utdallas.hadooprdf.query.jobrunner.GenericJobRunner.GenericReducer";
	
	/** The number of mappers to be used in a Hadoop Job **/
	public static final int numOfMappers = 10;

	/** The number of reducers to be used in a Hadoop Job **/
	public static final int numOfReducers = 1;
	
	/** The HDFS directory that contains all the input files **/
	public static final String inputHDFSDir = "HadoopRDF/input/";
	
	/** The HDFS directory that contains the output files **/
	public static final String outputHDFSDir = "HadoopRDF/output/";	
}
