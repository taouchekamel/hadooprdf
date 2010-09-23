/**
 * 
 */
package edu.utdallas.hadooprdf.data.io.input;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import edu.utdallas.hadooprdf.data.SubjectObjectPair;

/**
 * An input format class for binary encoded subject object pairs
 * @author Mohammad Farhan Husain
 *
 */
public class SOPInputFormat extends FileInputFormat<LongWritable, SubjectObjectPair> {
	private static final Log LOG = LogFactory.getLog(SOPInputFormat.class);
	/*
	 * The mask to be used to check divisibility by subject object pair size
	 */
	private long mask;
	
	public SOPInputFormat() {
		mask = (Long.SIZE >> 2) - 1;
		LOG.debug("Mask: " + mask);
	}
	
	@Override
	public RecordReader<LongWritable, SubjectObjectPair> createRecordReader(
			InputSplit arg0, TaskAttemptContext arg1) throws IOException,
			InterruptedException {
		return new SOPRecordReader();
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.lib.input.FileInputFormat#computeSplitSize(long, long, long)
	 */
	@Override
	protected long computeSplitSize(long blockSize, long minSize, long maxSize) {
		// We are going to use the formula used in super class: Math.max(minSize, Math.min(maxSize, blockSize))
		long min = Math.min(maxSize, blockSize);
		while (0 != (min & mask)) min--;			// Find the greatest number which is divisible by subject object pair size and less than or equal to min
		while (0 != (minSize & mask)) minSize++;	// Find the smallest number which is divisible by subject object pair size and greater than or equal to min
		LOG.debug("blockSize: " + blockSize + "\nminSize: " + minSize + "\nmaxSize: " + maxSize + "\nsplitSize: " + Math.max(minSize, min));
		return Math.max(minSize, min);
	}

}
