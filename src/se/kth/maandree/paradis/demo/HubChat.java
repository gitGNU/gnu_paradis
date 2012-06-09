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
import se.kth.maandree.paradis.io.*;

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
    public static void main(final String... args) throws Exception
    {
	final int port = Toolkit.getRandomPortUDP();
	System.out.println("Alive status: " + Toolkit.getAliveStatus());
	System.out.println("Local IP: " + Toolkit.getLocalIP());
	System.out.println("Public IP: " + Toolkit.getPublicIP());
	System.out.println("UDP port: " + port);
	
	final User localUser = new User(new UUID(),
					"nobody",
					"127.0.0.1",
					"127.0.0.1",
					port,
					new String[0],
					new UUID(),
					new byte[0],
					new UUID[0],
					new long[0],
					new String[0],
					new String[0],
					new String[0],
					new int[0],
					new String[0][],
					new byte[0][]);
	
	TransferProtocolRegister.register(String.class, "chat message");
	final PacketFactory factory = new PacketFactory(localUser, false, false, (short)16);
	
	final Scanner sc = new Scanner(System.in);
	final Hub hub = new Hub(port, localUser);
	
	final Thread thread = new Thread()
	        {   public void run()
		    {   for (;;)
			{   final Packet packet = hub.receive();
			    if (packet == null)
			    {   return;
			    }
			    System.out.print(packet.message);
		}   }   };
	
	thread.setDaemon(true);
	thread.start();
	
	for (String line;;)
	    if ((line = sc.nextLine()).isEmpty())
	    {
		hub.close();
		return;
	    }
	    else if (line.charAt(0) == '>')
		connect(hub, line.substring(1));
	    else
		hub.send(factory.createBroadcast(line + '\n', "chat message"));
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

