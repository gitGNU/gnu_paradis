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
import se.kth.maandree.paradis.io.PipedInputStream; //Explicit
import se.kth.maandree.paradis.io.PipedOutputStream; //Explicit
import se.kth.maandree.paradis.*;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * UDP socket created by {@link UDPServer}
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class UDPSocket
{
    /** ASCII control character */ private static final byte NULL = 0x00;
    /** ASCII control character */ private static final byte START_OF_HEADING = 0x01;
    /** ASCII control character */ private static final byte START_OF_TEXT = 0x02;
    /** ASCII control character */ private static final byte END_OF_TEXT = 0x03;
    /** ASCII control character */ private static final byte END_OF_TRANSMISSION = 0x04;
    /** ASCII control character */ private static final byte ENQUIRY = 0x05;
    /** ASCII control character */ private static final byte ACKNOWLEDGE = 0x06;
    /** ASCII control character */ private static final byte BELL = 0x07;
    /** ASCII control character */ private static final byte BACKSPACE = 0x08;
    /** ASCII control character */ private static final byte CHARACTER_TABULATION = 0x09;
    /** ASCII control character */ private static final byte LINE_FEED = 0x0A;
    /** ASCII control character */ private static final byte LINE_TABULATION = 0x0B;
    /** ASCII control character */ private static final byte FORM_FEED = 0x0C;
    /** ASCII control character */ private static final byte CARRIAGE_RETURN = 0x0D;
    /** ASCII control character */ private static final byte SHIFT_OUT = 0x0E;
    /** ASCII control character */ private static final byte SHIFT_IN = 0x0F;
    /** ASCII control character */ private static final byte DATA_LINK_ESCAPE = 0x10;
    /** ASCII control character */ private static final byte DEVICE_CONTROL_ONE = 0x11;
    /** ASCII control character */ private static final byte DEVICE_CONTROL_TWO = 0x12;
    /** ASCII control character */ private static final byte DEVICE_CONTROL_THREE = 0x13;
    /** ASCII control character */ private static final byte DEVICE_CONTROL_FOUR = 0x14;
    /** ASCII control character */ private static final byte NEGATIVE_ACKNOWLEDGE = 0x15;
    /** ASCII control character */ private static final byte SYNCHRONOUS_IDLE = 0x16;
    /** ASCII control character */ private static final byte END_OF_TRANSMISSION_BLOCK = 0x17;
    /** ASCII control character */ private static final byte CANCEL = 0x18;
    /** ASCII control character */ private static final byte END_OF_MEDIUM = 0x19;
    /** ASCII control character */ private static final byte SUBSTITUTE = 0x1A;
    /** ASCII control character */ private static final byte ESCAPE = 0x1B;
    /** ASCII control character */ private static final byte INFORMATION_SEPARATOR_FOUR = 0x1C;
    /** ASCII control character */ private static final byte INFORMATION_SEPARATOR_THREE = 0x1D;
    /** ASCII control character */ private static final byte INFORMATION_SEPARATOR_TWO = 0x1E;
    /** ASCII control character */ private static final byte INFORMATION_SEPARATOR_ONE = 0x1F;
    /** ASCII control character */ private static final byte DELETE = 0x7F;
    
    /**
     * Time to wait for acknowledgement before deeming receiver dead
     */
    public static final int TIME_OUT = 4_000;
    
    
    
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
	    
	    this.outputStreamReader = new TransferInputStream(new PipedInputStream(_outputStream));
	    this.inputStreamFeeder = new TransferOutputStream(new PipedOutputStream(_inputStream));
	    
	    this.inputStream = new TransferInputStream(_inputStream);
	    this.outputStream = new TransferOutputStream(_outputStream);
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
    public final TransferInputStream inputStream;
    
    /**
     * Output stream for the socket
     */
    public final TransferOutputStream outputStream;
    
    
    /**
     * Output stream for {@link #inputStream}
     */
    protected final TransferOutputStream inputStreamFeeder;
    
    /**
     * Input stream for {@link #outputStream}
     */
    protected final TransferInputStream outputStreamReader;
    
    
    /**
     * Transmission success monitor
     */
    private final Object transmissionMonitor = new Object();
    
    /**
     * Whether the socket is waiting on {@link #transmissionMonitor}
     */
    private boolean waiting = false;
    
    
    /**
     * Alive enquiry monitor
     */
    private final Object enquiryMonitor = new Object();
    
    /**
     * Whether the socket is waiting on {@link #enquiryMonitor}
     */
    private boolean ackWaiting = false;
    
    
    /**
     * Errors throws by sending mechanism, use this with synchronisation on itself.
     * You can get check if it is empty or wait for notifications can poll errors from it.
     */
    public final ArrayDeque<Throwable> errors = new ArrayDeque<>();
    
    
    
    /**
     * Toll the bell to inform that you are alive or connects
     * 
     * @throws  IOException  On I/O error
     */
    protected void toll() throws IOException
    {   this.server.socket.send(new DatagramPacket(new byte[] { BELL }, 0, 1, this.remoteAddress, this.remotePort));
    }
    
    
    /**
     * Invoke to send a datagram packet
     * 
     * @param  packet  The datagram packet to send
     * 
     * @throws  IOException  On I/O error
     */
    protected void send(final DatagramPacket packet) throws IOException
    {
	if (packet.getData().length == packet.getLength())
	{
	    final int len = packet.getLength() + 1;
	    final byte[] data = new byte[len];
	    System.arraycopy(packet.getData(), packet.getOffset(), data, 1, len - 1);
	    packet.setData(data, 0, len);
	}
	else
	{
	    System.arraycopy(packet.getData(), packet.getOffset(), packet.getData(), packet.getOffset() + 1, packet.getLength());
	    packet.setLength(packet.getLength() + 1);
	}
	
	packet.getData()[packet.getOffset()] = START_OF_TEXT;
	
	this.waiting = true;
	synchronized (this.server.outMonitor)
	{   this.server.socket.send(packet);
	}
	
	synchronized (this.transmissionMonitor)
	{   try
	    {   this.transmissionMonitor.wait(TIME_OUT);
		if (this.waiting)
		{   this.waiting = false;
		    synchronized (this.errors)
		    {   this.errors.offerLast(new ConnectException("Timed out, receiver is probabily dead."));
			this.errors.notifyAll();
		    }
		}
	    }
	    catch (final InterruptedException ignore)
	    {   //ignore
	    }
	}
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
	final byte signal = packet.getData()[packet.getOffset()];
	
	if (signal == START_OF_TEXT)
	{
	    synchronized (this.server.outMonitor)
	    {   this.server.socket.send(new DatagramPacket(new byte[] { END_OF_TEXT }, 0, 1, this.remoteAddress, this.remotePort));
	    }
	    this.inputStreamFeeder.write(packet.getData(), packet.getOffset() + 1, packet.getLength() - 1);
	    this.inputStreamFeeder.flush();
	}
	else if (signal == END_OF_TEXT)
	{
	    if (this.waiting)
		synchronized (this.transmissionMonitor)
	        {   this.waiting = false;
		    this.transmissionMonitor.notify();
		}
	}
	else if (signal == ENQUIRY)
	{
	    boolean ok = false;
	    try
	    {   final String remote = new String(packet.getData(), packet.getOffset() + 1, packet.getLength() - 1, "UTF-8");
		ok = remote.equals(Program.PACKAGE + (char)END_OF_TRANSMISSION);
	    }
	    catch (final Throwable err)
	    {   ok = false;
	    }
	    synchronized (this.server.outMonitor)
	    {   this.server.socket.send(new DatagramPacket(new byte[] { ok ? ACKNOWLEDGE : NEGATIVE_ACKNOWLEDGE }, 0, 1, this.remoteAddress, this.remotePort));
	    }
	}
	else if (signal == BELL)
	    return;
	
	if (this.ackWaiting)
	    synchronized (this.enquiryMonitor)
	    {   this.ackWaiting = (signal == NEGATIVE_ACKNOWLEDGE);
		this.enquiryMonitor.notify();
	    }
    }
    
    
    /**
     * <p>Used to check if the remote client is alive.</p>
     * <p>
     *   Note that this is stricter than reachablitiy which is provided by {@link Toolkit}.
     * </p>
     * 
     * @return  Whether this remote client is alive
     * 
     * @throws  IOException  On failure on your part
     */
    public boolean isAlive() throws IOException
    {
	ackWaiting = true;
	synchronized (this.server.outMonitor)
	{   final byte[] data = ((char)ENQUIRY + Program.PACKAGE + (char)END_OF_TRANSMISSION).getBytes("UTF-8");
	    this.server.socket.send(new DatagramPacket(data, 0, data.length, this.remoteAddress, this.remotePort));
	}
	
	synchronized (this.enquiryMonitor)
	{   try
	    {   this.enquiryMonitor.wait(TIME_OUT);
		if (this.ackWaiting)
		{   this.ackWaiting = false;
		    return false;
		}
	    }
	    catch (final InterruptedException ignore)
	    {   //ignore
	    }
	}
	
	return true;
    }
    
    
}

