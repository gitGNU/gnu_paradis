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


/**
 * UDP socket created by {@link UDPServer}
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class UDPSocket
{
    /**
     * <p>Constructor</p>
     * <p>
     *   Note that the socket must be binded by the {@link UDPServer}
     * </p>
     * 
     * @param  localPort      The local port
     * @param  remoteAddress  The remote machine's address
     * @param  remotePort     The remote machine's port
     * @param  server         The {@link UDPServer}
     */
    protected UDPSocket(final int localPort, final InetAddress remoteAddress, final int remotePort, final UDPServer server)
    {
	this.localPort = localPort;
	this.remoteAddress = remoteAddress;
	this.remotePort = remotePort;
	this.server = server;
	
	try
	{
	    final PipedInputStream _inputStream = new PipedInputStream();
	    final PipedOutputStream _outputStream = new PipedOutputStream();
	    
	    this.outputStreamReader = new BufferedInputStream(new PipedInputStream(_outputStream));
	    this.inputStreamFeeder = new BufferedOutputStream(new PipedOutputStream(_inputStream));
	    
	    this.inputStream = new BufferedInputStream(_inputStream);
	    this.outputStream = new BufferedOutputStream(_outputStream);
	}
	catch (final IOException err)
	{
	    throw new IOError(err);
	}
    }
    
    
    
    /**
     * The local port
     */
    public final int localPort;
    
    /**
     * The remote machine's address
     */
    public final InetAddress remoteAddress;
    
    /**
     * The remote machine's port
     */
    public final int remotePort;
    
    
    /**
     * The {@link UDPServer}
     */
    private final UDPServer server;
    
    
    /**
     * Input stream for the socket
     */
    public final InputStream inputStream;
    
    /**
     * Output stream for the socket
     */
    public final OutputStream outputStream;
    
    
    /**
     * Output stream for {@link #inputStream}
     */
    protected final OutputStream inputStreamFeeder;
    
    /**
     * Input stream for {@link #outputStream}
     */
    protected final InputStream outputStreamReader;
    
    
    
    /**
     * Invoke to send a datagram packet
     * 
     * @param  packet  The datagram packet to send
     * 
     * @throws  IOException  On I/O error
     */
    protected void send(final DatagramPacket packet) throws IOException
    {
	this.server.socket.send(packet);
    }
    
    
    /**
     * Invoke when a datagram packet is received
     * 
     * @param  packet  The received datagram packet
     * 
     * @throws  IOException  On I/O error
     */
    protected void receive(final DatagramPacket packet) throws IOException
    {
	this.inputStreamFeeder.write(packet.getData(), packet.getOffset(), packet.getLength());
	this.inputStreamFeeder.flush();
    }
    
    
}

