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
import se.kth.maandree.paradis.util.*;
import se.kth.maandree.paradis.*;

import java.io.*;
import java.net.*;
import java.util.*;

//TODO:  Routing information and UUID address lookup is needed
//TODO:  A router, that extends this class, would be nice


/**
 * Network hub
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
@requires("java-environment>=7")
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
        this.localPort = this.server.localPort;
        this.localUser = localUser;
        
        final Thread acceptthread = new Thread("Hub connection accepter")
                {   @Override
                    public void run()
                    {   try
                        {   for (;;)
                            {
                                final UDPSocket socket = Hub.this.server.accept();
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
    protected final WeakHashMap<UUID, Void> receivedPacketsSet = new WeakHashMap<>();
    
    /**
     * Time queue of UUID:s for already received packets
     */
    protected final LimitedTimeQueue<UUID> receivedPacketsQueue = new LimitedTimeQueue<>();
    
    /**
     * <p>Remote user lookup map.</p>
     * <p>
     *   Synchronise with {@link #sockets} instead of the object.
     * </p>
     */
    public final HashMap<UDPSocket, UUID> socketUUIDs = new HashMap<>();
    
    /**
     * <p>Reverse {@link #socketUUIDs}.</p>
     * <p>
     *   Synchronise with {@link #sockets} instead of the object.
     * </p>
     */
    public final HashMap<UUID, UDPSocket> uuidSockets = new HashMap<>();
    
    /**
     * Set of dead sockets
     */
    protected final HashSet<UDPSocket> deadSockets = new HashSet<>();
    
    /**
     * Errors throws by sending mechanism, use this with synchronisation on itself.
     * You can get check if it is empty or wait for notifications can poll errors from it.
     */
    @requires("java-runtime>=6")
    public final ArrayDeque<Throwable> errors = new ArrayDeque<>();
    
    /**
     * Mapping form address:port to socket
     */
    public final HashMap<InetAddress, HashMap<Integer, UDPSocket>> connections = new HashMap<>();
    
    /**
     * Multicast groups the user has joined
     */
    protected final HashSet<UUID> multicastGroups = new HashSet<>();
    
    
    
    /**
     * Fetches the next packet in the inbox, and waits for one if it is empty
     * 
     * @return  The next packet in the inbox
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
        synchronized (this.receivedPacketsSet)
        {   this.receivedPacketsSet.put(packet.uuid, null);
            this.receivedPacketsQueue.offer(packet.uuid);
        }
        
        packet.cast.addReceived(this.localUser.getUUID());
        
        if (packet.alsoSendToSelf)
            synchronized (this.inbox)
            {
                if (packet.urgent)  this.inbox.offerFirst(packet);
                else                this.inbox.offerLast(packet);
            }
        
        if ((packet.packetAge = 0) < packet.timeToLive)
            route(packet);
    }
    
    
    /**
     * Joined a multicast group
     * 
     * @param  group  The group to join
     */
    public void joinGroup(final UUID group)
    {   synchronized (this.multicastGroups)
        {   this.multicastGroups.add(group);
    }   }
    
    
    /**
     * Leaves a multicast group
     * 
     * @param  group  The group to leave
     */
    public void leaveGroup(final UUID group)
    {   synchronized (this.multicastGroups)
        {   this.multicastGroups.remove(group);
    }   }
    
    
    /**
     * Closes the hub
     * 
     * @throws  IOException  On closing error (unlikely) 
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
        UDPSocket socket = null;
        synchronized (this.connections)
        {
            final HashMap<Integer, UDPSocket> map = this.connections.get(remoteAddress);
            if (map != null)
                socket = map.get(Integer.valueOf(remotePort));
        }
        
        if (socket == null)
            hostSocket(this.server.connect(remoteAddress, remotePort));
        else
            try
            {   if (socket.isAlive())
                    synchronized (this.deadSockets)
                    {   this.deadSockets.remove(socket);
            }       }
            catch (final Exception ignore)
            {   // ignore
            }
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
                {   @Override
                    public void run()
                    {   for (;;)
                        {   try
                            {
                                final Packet packet = socket.inputStream.readObject(Packet.class);
                                packet.packetAge++;
                                boolean route;
                                boolean mine;
                                
                                synchronized (Hub.this.deadSockets)
                                {   Hub.this.deadSockets.remove(socket);
                                }
                                
                                synchronized (Hub.this.receivedPacketsSet)
                                {   if (Hub.this.receivedPacketsSet.containsKey(packet.uuid))
                                        continue;
                                    Hub.this.receivedPacketsSet.put(packet.uuid, null);
                                    Hub.this.receivedPacketsQueue.offer(packet.uuid);
                                }
                                
                                if (packet.cast instanceof Anycast)
                                {
                                    mine = true;
                                    route = false;
                                }
                                else if (packet.cast instanceof Unicast)
                                {
                                    route = !(mine = ((Unicast)(packet.cast)).receiver.equals(Hub.this.localUser.getUUID()));
                                }
                                else if (packet.cast instanceof Multicast)
                                {
                                    mine = Arrays.binarySearch(((Multicast)(packet.cast)).receivers, Hub.this.localUser.getUUID()) >= 0;
                                    route = (((Multicast)(packet.cast)).receivers.length - (mine ? 1 : 0)) > 0;
                                    
                                    synchronized (Hub.this.multicastGroups)
                                    {   if (Hub.this.multicastGroups.isEmpty() == false)
                                            for (final UUID group : ((Multicast)(packet.cast)).receivers)
                                                if (Hub.this.multicastGroups.contains(group))
                                                {   mine = true;
                                                    break;
                                                }
                                    }
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
                                        Hub.this.inbox.notifyAll();
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
        if      (packet.cast instanceof   Anycast)  anycast  (packet);
        else if (packet.cast instanceof   Unicast)  unicast  (packet);
        else if (packet.cast instanceof Multicast)  multicast(packet);
        else if (packet.cast instanceof Broadcast)  broadcast(packet);
        else
            throw new Error("Update cast list in ~.net.Hub");
    }
    
    
    /**
     * Sends a packet to everyone else that should have a copy, using anycast mechanism
     * 
     * @param  packet  The packet to send
     * 
     * @throws  IOException  On I/O error
     */
    protected void anycast(final Packet packet) throws IOException
    {
        synchronized (this.sockets)
        {   for (final UDPSocket socket : this.sockets)
            {
                synchronized (this.deadSockets)
                {   if (this.deadSockets.contains(socket))
                        continue;
                }
                socket.outputStream.writeObject(packet);
                socket.outputStream.flush();
                synchronized (socket.errors)
                {   if (socket.errors.pollFirst() != null)
                        synchronized (this.deadSockets)
                        {
                            this.deadSockets.add(socket);
                            continue;
                }       }
                return;
            }
            for (final UDPSocket socket : this.sockets)
            {
                synchronized (this.deadSockets)
                {   if (this.deadSockets.contains(socket))
                        this.deadSockets.remove(socket);
                }
                socket.outputStream.writeObject(packet);
                socket.outputStream.flush();
                synchronized (socket.errors)
                {   if (socket.errors.pollFirst() != null)
                        synchronized (this.deadSockets)
                        {
                            this.deadSockets.add(socket);
                            continue;
                }       }
                return;
            }
            synchronized (this.errors)
            {
                this.errors.offerLast(new NoneAliveException("No alive peers to anycast to."));
        }   }
    }
    
    
    /**
     * Sends a packet to everyone else that should have a copy, using unicast mechanism
     * 
     * @param  packet  The packet to send
     * 
     * @throws  IOException  On I/O error
     */
    protected void unicast(final Packet packet) throws IOException
    {
        final UUID receiver = ((Unicast)(packet.cast)).receiver;
        final UDPSocket socket;
        synchronized (this.sockets)
        {   socket = this.uuidSockets.get(receiver);
        }
        if (socket == null)
            synchronized (this.errors)
            {   this.errors.offerLast(new UnknownPathException("Don't know how to reach peer."));
                return;
            }
        socket.outputStream.writeObject(packet);
        socket.outputStream.flush();
        synchronized (socket.errors)
        {   if (socket.errors.pollFirst() != null)
                synchronized (this.deadSockets)
                {   this.deadSockets.add(socket);
                    synchronized (this.errors)
                    {   this.errors.offerLast(new PeerIsDeadException("Peer is dead."));
        }       }   }
    }
    
    
    /**
     * Sends a packet to everyone else that should have a copy, using multicast mechanism
     * 
     * @param  packet  The packet to send
     * 
     * @throws  IOException  On I/O error
     */
    protected void multicast(final Packet packet) throws IOException
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
                {   final UDPSocket socket = this.uuidSockets.get(receiver);
                    if (socket != null)
                    {   alreadyListed.add(sendTo[ptr++] = socket);
                        direct++;
                }   }
            }
            
            if (direct < receivers.length)
                for (final UDPSocket socket : this.sockets)
                    if (alreadyListed.contains(socket) == false)
                    {
                        final UUID uuid = this.socketUUIDs.get(socket);
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
            synchronized (this.deadSockets)
            {   if (this.deadSockets.contains(socket))
                    continue;
            }
            socket.outputStream.writeObject(packet);
            socket.outputStream.flush();
            synchronized (socket.errors)
            {   if (socket.errors.pollFirst() != null)
                    synchronized (this.deadSockets)
                    {   this.deadSockets.add(socket);
            }       }
        }
    }
    
    
    /**
     * Sends a packet to everyone else that should have a copy, using broadcast mechanism
     * 
     * @param  packet  The packet to send
     * 
     * @throws  IOException  On I/O error
     */
    protected void broadcast(final Packet packet) throws IOException
    {
        final UDPSocket[] sendTo;
        int ptr = 0;
        
        synchronized (this.sockets)
        {   sendTo = new UDPSocket[this.sockets.size()];
            for (final UDPSocket socket : this.sockets)
            {   final UUID uuid = this.socketUUIDs.get(socket);
                if (uuid == null)
                    sendTo[ptr++] = socket;
                else if (packet.cast.hasReceived(uuid) == false)
                {   packet.cast.addReceived(uuid);
                    sendTo[ptr++] = socket;
        }   }   }
        
        for (int i = 0; i < ptr; i++)
        {   final UDPSocket socket = sendTo[i];
            synchronized (this.deadSockets)
            {   if (this.deadSockets.contains(socket))
                    continue;
            }
            socket.outputStream.writeObject(packet);
            socket.outputStream.flush();
            synchronized (socket.errors)
            {   if (socket.errors.pollFirst() != null)
                    synchronized (this.deadSockets)
                    {   this.deadSockets.add(socket);
            }       }
        }
    }
    
}

