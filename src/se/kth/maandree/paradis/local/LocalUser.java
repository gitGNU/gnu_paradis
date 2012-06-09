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


/**
 * Local user data
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class LocalUser
{
    /**
     * Non-constructor
     */
    private LocalUser()
    {
	assert false : "You may not create instances of this class [LocalUser].";
    }
    
    
    
    public static UUID getUUID()
    {   //final UUID uuid              get
	return null;
    }
    
    public static String getName()
    {   //final String name            get set(once)
	return null;
    }
    
    public static boolean setName(final String value)
    {   //final String name            get set(once)
	return false;
    }
    
    public static int getPort()
    {   //final int port               get set(once)
	return 0;
    }
    
    public static boolean setPort(final int value)
    {   //final int port               get set(once)
	return false;
    }
    
    public static byte[] getSignature()
    {   //byte[] signature             get set(once)
	return null;
    }
    
    public static boolean setSignature(final byte[] value)
    {   //byte[] signature             get set(once)
	return false;
    }
    
    public static String[] getDNSNames()
    {   //String[] dnsNames            get add remove
	return null;
    }
    
    public static String[] addDNSName(final String value)
    {   //String[] dnsNames            get add remove
	return null;
    }
    
    public static String[] removeDNSName(final String value)
    {   //String[] dnsNames            get add remove
	return null;
    }
    
    
    
    //UUID[] friendUUIDs           get add remove
    //long[] friendUpdates         get set add remove
    //String[] friendNames         get set add remove
    //String[] friendLocalIPs      get set add remove
    //String[] friendPublicIPs     get set add remove
    //int[] friendPorts;           get set add remove
    //String[][] friendDNSNames    get set add remove
    //byte[][] friendSignatures    get set add remove
    
}

