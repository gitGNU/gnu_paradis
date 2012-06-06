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
 * Protocol for transfering a specific data type
 * 
 * @param  <T>  The transferable data type
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public interface TransferProtocol<T>
{
    /**
     * Reads one instance of the data type from a stream
     * 
     * @param   stream  The data input stream
     * @return          The next instance of the data type
     * 
     * @throws  IOException  Inherited from {@link InputStream#read()}
     */
    public T read(final TransferInputStream stream) throws IOException;
    
    /**
     * Writes an instance of the data type to a stream
     * 
     * @param  data    The instance of the data type
     * @param  stream  The data output stream
     * 
     * @throws  IOException  Inherited from {@link OutputStream#write(int)}
     */
    public void write(final T data, final TransferOutputStream stream) throws IOException;
    
}

