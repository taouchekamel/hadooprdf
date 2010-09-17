package edu.utdallas.hadooprdf.data.preprocessing.dictionary;

import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.preprocessing.lib.PreprocessorJobRunner;

/**
 * A class which creates dictionary from NTriples data
 * 
 * @author Mohammad Farhan Husain
 */
public class DictionaryCreator extends PreprocessorJobRunner {

	/**
	 * The class constructor
	 * @param dataSet the data set to work on
	 * @throws DataFileExtensionNotSetException
	 * @throws IOException
	 */
	public DictionaryCreator(DataSet dataSet)
			throws DataFileExtensionNotSetException, IOException {
		super(dataSet);
		m_InputDirectoryPath = m_DataSet.getPathToNTriplesData();
		m_OutputDirectoryPath = m_DataSet.getPathToDictionary();
	}
	
	public void createDictionary() throws DictionaryCreatorException, ConfigurationNotInitializedException {
		try {
			edu.utdallas.hadooprdf.conf.Configuration config =
				edu.utdallas.hadooprdf.conf.Configuration.getInstance();
			org.apache.hadoop.conf.Configuration hadoopConfiguration =
				new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
			// that the original one does not get cluttered with job specific key-value pairs
			FileSystem fs;
			fs = FileSystem.get(hadoopConfiguration);
			// Delete output directory
			fs.delete(m_OutputDirectoryPath, true);
			// Create the job
			String sJobName = "Dictionary creator for " + m_InputDirectoryPath.getParent() + '/' + m_InputDirectoryPath.getName();
			Job job = new Job(hadoopConfiguration, sJobName);
			// Specify input parameters
			job.setInputFormatClass(TextInputFormat.class);
			boolean bInputPathEmpty = true;
			// Get input file names
			FileStatus [] fstatus = fs.listStatus(m_InputDirectoryPath, new PathFilter() {
				@Override
				public boolean accept(Path path) {
					return path.getName().toLowerCase().endsWith(m_sInputFilesExtension);
				}
			});
			for (int i = 0; i < fstatus.length; i++) {
				if (!fstatus[i].isDir()) {
					FileInputFormat.addInputPath(job, fstatus[i].getPath());
					bInputPathEmpty = false;
				}
			}
			if (bInputPathEmpty)
				throw new DictionaryCreatorException("No file to create dictionary from!");
			// Specify output parameters
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(LongWritable.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(IntWritable.class);
			FileOutputFormat.setOutputPath(job, m_OutputDirectoryPath);
			// Set the mapper and reducer classes
			job.setMapperClass(edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred.DictionaryCreatorMapper.class);
			job.setReducerClass(edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred.DictionaryCreatorReducer.class);
			// Set the number of reducers
			if (-1 != getNumberOfReducers())	// Use the number set by the client, if any
				job.setNumReduceTasks(getNumberOfReducers());
			else if (-1 != config.getNumberOfTaskTrackersInCluster())	// Use one reducer per TastTracker, if the number of TaskTrackers is available
				job.setNumReduceTasks(10 * config.getNumberOfTaskTrackersInCluster()); // 10 reducers per node
			// Set the jar file
			job.setJarByClass(this.getClass());
			// Run the job
			job.waitForCompletion(true);
		} catch (IOException e) {
			throw new DictionaryCreatorException("IOException occurred:\n" + e.getMessage());
		} catch (InterruptedException e) {
			throw new DictionaryCreatorException("InterruptedException occurred:\n" + e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new DictionaryCreatorException("ClassNotFoundException occurred:\n" + e.getMessage());
		}
	}


}
