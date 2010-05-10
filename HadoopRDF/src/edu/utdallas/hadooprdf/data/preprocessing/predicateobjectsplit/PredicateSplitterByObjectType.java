package edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit;

import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import edu.utdallas.hadooprdf.data.commons.Constants;
import edu.utdallas.hadooprdf.data.commons.Tags;
import edu.utdallas.hadooprdf.data.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.lib.mapred.io.output.FilenameByKeyMultipleTextOutputFormat;
import edu.utdallas.hadooprdf.data.lib.util.PathFilterOnFilenameExtension;
import edu.utdallas.hadooprdf.data.lib.util.Utility;
import edu.utdallas.hadooprdf.data.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.preprocessing.lib.PreprocessorJobRunner;

/**
 * A class which runs a job to split PS files further according to the type of objects
 * @author Mohammad Farhan Husain
 *
 */
public class PredicateSplitterByObjectType extends PreprocessorJobRunner {
	private Path m_Job1OutputDirectoryPath;
	private Path m_Job2OutputDirectoryPath;
	/**
	 * The class constructor
	 * @param dataSet the dataset to work on
	 * @throws DataFileExtensionNotSetException
	 */
	public PredicateSplitterByObjectType(DataSet dataSet)
			throws DataFileExtensionNotSetException {
		super(dataSet);
		m_InputDirectoryPath = m_DataSet.getPathToPSData();
		m_Job1OutputDirectoryPath = new Path(m_DataSet.getPathToTemp(), Constants.POS_EXTENSION + 1);
		m_Job2OutputDirectoryPath = new Path(m_DataSet.getPathToTemp(), Constants.POS_EXTENSION + 2);
		m_sInputFilesExtension = Constants.PS_EXTENSION;
	}
	/**
	 * The method which actually splits PS files according to their object type
	 * @throws ConfigurationNotInitializedException
	 * @throws PredicateSplitterByObjectTypeException
	 */
	public void splitPredicateByObjectType() throws ConfigurationNotInitializedException, PredicateSplitterByObjectTypeException {
		try {
			edu.utdallas.hadooprdf.data.conf.Configuration config =
				edu.utdallas.hadooprdf.data.conf.Configuration.getInstance();
			org.apache.hadoop.conf.Configuration hadoopConfiguration =
				new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
			// that the original one does not get cluttered with job specific key-value pairs
			FileSystem fs = FileSystem.get(hadoopConfiguration);
			runJob1(config, hadoopConfiguration, fs);
			runJob2(config, hadoopConfiguration, fs);
		} catch (IOException e) {
			throw new PredicateSplitterByObjectTypeException("IOException occurred:\n" + e.getMessage());
		}
	}
	/**
	 * Runs job 1 of the process
	 * @param config the HadoopRDF configuration
	 * @param hadoopConfiguration the Hadoop configuration
	 * @param fs the file system
	 * @throws ConfigurationNotInitializedException
	 * @throws PredicateSplitterByObjectTypeException
	 */
	private void runJob1(edu.utdallas.hadooprdf.data.conf.Configuration config,
			org.apache.hadoop.conf.Configuration hadoopConfiguration,
			FileSystem fs) throws ConfigurationNotInitializedException, PredicateSplitterByObjectTypeException {
		try {
			// Delete output directory
			fs.delete(m_Job1OutputDirectoryPath, true);
			// Must set all the job parameters before creating the job
			hadoopConfiguration.set(Tags.RDF_TYPE_FILENAME, 
					Utility.convertPredicateToFilename(
							Utility.getPrefixNamespaceTreeForDataSet(hadoopConfiguration, m_DataSet.getPathToPrefixFile()).matchAndReplacePrefix(Constants.RDF_TYPE_URI),
							Constants.PS_EXTENSION));
			// Create the job
			String sJobName = "PS file splitter by Object Types Job1 for " + m_InputDirectoryPath;
			Job job = new Job(hadoopConfiguration, sJobName);
			// Specify input parameters
			job.setInputFormatClass(TextInputFormat.class);
			boolean bInputPathEmpty = true;
			// Get input file names
			FileStatus [] fstatus = fs.listStatus(m_InputDirectoryPath, new PathFilterOnFilenameExtension(m_sInputFilesExtension));
			for (int i = 0; i < fstatus.length; i++) {
				if (!fstatus[i].isDir()) {
					FileInputFormat.addInputPath(job, fstatus[i].getPath());
					bInputPathEmpty = false;
				}
			}
			if (bInputPathEmpty)
				throw new PredicateSplitterByObjectTypeException("No PS file to split by object type!");
			// Specify output parameters
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			FileOutputFormat.setOutputPath(job, m_Job1OutputDirectoryPath);
			// Set the mapper and reducer classes
			job.setMapperClass(edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.mapred.PredicateSplitterByObjectTypeJob1Mapper.class);
			job.setReducerClass(edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.mapred.PredicateSplitterByObjectTypeJob1Reducer.class);
			// Set the number of reducers
			if (-1 != getNumberOfReducers())	// Use the number set by the client, if any
				job.setNumReduceTasks(getNumberOfReducers());
			else if (-1 != config.getNumberOfTaskTrackersInCluster())	// Use one reducer per TastTracker, if the number of TaskTrackers is available
				job.setNumReduceTasks(config.getNumberOfTaskTrackersInCluster());
			// Set the jar file
			job.setJarByClass(this.getClass());
			// Run the job
			if (job.waitForCompletion(true))
				fs.delete(m_InputDirectoryPath, true);	// Delete input data i.e. PS data
			else
				throw new PredicateSplitterByObjectTypeException("Job1 of PredicateSplitterByObjectType failed");
		} catch (IOException e) {
			throw new PredicateSplitterByObjectTypeException("IOException occurred:\n" + e.getMessage());
		} catch (InterruptedException e) {
			throw new PredicateSplitterByObjectTypeException("InterruptedException occurred:\n" + e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new PredicateSplitterByObjectTypeException("ClassNotFoundException occurred:\n" + e.getMessage());
		}
	}
	/**
	 * Runs job 2 of the process
	 * @param config the HadoopRDF configuration
	 * @param hadoopConfiguration the Hadoop configuration
	 * @param fs the file system
	 * @throws ConfigurationNotInitializedException
	 * @throws PredicateSplitterByObjectTypeException
	 */
	private void runJob2(edu.utdallas.hadooprdf.data.conf.Configuration config,
			org.apache.hadoop.conf.Configuration hadoopConfiguration,
			FileSystem fs) throws ConfigurationNotInitializedException, PredicateSplitterByObjectTypeException {
		try {
			// Delete output directory
			fs.delete(m_Job2OutputDirectoryPath, true);
			// Create the job
			String sJobName = "PS file splitter by Object Types Job2 for " + m_InputDirectoryPath;
			Job job = new Job(hadoopConfiguration, sJobName);
			// Specify input parameters
			job.setInputFormatClass(TextInputFormat.class);
			boolean bInputPathEmpty = true;
			// Get input file names
			FileStatus [] fstatus = fs.listStatus(m_Job1OutputDirectoryPath);
			for (int i = 0; i < fstatus.length; i++) {
				if (!fstatus[i].isDir()) {
					FileInputFormat.addInputPath(job, fstatus[i].getPath());
					bInputPathEmpty = false;
				}
			}
			if (bInputPathEmpty)
				throw new PredicateSplitterByObjectTypeException("No file to process in Job2 for PredicateSplitterByObjectType!");
			// Specify output parameters
			job.setOutputFormatClass(FilenameByKeyMultipleTextOutputFormat.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			FileOutputFormat.setOutputPath(job, m_Job2OutputDirectoryPath);
			// Set the mapper and reducer classes
			job.setMapperClass(edu.utdallas.hadooprdf.data.preprocessing.predicateobjectsplit.mapred.PredicateSplitterByObjectTypeJob2Mapper.class);
			job.setReducerClass(edu.utdallas.hadooprdf.data.lib.mapred.IdentityReducer.class);
			// Set the number of reducers
			if (-1 != getNumberOfReducers())	// Use the number set by the client, if any
				job.setNumReduceTasks(getNumberOfReducers());
			else if (-1 != config.getNumberOfTaskTrackersInCluster())	// Use one reducer per TastTracker, if the number of TaskTrackers is available
				job.setNumReduceTasks(config.getNumberOfTaskTrackersInCluster());
			// Set the jar file
			job.setJarByClass(this.getClass());
			// Run the job
			if (job.waitForCompletion(true)) {
				// Delete output of job 1
				fs.delete(m_Job1OutputDirectoryPath, true);
				// Create POS data directory
				fs.mkdirs(m_DataSet.getPathToPOSData());
				// Move data from this job's output directory to POS data directory
				fstatus = fs.listStatus(m_Job2OutputDirectoryPath, new PathFilterOnFilenameExtension(Constants.POS_EXTENSION));
				for (int i = 0; i < fstatus.length; i++)
					fs.rename(fstatus[i].getPath(), new Path(m_DataSet.getPathToPOSData(), fstatus[i].getPath().getName()));
				// Delete output of this job
				fs.delete(m_Job2OutputDirectoryPath, true);
			}
			else
				throw new PredicateSplitterByObjectTypeException("Job2 of PredicateSplitterByObjectType failed");
		} catch (IOException e) {
			throw new PredicateSplitterByObjectTypeException("IOException occurred:\n" + e.getMessage());
		} catch (InterruptedException e) {
			throw new PredicateSplitterByObjectTypeException("InterruptedException occurred:\n" + e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new PredicateSplitterByObjectTypeException("ClassNotFoundException occurred:\n" + e.getMessage());
		}
	}
}
