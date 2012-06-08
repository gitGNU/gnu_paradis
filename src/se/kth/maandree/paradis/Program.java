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
package se.kth.maandree.paradis;
import se.kth.maandree.paradis.net.*;

import java.util.Scanner;
import java.net.InetAddress;


/**
 * The is the main class of the program
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Program
{
    /**
     * The name of the package, this should never be modified unleash you make a totally new program
     */
    public static final String PACKAGE = "paradis";
    
    /**
     * The fork path of the package, place ready documentation before changing this to ensure uniqueness
     */
    public static final String FORK = "maandree";
    
    /**
     * The version of the package
     */
    public static final String VERSION = "0.0.1.0";
    
    /**
     * Reference to where you can get more information about this package
     */
    public static final String WEBSITE = "www.github.com/maandree/paradis";
    
    
    
    /**
     * Non-constructor
     */
    private Program()
    {
	assert false : "You may not create instances of this class [Program].";
    }
    
    
    
    /**
     * This is the main entry point of the program
     * 
     * @param  args  Startup arguments, unused
     */
    public static void main(final String... args) throws java.io.IOException
    {
	final int port = Toolkit.getRandomPortUDP();
	System.out.println("Alive status: " + Toolkit.getAliveStatus());
	System.out.println("Local IP: " + Toolkit.getLocalIP());
	System.out.println("Public IP: " + Toolkit.getPublicIP());
	System.out.println("Random TCP port: " + Toolkit.getRandomPortTCP());
	System.out.println("Random UDP port: " + port);
	
	final Scanner sc = new Scanner(System.in);
	final String remote = sc.nextLine();
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
	
	final UDPServer server = new UDPServer(port);
	final UDPSocket socket = server.connect(remoteAddress, remotePort);
	
	final Thread thread = new Thread()
	        {   public void run()
		    {   try
			{
			    final byte[] buf = new byte[1024];
			    for (;;)
			    {
				final int len = socket.inputStream.read(buf);
				System.out.print("\033[31m");
				System.out.write(buf, 0, len);
				System.out.print("\033[39m\n");
				System.out.flush();
			    }
			}
			catch (final Throwable err)
			{   err.printStackTrace(System.err);
	        }   }   };
	
	thread.setDaemon(true);
	thread.start();
	
	for (;;)
	{
	    final String line = sc.nextLine();
	    if (line.isEmpty())
	    {   server.close();
		return;
	    }
	    socket.outputStream.write(line.getBytes("UTF-8"));
	    socket.outputStream.flush();
	}
    }
    
}

