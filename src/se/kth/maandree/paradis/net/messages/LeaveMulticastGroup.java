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
package se.kth.maandree.paradis.net.messages;
import se.kth.maandree.paradis.net.*;
import se.kth.maandree.paradis.*;


/**
 * Blackboard message to leave a multicast group
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class LeaveMulticastGroup implements Blackboard.BlackboardMessage
{
    /**
     * Constructor
     * 
     * @param  group  The group to leave
     */
    public LeaveMulticastGroup(final UUID group)
    {
        this.group = group;
    }
    
    
    
    /**
     * The group to leave
     */
    public final UUID group;
    
}

