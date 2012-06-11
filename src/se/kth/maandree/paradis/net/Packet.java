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
 * Network information packet class
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Packet implements Comparable<Packet>
{
    /**
     * Constructor
     * 
     * @param  uuid            The unique identifier of the package
     * @param  alsoSendToSelf  Whether to do a loop back send as well
     * @param  urgent          Whether the packet is urgent
     * @param  timeToLive      The time to live for the packet, in units of clients
     * @param  packetAge       The age of the packet, in units of clients
     * @param  cast            The cast information for the packet, either {@link Anycast}, {@link Unicast}, {@link Multicast} or {@link Broadcast}
     * @param  checksum        Packet checksum
     * @param  signature       Digital signature
     * @param  message         The message transmitted in the packet
     * @param  messageType     The type identifer for the message
     */
    public Packet(final UUID uuid, final boolean alsoSendToSelf, final boolean urgent, final short timeToLive, final short packetAge, final Cast cast, final byte[] checksum, final byte[] signature, final Object message, final String messageType)
    {
	this.uuid           = uuid;
	this.alsoSendToSelf = alsoSendToSelf;
	this.urgent         = urgent;
	this.timeToLive     = timeToLive;
	this.packetAge      = packetAge;
	this.cast           = cast;
	this.checksum       = checksum;
	this.signature      = signature;
	this.message        = message;
	this.messageType    = messageType;
    }
    
    
    
    /**
     * The unique identifier of the package
     */
    public final UUID uuid;
    
    /**
     * Whether to do a loop back send as well
     */
    public final boolean alsoSendToSelf;
    
    /**
     * Whether the packet is urgent
     */
    public final boolean urgent;
    
    /**
     * The time to live for the packet, in units of clients
     */
    public short timeToLive;
    
    /**
     * The age of the packet, in units of clients
     */
    public short packetAge;
    
    /**
     * The cast information for the packet, either {@link Anycast}, {@link Unicast}, {@link Multicast} or {@link Broadcast}
     */
    public final Cast cast;
    
    /**
     * Packet checksum
     */
    public final byte[] checksum;
    
    /**
     * Digital signature
     */
    public final byte[] signature;
    
    /**
     * The message transmitted in the packet
     */
    public final Object message;
    
    /**
     * The type identifer for the message
     */
    public final String messageType;
    
    
    
    /**
     * Protocol for transfering {@link Packet}s
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    public static class PacketTransferProtocol implements TransferProtocol<Packet>
    {
	//Has default constructor
	
	
	
	/**
	 * {@inheritDoc}
	 */
	public Packet read(final TransferInputStream stream) throws IOException
	{
	    final UUID uuid = stream.readObject(UUID.class);
	    final short ttl = stream.readShort();
	    final short age = stream.readShort();
	    byte bools = stream.readByte();
	    final boolean alsoSendToSelf = (bools & 1) != 0;
	    final boolean urgent = (bools & 2) != 0;
	    Class<? extends Cast> castClass = null;
	    if      ((bools & 12) == 0)  castClass = Anycast.class;
	    else if ((bools & 12) == 4)  castClass = Unicast.class;
	    else if ((bools & 12) == 8)  castClass = Multicast.class;
	    else                         castClass = Broadcast.class;
	    final Cast cast = stream.readObject(castClass);
	    final byte[] checksum = stream.readObject(byte[].class);
	    final byte[] signature = stream.readObject(byte[].class);
	    final String msgType = stream.readObject(String.class);
	    
	    final Class<?> msgClass = TransferProtocolRegister.getClassByID(msgType);
	    if ((msgClass != null) && (msgClass.isArray() == false))
		stream.readLen(); //skipping
	    final Object msg = stream.readObject(msgClass == null ? byte[].class : msgClass);
	    
	    return new Packet(uuid, alsoSendToSelf, urgent, ttl, age, cast, checksum, signature, msg, msgType);
	}
    
    
	/**
	 * {@inheritDoc}
	 */
	public void write(final Packet data, final TransferOutputStream stream) throws IOException
	{
	    stream.writeObject(data.uuid);
	    stream.writeShort(data.timeToLive);
	    stream.writeShort(data.packetAge);
	    byte bools = 0;
	    bools |= data.alsoSendToSelf ? 1 : 0;
	    bools |= data.urgent ? 2 : 0;
	    bools |= (data.cast instanceof Unicast) ? 4 : 0;
	    bools |= (data.cast instanceof Multicast) ? 8 : 0;
	    bools |= (data.cast instanceof Broadcast) ? 12 : 0;
	    stream.writeByte(bools);
	    stream.writeObject(data.cast);
	    stream.writeObject(data.checksum);
	    stream.writeObject(data.signature);
	    stream.writeObject(data.messageType);
	    if (data.message.getClass().isArray() == false)
		stream.writeLenOf(data.message);
	    stream.writeObject(data.message);
	}
    
    }
    
    
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object other)
    {
	if ((other == null) || (other instanceof Packet == false))
	    return false;
	
	if (other == this)
	    return true;
	
	return this.uuid.equals(((Packet)other).uuid);
    }
    
    
    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {   return this.uuid.hashCode();
    }
    
    
    /**
     * {@inheritDoc}
     */
    public int compareTo(final Packet other)
    {   return this.uuid.compareTo(other.uuid);
    }
    
}

