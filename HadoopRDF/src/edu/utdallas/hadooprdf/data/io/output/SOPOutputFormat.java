/**
 * 
 */
package edu.utdallas.hadooprdf.data.io.output;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import edu.utdallas.hadooprdf.data.SubjectObjectPair;
import edu.utdallas.hadooprdf.data.commons.Constants;

/**
 * An output format class for binary encoded subject object pairs
 * @author Mohammad Farhan Husain
 * 
 */
public class SOPOutputFormat extends FileOutputFormat<Text, SubjectObjectPair> {
	protected static class PSRecordWriter extends
			RecordWriter<Text, SubjectObjectPair> {
		protected TaskAttemptContext job;
		protected Map<String, DataOutputStream> outMap;

		/**
		 * Class constructor
		 * 
		 * @param job
		 *            the {@link TaskAttemptContext}
		 */
		public PSRecordWriter(TaskAttemptContext job) {
			this.job = job;
			outMap = new HashMap<String, DataOutputStream>();
		}

		/**
		 * @param context
		 *            the TaskAttemptContext
		 */
		@Override
		public synchronized void close(TaskAttemptContext context)
				throws IOException {
			Iterator<DataOutputStream> iter = outMap.values().iterator();
			// Iterate through all the DataOutputStream objects and close them
			while (iter.hasNext())
				iter.next().close();
		}

		/**
		 * @param key the predicate
		 * @param value the subject-object pair
		 */
		@Override
		public void write(Text key, SubjectObjectPair value)
				throws IOException, InterruptedException {
			// Get the stream
			String outputFile = key.toString();
			DataOutputStream out = outMap.get(outputFile);
			if (null == out) { // The stream is not there, it has to be created
				Path file = new Path(job.getConfiguration().get(
						"mapred.output.dir"), outputFile + '.' + Constants.PS_EXTENSION);
				FileSystem fs = file.getFileSystem(job.getConfiguration());
				out = fs.create(file, true);
				outMap.put(outputFile, out);
			}
			out.writeLong(value.getSubject());	// Write the subject
			if (0 != value.getObject())
				out.writeLong(value.getObject());	// Write the object
		}
	}

	@Override
	public RecordWriter<Text, SubjectObjectPair> getRecordWriter(
			TaskAttemptContext job) throws IOException, InterruptedException {
		return new PSRecordWriter(job);
	}
}
