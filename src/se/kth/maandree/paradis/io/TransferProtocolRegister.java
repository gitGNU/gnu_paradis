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
import se.kth.maandree.paradis.net.*;
import se.kth.maandree.paradis.pacman.*;

import java.util.HashMap;
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
     * String to class map
     */
    private static final HashMap<String, Class<?>> classes = new HashMap<>();
    
    
    
    /**
     * Class initialiser
     */
    static
    {
        register(   String.class, new       StringTransferProtocol());
        register(boolean[].class, new BooleanArrayTransferProtocol());
        register(   byte[].class, new    ByteArrayTransferProtocol());
        register(    int[].class, new     IntArrayTransferProtocol());
        register(  short[].class, new   ShortArrayTransferProtocol());
        register(   long[].class, new    LongArrayTransferProtocol());
        register(   char[].class, new    CharArrayTransferProtocol());
        
        register(       UUID.class, new        UUID.       UUIDTransferProtocol());
        register(    Anycast.class, new     Anycast.    AnycastTransferProtocol());
        register(    Unicast.class, new     Unicast.    UnicastTransferProtocol());
        register(  Multicast.class, new   Multicast.  MulticastTransferProtocol());
        register(  Broadcast.class, new   Broadcast.  BroadcastTransferProtocol());
        register(     Packet.class, new      Packet.     PacketTransferProtocol());
        register(       User.class, new        User.       UserTransferProtocol());
        register(PackageInfo.class, new PackageInfo.PackageInfoTransferProtocol());
    }
    
    
    
    /**
     * Maps an class ID to an actual class
     * 
     * @param  objectClass  The data type class
     * @param  classID      The data type class ID
     */
    public static void register(final Class<?> objectClass, final String classID)
    {   synchronized (monitor)
        {   classes.put(classID, objectClass);
    }   }
    
    
    /**
     * Gets a class from an ID
     * 
     * @param   classID  The data type class ID
     * @return           The data type class, {@code null} if not mapped
     */
    public static Class<?> getClassByID(final String classID)
    {   synchronized (monitor)
        {   return classes.get(classID);
    }   }
    
    
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
     * @param   type    The data type
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

