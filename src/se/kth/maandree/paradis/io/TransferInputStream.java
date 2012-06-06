/**
 *  Paradis — Ever growing network for parallell and distributed computing.
 *  Copyright © 2012  Mattias Andrée
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.kth.maandree.paradis.io;

import java.io.*;


/**
 * Buffered object input stream with cross-platform transfer protocol
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class TransferInputStream extends FilterInputStream
{
    /**
     * Constructor
     * 
     * @param  next  The next stream in the chain
     */
    public TransferInputStream(final InputStream next)
    {
	super(new BufferedInputStream(next));
    }
    
    
    
    /**
     * Reads a {@code boolean} from the stream
     * 
     * @return  The read data
     * 
     * @throws  IOException  Inherited from {@link #read()}
     */
    public synchronized boolean readBoolean() throws IOException
    {
	return this.read() != 0;
    }
    
    
    /**
     * Reads a {@code byte} from the stream
     * 
     * @return  The read data
     * 
     * @throws  IOException  Inherited from {@link #read()}
     */
    public synchronized byte readByte() throws IOException
    {
	return (byte)(this.read());
    }
    
    
    /**
     * Reads a {@code short} from the stream
     * 
     * @return  The read data
     * 
     * @throws  IOException  Inherited from {@link #read()}
     */
    public synchronized short readShort() throws IOException
    {
	return (short)((this.read() << 8) | this.read());
    }
    
    
    /**
     * Reads a {@code char} from the stream
     * 
     * @return  The read data
     * 
     * @throws  IOException  Inherited from {@link #read()}
     */
    public synchronized char readChar() throws IOException
    {
	return (char)(readWChar());
    }
    
    
    /**
     * Reads an {@code int} as a character from the stream
     * 
     * @return  The read data
     * 
     * @throws  IOException  Inherited from {@link #read()}
     */
    public synchronized int readWChar() throws IOException
    {
	int buf = 0;
	int n = 0;
	int b;
	
	while ((b = this.read()) != -1)
	    if ((b & 0x80) == 0)
		return b;
	    else if ((b & 0xC0) == 0xC0)
	    {
		n = 0;
		buf = b;
		while ((buf & 0x80) == 0x80)
		{
		    n++;
		    buf <<= 1;
		}
		buf = (buf & 0xFF) >> n--;
	    }
	    else
		if (n > 0)
		{
		    buf = (buf << 6) | (b & 63);
		    if (--n == 0)
			return buf;
		}
	
	return buf;
    }
    
    
    /**
     * Reads an {@code int} from the stream
     * 
     * @return  The read data
     * 
     * @throws  IOException  Inherited from {@link #read()}
     */
    public synchronized int readInt() throws IOException
    {
	return (this.read() << 24) |
	       (this.read() << 16) |
	       (this.read() << 8) |
	       this.read();
    }
    
    
    /**
     * Reads a 31-bit {@code int} that may have been compressed to 15-bits from the stream
     * 
     * @return  The read data
     * 
     * @throws  IOException  Inherited from {@link #read()}
     */
    public synchronized int readLen() throws IOException
    {
	short hi = readShort();
	if (hi >= 0)
	    return (int)hi;
	
	short lo = readShort();
	
	return ~(((int)hi << 16) | ((int)lo & 0xFFFF));
    }
    
    
    /**
     * Reads a {@code long} from the stream
     * 
     * @return  The read data
     * 
     * @throws  IOException  Inherited from {@link #read()}
     */
    public synchronized long readLong() throws IOException
    {
	return ((long)(this.read()) << 56L) |
	       ((long)(this.read()) << 48L) |
	       ((long)(this.read()) << 40L) |
	       ((long)(this.read()) << 32L) |
	       ((long)(this.read()) << 24L) |
	       ((long)(this.read()) << 16L) |
	       ((long)(this.read()) << 8L) |
	       (long)(this.read());
    }
    
    
    /**
     * Reads an object from the stream
     * 
     * @param   type  The data type
     * @return        The read data
     * 
     * @throws  IOException             Inherited from {@link #read()}
     */
    public synchronized <T> T readObject(final Class<T> type) throws IOException
    {
	if (Object[].class.isAssignableFrom(type))
	{
	    String elementTypeString = type.getName().substring(1);
	    if (elementTypeString.startsWith("[") == false)
		elementTypeString.substring(1);
	    
	    Class<?> elementType;
	    try
	    {   elementType = Class.forName(elementTypeString);
	    }
	    catch (final ClassNotFoundException err)
	    {   throw new Error(err); // This cannot happen
	    }
	    
	    int len;
	    Object[] array = new Object[len = readLen()];
	    for (int i = 0; i < len; i++)
		array[i] = readObject(elementType);
	}
	
	return TransferProtocolRegister.read(type, this);
    }
    
}

