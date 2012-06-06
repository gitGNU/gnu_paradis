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
package se.kth.maandree.paradis;

import java.io.*;


/**
 * Protocol for transfering {@code byte[]}s
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
class ByteArrayTransferProtocol implements TransferProtocol<byte[]>
{
    //Has default constructor
    
    
    
    /**
     * {@inheritDoc}
     */
    public byte[] read(final TransferInputStream stream) throws IOException
    {
	final int len = stream.readLen();
	final byte[] rc = new byte[len];
	for (int i = 0; i < len; i++)
	    rc[i] = stream.readByte();
	return rc;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void write(final byte[] data, final TransferOutputStream stream) throws IOException
    {
	stream.writeLen(data.length);
	for (int i = 0, n = data.length; i < n; i++)
	    stream.writeByte(data[i]);
    }
    
}

