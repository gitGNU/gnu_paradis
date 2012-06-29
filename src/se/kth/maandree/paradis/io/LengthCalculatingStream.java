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
 * Output stream used for calculating the size of an object
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
class LengthCalculatingStream extends TransferOutputStream
{
    /**
     * Constructor
     */
    public LengthCalculatingStream()
    {
        super(null);
    }
    
    
    
    /**
     * The length of the write data
     */
    public int length = 0;
    
    
    
    /**
     * Writes a {@code boolean} to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     */
    public synchronized void writeBoolean(final boolean data) throws IOException
    {   this.length++;
    }
    
    
    /**
     * Writes a {@code byte} to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     */
    public synchronized void writeByte(final byte data) throws IOException
    {   this.length++;
    }
    
    
    /**
     * Writes a {@code short} to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     */
    public synchronized void writeShort(final short data) throws IOException
    {   this.length += 2;
    }
    
    
    /**
     * Writes a {@code char} to the stream
     * 
     * @param  data  The data to write
     * 
     * @throws  IOException  Inherited from {@link #write(int)}
     */
    public synchronized void writeChar(final char data) throws IOException
    {   writeWChar((int)data);
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
            this.length++;
        else
        {
            int m = 0x100;
            int d = data;
            int ptr = 0;
            int buf = 0;
            for (;;)
            {
                m |= m >> 1;
                buf = d & 63;
                ptr++;
                d >>>= 6;
                if (d == 0)
                {
                    m >>= 1;
                    if ((m & buf) != 0)
                        ptr++;
                    break;
                }
            }
            
            while (ptr > 0)
                this.length++;
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
    {   this.length += 4;
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
    {   this.length += 8;
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
        final int cur = this.length;
        writeObject(data);
        writeLen(this.length - cur);
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

