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
import se.kth.maandree.paradis.io.PipedInputStream;
import se.kth.maandree.paradis.io.PipedOutputStream;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * Network hub
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Hub
{
    /**
     * Constructor
     * 
     * @param  localPort  The local port
     * @param  localUser  The local user
     * 
     * @throws  IOException  On I/O error
     */
    public Hub(final int localPort, final User localUser) throws IOException
    {
	assert (0 <= localPort) && (localPort < 0x10000) : "Invalid local port: " + localPort;
	assert localUser != null : "Invalid local user: null";
	
	this.server = new UDPServer(localPort);
	this.localPort = server.localPort;
	this.localUser = localUser;
	
	final Thread acceptthread = new Thread("Hub connection accepter")
	        {   public void run()
		    {   try
			{   for (;;)
			    {
				final UDPSocket socket = server.accept();
				if (socket == null)
				    return;
				Hub.this.hostSocket(socket);
			}   }
			catch (final Throwable err)
			{   err.printStackTrace(System.err);
		}   }   };
	
	acceptthread.setDaemon(true);
	acceptthread.start();
    }
    
    
    
    /**
     * The local port
     */
    public final int localPort;
    
    /**
     * The local user
     */
    public final User localUser;
    
    /**
     * The server socket
     */
    protected final UDPServer server;
    
    /**
     * Packet inbox deque
     */
    protected final ArrayDeque<Packet> inbox = new ArrayDeque<>();
    
    /**
     * Hosted sockets
     */
    protected final Vector<UDPSocket> sockets = new Vector<>();
    
    /**
     * Set of UUID:s for already received packets
     */
    protected final HashSet<UUID> receivedPackets = new HashSet<>(); //TODO: we need a mechanism that purges very old elements (stronger than soft references)
    
    /**
     * <p>Remote user lookup map.</p>
     * <p>
     *   Synchronise with {@link #sockets} instead of the object.
     * </p>
     */
    protected final HashMap<UDPSocket, UUID> socketUUIDs = new HashMap<>();
    
    /**
     * <p>Reverse {@link #socketUUIDs}.</p>
     * <p>
     *   Synchronise with {@link #sockets} instead of the object.
     * </p>
     */
    protected final HashMap<UUID, UDPSocket> uuidSockets = new HashMap<>();
    
    
    
    /**
     * Fetches the next packet in the inbox, and waits for one if it is empty
     * 
     * @param  The next packet in the inbox
     */
    public Packet receive()
    {
	synchronized (this.inbox)
	{
	    if (this.inbox.isEmpty())
		try
		{   this.inbox.wait();
		}
		catch (final InterruptedException err)
		{   return null;
		}
	    
	    return this.inbox.pollFirst();
	}
    }
    
    
    /**
     * Sends a packet
     * 
     * @param  packet  The packet to send
     * 
     * @throws  IOException  On I/O error
     */
    public void send(final Packet packet) throws IOException
    {
	synchronized (receivedPackets)
	{   receivedPackets.add(packet.uuid);
	}
	
	packet.cast.addReceived(this.localUser.uuid);
	
	if (packet.alsoSendToSelf)
	    synchronized (this.inbox)
	    {
		if (packet.urgent)  Hub.this.inbox.offerFirst(packet);
		else                Hub.this.inbox.offerLast(packet);
	    }
	
	if ((packet.packetAge = 0) < packet.timeToLive)
	    route(packet);
    }
    
    
    /**
     * Closes the hub
     */
    public void close() throws IOException
    {
	this.server.close();
    }
    
    
    /**
     * Connectes the hub to a remote machine
     * 
     * @param  remoteAddress  The remote machine's address
     * @param  remotePort     The remote machine's port
     */
    public void connect(final InetAddress remoteAddress, final int remotePort)
    {
	hostSocket(this.server.connect(remoteAddress, remotePort));
    }
    
    
    /**
     * Starts handling a socket
     * 
     * @param  socket  The socket
     */
    protected void hostSocket(final UDPSocket socket)
    {
	synchronized (this.sockets)
	{   this.sockets.add(socket);
	}
	
	final Thread thread = new Thread("Hub connection")
	        {   public void run()
		    {   for (;;)
			{   try
			    {
				final Packet packet = socket.inputStream.readObject(Packet.class);
				packet.packetAge++;
				boolean route;
				boolean mine;
				
				synchronized (receivedPackets)
				{   if (receivedPackets.contains(packet.uuid))
					continue;
				    receivedPackets.add(packet.uuid);
				}
				
				if (packet.cast instanceof Anycast)
				{
				    mine = true;
				    route = false;
				}
				else if (packet.cast instanceof Unicast)
				{
				    route = !(mine = ((Unicast)(packet.cast)).receiver.equals(localUser.uuid));
				}
				else if (packet.cast instanceof Multicast)
				{
				    mine = Arrays.binarySearch(((Multicast)(packet.cast)).receivers, localUser.uuid) >= 0;
				    route = (((Multicast)(packet.cast)).receivers.length - (mine ? 1 : 0)) > 0;
				}
				else if (packet.cast instanceof Broadcast)
				{
				    mine = route = true;
				}
				else
				    throw new Error("Update cast list in ~.net.Hub");
				
				if (mine)
				    synchronized (Hub.this.inbox)
				    {   if (packet.urgent)  Hub.this.inbox.offerFirst(packet);
					else                Hub.this.inbox.offerLast(packet);
				    }
				
				if (route)
				    if (packet.packetAge < packet.timeToLive)
					Hub.this.route(packet);
			    }
			    catch (final Throwable err)
			    {   err.printStackTrace(System.err);
				try
				{   Thread.sleep(500);
				}
				catch (final InterruptedException ierr)
				{   return;
		}   }   }   }   };
	
	thread.setDaemon(true);
	thread.start();
    }
    
    
    /**
     * Sends a packet to everyone else that should have a copy
     * 
     * @param  packet  The packet to send
     * 
     * @throws  IOException  On I/O error
     */
    protected void route(final Packet packet) throws IOException
    {
	if (packet.cast instanceof Anycast)
	{
	}
	else if (packet.cast instanceof Unicast)
	{
	}
	else if (packet.cast instanceof Multicast)
	{
	    final UDPSocket[] sendTo;
	    int ptr = 0;
	    
	    synchronized (this.sockets)
	    {
		sendTo = new UDPSocket[this.sockets.size()];
		int direct = 0;
		final HashSet<UDPSocket> alreadyListed = new HashSet<>();
		
		final UUID[] receivers = ((Multicast)(packet.cast)).receivers;
		for (final UUID receiver : receivers)
		{
		    if (packet.cast.hasReceived(receiver))
			direct++;
		    else
		    {   final UDPSocket socket = uuidSockets.get(receiver);
			if (socket != null)
			{   alreadyListed.add(sendTo[ptr++] = socket);
			    direct++;
		    }   }
		}
		
		if (direct < receivers.length)
		    for (final UDPSocket socket : this.sockets)
			if (alreadyListed.contains(socket) == false)
			{
			    final UUID uuid = socketUUIDs.get(socket);
			    if (uuid == null)
				sendTo[ptr++] = socket;
			    else if (packet.cast.hasReceived(uuid) == false)
			    {
				packet.cast.addReceived(uuid);
				sendTo[ptr++] = socket;
			    }
			}
	    }
	    
	    for (int i = 0; i < ptr; i++)
	    {   final UDPSocket socket = sendTo[i];
		socket.outputStream.writeObject(packet);
		socket.outputStream.flush();
	    }
	}
	else if (packet.cast instanceof Broadcast)
	{
	    final UDPSocket[] sendTo;
	    int ptr = 0;
	    
	    synchronized (this.sockets)
	    {   sendTo = new UDPSocket[this.sockets.size()];
		for (final UDPSocket socket : this.sockets)
		{   final UUID uuid = socketUUIDs.get(socket);
		    if (uuid == null)
			sendTo[ptr++] = socket;
		    else if (packet.cast.hasReceived(uuid) == false)
		    {   packet.cast.addReceived(uuid);
			sendTo[ptr++] = socket;
	    }   }   }
	    
	    for (int i = 0; i < ptr; i++)
	    {   final UDPSocket socket = sendTo[i];
		socket.outputStream.writeObject(packet);
		socket.outputStream.flush();
	    }
	}
	else
	    throw new Error("Update cast list in ~.net.Hub");
    }
    
}

