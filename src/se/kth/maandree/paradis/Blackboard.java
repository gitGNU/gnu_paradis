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
package se.kth.maandree.paradis;

import java.util.*;


/**
 * Client local message broadcasting blackboard
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Blackboard
{
    /**
     * Do not thread
     */
    public static final ThreadingPolicy NO_THREADING = null;
    
    /**
     * Normal thread
     */
    public static final ThreadingPolicy THREADED;
    
    /**
     * Daemon thread
     */
    public static final ThreadingPolicy DAEMON_THREADING;
    
    /**
     * Nice thread
     */
    public static final ThreadingPolicy NICE_THREADING;
    
    /**
     * Nice daemon thread
     */
    public static final ThreadingPolicy NICE_DAEMON_THREADING;
    
    
    
    /**
     * Multiton constructor
     */
    private Blackboard()
    {
	// Do nothing
    }
    
    
    /**
     * Gets, and if missing, creates, an instance of this class
     * 
     * @param   name  The name (unique identifier) of the instance, {@code null} is default
     * @return        The, possibily, new instance
     */
    public static Blackboard getInstance(final Object name)
    {
	Blackboard instance;
	if ((instance = instances.get(name)) == null)
	    synchronized (instances)
            {   if ((instance = instances.get(name)) == null)
		    instances.put(name, instance = new Blackboard());
            }
	return instance;
    }
    
    
    /**
     * The instance of this class
     */
    private static final HashMap<Object, Blackboard> instances = new HashMap<>();
    
    
    
    /**
     * Class initialiser
     */
    static
    {
	THREADED = new ThreadingPolicy()
	        {   /**
		     * {@inheritDoc}
		     */
		    public Thread createThread(final Runnable runnable)
		    {   final Thread thread = new Thread(runnable);
			thread.setDaemon(false);
			thread.setPriority(5); //normal: 5 of 1..10; corresponding nice value: 0
			return thread;
		}   };
	
	DAEMON_THREADING = new ThreadingPolicy()
	        {   /**
		     * {@inheritDoc}
		     */
		    public Thread createThread(final Runnable runnable)
		    {   final Thread thread = new Thread(runnable);
			thread.setDaemon(true);
			thread.setPriority(5); //normal: 5 of 1..10; corresponding nice value: 0
			return thread;
		}   };

	NICE_THREADING = new ThreadingPolicy()
	        {   /**
		     * {@inheritDoc}
		     */
		    public Thread createThread(final Runnable runnable)
		    {   final Thread thread = new Thread(runnable);
			thread.setDaemon(false);
			thread.setPriority(2); //below normal: 2 of 1..10; corresponding nice value: 3
			return thread;
		}   };
	
	NICE_DAEMON_THREADING = new ThreadingPolicy()
	        {   /**
		     * {@inheritDoc}
		     */
		    public Thread createThread(final Runnable runnable)
		    {   final Thread thread = new Thread(runnable);
			thread.setDaemon(true);
			thread.setPriority(2); //below normal: 2 of 1..10; corresponding nice value: 3
			return thread;
		}   };
    }
    
    
    
    /**
     * Registrered observers
     */
    private HashSet<BlackboardObserver> observers = new HashSet<>();
    
    /**
     * How to thread message observations
     */
    private HashMap<BlackboardObserver, HashMap<Class<? extends BlackboardMessage>, ThreadingPolicy>> observationThreading = new HashMap<>();
    
    /**
     * Concurrency monitor
     */
    private Object monitor = new Object();
    
    
    
    /**
     * This interface is used for all event
     */
    public static interface BlackboardMessage
    {
	//Marker interface
    }
    
    
    /**
     * Message broadcasted when a new observer is registrered or when a observer is unregistrered
     */
    public static class ObserverRegisterMessage implements BlackboardMessage
    {
	/**
	 * Constructor
	 * 
	 * @param  observer  The observer
	 * @param  register  {@code true} if the observer is newly registered, {@code false} if newly unregistered
	 */
	public ObserverRegisterMessage(final BlackboardObserver observer, final boolean register)
	{
	    this.observer = observer;
	    this.register = register;
	}
	
	
	
	/**
	 * The observer
	 */
	private final BlackboardObserver observer;
	
	/**
	 * {@code true} if the observer is newly registered, {@code false} if newly unregistered
	 */
	private final boolean register;
	
	
	
	/**
	 * Gets the observer
	 * 
	 * @param  The observer
	 */
	public BlackboardObserver getObserver()
	{   return this.observer;
	}
	
	/**
	 * Gets whether the observer is newly registered or newly unregistered 
	 * 
	 * @return  {@code true} if the observer is newly registered, {@code false} if newly unregistered
	 */
	public boolean getRegister()
	{   return this.register;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{   return observer.toString() + (this.register ? " registered" : " unregistered");
	}
	
    }
    
    
    /**
     * This interface makes observersion on the enclosing class possible
     */
    public static interface BlackboardObserver
    {
	/**
	 * This method is invoked when the a message is pinned on the blackboard
	 */
	public void messageBroadcasted(final Blackboard.BlackboardMessage message);
    }
    
    
    /**
     * Message observation threading policy
     */
    public static interface ThreadingPolicy
    {
	/**
	 * Creates a thread according to the policy
	 *
	 * @param   runnable  The {@code run} implementation of the thread
	 * @return            The new thread
	 */
	public Thread createThread(final Runnable runnable);
    }
    
    
    
    /**
     * Registers a message type-wide observer
     *
     * @param  observer  The observer to register
     */
    public void registerObserver(final BlackboardObserver observer)
    {
	synchronized (monitor)
	{
	    System.err.println("BLACKBOARD.registerObserver(" + observer + ")");
	    this.observers.add(observer);
	    this.broadcastMessage(new ObserverRegisterMessage(observer, true));
	}
    }
    
    
    /**
     * Unregisters a message type-wide observer
     *
     * @param  observer  The observer to unregister
     */
    public void unregisterObserver(final BlackboardObserver observer)
    {
	synchronized (monitor)
	{
	    System.err.println("BLACKBOARD.unregisterObserver(" + observer + ")");
	    this.observers.remove(observer);
	    this.observationThreading.remove(observer);
	    this.broadcastMessage(new ObserverRegisterMessage(observer, false));
	}
    }
    
    
    /**
     * Registers a threading policy for an observer and a message type.<br/>
     * If a threading policy is registrered for an observer it will only receive message with a registrered threading policy.
     * 
     * @param  observer     The observer
     * @param  messageType  The message type
     * @param  policy       The threading policy
     * 
     * @deprecated  Use overloading {@link #registerThreadingPolicy(BlackboardObserver, ThreadingPolicy, Class<? extends BlackboardMessage>...)} instead
     */
    @Deprecated
    public void registerThreadingPolicy(final BlackboardObserver observer, final Class<? extends BlackboardMessage> messageType, final ThreadingPolicy policy)
    {
	registerThreadingPolicy(observer, policy, messageType);
    }
    
    
    /**
     * Registers a threading policy for an observer and some message types.<br/>
     * If a threading policy is registrered for an observer it will only receive message with a registrered threading policy.
     * 
     * @param  observer      The observer
     * @param  policy        The threading policy
     * @param  messageTypes  The message types, must be <code>Class<? extends BlackboardMessage></code>
     */
    @SuppressWarnings("unchecked")
    public void registerThreadingPolicy(final BlackboardObserver observer, final ThreadingPolicy policy, final Class... messageTypes)
    {
	synchronized (monitor)
	{
	    HashMap<Class<? extends BlackboardMessage>, ThreadingPolicy> map = observationThreading.get(observer);
	    if (map == null)
	    {
		map = new HashMap<Class<? extends BlackboardMessage>, ThreadingPolicy>();
		this.observationThreading.put(observer, map);
	    }
	    for (final Class<? extends BlackboardMessage> messageType : messageTypes)
		map.put(messageType, policy);
	}
    }
    
    
    /**
     * Broadcasts a message to all observers
     * 
     * @param  message  The message to broadcast
     */
    public void broadcastMessage(final BlackboardMessage message)
    {
	synchronized (monitor)
	{
	    System.err.println("BLACKBOARD.broadcastMessage(" + message.toString() + ")");
	    final ArrayList<Thread> threads = new ArrayList<Thread>();
	    
	    for (final BlackboardObserver observer : observers)
	    {
		System.err.println("BLACKBOARD.broadcastMessage() ==> " + observer.toString());
		final ThreadingPolicy policy;
		final Runnable runnable = new Runnable()
		        {
			    /**
			     * {@inheritDoc}
			     */
			    public void run()
			    {   observer.messageBroadcasted(message);
			    }
		    };
		
		final HashMap<Class<? extends BlackboardMessage>, ThreadingPolicy> map = observationThreading.get(observer);
		
		if (map == null)
		    policy = null;
		else if (map.containsKey(message.getClass()))
		    policy = map.get(message.getClass());
		else
		    continue;
		
		if (policy == null)  runnable.run();
		else                 threads.add(policy.createThread(runnable));
	    }
	    
	    for (final Thread thread : threads)
		thread.start();
	    
	    System.err.println("BLACKBOARD.broadcastMessage() <<<<");
	}
    }
    
}

