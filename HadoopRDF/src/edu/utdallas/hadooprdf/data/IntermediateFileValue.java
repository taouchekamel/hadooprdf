package edu.utdallas.hadooprdf.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * A class that implements the Writable interface for an intermediate file
 * @author vaibhav
 *
 */
public class IntermediateFileValue implements Writable
{
	/** An array of variables **/
	private int[] arrVars = new int[1];
	private long[] arrValues = new long[1];
	private int setCount = 0;
	private int getCount = 0;
	
	public IntermediateFileValue()
	{
/*		for( int i = 0; i < 1; i++ )
		{
			arrVars[i] = -1;
			arrValues[i] = -1;
		}
*/	}
	
	public long[] getValues() { return arrValues; }
	
	public int getArrayLength() { return arrVars.length; }
	
	public void setElement( int joiningVar, long value ) 
	{ 
		arrVars[setCount] = joiningVar;
		arrValues[setCount] = value;
		setCount++;		
	}
	
	@Override
	public void readFields(DataInput in) throws IOException 
	{
		arrVars[0] = in.readInt();
		arrValues[0] = in.readLong();
		getCount++;
	}

	@Override
	public void write(DataOutput out) throws IOException 
	{
		for( int i = 0; i < arrVars.length; i++ )
		{
			out.writeInt( arrVars[i] );
			out.writeLong( arrValues[i] );
		}
	}
}
