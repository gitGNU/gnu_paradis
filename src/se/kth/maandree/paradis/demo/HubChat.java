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
package se.kth.maandree.paradis.demo;
import se.kth.maandree.paradis.net.*;
import se.kth.maandree.paradis.net.UUID; //Explicit

import java.util.*;
import java.net.*;
import java.io.*;


/**
 * Multi users chat using {@link Hub} demo
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class HubChat
{
    /**
     * Non-constructor
     */
    private HubChat()
    {
	assert false : "You may not create instances of this class [HubChat].";
    }
    
    
    
    /**
     * This is the main entry point of the demo
     * 
     * @param  args  Startup arguments, unused
     */
    public static void main(final String... args) throws java.io.IOException
    {
	final int port = Toolkit.getRandomPortUDP();
	System.out.println("Alive status: " + Toolkit.getAliveStatus());
	System.out.println("Local IP: " + Toolkit.getLocalIP());
	System.out.println("Public IP: " + Toolkit.getPublicIP());
	System.out.println("UDP port: " + port);
	
	final Scanner sc = new Scanner(System.in);
	final Hub hub = new Hub(port, new User(new UUID(), null, null, null, port, null, null, null, null, null, null, null, null, null, null));
	
	for (String line;;)
	    if ((line = sc.nextLine()).isEmpty())
	    {
		hub.close();
		return;
	    }
	    else if (line.charAt(0) == '>')
	    {
		connect(hub, line.substring(1));
	    }
	    else
	    {
		final byte[] data = line.getBytes("UTF-8");
		hub.send(data);
	    }
    }
    
    private static void connect(final Hub hub, final String remote) throws IOException
    {
	final InetAddress remoteAddress;
	final int remotePort;
	
	if (remote.startsWith("[") && remote.contains("]:"))
	{
	    remoteAddress = InetAddress.getByName(remote.substring(1, remote.lastIndexOf("]:")));
	    remotePort = Integer.parseInt(remote.substring(2 + remote.lastIndexOf("}:")));;
	}
	else
	{
	    remoteAddress = InetAddress.getByName(remote.substring(0, remote.lastIndexOf(":")));
	    remotePort = Integer.parseInt(remote.substring(1 + remote.lastIndexOf(":")));
	}
	
	hub.connect(remoteAddress, remotePort);
    }
    
}

