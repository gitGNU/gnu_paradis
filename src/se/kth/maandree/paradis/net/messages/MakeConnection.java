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
package se.kth.maandree.paradis.net.messages;
import se.kth.maandree.paradis.*;

import java.net.*;


/**
 * Blackboard message to connect to remote machines
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class MakeConnection implements Blackboard.BlackboardMessage
{
    /**
     * Constructor
     * 
     * @param  remoteAddress  The remote machine's address
     * @param  remotePort     The remote machine's port
     */
    public MakeConnection(final InetAddress remoteAddress, final int remotePort)
    {
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
    }
    
    
    
    /**
     * The remote machine's address
     */
    public final InetAddress remoteAddress;
    
    /**
     * The remote machine's port
     */
    public final int remotePort;
    
}

