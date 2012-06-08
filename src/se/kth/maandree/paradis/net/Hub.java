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
    
    
    public void send(final byte[] data) throws IOException
    {
	synchronized (this.sockets)
	{   for (final UDPSocket socket : this.sockets)
	    {   socket.outputStream.write(data);
		socket.outputStream.flush();
	}   }
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
		    {   try
			{   final byte[] buf = new byte[1024];
			    for (;;)
			    {   final int len = socket.inputStream.read(buf);
				synchronized (System.out)
				{
				    System.out.write(buf, 0, len);
				    System.out.flush();
				}
			}   }
			catch (final Throwable err)
		        {   err.printStackTrace(System.err);
		}   }   };
	
	thread.setDaemon(true);
	thread.start();
    }
    
}

