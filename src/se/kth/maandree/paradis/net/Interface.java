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

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * Network interface, something of a façade
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Interface
{
    /**
     * Constructor
     * 
     * @param  localPort  The local port
     * @param  localUser  The local user
     * 
     * @throws  IOException  On I/O error
     */
    public Interface(final int localPort, final User localUser) throws IOException
    {
	this.hub = new Hub(localPort, localUser);
	
	this.localPort = this.hub.localPort;
	this.localUser = this.hub.localUser;
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
     * Network hub
     */
    public final Hub hub;
    
    
    
    /**
     * Fetches the next packet in the inbox, and waits for one if it is empty
     * 
     * @param  The next packet in the inbox
     */
    public Packet receive()
    {
	final Packet packet = this.hub.receive();
	final Cast cast = packet.cast;
	String address = null;
	if (cast instanceof Anycast)    address = ((Anycast)cast).senderAddress;
	if (cast instanceof Unicast)    address = ((Unicast)cast).senderAddress;
	if (cast instanceof Multicast)  address = ((Multicast)cast).senderAddress;
	if (cast instanceof Broadcast)  address = ((Broadcast)cast).senderAddress;
	
	if (address != null)
	    try
	    {
		final String _pub = address.substring(0, address.indexOf("/"));
		final String _port = address.substring(address.lastIndexOf(':') + 1);
		String _loc = address.substring(_pub.length() + 1);
		_loc = _loc.substring(0, _port.length() - 1);
		
		final int port = Integer.parseInt(_port);
		final InetAddress host = _pub.equals(localUser.getPublicIP())
		                         ? InetAddress.getByName(_loc)
		                         : InetAddress.getByName(_pub);
		
		final HashMap<Integer, UDPSocket> map = this.hub.connections.get(host);
		if ((map == null) || (map.containsKey(Integer.valueOf(port))) == false)
		    connect(host, port);
	    }
	    catch (final Exception ignore)
	    {   //Ignore
	    }
	
	return packet;
    }
    
    
    /**
     * Connectes the hub to a remote machine
     * 
     * @param  remoteAddress  The remote machine's address
     * @param  remotePort     The remote machine's port
     */
    public void connect(final InetAddress remoteAddress, final int remotePort)
    {   this.hub.connect(remoteAddress, remotePort);
    }
    
    
    /**
     * Sends a packet
     * 
     * @param  packet  The packet to send
     * 
     * @throws  IOException  On I/O error
     */
    public void send(final Packet packet) throws IOException
    {   this.hub.send(packet);
    }
    
    
    /**
     * Joined a multicast group
     * 
     * @param  group  The group to join
     */
    public void joinGroup(final UUID group)
    {   this.hub.joinGroup(group);
    }
    
    
    /**
     * Leaves a multicast group
     * 
     * @param  group  The group to leave
     */
    public void leaveGroup(final UUID group)
    {   this.hub.leaveGroup(group);
    }
    
    
    /**
     * Closes the interface
     */
    public void close() throws IOException
    {   this.hub.close();
    }
    
}

