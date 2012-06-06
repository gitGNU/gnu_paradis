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
 * Network tool kit
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Toolkit
{
    /**
     * Non-constructor
     */
    private Toolkit()
    {
        assert false : "You may not create instances of this class [Toolkit].";
    }
    
    
    
    /**
     * Gets the LAN's public IP address
     * 
     * @return  The LAN's public IP address
     * 
     * @throws  IOException  If it was not possible to get the IP address
     */
    public static String getPublicIP() throws IOException
    {
	final Socket sock = new Socket("checkip.dyndns.org", 80);
	final InputStream is = new BufferedInputStream(sock.getInputStream());
	final OutputStream os = new BufferedOutputStream(sock.getOutputStream());
	    
	final Scanner in = new Scanner(is);
	final PrintStream out = new PrintStream(os);
	    
	out.print("GET / HTTP/1.1\r\n");
	out.print("Host: checkip.dyndns.org\r\n");
	out.print("\r\n");
	out.flush();
	    
	for (;;)
	    if (in.nextLine().isEmpty())
		break;
	    
	String line = in.nextLine();
	sock.close();
	    
	line = line.substring(0, line.indexOf("</body>"));
	line = line.substring(line.lastIndexOf(' ') + 1);
	
	return line;
    }
    
    
    /**
     * Gets the LAN local IP address of the machine
     * 
     * @return  The LAN local IP address
     * 
     * @throws  IOException  If it was not possible to get the IP address
     * 
     * @author  Calle Lejdbrandt, <a href="mailto:callel@kth.se">callel@kth.se</a>, as a port of <a href="www.gitub.com/maandree/cnt">CNT</a>.
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>, changed failure precedure
     */
    public static String getLocalIP() throws IOException
    {
	// This is all because InetAddress.getLocalHost().getHostAddress() returns loopback (127.0.*.1) to where we cannot portforward
	// See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037 for details of problem.
	// This hopefully solves it. 
	
	try
        {
	    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) //Get all interfaces
	    {
		NetworkInterface iface = en.nextElement();
		if (iface.isUp() == false) // If the interface is not up, then we don't want to use it.
		    continue;
		
		for (InterfaceAddress eth : iface.getInterfaceAddresses()) // Get ALL addresses listed on the interface
		{
		    System.err.println("Possible Address: " + eth.getAddress().getHostAddress());
		    
		    // We don't want loopback or IPv6. TODO: better way of sorting out IPv6
		    if ((eth.getAddress().isLoopbackAddress() == false) && (eth.getAddress().getHostAddress().contains(":") == false))
		    {
			System.err.println("Choosen Address: " + eth.getAddress().getHostAddress());
			return eth.getAddress().getHostAddress();
		    }
		}
	    }
	}
	catch (final Throwable err)
	{   throw new IOException(err);
	}
	
	throw new IOException();
    }
    
    
    /**
     * Checks the alive status for the client, with test timeout at 4 seconds
     * 
     * @return  Statuses: 0. totally dead. 1. can reach to router, 2 can reach primary or
     *          secondary standard DNS server, 3 can get public IP address and can reach
     *          it, and satisfies level 2 as well.  Level 0 is returned if the client
     *          cannot get its local IP address or cannot reachit.
     */
    public static byte getAliveStatus()
    {
	try
	{   if (isReachable(getLocalIP()) == false)
		return 0;
	}
	catch (final Throwable err)
	{   return 0;
	}
	
	final boolean level1 = isReachable("192.168.0.1") || isReachable("192.168.1.1");	
	final boolean level2 = isReachable("83.255.245.11") || isReachable("193.150.193.150");
	boolean level3 = level2;
	if (level3)
	    try
	    {   level3 = isReachable(getPublicIP());
	    }
	    catch (final Throwable err)
	    {   level3 = false;
	    }
	
	if (level3)  return 3;
	if (level2)  return 2;
	if (level1)  return 1;
	return 0;
    }
    
    
    /**
     * Tests whether a host is reachable, with test timeout at 4 seconds
     * 
     * @param   host  The remote host's address, IP or DNS
     * @return        Whether the host is reachable
     */
    public static boolean isReachable(final String host)
    {
	try
	{   return InetAddress.getByName(host).isReachable(4_000);
	}
	catch (final Exception err)
        {   return false;
	}
    }
    
    
    /**
     * Gets a random port tested on TCP
     * 
     * @return  A random port
     * 
     * @throws  IOException  If a port cannot be choosen
     */
    public static int getRandomPortTCP() throws IOException
    {
	final ServerSocket socket = new ServerSocket(0);
	final int port = socket.getLocalPort();
	socket.close();
	return port;
    }
    
    
    /**
     * Gets a random port tested on UDP
     * 
     * @return  A random port
     * 
     * @throws  IOException  If a port cannot be choosen
     */
    public static int getRandomPortUCP() throws IOException
    {
	final DatagramSocket socket = new DatagramSocket(0);
	final int port = socket.getLocalPort();
	socket.close();
	return port;
    }
    
}

