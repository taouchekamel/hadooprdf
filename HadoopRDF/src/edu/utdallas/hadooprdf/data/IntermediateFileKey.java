package edu.utdallas.hadooprdf.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class IntermediateFileKey implements WritableComparable<IntermediateFileKey>
{
	/** An array of variables **/
	private int var;
	private long value;

	public IntermediateFileKey() { this( 0, 0 ); } 
	
	public IntermediateFileKey( int i, long l )
	{
		this.var = i;
		this.value = l;
	}
	
	public int getVar() { return var; }
	
	public void setVar( int var ) { this.var = var; }
	
	public long getValue() { return value; }
	
	public void setValue( long value ) { this.value = value; }
	
	@Override
	public void readFields(DataInput in) throws IOException 
	{
		var = in.readInt();
		value = in.readLong();
	}

	@Override
	public void write(DataOutput out) throws IOException 
	{
		out.writeInt( var );
		out.writeLong( value );
	}
	
	@Override
	public int compareTo(IntermediateFileKey o) 
	{
		if( var != o.getVar() ) return var < o.getVar() ? -1 : 1;
		else return value < o.getValue() ? -1 : 1;
	}
}
