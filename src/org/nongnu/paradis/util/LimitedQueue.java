/**
 *  Paradis — Ever growing network for parallel and distributed computing.
 *  Copyright © 2012, 2013  Mattias Andrée (maandree@member.fsf.org)
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
package org.nongnu.paradis.util;
import org.nongnu.paradis.*;

import java.util.*;


/**
 * Minimalistic queue with without poll and peek put with self maintained polling
 * with polling depending on size
 * 
 * @param  <E>  The type of elements stored in this collection 
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
public class LimitedQueue<E>
{
    /**
     * Constructor
     */
    public LimitedQueue()
    {
        this(10_000);
    }
    
    /**
     * Contructor
     * 
     * @param  limit  The maximum number of allowed elements
     */
    public LimitedQueue(final int limit)
    {
        this.limit = limit;
    }
    
    
    
    /**
     * The maximum number of allowed elements
     */
    protected int limit;
    
    /**
     * Actual queue with content
     */
    @requires("java-runtime>=6")
    protected final ArrayDeque<E> elements = new ArrayDeque<E>();
    
    
    
    /**
     * Adds a new element to the end of the queue
     * 
     * @param  element  The element to add
     */
    public void offer(final E element)
    {
        synchronized (this)
        {
            if (this.elements.size() == this.limit)
                this.elements.pollFirst();
            
            this.elements.offerLast(element);
        }
    }
    
}

