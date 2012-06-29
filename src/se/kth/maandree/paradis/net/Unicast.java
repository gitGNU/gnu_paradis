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
package se.kth.maandree.paradis.net;
import se.kth.maandree.paradis.io.*;

import java.io.IOException;


/**
 * Unicast sending path information
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Unicast implements Cast
{
    /**
     * Constructor
     * 
     * @param  sender         The sender of the packet
     * @param  receiver       The desired receiver of the packet
     * @param  senderAddress  The address:port of the send, {@code null} if not shared
     */
    public Unicast(final UUID sender, final UUID receiver, final String senderAddress)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.senderAddress = senderAddress == null ? null : senderAddress.isEmpty() ? null : senderAddress;
    }
    
    
    
    /**
     * The sender of the packet
     */
    public final UUID sender;
    
    /**
     * The desired receiver of the packet
     */
    public final UUID receiver;
    
    /**
     * The address:port of the send, {@code null} if not shared
     */
    public final String senderAddress;
    
    
    
    /**
     * Protocol for transfering {@link Unicast}s
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    public static class UnicastTransferProtocol implements TransferProtocol<Unicast>
    {
        //Has default constructor
        
        
        
        /**
         * {@inheritDoc}
         */
        public Unicast read(final TransferInputStream stream) throws IOException
        {   return new Unicast(stream.readObject(UUID.class),
                               stream.readObject(UUID.class),
                               stream.readObject(String.class));
        }
        
        
        /**
         * {@inheritDoc}
         */
        public void write(final Unicast data, final TransferOutputStream stream) throws IOException
        {   stream.writeObject(data.sender);
            stream.writeObject(data.receiver);
            stream.writeObject(data.senderAddress == null ? "" : data.senderAddress);
        }
        
    }
    
    
    
    /**
     * {@inheritDoc}
     */
    public void addReceived(final UUID uuid)
    {   //Do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasReceived(final UUID uuid)
    {   return false;
    }
    
}

