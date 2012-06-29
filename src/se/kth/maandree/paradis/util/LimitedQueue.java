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
package se.kth.maandree.paradis.util;
import se.kth.maandree.paradis.*;

import java.util.*;


/**
 * Minimalistic queue with without poll and peek put with self maintained polling
 * with polling depending on size
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
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
     */
    public void offer(final E element)
    {
        synchronized (this)
        {
            if (this.elements.size() == limit)
                this.elements.pollFirst();
            
            this.elements.offerLast(element);
        }
    }
    
}

