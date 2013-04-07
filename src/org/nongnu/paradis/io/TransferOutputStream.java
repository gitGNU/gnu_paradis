/**
 *  Paradis — Ever growing network for parallel and distributed computing.
 *  Copyright © 2012, 2013  Mattias Andrée (maandree@member.fsf.org)
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
package org.nongnu.paradis.io;

import java.io.*;


/**
 * Buffered object output stream with cross-platform transfer protocol
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
public class TransferOutputStream extends FilterOutputStream
{
    /**
     * Constructor
     * 
     * @param  next  The next stream in the chain
     */
    public TransferOutputStream(final OutputStream next)
    {
        super(new BufferedOutputStream(next));
    }
    
    
    
    /**
     * Helper buffer for {@link #writeWChar(int)}
     */
    private final int[] wcharbuf = new int[6];
    
    
    
    /**
     * Writes a {@code boolean} to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     */
    public synchronized void writeBoolean(final boolean data) throws IOException
    {
        this.write(data ? 1 : 0);
    }
    
    
    /**
     * Writes a {@code byte} to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     */
    public synchronized void writeByte(final byte data) throws IOException
    {
        this.write(data & 255);
    }
    
    
    /**
     * Writes a {@code short} to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     */
    public synchronized void writeShort(final short data) throws IOException
    {
        this.write((data >>> 8) & 255);
        this.write(data & 255);
    }
    
    
    /**
     * Writes a {@code char} to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     */
    public synchronized void writeChar(final char data) throws IOException
    {
        writeWChar(data);
    }
    
    
    /**
     * Writes an {@code int} as a character to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     */
    public synchronized void writeWChar(final int data) throws IOException
    {
        if (data < 0x80)
            this.write(data);
        else
        {
            int m = 0x100;
            int d = data;
            int ptr = 0;
            for (;;)
            {
                m |= m >> 1;
                this.wcharbuf[ptr++] = d & 63;
                d >>>= 6;
                if (d == 0)
                {
                    m >>= 1;
                    if ((m & this.wcharbuf[ptr - 1]) == 0)
                        this.wcharbuf[ptr - 1] |= (m << 1) & 0xFF;
                    else
                        this.wcharbuf[ptr++] = m;
                    break;
                }
            }
            
            while (ptr > 0)
                this.write(this.wcharbuf[--ptr]);
        }
    }
    
    
    /**
     * Writes an {@code int} to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     */
    public synchronized void writeInt(final int data) throws IOException
    {
        this.write((data >>> 24) & 255);
        this.write((data >>> 16) & 255);
        this.write((data >>> 8) & 255);
        this.write(data & 255);
    }
    
    
    /**
     * Writes a 31-bit {@code int} compressable to 15-bits to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     */
    public synchronized void writeLen(final int data) throws IOException
    {
        if ((data & 0x7FFF) != 0)
            writeShort((short)data);
        else
            writeInt(~data);
    }
    
    
    /**
     * Writes a {@code long} to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     */
    public synchronized void writeLong(final long data) throws IOException
    {
        this.write((int)((data >>> 56) & 255));
        this.write((int)((data >>> 48) & 255));
        this.write((int)((data >>> 40) & 255));
        this.write((int)((data >>> 32) & 255));
        this.write((int)((data >>> 24) & 255));
        this.write((int)((data >>> 16) & 255));
        this.write((int)((data >>> 8) & 255));
        this.write((int)(data & 255));
    }
    
    
    /**
     * Writes the size of an object as a 31-bit {@code int} compressable to 15-bits to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     * 
     * @see     #writeLen(int)
     * @see     #writeObject(Object)
     */
    public synchronized void writeLenOf(final Object data) throws IOException
    {
        LengthCalculatingStream lcs = null;
        try
        {   lcs = new LengthCalculatingStream();
            lcs.writeObject(data);
            writeLen(lcs.length);
        }
        finally
        {   if (lcs != null)
                try
                {   lcs.close();
                }
                catch (final Throwable ignore)
                {   //Ignore
        }       }
    }
    
    
    /**
     * Writes an object to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     */
    public synchronized void writeObject(final Object data) throws IOException
    {
        if (data instanceof Object[])
        {
            int len;
            final Object[] array = (Object[])data;
            writeLen(len = array.length);
            for (int i = 0; i < len; i++)
                writeObject(array[i]);
        }
        else
            TransferProtocolRegister.write(data, this);
    }
    
}

