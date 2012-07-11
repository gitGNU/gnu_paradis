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
package se.kth.maandree.paradis.util;
import se.kth.maandree.paradis.*;

import java.util.*;


/**
 * Minimalistic queue with without poll and peek put with self maintained polling,
 * with polling depending on time
 * 
 * @param  <E>  The type of elements stored in this collection 
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class TimeQueue<E>
{
    /**
     * Constructor
     */
    public TimeQueue()
    {
        this(10 * 60_000);
    }
    
    
    /**
     * Contructor
     * 
     * @param  age  How old the oldest element may be, in milli seconds, default is 10 minutes (600'000 milliseconds)
     */
    public TimeQueue(final int age)
    {
        this.thread = new Thread("TimeQueue cleaner")
                {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void run()
                    {
                        try
                        {   for (;;)
                                synchronized (TimeQueue.this)
                                {
                                    if (TimeQueue.this.stopped)
                                        return;
                                        
                                    if (TimeQueue.this.times.isEmpty())
                                        TimeQueue.this.wait();
                                        
                                    if (TimeQueue.this.stopped)
                                        return;
                                    
                                    if (TimeQueue.this.times.isEmpty())
                                        continue;
                                        
                                    final long time = System.currentTimeMillis() - TimeQueue.this.times.peekFirst().longValue();
                                    
                                    if (time > age)
                                    {   TimeQueue.this.times.pollFirst();
                                        TimeQueue.this.elements.pollFirst();
                                    }
                                    else
                                    {
                                        if (time % 60_000L != 0)
                                            Thread.sleep(time % 60_000L);
                                        for (int i = 0, n = (int)(time / 60_000L); i < n; i++)
                                            Thread.sleep(60_000);
                                        TimeQueue.this.times.pollFirst();
                                        TimeQueue.this.elements.pollFirst();
                                    }
                        }       }
                        catch (final InterruptedException quit)
                        {   // quit method
                        }
                    }
                };
        
        this.thread.setDaemon(true);
        this.thread.start();
    }
    
    
    
    /**
     * Actual queue with content
     */
    @requires("java-runtime>=6")
    protected final ArrayDeque<E> elements = new ArrayDeque<E>();
    
    /**
     * Queue with the contents insert times
     */
    @requires("java-runtime>=6")
    protected final ArrayDeque<Long> times = new ArrayDeque<Long>();
    
    /**
     * Whether cleaning is stopped
     */
    protected volatile boolean stopped = false;
    
    /**
     * Cleaning thread
     */
    protected Thread thread;
    
    
    
    /**
     * Adds a new element to the end of the queue
     * 
     * @param  element  The element to add
     */
    public void offer(final E element)
    {
        final long time = System.currentTimeMillis();
        synchronized (this)
        {   this.elements.offerLast(element);
            this.times.offerLast(Long.valueOf(time));
            this.notifyAll();
        }
    }
    
    
    /**
     * Stops the automated cleaning
     */
    public void stop()
    {
        synchronized (this)
        {   this.stopped = true;
            this.notifyAll();
            this.thread.interrupt();
        }
    }
    
}

