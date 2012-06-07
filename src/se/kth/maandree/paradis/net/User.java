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


/**
 * User identification class
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class User
{
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
    
}

