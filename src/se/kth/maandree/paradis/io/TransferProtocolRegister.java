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

import java.util.*;
import java.io.*;


/**
 * Register fro protocol for transfering specific data types
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class TransferProtocolRegister
{
    /**
     * Non-constructor
     */
    private TransferProtocolRegister()
    {
	assert false : "You may not create instances of this class [TransferProtocolRegister].";
    }
    
    
    
    /**
     * Synchronisation monitor
     */
    private static final Object monitor = new Object();
    
    /**
     * Protocol register
     */
    private static final HashMap<Class<?>, TransferProtocol<?>> protocols = new HashMap<>();
    
    
    
    /**
     * Class initialiser
     */
    static
    {
	protocols.put(String.class, new StringTransferProtocol());
    }
    
    
    
    /**
     * Registers a data type transfer protocol
     * 
     * @param  objectClass  The data type
     * @param  protocol     The data type transfer protocol
     */
    public static <T> void register(final Class<T> objectClass, final TransferProtocol<T> protocol)
    {   synchronized (monitor)
	{   protocols.put(objectClass, protocol);
    }   }
    
    
    /**
     * Reads one instance of the data type from a stream
     * 
     * @param   stream  The data input stream
     * @return          The next instance of the data type
     * 
     * @throws  IOException  Inherited from {@link InputStream#read()}
     */
    @SuppressWarnings("unchecked")
    static <T> T read(final Class<T> type, final TransferInputStream stream) throws IOException
    {
	final TransferProtocol<T> protocol;
	synchronized (monitor)
	{   protocol = (TransferProtocol<T>)(protocols.get(type));
	}
	
	return protocol.read(stream);
    }
    
    
    /**
     * Writes an instance of the data type to a stream
     * 
     * @param  data    The instance of the data type
     * @param  stream  The data output stream
     * 
     * @throws  IOException  Inherited from {@link OutputStream#write(int)}
     */
    @SuppressWarnings("unchecked")
    static <T> void write(final T data, final TransferOutputStream stream) throws IOException
    {
	final TransferProtocol<T> protocol;
	synchronized (monitor)
	{   protocol = (TransferProtocol<T>)(protocols.get(data.getClass()));
	}
	
	protocol.write(data, stream);
    }
    
}

