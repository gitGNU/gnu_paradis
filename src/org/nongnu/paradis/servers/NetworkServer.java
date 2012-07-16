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
package org.nongnu.paradis.servers;
import org.nongnu.paradis.net.*;
import org.nongnu.paradis.net.messages.*;
import org.nongnu.paradis.local.*;
import org.nongnu.paradis.*;

import java.util.*;
import java.net.*;
import java.io.*;


/**
 * Basic network server
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class NetworkServer extends AbstractServer
{
    /**
     * Constructor
     */
    public NetworkServer()
    {
        super(-1 ^ (-1 >>> 1));
    }
    
    
    /**
     * The local users
     */
    public static User localUser = null;
    
    /**
     * The networking interface
     */
    private Interface intrf = null;
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean invoke(final String command, final boolean consumed, final Scanner scanner)
    {
	if ((command.equals("network") || command.startsWith("network ")) == false)
	    return false;
	if (consumed)
	    return true;
	
	try
	{   if (command.equals("network init"))
	    {
		localUser = new User(LocalUser.getUUID(),
				     LocalUser.getName(),
				     Toolkit.getLocalIP(),
				     Toolkit.getPublicIP(),
				     LocalUser.getPort() == 0 ? Toolkit.getRandomPortUDP() : LocalUser.getPort(),
				     LocalUser.getDNSNames(),
				     LocalUser.getUUID(),
				     LocalUser.getSignature(),
				     LocalUser.getFriendUUIDs(),
				     LocalUser.getFriendUpdates(),
				     LocalUser.getFriendNames(),
				     LocalUser.getFriendLocalIPs(),
				     LocalUser.getFriendPublicIPs(),
				     LocalUser.getFriendPorts(),
				     LocalUser.getFriendDNSNames(),
				     LocalUser.getFriendSignatures());
		
		this.intrf = new Interface(localUser.getPort(), localUser);
		if (this.intrf.localPort != localUser.getPort())
		{
		    localUser.setPort(this.intrf.localPort);
		    LocalUser.setPort(this.intrf.localPort);
		}
		
		System.out.println("Your connection information:");
		System.out.println("    Public IP address:    " + localUser.getPublicIP());
		System.out.println("    LAN-local IP address: " + localUser.getLocalIP());
		System.out.println("    UDP port:             " + localUser.getPort());
	    }
	    else if (command.startsWith("network connect "))
	    {
		final String remote;
		final String[] args = command.split(" ");
		
		if (args.length == 4)
		    remote = "[" + args[2] + "]:" + args[3];
		else
		    remote = args[2];
		
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
		
		Blackboard.getInstance(null).broadcastMessage(new MakeConnection(remoteAddress, remotePort));
	    }
	}
	catch (final Throwable err)
	{   err.printStackTrace(System.err);
	}
	    
	return true;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose()
    {
	try
	{   this.intrf.close();
	}
	catch (final IOException err)
	{   err.printStackTrace(System.err);
	}
	super.dispose();
    }
    
}

