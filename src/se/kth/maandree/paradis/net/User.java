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

import java.io.IOException;


/**
 * User identification class
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class User implements Comparable<User>
{
    /**
     * Constructor
     * 
     * @param  uuid              {@link #uuid}
     * @param  name              {@link #name}
     * @param  localIP           {@link #localIP}
     * @param  publicIP          {@link #publicIP}
     * @param  port              {@link #port}
     * @param  dnsNames          {@link #dnsNames}
     * @param  connectedTo       {@link #connectedTo}
     * @param  signature         {@link #signature}
     * @param  friendUUIDs       {@link #friendUUIDs}
     * @param  friendUpdates     {@link #friendUpdates}
     * @param  friendLocalIPs    {@link #friendLocalIPs}
     * @param  friendPublicIPs   {@link #friendPublicIPs}
     * @param  friendPorts       {@link #friendPorts}
     * @param  friendDNSNames    {@link #friendDNSNames}
     * @param  friendSignatures  {@link #friendSignatures}
     */
    public User(final UUID uuid, final String name, final String localIP, final String publicIP, final int port, final String[] dnsNames, final UUID connectedTo,
		final byte[] signature, final UUID[] friendUUIDs, final long[] friendUpdates, final String[] friendLocalIPs, final String[] friendPublicIPs,
		final int[] friendPorts, final String[][] friendDNSNames, final byte[][] friendSignatures)
    {
	this.uuid             = uuid;
	this.name             = name;
	this.localIP          = localIP;
	this.publicIP         = publicIP;
	this.port             = port;
	this.dnsNames         = dnsNames;
	this.connectedTo      = connectedTo;
	this.signature        = signature;
	this.friendUUIDs      = friendUUIDs;
	this.friendUpdates    = friendUpdates;
	this.friendLocalIPs   = friendLocalIPs;
	this.friendPublicIPs  = friendPublicIPs;
	this.friendPorts      = friendPorts;
	this.friendDNSNames   = friendDNSNames;
	this.friendSignatures = friendSignatures;
    }
    
    
    
    /**
     * Unique identifier of the user
     */
    public final UUID uuid;
    
    /**
     * The display name of the user, does not need to be unique
     */
    public final String name;
    
    /**
     * The LAN private IP address of the user
     */
    public final String localIP;
    
    /**
     * The WAN public IP address of the user
     */
    public final String publicIP;
    
    /**
     * The port the user uses for communications
     */
    public final int port;
    
    /**
     * The DNS names of the user
     */
    public String[] dnsNames;
    
    /**
     * The UUID of the user this user is connected to
     */
    public UUID connectedTo;
    
    /**
     * Digital signature, may be used to prove identify among friends
     */
    public byte[] signature;
    
    
    /**
     * {@link #uuid} of friends
     */
    public UUID[] friendUUIDs;
    
    /**
     * The list update time of friend information, in milliseconds since 1970-(01)jan-01 00:00:00.000
     */
    public long[] friendUpdates;
    
    /**
     * {@link #localIP} of friends
     */
    public String[] friendLocalIPs;
    
    /**
     * {@link #publicIP} of friends
     */
    public String[] friendPublicIPs;
    
    /**
     * {@link #port} of friends
     */
    public int[] friendPorts;
    
    /**
     * {@link #dnsNames} of friends
     */
    public String[][] friendDNSNames;
    
    /**
     * {@link #signature} of friends
     */
    public byte[][] friendSignatures;
    
    
    
    /**
     * Protocol for transfering {@link User}s
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    public static class UserTransferProtocol implements TransferProtocol<User>
    {
	//Has default constructor
	
	
	
	/**
	 * {@inheritDoc}
	 */
	public User read(final TransferInputStream stream) throws IOException
	{
	    return new User(stream.readObject(UUID.class),
			    stream.readObject(String.class),
			    stream.readObject(String.class),
			    stream.readObject(String.class),
			    stream.readInt(),
			    stream.readObject(String[].class),
			    stream.readObject(UUID.class),
			    stream.readObject(byte[].class),
			    stream.readObject(UUID[].class),
			    stream.readObject(long[].class),
			    stream.readObject(String[].class),
			    stream.readObject(String[].class),
			    stream.readObject(int[].class),
			    stream.readObject(String[][].class),
			    stream.readObject(byte[][].class));
	}
    
    
	/**
	 * {@inheritDoc}
	 */
	public void write(final User data, final TransferOutputStream stream) throws IOException
	{
	    stream.writeObject(data.uuid);
	    stream.writeObject(data.name);
	    stream.writeObject(data.localIP);
	    stream.writeObject(data.publicIP);
	    stream.writeInt(data.port);
	    stream.writeObject(data.dnsNames);
	    stream.writeObject(data.connectedTo);
	    stream.writeObject(data.signature);
	    stream.writeObject(data.friendUUIDs);
	    stream.writeObject(data.friendUpdates);
	    stream.writeObject(data.friendLocalIPs);
	    stream.writeObject(data.friendPublicIPs);
	    stream.writeObject(data.friendPorts);
	    stream.writeObject(data.friendDNSNames);
	    stream.writeObject(data.friendSignatures);
	}
    
    }
    
    
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object other)
    {
	if ((other == null) || (other instanceof User == false))
	    return false;
	
	if (other == this)
	    return true;
	
	return this.uuid.equals(((User)other).uuid);
    }
    
    
    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {   return this.uuid.hashCode();
    }
    
    
    /**
     * {@inheritDoc}
     */
    public int compareTo(final User other)
    {   return this.uuid.compareTo(other.uuid);
    }
    
    
    /**
     * {@inheritDoc}
     */
    public String toString()
    {   return (this.name + " (") + (this.uuid.toString() + ")");
    }

}

