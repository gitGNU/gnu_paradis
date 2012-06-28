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
import java.util.Arrays;


/**
 * Broadcast sending path information
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Broadcast implements Cast
{
    /**
     * Constructor
     * 
     * @param  sender         The sender of the packet
     * @param  senderAddress  The address:port of the send, {@code null} if not shared
     */
    public Broadcast(final UUID sender, final String senderAddress)
    {
	this(sender, new UUID[] { sender }, senderAddress);
    }
    
    /**
     * Constructor
     * 
     * @param  sender         The sender of the packet
     * @param  received       Clients known to have, or currenty is receiving, a copy of the packet
     * @param  senderAddress  The address:port of the send, {@code null} if not shared
     */
    protected Broadcast(final UUID sender, final UUID[] received, final String senderAddress)
    {
	this.sender = sender;
	this.received = received;
	this.receivedCount = received.length;
	this.senderAddress = senderAddress == null ? null : senderAddress.isEmpty() ? null : senderAddress;
    }
    
    
    
    /**
     * The sender of the packet
     */
    public final UUID sender;
    
    /**
     * Clients known to have, or currenty is receiving, a copy of the packet
     */
    public UUID[] received;
    
    /**
     * The logial length of {@link #received}
     */
    public int receivedCount;
    
    /**
     * The address:port of the send, {@code null} if not shared
     */
    public final String senderAddress;
    
    
    
    /**
     * Protocol for transfering {@link Broadcast}s
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    public static class BroadcastTransferProtocol implements TransferProtocol<Broadcast>
    {
	//Has default constructor
	
	
	
	/**
	 * {@inheritDoc}
	 */
	public Broadcast read(final TransferInputStream stream) throws IOException
	{   return new Broadcast(stream.readObject(UUID.class),
				 stream.readObject(UUID[].class),
				 stream.readObject(String.class));
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void write(final Broadcast data, final TransferOutputStream stream) throws IOException
	{
	    if (data.receivedCount != data.received.length)
	    {
		final UUID[] tmp = new UUID[data.receivedCount];
		System.arraycopy(data.received, 0, tmp, 0, data.receivedCount);
		data.received = tmp;
	    }
	    stream.writeObject(data.sender);
	    stream.writeObject(data.received);
	    stream.writeObject(data.senderAddress == null ? "" : data.senderAddress);
	}
	
    }
    
    
    
    /**
     * {@inheritDoc}
     */
    public void addReceived(final UUID uuid)
    {
	int pos = Arrays.binarySearch(this.received, 0, this.receivedCount, uuid);
	if (pos >= 0)
	    return;
	pos = ~pos;
	
	if (this.receivedCount == this.received.length)
	{
	    final UUID[] tmp = new UUID[this.receivedCount << 1];
	    System.arraycopy(this.received, 0, tmp, 0, this.received.length);
	    this.received = tmp;
	}
	
	System.arraycopy(this.received, pos, this.received, pos + 1, this.receivedCount - pos);
	this.received[pos] = uuid;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean hasReceived(final UUID uuid)
    {   return Arrays.binarySearch(this.received, 0, this.receivedCount, uuid) >= 0;
    }
    
}

