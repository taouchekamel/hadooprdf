/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.indexing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.commons.Constants;
import edu.utdallas.hadooprdf.data.commons.Tags;
import edu.utdallas.hadooprdf.data.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.metadata.PredicateIdPairs;
import edu.utdallas.hadooprdf.data.metadata.StringIdPairsException;
import edu.utdallas.hadooprdf.data.metadata.summarystatistics.FileStatistics;
import edu.utdallas.hadooprdf.data.metadata.summarystatistics.SummaryStatistics;
import edu.utdallas.hadooprdf.data.metadata.summarystatistics.SummaryStatisticsException;
import edu.utdallas.hadooprdf.data.preprocessing.lib.PreprocessorJobRunner;
import edu.utdallas.hadooprdf.lib.util.PathFilterOnFilenameExtension;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class Indexer extends PreprocessorJobRunner {
	/**
	 * Path to a temporary directory containing the sorted pos data 
	 */
	private Path pathToSortedPOS;
	/**
	 * The rdf:type id as a string
	 */
	private String rdfTypePredicate;
	/**
	 * Path to summary statistics file
	 */
	private Path pathToSummaryStatisticsFile;
	/**
	 * Class constructor
	 * @param dataSet the data set to work on
	 * @throws DataFileExtensionNotSetException
	 * @throws StringIdPairsException 
	 */
	public Indexer(DataSet dataSet) throws DataFileExtensionNotSetException, StringIdPairsException {
		super(dataSet);
		inputDirectoryPath = dataSet.getPathToPOSData();
		inputFilesExtension = Constants.POS_EXTENSION;
		pathToSortedPOS = new Path(dataSet.getPathToTemp(), "sorted_pos");
		rdfTypePredicate = "" + new PredicateIdPairs(dataSet).getId(Constants.RDF_TYPE_URI_NTRIPLES_STRING);
		pathToSummaryStatisticsFile = dataSet.getPathToSummaryStatistics();
	}
	
	public void index() throws ConfigurationNotInitializedException, IndexerException {
		try {
			edu.utdallas.hadooprdf.conf.Configuration config =
				edu.utdallas.hadooprdf.conf.Configuration.getInstance();
			org.apache.hadoop.conf.Configuration hadoopConfiguration =
				new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
			// Must set all the job parameters before creating the job
			hadoopConfiguration.set(Tags.RDF_TYPE_PREDICATE, rdfTypePredicate);
			// that the original one does not get cluttered with job specific key-value pairs
			FileSystem fs = FileSystem.get(hadoopConfiguration);
			sortPosDirectory(config, hadoopConfiguration, fs);
		} catch (IOException e) {
			throw new IndexerException("IOException occurred while indexing because\n" + e.getMessage());
		}
	}
	
	private void sortPosDirectory(edu.utdallas.hadooprdf.conf.Configuration config,
			org.apache.hadoop.conf.Configuration hadoopConfiguration,
			FileSystem fs) throws IndexerException {
		try {
			SummaryStatistics ss = new SummaryStatistics(hadoopConfiguration, pathToSummaryStatisticsFile, true);
			// Delete output directory
			fs.delete(pathToSortedPOS, true);
			boolean bInputPathEmpty = true;
			// Get input file names
			FileStatus [] fstatus = fs.listStatus(inputDirectoryPath, new PathFilterOnFilenameExtension(inputFilesExtension));
			for (int i = 0; i < fstatus.length; i++) {
				if (!fstatus[i].isDir()) {
					ss.addFileStatistics(sortPosFile(fs, fstatus[i].getPath()));	// Sort the file and add the statistics
					bInputPathEmpty = false;
				}
			}
			if (bInputPathEmpty)
				throw new IndexerException("No POS file to sort by subject!");
			ss.persist();
		} catch (IOException e) {
			throw new IndexerException("IOException occurred:\n" + e.getMessage());
		} catch (SummaryStatisticsException e) {
			throw new IndexerException("SummaryStatisticsException occurred:\n" + e.getMessage());
		}
	}
	
	private FileStatistics sortPosFile(FileSystem fs, Path f) throws IOException {
		boolean isTypeFile = false;
		final int index = f.getName().indexOf(Constants.PREDICATE_OBJECT_TYPE_SEPARATOR);
		if (-1 != index)
			isTypeFile = f.getName().subSequence(0, index).equals(rdfTypePredicate);
		DataInputStream dis = fs.open(f);
		long subject;
		long largestSubjectId = Long.MIN_VALUE;
		long smallestSubjectId = Long.MAX_VALUE;
		long numberOfRecords = 0;
		if (isTypeFile) {
			Set<Long> subjectSet = new TreeSet<Long> ();
			try {
				while (true) {
					subject = dis.readLong();
					// Update statistics
					numberOfRecords++;
					if (subject < smallestSubjectId)
						smallestSubjectId = subject;
					if (subject > largestSubjectId)
						largestSubjectId = subject;
					// Add subject to the set
					subjectSet.add(subject);
				}
			} catch (EOFException e) {	// end of file reached
				dis.close();
			}
			DataOutputStream dos = fs.create(new Path(pathToSortedPOS, f.getName()), true);
			for (Long l : subjectSet)
				dos.writeLong(l);
			dos.close();
			return new FileStatistics(f.getName(), f.toString(), largestSubjectId, smallestSubjectId, numberOfRecords);
		} else {
			Map<Long, Set<Long>> subjectObjectMap = new TreeMap<Long, Set<Long>> ();
			long object;
			try {
				while (true) {
					subject = dis.readLong();
					object = dis.readLong();
					// Update statistics
					numberOfRecords++;
					if (subject < smallestSubjectId)
						smallestSubjectId = subject;
					if (subject > largestSubjectId)
						largestSubjectId = subject;
					// Add pair to the cache
					Set<Long> objectSet = subjectObjectMap.get(subject);
					if (null == objectSet) {
						objectSet = new TreeSet<Long> ();
						subjectObjectMap.put(subject, objectSet);
					}
					objectSet.add(object);
				}
			} catch (EOFException e) {	// end of file reached
				dis.close();
			}
			DataOutputStream dos = fs.create(new Path(pathToSortedPOS, f.getName()), true);
			for (Long sub : subjectObjectMap.keySet())
				for (Long obj : subjectObjectMap.get(sub)) {
					dos.writeLong(sub);
					dos.writeLong(obj);
				}
			dos.close();
			return new FileStatistics(f.getName(), f.toString(), largestSubjectId, smallestSubjectId, numberOfRecords);
		}
	}

}
