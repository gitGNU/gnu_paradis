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
    
    
    
    // DATA IN USER OBJECT
    
    //final UUID uuid              get
    //final String name            get set(once)
    //final String localIP         not here
    //final String publicIP        not here
    //final int port               get set(once)
    //String[] dnsNames            get set add remove
    //UUID connectedTo             not here
    //byte[] signature             get set(once)
    //UUID[] friendUUIDs           get add remove
    //long[] friendUpdates         get set add remove
    //String[] friendNames         get set add remove
    //String[] friendLocalIPs      get set add remove
    //String[] friendPublicIPs     get set add remove
    //int[] friendPorts;           get set add remove
    //String[][] friendDNSNames    get set add remove
    //byte[][] friendSignatures    get set add remove
    

}

