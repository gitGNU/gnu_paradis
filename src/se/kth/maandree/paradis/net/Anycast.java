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
 * Anycast sending path information
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Anycast implements Cast
{
    /**
     * Constructor
     * 
     * @param  sender  The sender of the packet
     */
    public Anycast(final UUID sender)
    {
	this.sender = sender;
    }
    
    
    
    /**
     * The sender of the packet
     */
    public final UUID sender;
    
    
    
    /**
     * Protocol for transfering {@link Anycast}s
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    public static class AnycastTransferProtocol implements TransferProtocol<Anycast>
    {
	//Has default constructor
	
	
	
	/**
	 * {@inheritDoc}
	 */
	public Anycast read(final TransferInputStream stream) throws IOException
	{   return new Anycast(stream.readObject(UUID.class));
	}
    
    
	/**
	 * {@inheritDoc}
	 */
	public void write(final Anycast data, final TransferOutputStream stream) throws IOException
	{   stream.writeObject(data.sender);
	}
    
    }
    
    
    
    /**
     * {@inheritDoc}
     */
    public void addReceived(final UUID uuid)
    {   //Do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasReceived(final UUID uuid)
    {   return false;
    }
    
}

