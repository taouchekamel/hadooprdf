package edu.utdallas.hadooprdf.data.preprocessing.serialization;

import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import edu.utdallas.hadooprdf.data.commons.Tags;
import edu.utdallas.hadooprdf.data.commons.Constants.SerializationFormat;
import edu.utdallas.hadooprdf.data.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.lib.mapred.io.output.FilenameByKeyMultipleTextOutputFormat;
import edu.utdallas.hadooprdf.data.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.preprocessing.lib.PreprocessorJobRunner;

/**
 * A class to convert RDF data from any format other than NTriples to NTriples
 * format
 * 
 * @author Mohammad Farhan Husain
 */
public class ConvertToNTriples extends PreprocessorJobRunner {
	/**
	 * The serialization format the RDF data is in
	 */
	private SerializationFormat m_InputFormat;
	/**
	 * The class constructor
	 * 
	 * @param config
	 *            the configuration to be cloned
	 * @param inputFormat
	 *            the input serialization format
	 * @param inputDirectoryPath
	 *            the input directory, all the files would be read. Directory
	 *            traversal is non-recursive
	 * @param outputDirectoryPath
	 *            the output directory
	 * @throws DataFileExtensionNotSetException 
	 */
	public ConvertToNTriples(SerializationFormat inputFormat, DataSet dataSet) throws DataFileExtensionNotSetException {
		super(dataSet);
		m_InputFormat = inputFormat;
		m_InputDirectoryPath = m_DataSet.getPathToOriginalData();
		m_OutputDirectoryPath = m_DataSet.getPathToNTriplesData();
	}

	/**
	 * The method which actually does the conversion
	 * 
	 * @throws ConversionToNTriplesException
	 * @throws ConfigurationNotInitializedException
	 */
	public void doConversion() throws ConversionToNTriplesException, ConfigurationNotInitializedException {
		if (SerializationFormat.NTRIPLES == m_InputFormat) {
			throw new ConversionToNTriplesException("Input data is already in NTriples format");
		}
		try {
			edu.utdallas.hadooprdf.data.conf.Configuration config =
				edu.utdallas.hadooprdf.data.conf.Configuration.getInstance();
			org.apache.hadoop.conf.Configuration hadoopConfiguration =
				new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
			// that the original one does not get cluttered with job specific key-value pairs
			FileSystem fs;
			fs = FileSystem.get(hadoopConfiguration);
			// Delete output directory
			fs.delete(m_OutputDirectoryPath, true);
			String sInputFormat = SerializationFormat.getSerializationFormatName(m_InputFormat);
			// Must set all the job parameters before creating the job
			hadoopConfiguration.set(Tags.INPUT_SERIALIZATION_FORMAT, sInputFormat);
			hadoopConfiguration.set(Tags.OUTPUT_SERIALIZATION_FORMAT, SerializationFormat.getSerializationFormatName(SerializationFormat.NTRIPLES));
			// Create the job
			String sJobName = sInputFormat + " to NTriples Converter for " + m_InputDirectoryPath.getParent() + '/' + m_InputDirectoryPath.getName();
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
				throw new ConversionToNTriplesException("No file to convert!");
			// Specify output parameters
			job.setOutputFormatClass(FilenameByKeyMultipleTextOutputFormat.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			FileOutputFormat.setOutputPath(job, m_OutputDirectoryPath);
			// Set the mapper and reducer classes
			job.setMapperClass(edu.utdallas.hadooprdf.data.lib.mapred.serialization.conversion.ConversionMapper.class);
			job.setReducerClass(edu.utdallas.hadooprdf.data.lib.mapred.serialization.conversion.ConversionReducer.class);
			// Set the number of reducers
			if (-1 != getNumberOfReducers())	// Use the number set by the client, if any
				job.setNumReduceTasks(getNumberOfReducers());
			else if (-1 != config.getNumberOfTaskTrackersInCluster())	// Use one reducer per TastTracker, if the number of TaskTrackers is available
				job.setNumReduceTasks(config.getNumberOfTaskTrackersInCluster());
			// Set the jar file
			job.setJarByClass(this.getClass());
			// Run the job
			job.waitForCompletion(true);
		} catch (IOException e) {
			throw new ConversionToNTriplesException("IOException occurred:\n" + e.getMessage());
		} catch (InterruptedException e) {
			throw new ConversionToNTriplesException("InterruptedException occurred:\n" + e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new ConversionToNTriplesException("ClassNotFoundException occurred:\n" + e.getMessage());
		}
	}

}
