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
package se.kth.maandree.paradis.local;
import se.kth.maandree.paradis.net.*;
import se.kth.maandree.paradis.io.*;
import se.kth.maandree.paradis.*;

import java.io.*;
import java.util.Arrays;


/**
 * Local user data
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class LocalUser
{
    /**
     * Type initialiser
     */
    static
    {
	load();
    }
    
    
    
    /**
     * Non-constructor
     */
    private LocalUser()
    {
	assert false : "You may not create instances of this class [LocalUser].";
    }
    
    
    
    /**
     * The unique identifier of the local user
     */
    private static UUID uuid;
    
    /**
     * The display name of the local user
     */
    private static String name;
    
    /**
     * The port the local user uses for communications
     */
    private static int port;
    
    /**
     * The DNS names of the local user
     */
    private static String[] dnsNames;
    
    /**
     * The local user's digital signature
     */
    private static byte[] signature;
    
    
    /**
     * The unique identifieres for friends of the local user
     */
    private static UUID[] friendUUIDs;
    
    /**
     * The list update time of friend information of the local user, in milliseconds since 1970-(01)jan-01 00:00:00.000
     */
    private static long[] friendUpdates;
    
    /**
     * The display names for friends of the local user
     */
    private static String[] friendNames;
    
    /**
     * The LAN local IP addresses for friends of the local user
     */
    private static String[] friendLocalIPs;
    
    /**
     * The WAN public IP addresses for friends of the local user
     */
    private static String[] friendPublicIPs;
    
    /**
     * The ports the friends of the local user uses for communications
     */
    private static int[] friendPorts;
    
    /**
     * The DNS names of the friends of the local user
     */
    private static String[][] friendDNSNames;
    
    /**
     * The digital signatures of the friends of the local user
     */
    private static byte[][] friendSignatures;
    
    
    /**
     * Whether {@link #name} has been modified
     */
    private static boolean nameChanged = false;
    
    /**
     * Whether {@link #port} has been modified
     */
    private static boolean portChanged = false;
    
    /**
     * Whether {@link #signature} has been modified
     */
    private static boolean signatureChanged = false;
    
    
    
    /**
     * Save the local user data
     * 
     * @throws  IOException  On failure
     */
    public static void save() throws IOException
    {
	TransferOutputStream out = null;
	try
	{
	    final String sep = Properties.getFileSeparator();
	    String file = Properties.getHome();
	    if (file.endsWith(sep))
		file += sep;
	    (new File(file += "." + Program.PACKAGE)).mkdirs();
	    file += sep + "localuser";
	    out = new TransferOutputStream(new FileOutputStream(new File(file)));
	    
	    out.writeObject(uuid);
	    out.writeObject(name);
	    out.writeInt(port);
	    out.writeObject(dnsNames);
	    out.writeObject(signature);
	    out.writeObject(friendUUIDs);
	    out.writeObject(friendUpdates);
	    out.writeObject(friendNames);
	    out.writeObject(friendLocalIPs);
	    out.writeObject(friendPublicIPs);
	    out.writeObject(friendPorts);
	    out.writeObject(friendDNSNames);
	    out.writeObject(friendSignatures);
	    
	    out.flush();
	}
	finally
	{   if (out != null)
		try
		{    out.close();
		}
		catch (final Throwable ignore)
		{    // ignore
	}       }
    }
    
    /**
     * Load the local user data, and generates and saves if missing or corrupt
     */
    private static void load()
    {
	boolean gen = true;
	TransferInputStream in = null;
	try
	{
	    final String sep = Properties.getFileSeparator();
	    String file = Properties.getHome();
	    if (file.endsWith(sep))
		file += sep;
	    (new File(file += "." + Program.PACKAGE)).mkdirs();
	    file += sep + "localuser";
	    in = new TransferInputStream(new FileInputStream(new File(file)));
	    
	    uuid             = in.readObject(    UUID  .class);
	    name             = in.readObject(  String  .class);
	    port             = in.readInt();
	    dnsNames         = in.readObject(String[]  .class);
	    signature        = in.readObject(  byte[]  .class);
	    friendUUIDs      = in.readObject(  UUID[]  .class);
	    friendUpdates    = in.readObject(  long[]  .class);
	    friendNames      = in.readObject(String[]  .class);
	    friendLocalIPs   = in.readObject(String[]  .class);
	    friendPublicIPs  = in.readObject(String[]  .class);
	    friendPorts      = in.readObject(   int[]  .class);
	    friendDNSNames   = in.readObject(String[][].class);
	    friendSignatures = in.readObject(  byte[][].class);
	    
	    gen = false;
	}
	catch (final Throwable err)
	{   System.err.println("Failed to load local user data, generates instead, and saves");
	}
	finally
	{   if (in != null)
		try
		{    in.close();
		}
		catch (final Throwable ignore)
		{    // ignore
	}       }
	
	if (gen)
	{   uuid             = new UUID();
	    name             = Properties.getUser();
	    if (name == null)
		name = "nopony";
	    port             = 0;
	    dnsNames         = new String[0];
	    signature        = new byte[0];
	    friendUUIDs      = new UUID[0];
	    friendUpdates    = new long[0];
	    friendNames      = new String[0];
	    friendLocalIPs   = new String[0];
	    friendPublicIPs  = new String[0];
	    friendPorts      = new int[0];
	    friendDNSNames   = new String[0][];
	    friendSignatures = new byte[0][];
	}
	
	if (gen)
	    try
	    {   save();
	    }
	    catch (final Throwable err)
	    {   System.err.println("Failed to save generated local user data");
	    }
    }
    
    
    
    /**
     * Gets the unique identifier of the local user
     * 
     * @return  The unique identifier of the local user
     */
    public static UUID getUUID()
    {   return uuid;
    }
    
    /**
     * Gets the display name of the local user
     * 
     * @return  The display name of the local user
     */
    public static String getName()
    {   return name;
    }
    
    /**
     * Sets the display name of the local user
     * 
     * @param   value  The new display name of the local user
     * @return         {@code false} if already modified, and thus cannot be modified
     */
    public static boolean setName(final String value)
    {   if (nameChanged)
	    return false;
	nameChanged = true;
	name = value;
	return true;
    }
    
    /**
     * Gets the port the local user uses for communications
     * 
     * @return  The port the local user uses for communications
     */
    public static int getPort()
    {   return port;
    }
    
    /**
     * Sets the port the local user uses for communications
     * 
     * @param   value  The new port the local user uses for communications
     * @return         {@code false} if already modified, and thus cannot be modified
     */
    public static boolean setPort(final int value)
    {   if (portChanged)
	    return false;
	portChanged = true;
	port = value;
	return true;
    }
    
    /**
     * Gets the local user's digital signature
     * 
     * @return  The local user's digital signature
     */
    public static byte[] getSignature()
    {   return signature;
    }
    
    /**
     * Sets the local user's digital signature
     * 
     * @param   value  The new local user's digital signature
     * @return         {@code false} if already modified, and thus cannot be modified
     */
    public static boolean setSignature(final byte[] value)
    {   if (signatureChanged)
	    return false;
	signatureChanged = true;
	signature = value;
	return true;
    }
    
    /**
     * Gets the DNS names of the local user
     * 
     * @return  The DNS names of the local user
     */
    public static String[] getDNSNames()
    {   return dnsNames;
    }
    
    /**
     * Adds a DNS name for the local user
     * 
     * @param   value  The new DNS name
     * @return         The DNS names of the local user
     */
    public static String[] addDNSName(final String value)
    {   int pos = Arrays.binarySearch(dnsNames, value);
	if (pos >= 0)
	    return dnsNames;
	pos = ~pos;
	final String[] tmp = new String[dnsNames.length + 1];
	System.arraycopy(dnsNames, 0, tmp, 0, pos);
	System.arraycopy(dnsNames, pos, tmp, pos + 1, dnsNames.length - pos);
	tmp[pos] = value;
	return dnsNames = tmp;
    }
    
    /**
     * Removes a DNS name for the local user
     * 
     * @param   value  The DNS name to remove
     * @return         The DNS names of the local user
     */
    public static String[] removeDNSName(final String value)
    {   int pos = Arrays.binarySearch(dnsNames, value);
	if (pos < 0)
	    return dnsNames;
	final String[] tmp = new String[dnsNames.length - 1];
	System.arraycopy(dnsNames, 0, tmp, 0, pos);
	if (pos < dnsNames.length)
	    System.arraycopy(dnsNames, pos + 1, tmp, pos, tmp.length - pos);
	return dnsNames = tmp;
    }
    
    
    /**
     * Removes a add
     * 
     * @param   uuid       The unique identifier if the friend
     * @param   name       The display name of the friend
     * @param   localIP    The LAN local IP address of the friend
     * @param   publicIP   The WAN public IP address of the friend
     * @param   port       The port the friend uses for communication
     * @param   dnsNames   The DNS names of the friend
     * @param   signature  The digital signature of the friend
     * @return             {@code false} iff their was nothing to be done
     */
    public static boolean addFriend(final UUID uuid, final String name, final String localIP, final String publicIP, final int port, final String[] dnsNames, final byte[] signature)
    {   int pos = Arrays.binarySearch(friendUUIDs, uuid);
	if (pos >= 0)
	    return false;
	pos = ~pos;
	
        {   final UUID[] x = friendUUIDs, tmp = new UUID[x.length + 1];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    System.arraycopy(x, pos, tmp, pos + 1, x.length - pos);
	    (friendUUIDs = tmp)[pos] = uuid;
	}
        {   final long[] x = friendUpdates, tmp = new long[x.length + 1];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    System.arraycopy(x, pos, tmp, pos + 1, x.length - pos);
	    (friendUpdates = tmp)[pos] = System.currentTimeMillis();
	}
        {   final String[] x = friendNames, tmp = new String[x.length + 1];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    System.arraycopy(x, pos, tmp, pos + 1, x.length - pos);
	    (friendNames = tmp)[pos] = name;
	}
        {   final String[] x = friendLocalIPs, tmp = new String[x.length + 1];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    System.arraycopy(x, pos, tmp, pos + 1, x.length - pos);
	    (friendLocalIPs = tmp)[pos] = localIP;
	}
        {   final String[] x = friendPublicIPs, tmp = new String[x.length + 1];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    System.arraycopy(x, pos, tmp, pos + 1, x.length - pos);
	    (friendPublicIPs = tmp)[pos] = publicIP;
	}
        {   final int[] x = friendPorts, tmp = new int[x.length + 1];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    System.arraycopy(x, pos, tmp, pos + 1, x.length - pos);
	    (friendPorts = tmp)[pos] = port;
	}
        {   final String[][] x = friendDNSNames, tmp = new String[x.length + 1][];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    System.arraycopy(x, pos, tmp, pos + 1, x.length - pos);
	    (friendDNSNames = tmp)[pos] = dnsNames;
	}
        {   final byte[][] x = friendSignatures, tmp = new byte[x.length + 1][];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    System.arraycopy(x, pos, tmp, pos + 1, x.length - pos);
	    (friendSignatures = tmp)[pos] = signature;
	}
	
	return true;
    }
    
    /**
     * Removes a friend
     * 
     * @param   uuid  The unique identifier if the friend
     * @return        {@code false} iff their was nothing to be done
     */
    public static boolean removeFriend(final UUID uuid)
    {   int pos = Arrays.binarySearch(friendUUIDs, uuid);
	if (pos < 0)
	    return false;
	
	{   final UUID[] x = friendUUIDs, tmp = new UUID[x.length - 1];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    if (pos < x.length)  System.arraycopy(x, pos + 1, tmp, pos, tmp.length - pos);
	    friendUUIDs = tmp;
	}
	{   final long[] x = friendUpdates, tmp = new long[x.length - 1];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    if (pos < x.length)  System.arraycopy(x, pos + 1, tmp, pos, tmp.length - pos);
	    friendUpdates = tmp;
	}
	{   final String[] x = friendNames, tmp = new String[x.length - 1];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    if (pos < x.length)  System.arraycopy(x, pos + 1, tmp, pos, tmp.length - pos);
	    friendNames = tmp;
	}
	{   final String[] x = friendLocalIPs, tmp = new String[x.length - 1];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    if (pos < x.length)  System.arraycopy(x, pos + 1, tmp, pos, tmp.length - pos);
	    friendLocalIPs = tmp;
	}
	{   final String[] x = friendPublicIPs, tmp = new String[x.length - 1];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    if (pos < x.length)  System.arraycopy(x, pos + 1, tmp, pos, tmp.length - pos);
	    friendPublicIPs = tmp;
	}
	{   final int[] x = friendPorts, tmp = new int[x.length - 1];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    if (pos < x.length)  System.arraycopy(x, pos + 1, tmp, pos, tmp.length - pos);
	    friendPorts = tmp;
	}
	{   final String[][] x = friendDNSNames, tmp = new String[x.length - 1][];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    if (pos < x.length)  System.arraycopy(x, pos + 1, tmp, pos, tmp.length - pos);
	    friendDNSNames = tmp;
	}
	{   final byte[][] x = friendSignatures, tmp = new byte[x.length - 1][];
	    System.arraycopy(x, 0, tmp, 0, pos);
	    if (pos < x.length)  System.arraycopy(x, pos + 1, tmp, pos, tmp.length - pos);
	    friendSignatures = tmp;
	}
	
	return true;
    }

    /**
     * Gets the unique identifieres for friends of the local user
     * 
     * @return  The unique identifieres for friends of the local user
     */
    public static UUID[] getFriendUUIDs()
    {   return friendUUIDs;
    }
    
    /**
     * Gets the list update time of friend information of the local user, in milliseconds since 1970-(01)jan-01 00:00:00.000
     * 
     * @return  The list update time of friend information of the local user, in milliseconds since 1970-(01)jan-01 00:00:00.000
     */
    public static long[] getFriendUpdates()
    {   return friendUpdates;
    }
    
    /**
     * Gets the display names for friends of the local user
     * 
     * @return  The display names for friends of the local user
     */
    public static String[] getFriendNames()
    {   return friendNames;
    }
    
    /**
     * Gets the LAN local IP addresses for friends of the local user
     * 
     * @return  The LAN local IP addresses for friends of the local user
     */
    public static String[] getFriendLocalIPs()
    {   return friendLocalIPs;
    }
    
    /**
     * Gets the WAN public IP addresses for friends of the local user
     * 
     * @return  The WAN public IP addresses for friends of the local user
     */
    public static String[] getFriendPublicIPs()
    {   return friendPublicIPs;
    }
    
    /**
     * Gets the ports the friends of the local user uses for communications
     * 
     * @return  The ports the friends of the local user uses for communications
     */
    public static int[] getFriendPorts()
    {   return friendPorts;
    }
    
    /**
     * Gets the digital signatures of the friends of the local user
     * 
     * @return  The digital signatures of the friends of the local user
     */
    public static byte[][] getFriendSignatures()
    {   return friendSignatures;
    }
    
    /**
     * Gets the DNS names of the friends of the local user
     * 
     * @return  The DNS names of the friends of the local user
     */
    public static String[][] getFriendDNSNames()
    {   return friendDNSNames;
    }
    
    /**
     * Gets the DNS names of a friend of the local user
     * 
     * @param   index  The index of the friend, as returned by {@link #getFriendUUIDs()}
     * @return         The DNS names of a friend of the local user
     */
    public static String[] getFriendDNSNames(final int index)
    {   return friendDNSNames[index];
    }
    
    /**
     * Adds a DNS name of a friend of the local user
     * 
     * @param   index  The index of the friend, as returned by {@link #getFriendUUIDs()}
     * @param   value  The new DNS name
     * @return         The DNS names of a friend of the local user
     */
    public static String[] addFriendDNSName(final int index, final String value)
    {   int pos = Arrays.binarySearch(friendDNSNames[index], value);
	if (pos >= 0)
	    return friendDNSNames[index];
	pos = ~pos;
	final String[] tmp = new String[friendDNSNames[index].length + 1];
	System.arraycopy(friendDNSNames[index], 0, tmp, 0, pos);
	System.arraycopy(friendDNSNames[index], pos, tmp, pos + 1, friendDNSNames[index].length - pos);
	tmp[pos] = value;
	return friendDNSNames[index] = tmp;
    }
    
    /**
     * Removes a DNS name of a friend of the local user
     * 
     * @param   index  The index of the friend, as returned by {@link #getFriendUUIDs()}
     * @param   value  The DNS name to remove
     * @return         The DNS names of a friend of the local user
     */
    public static String[] removeFriendDNSName(final int index, final String value)
    {   int pos = Arrays.binarySearch(friendDNSNames[index], value);
	if (pos < 0)
	    return friendDNSNames[index];
	pos = ~pos;
	final String[] tmp = new String[friendDNSNames[index].length - 1];
	System.arraycopy(friendDNSNames[index], 0, tmp, 0, pos);
	if (pos < friendDNSNames[index].length)
	    System.arraycopy(friendDNSNames[index], pos + 1, tmp, pos, tmp.length - pos);
	return friendDNSNames[index] = tmp;
    }
    
}

