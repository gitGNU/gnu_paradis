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
import org.nongnu.paradis.*;

import java.util.*;


/**
 * Abstract server
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public abstract class AbstractServer implements Blackboard.BlackboardObserver
{
    /**
     * Constructor
     * 
     * @param  nice  The priority, zero is default, and positive is low priority (that is, executed later that negative)
     */
    @SuppressWarnings("unchecked")
    public AbstractServer(final int nice)
    {
        Blackboard.getInstance(null).registerObserver(this);
        Blackboard.getInstance(null).registerPriority(this, nice, ServerInvoke.class);
    }
    
    
    
    /**
     * Executes a command, if it is not already consumed
     * 
     * @param   command   The command to execute
     * @param   consumed  Whether the command is already consumed, in which case, do not execute
     * @param   scanner   Input scanner you can use
     * @return            Whether the server recognises the command
     */
    public abstract boolean invoke(final String command, final boolean consumed, final Scanner scanner);
    
    
    /**
     * Disposes the server, this includes unregistering it from the blackboard
     */
    public void dispose()
    {
        Blackboard.getInstance(null).unregisterObserver(this);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
    {
        if (message instanceof ServerInvoke)
        {
            final ServerInvoke invocation = (ServerInvoke)message;
            try
                {   if (invoke(invocation.command, invocation.consumed, invocation.scanner))
                    if (invocation.consumed)
                        System.err.println("Warning: command already consumed by another server");
                    else
                        invocation.consumed = true;
            }
            catch (final Throwable err)
            {   err.printStackTrace(System.err);
            }
        }
    }
    
}

