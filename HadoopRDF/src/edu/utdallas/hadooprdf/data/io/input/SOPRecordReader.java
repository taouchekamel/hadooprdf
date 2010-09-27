/**
 * 
 */
package edu.utdallas.hadooprdf.data.io.input;

import java.io.DataInputStream;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import edu.utdallas.hadooprdf.data.SubjectObjectPair;
import edu.utdallas.hadooprdf.data.commons.Constants;
import edu.utdallas.hadooprdf.data.commons.Tags;

/**
 * @author Mohammad Farhan Husain
 * 
 */
public class SOPRecordReader extends
		RecordReader<LongWritable, SubjectObjectPair> {
	private static final long SIZE_OF_LONG_IN_BYTES = Long.SIZE >> 3;
	private long start;
	private long pos;
	private long end;
	private DataInputStream in = null;
	private LongWritable key = null;
	private SubjectObjectPair value = null;
	private boolean inputFromTypeFile = false;
	
	@Override
	public synchronized void close() throws IOException {
		if (null != in)
			in.close();
	}

	@Override
	public LongWritable getCurrentKey() throws IOException,
			InterruptedException {
		return key;
	}

	@Override
	public SubjectObjectPair getCurrentValue() throws IOException,
			InterruptedException {
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		if (start == end) {
			return 0.0f;
		} else {
			return Math.min(1.0f, (pos - start) / (float) (end - start));
		}
	}

	@Override
	public void initialize(InputSplit genericSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
		FileSplit split = (FileSplit) genericSplit;
		final String typePredicateId = context.getConfiguration().get(Tags.RDF_TYPE_PREDICATE);
		inputFromTypeFile = false;
		if (null != typePredicateId) {
			String fileName = split.getPath().getName();
			final int index = fileName.indexOf(Constants.PREDICATE_OBJECT_TYPE_SEPARATOR);
			if (-1 != index)
				inputFromTypeFile = fileName.substring(0, index).startsWith(typePredicateId);
		}
		Configuration job = context.getConfiguration();
		start = split.getStart();
		end = start + split.getLength();
		final Path file = split.getPath();
		// open the file and seek to the start of the split
		FileSystem fs = file.getFileSystem(job);
		FSDataInputStream fileIn = fs.open(split.getPath());
		fileIn.seek(start);
		pos = start;
		in = fileIn;
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (key == null) {
			key = new LongWritable();
		}
		key.set(pos);
		if (value == null) {
			value = new SubjectObjectPair();
		}
		if (pos < end) {
			value.setSubject(in.readLong());
			pos += SIZE_OF_LONG_IN_BYTES;
			if (inputFromTypeFile) {
				value.setObject(0);
				return true;
			}
			else if (pos < end) {
				value.setObject(in.readLong());
				pos += SIZE_OF_LONG_IN_BYTES;
				return true;
 			}
		}
		key = null;
		value = null;
		return false;
	}

}
