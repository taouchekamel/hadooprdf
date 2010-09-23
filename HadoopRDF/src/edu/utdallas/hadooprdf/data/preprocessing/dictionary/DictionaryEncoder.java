/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.dictionary;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.SubjectObjectPair;
import edu.utdallas.hadooprdf.data.commons.Tags;
import edu.utdallas.hadooprdf.data.io.output.SOPOutputFormat;
import edu.utdallas.hadooprdf.data.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.preprocessing.lib.PreprocessorJobRunner;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class DictionaryEncoder extends PreprocessorJobRunner {

	/**
	 * The path to dictionary
	 */
	private Path pathToDictionary;
	private Path pathToSubjectEncodingOutput;
	private Path pathToPredicateEncodingOutput;
	private Path pathToObjectEncodingOutput;
	
	public DictionaryEncoder(DataSet dataSet)
			throws DataFileExtensionNotSetException {
		super(dataSet);
		inputDirectoryPath = dataSet.getPathToNTriplesData();
		pathToDictionary = dataSet.getPathToDictionary();
		pathToSubjectEncodingOutput = new Path(dataSet.getPathToTemp(), "encoder1");
		pathToPredicateEncodingOutput = new Path(dataSet.getPathToTemp(), "encoder2");
		pathToObjectEncodingOutput = new Path(dataSet.getPathToTemp(), "encoder3");
		outputDirectoryPath = dataSet.getPathToPSData();
	}

	public void dictionaryEncode() throws DictionaryEncoderException {
		encodeSubject();
		encodePredicate();
		encodeObject();
		convertToBinaryAndPredicateSplit();
		listPredicates();
	}
	
	private void listPredicates() throws DictionaryEncoderException {
		edu.utdallas.hadooprdf.conf.Configuration config;
		try {
			config = edu.utdallas.hadooprdf.conf.Configuration.getInstance();
			org.apache.hadoop.conf.Configuration hadoopConfiguration =
				new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
			// that the original one does not get cluttered with job specific key-value pairs
			// Must set all the job parameters before creating the job
			hadoopConfiguration.set(Tags.PATH_TO_DICTIONARY, pathToDictionary.toString());
			FileSystem fs;
			fs = FileSystem.get(hadoopConfiguration);
			// Create the job
			String sJobName = "Listing predicates for " + outputDirectoryPath.toString();
			Job job = new Job(hadoopConfiguration, sJobName);
			// Specify input parameters
			job.setInputFormatClass(TextInputFormat.class);
			// Get input file names from previous phase output
			FileStatus [] fstatus = fs.listStatus(outputDirectoryPath);
			List<String> predicates = new LinkedList<String> ();
			String filename;
			for (int i = 0; i < fstatus.length; i++) {
				if (!fstatus[i].isDir()) {
					filename = fstatus[i].getPath().getName();
					predicates.add(filename.substring(0, filename.indexOf('.')));
				}
			}
			Path inputPath = new Path(dataSet.getPathToTemp(), "predicateInput");
			fs.delete(inputPath, true);
			DataOutputStream dos = fs.create(new Path(inputPath, "predicates"), true);
			for (String predicate : predicates)
				dos.writeBytes(predicate + '\n');
			dos.close();
			FileInputFormat.addInputPath(job, inputPath);
			// Get input file names from dictionary directory
			boolean bInputPathEmpty = true;
			fstatus = fs.listStatus(pathToDictionary);
			for (int i = 0; i < fstatus.length; i++) {
				if (!fstatus[i].isDir()) {
					FileInputFormat.addInputPath(job, fstatus[i].getPath());
					bInputPathEmpty = false;
				}
			}
			if (bInputPathEmpty)
				throw new DictionaryEncoderException("No dictionary file to use for listing predicates!");
			// Specify output parameters
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			// Delete output directory
			Path tmpOutputPath = new Path(dataSet.getPathToTemp(), "predicatesOutput");
			fs.delete(tmpOutputPath, true);
			FileOutputFormat.setOutputPath(job, tmpOutputPath);
			// Set the mapper and reducer classes
			job.setMapperClass(edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred.PredicateListerMapper.class);
			job.setReducerClass(edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred.PredicateListerReducer.class);
			// Set the number of reducers
			job.setNumReduceTasks(1);
			// Set the jar file
			job.setJarByClass(this.getClass());
			// Run the job
			if (job.waitForCompletion(true)) {
				fs.delete(inputPath, true);
				fs.delete(dataSet.getPathToPredicateList(), false);
				fs.rename(new Path(tmpOutputPath, "part-r-00000"), dataSet.getPathToPredicateList());
				fs.delete(tmpOutputPath, true);
			} else
				throw new DictionaryEncoderException("" + sJobName + " failed");
		} catch (ConfigurationNotInitializedException e) {
			throw new DictionaryEncoderException("ConfigurationNotInitializedException occurred:\n" + e.getMessage());
		} catch (IOException e) {
			throw new DictionaryEncoderException("IOException occurred:\n" + e.getMessage());
		} catch (InterruptedException e) {
			throw new DictionaryEncoderException("InterruptedException occurred:\n" + e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new DictionaryEncoderException("ClassNotFoundException occurred:\n" + e.getMessage());
		}
	}
	
	private void convertToBinaryAndPredicateSplit() throws DictionaryEncoderException {
		edu.utdallas.hadooprdf.conf.Configuration config;
		try {
			config = edu.utdallas.hadooprdf.conf.Configuration.getInstance();
			org.apache.hadoop.conf.Configuration hadoopConfiguration =
				new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
			// that the original one does not get cluttered with job specific key-value pairs
			FileSystem fs;
			fs = FileSystem.get(hadoopConfiguration);
			// Delete output directory
			fs.delete(outputDirectoryPath, true);
			// Create the job
			String sJobName = "Binary encoding and predicate splitting for " + inputDirectoryPath.toString();
			Job job = new Job(hadoopConfiguration, sJobName);
			// Specify input parameters
			job.setInputFormatClass(TextInputFormat.class);
			boolean bInputPathEmpty = true;
			// Get input file names from previous phase output
			FileStatus [] fstatus = fs.listStatus(pathToObjectEncodingOutput);
			for (int i = 0; i < fstatus.length; i++) {
				if (!fstatus[i].isDir()) {
					FileInputFormat.addInputPath(job, fstatus[i].getPath());
					bInputPathEmpty = false;
				}
			}
			if (bInputPathEmpty)
				throw new DictionaryEncoderException("No file to binary encode and predicate split from encoder phase 3 output!");
			// Specify output parameters
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(SubjectObjectPair.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputFormatClass(SOPOutputFormat.class);
			FileOutputFormat.setOutputPath(job, outputDirectoryPath);
			// Set the mapper and reducer classes
			job.setMapperClass(edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred.BinaryEncoderPredicateSplitterMapper.class);
			job.setReducerClass(edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred.BinaryEncoderPredicateSplitterReducer.class);
			// Set the number of reducers
			if (-1 != getNumberOfReducers())	// Use the number set by the client, if any
				job.setNumReduceTasks(getNumberOfReducers());
			else if (-1 != config.getNumberOfTaskTrackersInCluster())	// Use one reducer per TastTracker, if the number of TaskTrackers is available
				job.setNumReduceTasks(5 * config.getNumberOfTaskTrackersInCluster()); // 5 reducers per node
			// Set the jar file
			job.setJarByClass(this.getClass());
			// Run the job
			if (job.waitForCompletion(true)) {
				fs.delete(pathToObjectEncodingOutput, true);
			} else
				throw new DictionaryEncoderException("" + sJobName + " failed");
		} catch (ConfigurationNotInitializedException e) {
			throw new DictionaryEncoderException("ConfigurationNotInitializedException occurred:\n" + e.getMessage());
		} catch (IOException e) {
			throw new DictionaryEncoderException("IOException occurred:\n" + e.getMessage());
		} catch (InterruptedException e) {
			throw new DictionaryEncoderException("InterruptedException occurred:\n" + e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new DictionaryEncoderException("ClassNotFoundException occurred:\n" + e.getMessage());
		}
	}
	
	private void encodeObject() throws DictionaryEncoderException {
		edu.utdallas.hadooprdf.conf.Configuration config;
		try {
			config = edu.utdallas.hadooprdf.conf.Configuration.getInstance();
			org.apache.hadoop.conf.Configuration hadoopConfiguration =
				new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
			// that the original one does not get cluttered with job specific key-value pairs
			// Must set all the job parameters before creating the job
			hadoopConfiguration.set(Tags.PATH_TO_DICTIONARY, pathToDictionary.toString());
			FileSystem fs;
			fs = FileSystem.get(hadoopConfiguration);
			// Delete output directory
			fs.delete(pathToObjectEncodingOutput, true);
			// Create the job
			String sJobName = "Dictionary encoder phase 3 for " + inputDirectoryPath.toString();
			Job job = new Job(hadoopConfiguration, sJobName);
			// Specify input parameters
			job.setInputFormatClass(TextInputFormat.class);
			boolean bInputPathEmpty = true;
			// Get input file names from previous phase output
			FileStatus [] fstatus = fs.listStatus(pathToPredicateEncodingOutput);
			for (int i = 0; i < fstatus.length; i++) {
				if (!fstatus[i].isDir()) {
					FileInputFormat.addInputPath(job, fstatus[i].getPath());
					bInputPathEmpty = false;
				}
			}
			if (bInputPathEmpty)
				throw new DictionaryEncoderException("No file to encode from encoder phase 2 output!");
			// Get input file names from dictionary directory
			bInputPathEmpty = true;
			fstatus = fs.listStatus(pathToDictionary);
			for (int i = 0; i < fstatus.length; i++) {
				if (!fstatus[i].isDir()) {
					FileInputFormat.addInputPath(job, fstatus[i].getPath());
					bInputPathEmpty = false;
				}
			}
			if (bInputPathEmpty)
				throw new DictionaryEncoderException("No dictionary file to use for encoding!");
			// Specify output parameters
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			FileOutputFormat.setOutputPath(job, pathToObjectEncodingOutput);
			// Set the mapper and reducer classes
			job.setMapperClass(edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred.DictionaryEncoderMapper3.class);
			job.setReducerClass(edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred.DictionaryEncoderReducer3.class);
			// Set the number of reducers
			if (-1 != getNumberOfReducers())	// Use the number set by the client, if any
				job.setNumReduceTasks(getNumberOfReducers());
			else if (-1 != config.getNumberOfTaskTrackersInCluster())	// Use one reducer per TastTracker, if the number of TaskTrackers is available
				job.setNumReduceTasks(5 * config.getNumberOfTaskTrackersInCluster()); // 5 reducers per node
			// Set the jar file
			job.setJarByClass(this.getClass());
			// Run the job
			if (job.waitForCompletion(true)) {
				fs.delete(pathToPredicateEncodingOutput, true);
			} else
				throw new DictionaryEncoderException("" + sJobName + " failed");
		} catch (ConfigurationNotInitializedException e) {
			throw new DictionaryEncoderException("ConfigurationNotInitializedException occurred:\n" + e.getMessage());
		} catch (IOException e) {
			throw new DictionaryEncoderException("IOException occurred:\n" + e.getMessage());
		} catch (InterruptedException e) {
			throw new DictionaryEncoderException("InterruptedException occurred:\n" + e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new DictionaryEncoderException("ClassNotFoundException occurred:\n" + e.getMessage());
		}
	}
	
	private void encodePredicate() throws DictionaryEncoderException {
		
		edu.utdallas.hadooprdf.conf.Configuration config;
		try {
			config = edu.utdallas.hadooprdf.conf.Configuration.getInstance();
			org.apache.hadoop.conf.Configuration hadoopConfiguration =
				new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
			// that the original one does not get cluttered with job specific key-value pairs
			// Must set all the job parameters before creating the job
			hadoopConfiguration.set(Tags.PATH_TO_DICTIONARY, pathToDictionary.toString());
			FileSystem fs;
			fs = FileSystem.get(hadoopConfiguration);
			// Delete output directory
			fs.delete(pathToPredicateEncodingOutput, true);
			// Create the job
			String sJobName = "Dictionary encoder phase 2 for " + inputDirectoryPath.toString();
			Job job = new Job(hadoopConfiguration, sJobName);
			// Specify input parameters
			job.setInputFormatClass(TextInputFormat.class);
			boolean bInputPathEmpty = true;
			// Get input file names from N-Triples directory
			FileStatus [] fstatus = fs.listStatus(pathToSubjectEncodingOutput);
			for (int i = 0; i < fstatus.length; i++) {
				if (!fstatus[i].isDir()) {
					FileInputFormat.addInputPath(job, fstatus[i].getPath());
					bInputPathEmpty = false;
				}
			}
			if (bInputPathEmpty)
				throw new DictionaryEncoderException("No file to encode from encoder phase 1 output!");
			// Get input file names from dictionary directory
			bInputPathEmpty = true;
			fstatus = fs.listStatus(pathToDictionary);
			for (int i = 0; i < fstatus.length; i++) {
				if (!fstatus[i].isDir()) {
					FileInputFormat.addInputPath(job, fstatus[i].getPath());
					bInputPathEmpty = false;
				}
			}
			if (bInputPathEmpty)
				throw new DictionaryEncoderException("No dictionary file to use for encoding!");
			// Specify output parameters
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			FileOutputFormat.setOutputPath(job, pathToPredicateEncodingOutput);
			// Set the mapper and reducer classes
			job.setMapperClass(edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred.DictionaryEncoderMapper2.class);
			job.setReducerClass(edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred.DictionaryEncoderReducer2.class);
			// Set the number of reducers
			if (-1 != getNumberOfReducers())	// Use the number set by the client, if any
				job.setNumReduceTasks(getNumberOfReducers());
			else if (-1 != config.getNumberOfTaskTrackersInCluster())	// Use one reducer per TastTracker, if the number of TaskTrackers is available
				job.setNumReduceTasks(5 * config.getNumberOfTaskTrackersInCluster()); // 5 reducers per node
			// Set the jar file
			job.setJarByClass(this.getClass());
			// Run the job
			if (job.waitForCompletion(true)) {
				fs.delete(pathToSubjectEncodingOutput, true);
			} else
				throw new DictionaryEncoderException("" + sJobName + " failed");
		} catch (ConfigurationNotInitializedException e) {
			throw new DictionaryEncoderException("ConfigurationNotInitializedException occurred:\n" + e.getMessage());
		} catch (IOException e) {
			throw new DictionaryEncoderException("IOException occurred:\n" + e.getMessage());
		} catch (InterruptedException e) {
			throw new DictionaryEncoderException("InterruptedException occurred:\n" + e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new DictionaryEncoderException("ClassNotFoundException occurred:\n" + e.getMessage());
		}
	}
	
	private void encodeSubject() throws DictionaryEncoderException {
		edu.utdallas.hadooprdf.conf.Configuration config;
		try {
			config = edu.utdallas.hadooprdf.conf.Configuration.getInstance();
			org.apache.hadoop.conf.Configuration hadoopConfiguration =
				new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
			// that the original one does not get cluttered with job specific key-value pairs
			// Must set all the job parameters before creating the job
			hadoopConfiguration.set(Tags.PATH_TO_DICTIONARY, pathToDictionary.toString());
			FileSystem fs;
			fs = FileSystem.get(hadoopConfiguration);
			// Delete output directory
			fs.delete(pathToSubjectEncodingOutput, true);
			// Create the job
			String sJobName = "Dictionary encoder phase 1 for " + inputDirectoryPath.getParent() + '/' + inputDirectoryPath.getName();
			Job job = new Job(hadoopConfiguration, sJobName);
			// Specify input parameters
			job.setInputFormatClass(TextInputFormat.class);
			boolean bInputPathEmpty = true;
			// Get input file names from N-Triples directory
			FileStatus [] fstatus = fs.listStatus(inputDirectoryPath);
			for (int i = 0; i < fstatus.length; i++) {
				if (!fstatus[i].isDir()) {
					FileInputFormat.addInputPath(job, fstatus[i].getPath());
					bInputPathEmpty = false;
				}
			}
			if (bInputPathEmpty)
				throw new DictionaryEncoderException("No file to encode from N-Triples directory!");
			// Get input file names from dictionary directory
			bInputPathEmpty = true;
			fstatus = fs.listStatus(pathToDictionary);
			for (int i = 0; i < fstatus.length; i++) {
				if (!fstatus[i].isDir()) {
					FileInputFormat.addInputPath(job, fstatus[i].getPath());
					bInputPathEmpty = false;
				}
			}
			if (bInputPathEmpty)
				throw new DictionaryEncoderException("No dictionary file to use for encoding!");
			// Specify output parameters
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			FileOutputFormat.setOutputPath(job, pathToSubjectEncodingOutput);
			// Set the mapper and reducer classes
			job.setMapperClass(edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred.DictionaryEncoderMapper1.class);
			job.setReducerClass(edu.utdallas.hadooprdf.data.preprocessing.dictionary.mapred.DictionaryEncoderReducer1.class);
			// Set the number of reducers
			if (-1 != getNumberOfReducers())	// Use the number set by the client, if any
				job.setNumReduceTasks(getNumberOfReducers());
			else if (-1 != config.getNumberOfTaskTrackersInCluster())	// Use one reducer per TastTracker, if the number of TaskTrackers is available
				job.setNumReduceTasks(config.getNumberOfTaskTrackersInCluster()); // 10 reducers per node
			// Set the jar file
			job.setJarByClass(this.getClass());
			// Run the job
			if (job.waitForCompletion(true)) {
				fs.delete(inputDirectoryPath, true);
			} else
				throw new DictionaryEncoderException("" + sJobName + " failed");
		} catch (ConfigurationNotInitializedException e) {
			throw new DictionaryEncoderException("ConfigurationNotInitializedException occurred:\n" + e.getMessage());
		} catch (IOException e) {
			throw new DictionaryEncoderException("IOException occurred:\n" + e.getMessage());
		} catch (InterruptedException e) {
			throw new DictionaryEncoderException("InterruptedException occurred:\n" + e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new DictionaryEncoderException("ClassNotFoundException occurred:\n" + e.getMessage());
		}
	}
}
