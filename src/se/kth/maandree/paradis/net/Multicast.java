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
 * Multicast sending path information
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Multicast implements Cast
{
    /**
     * Constructor
     * 
     * @param  sender     The sender of the packet
     * @param  receivers  The desired receivers of the packet
     */
    public Multicast(final UUID sender, final UUID[] receivers)
    {
	this.sender = sender;
	this.receivers = receivers;
	this.received = new UUID[] { sender };
    }
    
    /**
     * Constructor
     * 
     * @param  sender     The sender of the packet
     * @param  receivers  The desired receivers of the packet
     * @param  received   Clients known to have, or currenty is receiving, a copy of the packet
     */
    protected Multicast(final UUID sender, final UUID[] receivers, final UUID[] received)
    {
	this.sender = sender;
	this.receivers = receivers;
	this.received = received;
    }
    
    
    
    /**
     * The sender of the packet
     */
    public final UUID sender;
    
    /**
     * The desired receivers of the packet
     */
    public final UUID[] receivers;
    
    /**
     * Clients known to have, or currenty is receiving, a copy of the packet
     */
    public UUID[] received;
    
    
    
    /**
     * Protocol for transfering {@link Multicast}s
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    public static class MulticastTransferProtocol implements TransferProtocol<Multicast>
    {
	//Has default constructor
	
	
	
	/**
	 * {@inheritDoc}
	 */
	public Multicast read(final TransferInputStream stream) throws IOException
	{   return new Multicast(stream.readObject(UUID.class), stream.readObject(UUID[].class), stream.readObject(UUID[].class));
	}
    
    
	/**
	 * {@inheritDoc}
	 */
	public void write(final Multicast data, final TransferOutputStream stream) throws IOException
	{   stream.writeObject(data.sender);
	    stream.writeObject(data.receivers);
	    stream.writeObject(data.received);
	}
    
    }
    
}

