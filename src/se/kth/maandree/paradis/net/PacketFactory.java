/**
 *  Paradis — Ever growing network for parallel and distributed computing.
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


/**
 * Network information packet factory
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PacketFactory
{
    /**
     * Constructor
     * 
     * @param  localUser       The local user
     * @param  alsoSendToSelf  Whether to do a loop back send as well
     * @param  urgent          Whether the assemblied packets are urgent
     * @param  timeToLive      The time to live for the assemblied packets, in units of clients
     */
    public PacketFactory(final User localUser, final boolean alsoSendToSelf, final boolean urgent, final short timeToLive)
    {
        this.localUser      = localUser;
        this.alsoSendToSelf = alsoSendToSelf;
        this.urgent         = urgent;
        this.timeToLive     = timeToLive;
        this.address        = localUser.getAddress();
    }
    
    
    
    /**
     * The local user
     */
    private final User localUser;
    
    /**
     * Whether to do a loop back send as well
     */
    private final boolean alsoSendToSelf;
    
    /**
     * Whether the assemblied packets are urgent
     */
    private final boolean urgent;
    
    /**
     * The time to live for the assemblied packets, in units of clients
     */
    private final short timeToLive;
    
    /**
     * The address:port of the local use
     */
    private final String address;
    
    
    
    /**
     * Creates an anycast packet
     * 
     * @param   message      The message transmitted in the packet (payload)
     * @param   messageType  The type identifer for the message
     * @return               The created packet
     */
    public Packet createAnycast(final Object message, final String messageType)
    {   return createPacket(message, messageType, new Anycast(this.localUser.getUUID(), null));
    }
    
    /**
     * Creates an unicast packet
     * 
     * @param   message      The message transmitted in the packet (payload)
     * @param   messageType  The type identifer for the message
     * @param   receiver     The UUID of the intended receiver
     * @return               The created packet
     */
    public Packet createUnicast(final Object message, final String messageType, final UUID receiver)
    {   return createPacket(message, messageType, new Unicast(this.localUser.getUUID(), receiver, null));
    }
    
    /**
     * Creates an multicast packet
     * 
     * @param   message      The message transmitted in the packet (payload)
     * @param   messageType  The type identifer for the message
     * @param   receivers    The UUID:s of the intended receivers
     * @return               The created packet
     */
    public Packet createMulticast(final Object message, final String messageType, final UUID... receivers)
    {   return createPacket(message, messageType, new Multicast(this.localUser.getUUID(), receivers, this.address));
    }
    
    /**
     * Creates an broadcast packet
     * 
     * @param   message      The message transmitted in the packet (payload)
     * @param   messageType  The type identifer for the message
     * @return               The created packet
     */
    public Packet createBroadcast(final Object message, final String messageType)
    {   return createPacket(message, messageType, new Broadcast(this.localUser.getUUID(), this.address));
    }
    
    
    /**
     * Creates a packet
     * 
     * @param   message      The message transmitted in the packet (payload)
     * @param   messageType  The type identifer for the message
     * @param   cast         The cast information
     * @return               The created packet
     */
    private Packet createPacket(final Object message, final String messageType, final Cast cast)
    {   return new Packet(new UUID(), this.alsoSendToSelf, this.urgent, this.timeToLive, (short)0, cast, new byte[0], new byte[0], message, messageType);
    }
    
    
    /**
     * Forks the factory
     * 
     * @param   timeToLive  The time to live for the assemblied packets, in units of clients
     * @return              A new packet factory
     */
    @SuppressWarnings("hiding")
    public PacketFactory fork(final short timeToLive)
    {   return new PacketFactory(this.localUser, this.alsoSendToSelf, this.urgent, timeToLive);
    }
    
    /**
     * Forks the factory
     * 
     * @param   alsoSendToSelf  Whether to do a loop back send as well
     * @param   urgent          Whether the assemblied packets are urgent
     * @return                  A new packet factory
     */
    @SuppressWarnings("hiding")
    public PacketFactory fork(final boolean alsoSendToSelf, final boolean urgent)
    {   return new PacketFactory(this.localUser, alsoSendToSelf, urgent, this.timeToLive);
    }
    
    /**
     * Forks the factory
     * 
     * @param   alsoSendToSelf  Whether to do a loop back send as well
     * @param   urgent          Whether the assemblied packets are urgent
     * @param   timeToLive      The time to live for the assemblied packets, in units of clients
     * @return                  A new packet factory
     */
    @SuppressWarnings("hiding")
    public PacketFactory fork(final boolean alsoSendToSelf, final boolean urgent, final short timeToLive)
    {   return new PacketFactory(this.localUser, alsoSendToSelf, urgent, timeToLive);
    }
    
    /**
     * Forks the factory
     * 
     * @param   alsoSendToSelf  Whether to do a loop back send as well
     * @return                  A new packet factory
     */
    @SuppressWarnings("hiding")
    public PacketFactory forkLoopback(final boolean alsoSendToSelf)
    {   return new PacketFactory(this.localUser, alsoSendToSelf, this.urgent, this.timeToLive);
    }
    
    /**
     * Forks the factory
     * 
     * @param   alsoSendToSelf  Whether to do a loop back send as well
     * @param   timeToLive      The time to live for the assemblied packets, in units of clients
     * @return                  A new packet factory
     */
    @SuppressWarnings("hiding")
    public PacketFactory forkLoopback(final boolean alsoSendToSelf, final short timeToLive)
    {   return new PacketFactory(this.localUser, alsoSendToSelf, this.urgent, timeToLive);
    }
    
    /**
     * Forks the factory
     * 
     * @param   urgent  Whether the assemblied packets are urgent
     * @return          A new packet factory
     */
    @SuppressWarnings("hiding")
    public PacketFactory forkUrgent(final boolean urgent)
    {   return new PacketFactory(this.localUser, this.alsoSendToSelf, urgent, this.timeToLive);
    }
    
    /**
     * Forks the factory
     * 
     * @param   urgent      Whether the assemblied packets are urgent
     * @param   timeToLive  The time to live for the assemblied packets, in units of clients
     * @return              A new packet factory
     */
    @SuppressWarnings("hiding")
    public PacketFactory forkUrgent(final boolean urgent, final short timeToLive)
    {   return new PacketFactory(this.localUser, this.alsoSendToSelf, urgent, timeToLive);
    }
    
}

