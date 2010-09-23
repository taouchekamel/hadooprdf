/**
 * 
 */
package edu.utdallas.hadooprdf.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * A class containing a pair of subject and object
 * in binary encoded form in long native data type.
 * 
 * @author Mohammad Farhan Husain
 *
 */
public class SubjectObjectPair implements Writable {
	private long subject;
	private long object;
	
	public SubjectObjectPair() {
		this(0, 0);
	}
	
	public SubjectObjectPair(long subject, long object) {
		this.subject = subject;
		this.object = object;
	}

	/**
	 * @return the subject
	 */
	public long getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(long subject) {
		this.subject = subject;
	}

	/**
	 * @return the object
	 */
	public long getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(long object) {
		this.object = object;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		subject = in.readLong();
		object = in.readLong();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(subject);
		out.writeLong(object);
	}
}
