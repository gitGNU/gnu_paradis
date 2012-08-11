/**
 *  Paradis — Ever growing network for parallel and distributed computing.
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
package org.nongnu.paradis.net;
import org.nongnu.paradis.*;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * Network tool kit
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 * @author  Calle Lejdbrandt, <a href="mailto:callel@kth.se">callel@kth.se</a> (Author of <a href="www.github.com/maandree/cnt">CNT</a>'s method ported to {@link #getLocalIP()})
 */
@requires("java-runtime>=7")
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
        try (final Socket sock = new Socket("checkip.dyndns.org", 80))
        {   final InputStream is = new BufferedInputStream(sock.getInputStream());
            final OutputStream os = new BufferedOutputStream(sock.getOutputStream());
                
            try (final Scanner in = new Scanner(is))
            {   final PrintStream out = new PrintStream(os);
                    
                out.print("GET / HTTP/1.1\r\n");
                out.print("Host: checkip.dyndns.org\r\n");
                out.print("\r\n");
                out.flush();
                    
                for (;;)
                    if (in.nextLine().isEmpty())
                        break;
                    
                String line = in.nextLine();
                line = line.substring(0, line.indexOf("</body>"));
                line = line.substring(line.lastIndexOf(' ') + 1);
            
                return line;
            }
        }
    }
    
    
    /**
     * Gets the LAN local IP address of the machine
     * 
     * @return  The LAN local IP address
     * 
     * @throws  IOException  If it was not possible to get the IP address
     */
    @requires("java-runtime>=6")
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
                    
                    // We don't want loopback or IPv6.
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
     * Checks the alive status for the client, with test timeout at (by default) 4 seconds
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
        
        final boolean level1 = isAnyReachable("192.168.0.1", "192.168.1.1");
        System.err.println("Level 1: " + (level1 ? "yes" : "no"));
        
        final String[] nameservers = getNameServers();
        final boolean level2;
        if (nameservers == null)
        {   System.err.println("Could not read nameservers, level 2 is automatically passed.");
            level2 = true;
        }
        else if (nameservers.length == 0)
        {   System.err.println("No nameservers found, level 2 is automatically passed.");
            level2 = true;
        }
        else
            level2 = isAnyReachable(nameservers);
        System.err.println("Level 2: " + (level2 ? "yes" : "no"));
        
        boolean level3 = level2;
        if (level3)
            try
            {   level3 = isReachable(getPublicIP());
            }
            catch (final Throwable err)
            {   level3 = false;
            }
        System.err.println("Level 3: " + (level3 ? "yes" : "no"));
        
        if (level3)  return 3;
        if (level2)  return 2;
        if (level1)  return 1;
        return 0;
    }
    
    
    /**
     * Returns an array of all DNS name servers
     * 
     * @return  All DNS name servers, {@code null} on error
     */
    public static String[] getNameServers()
    {
        InputStream is = null;
        Scanner sc = null;
        try
        {
            final Vector<String> rc = new Vector<String>();
            
            is = new BufferedInputStream(new FileInputStream(new File("/etc/resolv.conf")));
            sc = new Scanner(is);
            
            while (sc.hasNextLine())
            {
                String line = sc.nextLine();
                int col = 0;
                while ((col < line.length()) && ((line.charAt(col) == ' ') || (line.charAt(col) == '\t')))
                    col++;
                line = line.substring(col);
                
                if (line.startsWith("nameserver ") || line.startsWith("nameserver\t"))
                {
                    col = "nameserver ".length();
                    while ((col < line.length()) && ((line.charAt(col) == ' ') || (line.charAt(col) == '\t')))
                        col++;
                    line = line.substring(col);
                    
                    line.replace("\t", " ");
                    if (line.contains(" "))
                        line = line.substring(0, line.indexOf(' '));
                    
                    if (line.length() > 0)
                    {
                        rc.add(line);
                        System.err.println("DNS nameserver found: " + line);
                    }
                }
            }
            
            final String[] _rc = new String[rc.size()];
            rc.toArray(_rc);
            return _rc;
        }
        catch (final FileNotFoundException err)
        {
            System.err.println("System does not have file: /etc/resolv.conf");
            return null;
        }
        catch (final Throwable err)
        {
            System.err.println("Cannot read /etc/resolv.conf");
            return null;
        }
        finally
        {   if (is != null)
                try
                {   is.close();
                }
                catch  (final Throwable ignore)
                {   //ignore
                }
            if (sc != null)
                try
                {   sc.close();
                }
                catch  (final Throwable ignore)
                {   //ignore
        }       }
    }
    
    
    /**
     * Tests whether any host is reachable, with test timeout at (by default) 4 seconds
     * 
     * @param   hosts  The remote hosts' addresses, IP or DNS
     * @return         Whether the remote host is reachable
     */
    public static boolean isAnyReachable(final String... hosts)
    {
        for (final String host : hosts)
            if (isReachable(host))
                return true;
        return false;
    }
    
    
    /**
     * Tests whether a host is reachable, with test timeout at (by default) 4 seconds
     * 
     * @param   host  The remote host's address, IP or DNS
     * @return        Whether the remote host is reachable
     */
    @requires("iputils")
    public static boolean isReachable(final String host)
    {
        final boolean isWindows = System.getProperty("os.name").startsWith("Windows ");
        
        try
        {
            byte[] buf = new byte[256];
            int ptr = 0;
            
            final ProcessBuilder procBuilder = isWindows ? new ProcessBuilder("ping", host, "-n", "1", "-w", Integer.toString(NetConf.getTimeout() * 1000))
                                                         : new ProcessBuilder("ping", host, "-c", "1", "-q", "-w", Integer.toString(NetConf.getTimeout()));
            
            final Process process = procBuilder.start();
            final InputStream stream = process.getInputStream();
            
            for (int d; (d = stream.read()) != -1; )
            {
                if (ptr == buf.length)
                {
                    final byte[] nbuf = new byte[ptr + 128];
                    System.arraycopy(buf, 0, nbuf, 0, ptr);
                    buf = nbuf;
                }
                buf[ptr++] = (byte)d;
            }
            
            process.waitFor();
            if (process.exitValue() != 0)
                return false;
            
            String data = new String(buf, 0, ptr, "UTF-8");
            if (isWindows)
            {
                data = data.split("\n")[5].split("[()]")[1];
                return data.equals("100% loss") == false;
            }
            {
                data = data.substring(data.indexOf("\n---") + 1);
                data = data.substring(data.indexOf('\n') + 1);
                data = data.split("\n")[0].replace(", ", ",");
                data = data.split(",")[1].split(" ")[0];
                return data.equals("1");
            }
        }
        catch (final Throwable err)
        {
            return false;
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
        try (final ServerSocket socket = new ServerSocket(0))
        {   final int port = socket.getLocalPort();
            return port;
        }
    }
    
    
    /**
     * Gets a random port tested on UDP
     * 
     * @return  A random port
     * 
     * @throws  IOException  If a port cannot be choosen
     */
    public static int getRandomPortUDP() throws IOException
    {
        try (final DatagramSocket socket = new DatagramSocket(0))
        {   final int port = socket.getLocalPort();
            return port;
        }
    }
    
}

