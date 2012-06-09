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
     * @param  friendNames       {@link #friendNames}
     * @param  friendLocalIPs    {@link #friendLocalIPs}
     * @param  friendPublicIPs   {@link #friendPublicIPs}
     * @param  friendPorts       {@link #friendPorts}
     * @param  friendDNSNames    {@link #friendDNSNames}
     * @param  friendSignatures  {@link #friendSignatures}
     */
    public User(final UUID uuid, final String name, final String localIP, final String publicIP, final int port, final String[] dnsNames, final UUID connectedTo,
		final byte[] signature, final UUID[] friendUUIDs, final long[] friendUpdates, final String[] friendNames, final String[] friendLocalIPs,
		final String[] friendPublicIPs, final int[] friendPorts, final String[][] friendDNSNames, final byte[][] friendSignatures)
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
	this.friendNames      = friendNames;
	this.friendLocalIPs   = friendLocalIPs;
	this.friendPublicIPs  = friendPublicIPs;
	this.friendPorts      = friendPorts;
	this.friendDNSNames   = friendDNSNames;
	this.friendSignatures = friendSignatures;
    }
    
    
    
    /**
     * Unique identifier of the user
     */
    private final UUID uuid;
    
    /**
     * The display name of the user, does not need to be unique
     */
    private final String name;
    
    /**
     * The LAN private IP address of the user
     */
    private final String localIP;
    
    /**
     * The WAN public IP address of the user
     */
    private final String publicIP;
    
    /**
     * The port the user uses for communications
     */
    private final int port;
    
    /**
     * The DNS names of the user
     */
    private String[] dnsNames;
    
    /**
     * The UUID of the user this user is connected to
     */
    private UUID connectedTo;
    
    /**
     * Digital signature, may be used to prove identify among friends
     */
    private byte[] signature;
    
    
    /**
     * {@link #uuid} for friends
     */
    private UUID[] friendUUIDs;
    
    /**
     * The list update time of friend information, in milliseconds since 1970-(01)jan-01 00:00:00.000
     */
    private long[] friendUpdates;
    
    /**
     * {@link #name} for friends
     */
    private String[] friendNames;
    
    /**
     * {@link #localIP} for friends
     */
    private String[] friendLocalIPs;
    
    /**
     * {@link #publicIP} for friends
     */
    private String[] friendPublicIPs;
    
    /**
     * {@link #port} for friends
     */
    private int[] friendPorts;
    
    /**
     * {@link #dnsNames} for friends
     */
    private String[][] friendDNSNames;
    
    /**
     * {@link #signature} for friends
     */
    private byte[][] friendSignatures;
    
    
    
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
	    stream.writeObject(data.getUUID());
	    stream.writeObject(data.getName());
	    stream.writeObject(data.getLocalIP());
	    stream.writeObject(data.getPublicIP());
	    stream.writeInt(data.getPort());
	    synchronized (data)
	    {   stream.writeObject(data.getDNSNames());
		stream.writeObject(data.getConnectedTo());
		stream.writeObject(data.getSignature());
		stream.writeObject(data.getFriendUUIDs());
		stream.writeObject(data.getFriendUpdates());
		stream.writeObject(data.getFriendNames());
		stream.writeObject(data.getFriendLocalIPs());
		stream.writeObject(data.getFriendPublicIPs());
		stream.writeObject(data.getFriendPorts());
		stream.writeObject(data.getFriendDNSNames());
		stream.writeObject(data.getFriendSignatures());
	}   }
    
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
    
    
    
    /**
     * Gets the unique identifier of the user
     * 
     * @return  The descripted data
     */
    public UUID getUUID()
    {   return this.uuid;
    }
    
    /**
     * Gets the display name of the user, does not need to be unique
     * 
     * @return  The descripted data
     */
    public String getName()
    {   return this.name;
    }
    
    /**
     * Gets the LAN private IP address of the user
     * 
     * @return  The descripted data
     */
    public String getLocalIP()
    {   return this.localIP;
    }
    
    /**
     * Gets the WAN public IP address of the user
     * 
     * @return  The descripted data
     */
    public String getPublicIP()
    {   return this.publicIP;
    }
    
    /**
     * Gets the port the user uses for communications
     * 
     * @return  The descripted data
     */
    public int getPort()
    {   return this.port;
    }
    
    /**
     * Gets the DNS names of the user
     * 
     * @return  The descripted data
     */
    public String[] getDNSNames()
    {   synchronized (this)
	{   return this.dnsNames;
    }   }
    
    /**
     * Setter for {@link #getDNSNames()}
     * 
     * @param  value  The descripted data
     */
    public void setDNSNames(final String[] value)
    {   synchronized (this)
	{   this.dnsNames = value;
    }   }
    
    /**
     * Gets the UUID of the user this user is connected to
     * 
     * @return  The descripted data
     */
    public UUID getConnectedTo()
    {   synchronized (this)
	{   return this.connectedTo;
    }   }
    
    /**
     * Setter for {@link #getConnectedTo()}
     * 
     * @param  value  The descripted data
     */
    public void setConnectedTo(final UUID value)
    {   synchronized (this)
	{   this.connectedTo = value;
    }   }
    
    /**
     * Gets the digital signature, may be used to prove identify among friends
     * 
     * @return  The descripted data
     */
    public byte[] getSignature()
    {   synchronized (this)
	{   return this.signature;
    }   }
    
    /**
     * Setter for {@link #getSignature()}
     * 
     * @param  value  The descripted data
     */
    public void setSignature(final byte[] value)
    {   synchronized (this)
	{   this.signature = value;
    }   }
    
    
    /**
     * {@link #getUUID()} for friends
     * 
     * @return  The descripted data
     */
    public UUID[] getFriendUUIDs()
    {   synchronized (this)
	{   return this.friendUUIDs;
    }   }
    
    /**
     * Setter for {@link #getFriendUUIDs()}
     * 
     * @param  value  The descripted data
     */
    public void setFriendUUIDs(final UUID[] value)
    {   synchronized (this)
	{   this.friendUUIDs = value;
    }   }
    
    /**
     * The list update time of friend information, in milliseconds since 1970-(01)jan-01 00:00:00.000
     * 
     * @return  The descripted data
     */
    public long[] getFriendUpdates()
    {   synchronized (this)
	{   return this.friendUpdates;
    }   }
    
    /**
     * Setter for {@link #getFriendUpdates()}
     * 
     * @param  value  The descripted data
     */
    public void setFriendUpdates(final long[] value)
    {   synchronized (this)
	{   this.friendUpdates = value;
    }   }
    
    /**
     * {@link #getName()} for friends
     * 
     * @return  The descripted data
     */
    public String[] getFriendNames()
    {   synchronized (this)
	{   return this.friendNames;
    }   }
    
    /**
     * Setter for {@link #getFriendNames()}
     * 
     * @param  value  The descripted data
     */
    public void setFriendNames(final String[] value)
    {   synchronized (this)
	{   this.friendNames = value;
    }   }
    
    /**
     * {@link #getLocalIP()} for friends
     * 
     * @return  The descripted data
     */
    public String[] getFriendLocalIPs()
    {   synchronized (this)
	{   return this.friendLocalIPs;
    }   }
    
    /**
     * Setter for {@link #getFriendLocalIPs()}
     * 
     * @param  value  The descripted data
     */
    public void setFriendLocalIPs(final String[] value)
    {   synchronized (this)
	{   this.friendLocalIPs = value;
    }   }
    
    /**
     * {@link #getPublicIP()} for friends
     * 
     * @return  The descripted data
     */
    public String[] getFriendPublicIPs()
    {   synchronized (this)
	{   return this.friendPublicIPs;
    }   }
    
    /**
     * Setter for {@link #getFriendPublicIPs()}
     * 
     * @param  value  The descripted data
     */
    public void setFriendPublicIPs(final String[] value)
    {   synchronized (this)
	{   this.friendPublicIPs = value;
    }   }
    
    /**
     * {@link #getPort()} for friends
     * 
     * @return  The descripted data
     */
    public int[] getFriendPorts()
    {   synchronized (this)
	{   return this.friendPorts;
    }   }
    
    /**
     * Setter for {@link #getFriendPorts()}
     * 
     * @param  value  The descripted data
     */
    public void setFriendPorts(final int[] value)
    {   synchronized (this)
	{   this.friendPorts = value;
    }   }
    
    /**
     * {@link #getDNSNames()} for friends
     * 
     * @return  The descripted data
     */
    public String[][] getFriendDNSNames()
    {   synchronized (this)
	{   return this.friendDNSNames;
    }   }
    
    /**
     * Setter for {@link #getFriendDNSNames()}
     * 
     * @param  value  The descripted data
     */
    public void setFriendDNSNames(final String[][] value)
    {   synchronized (this)
	{   this.friendDNSNames = value;
    }   }
    
    /**
     * {@link #getSignature()} for friends
     * 
     * @return  The descripted data
     */
    public byte[][] getFriendSignatures()
    {   synchronized (this)
	{   return this.friendSignatures;
    }   }
    
    /**
     * Setter for {@link #getFriendSignatures()}
     * 
     * @param  value  The descripted data
     */
    public void setFriendSignatures(final byte[][] value)
    {   synchronized (this)
	{   this.friendSignatures = value;
    }   }

}

