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
 * UDP server socket
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class UDPServer implements Runnable
{
    /**
     * Constructor
     * 
     * @param  localPort  The local port
     * 
     * @throws  IOException  On I/O error
     */
    protected UDPServer(final int localPort) throws IOException
    {
	this.socket = new DatagramSocket(localPort);
	this.localPort = socket.getLocalPort();
	
	(new Thread(this)).start();
    }
    
    
    
    /**
     * The native UDP socket
     */
    private final DatagramSocket socket;
    
    /**
     * The local port
     */
    public final int localPort;
    
    /**
     * Whether the socket is closing
     */
    protected boolean closing = false;
    
    /**
     * Whether {@link #run()} has started
     */
    private boolean runStarted = false;
    
    /**
     * Socket map
     */
    private final HashMap<String, UDPSocket> sockets = new HashMap<>();
    
    /**
     * List of new sockets
     */
    private final ArrayDeque<UDPSocket> newSockets = new ArrayDeque<>();
    
    
    
    /**
     * Waits for a new client, {@code null} is returned if the server socket is closing
     * 
     * @return  A socket binded to the new client through this server socket
     * 
     * @throws  IOException  On I/O error
     */
    public UDPSocket accept() throws IOException
    {
	synchronized (this.newSockets)
	{   if (this.newSockets.isEmpty())
		try
		{   this.newSockets.wait();
		}
		catch (final InterruptedException err)
		{   return null;
		}
	    
	    if (this.newSockets.isEmpty())
		return null;
	    return this.newSockets.pollFirst();
	}
    }
    
    
    /**
     * Closes the socket
     * 
     * @throws  IOException  On I/O error
     */
    public void close() throws IOException
    {
	this.closing = true;
	synchronized (this.newSockets)
	{   this.newSockets.notifyAll();
	}
	this.socket.close();
    }
    
    
    /**
     * Creates a UDP socket connected to a remote machine through this server socket
     * 
     * @param   remoteAddress  The remote machine's address
     * @param   remotePort     The remote machine's port
     * @erturn                 The UDP socket connected to the remote machine
     */
    public UDPSocket connect(final InetAddress remoteAddress, final int remotePort)
    {
	final String address = remoteAddress.getHostAddress() + ":" + remotePort;
	synchronized (this.sockets)
	{   UDPSocket sock = this.sockets.get(address);
	    if (sock == null)
	    {   sock = new UDPSocket(this.localPort, remoteAddress, remotePort);
		this.sockets.put(address, sock);
		bind(sock);
	    }
	    return sock;
	}
    }
    
    
    /**
     * Binds a UDP socket's output to tthe server socket
     * 
     * @parma  sock  The UDP socket
     */
    private void bind(final UDPSocket sock)
    {
	//
    }
    
    
    /**
     * Socket listener rutine
     */
    public void run()
    {
	if (runStarted)
	    throw new Error("Excuse me!");
	runStarted = true;
	
	try
	{
	    final byte[] bytes = new byte[0xC000];
	    final DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length);
	    
	    for (;;)
	    {
		this.socket.receive(packet);
		final String address = packet.getAddress().getHostAddress() + ":" + packet.getPort();
		
		UDPSocket sock;
		
		synchronized (this.sockets)
		{   if ((sock = this.sockets.get(address)) == null)
		    {   sock = new UDPSocket(this.localPort, packet.getAddress(), packet.getPort());
			this.sockets.put(address, sock);
			bind(sock);
			synchronized (this.newSockets)
			{   this.newSockets.offerLast(sock);
			    this.newSockets.notifyAll();
		}   }   }
		
		sock.inputStreamFeeder.write(packet.getData(), packet.getOffset(), packet.getLength());
	    }
	}
	catch (final Throwable err)
	{   if (this.closing == false)
		err.printStackTrace(System.err);
	}
    }
    
}

