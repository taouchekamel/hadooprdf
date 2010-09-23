/**
 * 
 */
package edu.utdallas.hadooprdf.lib.mapred.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class ByteLongLongWritable implements Writable {
	private byte flag;
	private long data1;
	private long data2;
	
	public ByteLongLongWritable(byte flag, long data1, long data2) {
		this.flag = flag;
		this.data1 = data1;
		this.data2 = data2;
	}
	
	public ByteLongLongWritable() {
		this((byte) 0, 0l, 0l);
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		flag = in.readByte();
		data1 = in.readLong();
		data2 = in.readLong();
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeByte(flag);
		out.writeLong(data1);
		out.writeLong(data2);
	}

	/**
	 * @return the flag
	 */
	public byte getFlag() {
		return flag;
	}

	/**
	 * @param flag the flag to set
	 */
	public void setFlag(byte flag) {
		this.flag = flag;
	}

	/**
	 * @return the data
	 */
	public long getData1() {
		return data1;
	}

	/**
	 * @param data the data to set
	 */
	public void setData1(long data1) {
		this.data1 = data1;
	}

	/**
	 * @return the data2
	 */
	public long getData2() {
		return data2;
	}

	/**
	 * @param data2 the data2 to set
	 */
	public void setData2(long data2) {
		this.data2 = data2;
	}

}
