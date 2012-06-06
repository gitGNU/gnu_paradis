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
 * Protocol for transfering {@code boolean[]}s
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
class BooleanArrayTransferProtocol implements TransferProtocol<boolean[]>
{
    //Has default constructor
    
    
    
    /**
     * {@inheritDoc}
     */
    public boolean[] read(final TransferInputStream stream) throws IOException
    {
	final int len = stream.readLen();
	final boolean[] rc = new boolean[len];
	
	int n = len >>> 3;
	for (int i = 0; i < n; i++)
	{
	    int d = (int)(stream.readByte()) & 255;
	    rc[(i << 3) | 0] = (d & 128) != 0;
	    rc[(i << 3) | 1] = (d &  64) != 0;
	    rc[(i << 3) | 2] = (d &  32) != 0;
	    rc[(i << 3) | 3] = (d &  16) != 0;
	    rc[(i << 3) | 4] = (d &   8) != 0;
	    rc[(i << 3) | 5] = (d &   4) != 0;
	    rc[(i << 3) | 6] = (d &   2) != 0;
	    rc[(i << 3) | 7] = (d &   1) != 0;
	}
	
	n = len & 7;
	if (n != 0)
	{
	    int off = (len >>> 3) + 1;
	    int d = (int)(stream.readByte()) & 255;
	    if (n > 0)  rc[off | 0] = (d & 128) != 0;
	    if (n > 1)  rc[off | 1] = (d &  64) != 0;
	    if (n > 2)  rc[off | 2] = (d &  32) != 0;
	    if (n > 3)  rc[off | 3] = (d &  16) != 0;
	    if (n > 4)  rc[off | 4] = (d &   8) != 0;
	    if (n > 5)  rc[off | 5] = (d &   4) != 0;
	    if (n > 6)  rc[off | 6] = (d &   2) != 0;
	    if (n > 7)  rc[off | 7] = (d &   1) != 0;
	}
	
	return rc;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void write(final boolean[] data, final TransferOutputStream stream) throws IOException
    {
	stream.writeLen(data.length);
	for (int i = 0, n = data.length >>> 3; i < n; i++)
	    stream.writeByte((byte)((data[(i << 3) | 0] ? 128 : 0) | 
				    (data[(i << 3) | 1] ?  64 : 0) | 
				    (data[(i << 3) | 2] ?  32 : 0) | 
				    (data[(i << 3) | 3] ?  16 : 0) | 
				    (data[(i << 3) | 4] ?   8 : 0) | 
				    (data[(i << 3) | 5] ?   4 : 0) | 
				    (data[(i << 3) | 6] ?   2 : 0) | 
				    (data[(i << 3) | 7] ?   1 : 0)));
	
	int len = data.length & 7;
	if (len != 0)
	{
	    int off = (data.length >>> 3) + 1;
	    int buf = 0;
	    if (len > 0)  buf |= data[off | 0] ? 128 : 0;
	    if (len > 1)  buf |= data[off | 1] ?  64 : 0;
	    if (len > 2)  buf |= data[off | 2] ?  32 : 0;
	    if (len > 3)  buf |= data[off | 3] ?  16 : 0;
	    if (len > 4)  buf |= data[off | 4] ?   8 : 0;
	    if (len > 5)  buf |= data[off | 5] ?   4 : 0;
	    if (len > 6)  buf |= data[off | 6] ?   2 : 0;
	    if (len > 7)  buf |= data[off | 7] ?   1 : 0;
	    stream.writeByte((byte)buf);
	}
    }
    
}

