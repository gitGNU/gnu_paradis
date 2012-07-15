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
package org.nongnu.paradis.net.messages;
import org.nongnu.paradis.net.*;
import org.nongnu.paradis.*;


/**
 * Blackboard message to send a packet
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class SendPacket implements Blackboard.BlackboardMessage
{
    /**
     * Constructor
     * 
     * @param  packet  The packet to send
     */
    public SendPacket(final Packet packet)
    {
        this.packet = packet;
    }
    
    
    
    /**
     * The packet to send
     */
    public final Packet packet;
    
    /**
     * An error may be set on this varible by the network module on failure
     */
    public Throwable error = null;
    
}

