/**
 *  Paradis — Ever growing network for parallel and distributed computing.
 *  Copyright © 2012, 2013  Mattias Andrée (maandree@member.fsf.org)
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
package org.nongnu.paradis.demo;
import org.nongnu.paradis.net.*;
import org.nongnu.paradis.*;

import java.util.*;
import java.net.*;
import java.io.*;


/**
 * Multi users chat demo
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
public class MultiChat
{
    /**
     * Non-constructor
     */
    private MultiChat()
    {
        assert false : "You may not create instances of this class [MultiChat].";
    }
    
    
    
    /**
     * This is the main entry point of the demo
     * 
     * @param  args  Startup arguments, unused
     * 
     * @throws  IOException  On error
     */
    @requires("java-environment>=7")
    public static void main(final String... args) throws IOException
    {
        final int port = Toolkit.getRandomPortUDP();
        System.out.println("Alive status: " + Toolkit.getAliveStatus());
        System.out.println("Local IP: " + Toolkit.getLocalIP());
        System.out.println("Public IP: " + Toolkit.getPublicIP());
        //System.out.println("Random TCP port: " + Toolkit.getRandomPortTCP());
        System.out.println("UDP port: " + port);
        
        try (final Scanner sc = new Scanner(System.in))
        {
            final UDPServer server = new UDPServer(port);
            final int[] colour = { 31 };
            final ArrayList<UDPSocket> sockets = new ArrayList<>();
            
            final Thread acceptthread = new Thread()
                    {   @Override
                        public void run()
                        {   try
                            {
                                for (;;)
                                {
                                    final UDPSocket socket = server.accept();
                                    if (socket == null)
                                        return;
                                    final String sockcolour;
                                    synchronized (sockets)
                                    {   sockcolour = Integer.toString(colour[0]++);
                                        sockets.add(socket);
                                    }
                                    
                                    final Thread thread = new Thread()
                                            {   @Override
                                                public void run()
                                                {   try
                                                    {   final byte[] buf = new byte[1024];
                                                        for (;;)
                                                        {   final int len = socket.inputStream.read(buf);
                                                            synchronized (System.out)
                                                            {   System.out.print("\033[" + sockcolour + "m");
                                                                System.out.write(buf, 0, len);
                                                                System.out.print("\033[00m\n");
                                                                System.out.flush();
                                                     }   }   }
                                                    catch (final Throwable err)
                                                    {   err.printStackTrace(System.err);
                                            }   }   };
                                    
                                    thread.setDaemon(true);
                                    thread.start();
                                }
                            }
                            catch (final Throwable err)
                            {   err.printStackTrace(System.err);
                    }   }   };
            
            acceptthread.setDaemon(true);
            acceptthread.start();
            
            
            for (String line;;)
                if ((line = sc.nextLine()).isEmpty())
                {
                    server.close();
                    return;
                }
                else if (line.charAt(0) == '>')
                {
                    final UDPSocket socket = connect(server, line.substring(1));
                    final String sockcolour;
                    synchronized (sockets)
                    {   sockcolour = Integer.toString(colour[0]++);
                        sockets.add(socket);
                    }
                    
                    final Thread thread = new Thread()
                            {   @Override
                                public void run()
                                {   try
                                    {   final byte[] buf = new byte[1024];
                                        for (;;)
                                        {   final int len = socket.inputStream.read(buf);
                                            synchronized (System.out)
                                            {   System.out.print("\033[" + sockcolour + "m");
                                                System.out.write(buf, 0, len);
                                                System.out.print("\033[00m\n");
                                                System.out.flush();
                                    }   }   }
                                    catch (final Throwable err)
                                    {   err.printStackTrace(System.err);
                            }   }   };
                    
                    thread.setDaemon(true);
                    thread.start();
                }
                else if (line.equals("?"))
                {
                    int c = 31;
                    synchronized (System.out)
                    {   synchronized (sockets)
                        {   for (final UDPSocket socket : sockets)
                            {   System.out.println("\033[1;" + Integer.toString(c++) + "m" + (socket.isAlive() ? "alive" : "dead") + "\033[00m");
                        }   }
                        System.out.println("\033[35mdone\033[00m");
                    }
                }
                else
                {
                    final byte[] data = line.getBytes("UTF-8");
                    synchronized (sockets)
                    {   for (final UDPSocket socket : sockets)
                        {   socket.outputStream.write(data);
                            socket.outputStream.flush();
                    }   }
                    synchronized (System.out)
                    {   System.out.println("\033[35mdone\033[00m");
                    }
                }
        }
    }
    
    /**
     * Connects to a remote machine
     * 
     * @param   server  The socket with which to connect
     * @param   remote  The to which to machine connect
     * @return          The offspring socket of the connection
     * 
     * @throws  IOException  On error
     */
    private static UDPSocket connect(final UDPServer server, final String remote) throws IOException
    {
        final InetAddress remoteAddress;
        final int remotePort;
        
        if (remote.startsWith("[") && remote.contains("]:"))
        {
            remoteAddress = InetAddress.getByName(remote.substring(1, remote.lastIndexOf("]:")));
            remotePort = Integer.parseInt(remote.substring(2 + remote.lastIndexOf("]:")));
        }
        else
        {
            remoteAddress = InetAddress.getByName(remote.substring(0, remote.lastIndexOf(":")));
            remotePort = Integer.parseInt(remote.substring(1 + remote.lastIndexOf(":")));
        }
        
        return server.connect(remoteAddress, remotePort);
    }
    
}

