package net.cc110.aeon.util;

import java.io.*;

public class TeeOutputStream extends OutputStream
{
	private final OutputStream out1, out2;
	
	public TeeOutputStream(OutputStream out1, OutputStream out2)
	{
		this.out1 = out1;
		this.out2 = out2;
	}
	
	public void write(int b) throws IOException
	{
		out1.write(b);
		out2.write(b);
	}
	
	public void write(byte[] b, int off, int len) throws IOException
	{
		out1.write(b, off, len);
		out2.write(b, off, len);
	}
	
	public void flush() throws IOException
	{
		out1.flush();
		out2.flush();
	}
	
	public void close() throws IOException
	{
		try(OutputStream out = out1) { out.close(); }
		try(OutputStream out = out2) { out.close(); }
	}
}
