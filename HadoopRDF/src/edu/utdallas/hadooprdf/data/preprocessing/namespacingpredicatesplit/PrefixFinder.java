package edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit;

import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.preprocessing.lib.PreprocessorJobRunner;
import edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.PrefixFinderException;
import edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.mapred.PrefixFinderMapper;
import edu.utdallas.hadooprdf.data.preprocessing.namespacingpredicatesplit.mapred.PrefixFinderReducer;
import edu.utdallas.hadooprdf.lib.mapred.io.output.NullKeyTextOutputFormat;
import edu.utdallas.hadooprdf.lib.util.PathFilterOnFilenameExtension;

/**
 * This class finds the prefixes using one or more reducers
 * @author Mohammad Farhan Husain
 */
public class PrefixFinder extends PreprocessorJobRunner {
	/**
	 * The class constructor
	 * @param dataSet the data set to find the prefix for
	 * @throws DataFileExtensionNotSetException 
	 */
	public PrefixFinder(DataSet dataSet) throws DataFileExtensionNotSetException {
		super(dataSet);
		m_InputDirectoryPath = dataSet.getPathToNTriplesData();
		m_OutputDirectoryPath = new Path(m_DataSet.getPathToTemp(), "prefixes");
	}
	/**
	 * The method which actually finds prefixes
	 * @throws ConfigurationNotInitializedException
	 * @throws IOException
	 */
	public void findPrefixes() throws ConfigurationNotInitializedException, PrefixFinderException {
		edu.utdallas.hadooprdf.conf.Configuration config =
			edu.utdallas.hadooprdf.conf.Configuration.getInstance();
		org.apache.hadoop.conf.Configuration hadoopConfiguration =
			new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
		// that the original one does not get cluttered with job specific key-value pairs
		try {
			FileSystem fs = FileSystem.get(hadoopConfiguration);
			// Delete output directory
			fs.delete(m_OutputDirectoryPath, true);
			// Create the job
			String sJobName = "PrefixFinder for " + m_InputDirectoryPath.getParent() + '/' + m_InputDirectoryPath.getName();
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
				throw new PrefixFinderException("No file to find prefix in!");
			// Specify output parameters
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setMapOutputKeyClass(IntWritable.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputFormatClass(NullKeyTextOutputFormat.class);
			FileOutputFormat.setOutputPath(job, m_OutputDirectoryPath);
			// Set the mapper and reducer classes
			job.setMapperClass(PrefixFinderMapper.class);
			job.setReducerClass(PrefixFinderReducer.class);
			// Set the number of reducers
			job.setNumReduceTasks(1);
			// Set the jar file
			job.setJarByClass(this.getClass());
			if (job.waitForCompletion(true)) {
				fs.delete(m_DataSet.getPathToPrefixFile(), false);
				fs.rename(new Path(m_OutputDirectoryPath, "part-r-00000"), m_DataSet.getPathToPrefixFile());
				fs.delete(m_OutputDirectoryPath, true);
			}
			else
				throw new PrefixFinderException("Prefix finding job failed");
		} catch (IOException e) {
			throw new PrefixFinderException("Prefix finding failed because\n" + e.getMessage());
		} catch (InterruptedException e) {
			throw new PrefixFinderException("Prefix finding failed because\n" + e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new PrefixFinderException("Prefix finding failed because\n" + e.getMessage());
		}
	}
}
