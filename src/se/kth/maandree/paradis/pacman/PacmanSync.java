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
package se.kth.maandree.paradis.pacman;
import se.kth.maandree.paradis.*;

import java.util.*;
import java.io.*;


/**
 * -S invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PacmanSync implements Blackboard.BlackboardObserver
{
    /** Synchronise (install or upgrade) packages
     */ private static final String SYNC = Pacman.SYNC;
    
    /** Ignore dependencies
     */ private static final String SYNC_NODEPS = Pacman.SYNC_NODEPS;
    
    /** Mark dependencies as explicitly installed
     */ private static final String SYNC_ASEXPLICIT = Pacman.SYNC_ASEXPLICIT;
    
    /** Override existing files not installed by the package itself
     */ private static final String SYNC_FORCE = Pacman.SYNC_FORCE;
    
    /** Mark installed packages as installed as a dependency
     */ private static final String SYNC_ASDEPS = Pacman.SYNC_ASDEPS;
    
    /** Ignore up to date packages
     */ private static final String SYNC_NEEDED = Pacman.SYNC_NEEDED;
    
    /** Do not install the files
     */ private static final String SYNC_DBONLY = Pacman.SYNC_DBONLY;
    
    /** Recursively reinstall all dependencies
     */ private static final String SYNC_RECURSIVE = Pacman.SYNC_RECURSIVE;
    
    /** Use regular expressions
     */ private static final String SYNC_SEARCH = Pacman.SYNC_SEARCH;
    
    /** Include all installed non-up to date packages
     */ private static final String SYNC_UPGRADE = Pacman.SYNC_UPGRADE;
    
    
    
    //Has default constructor
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
    {
        if ((message instanceof Pacman.PacmanInvoke == false) || (((Pacman.PacmanInvoke)message).masteropt.equals(SYNC) == false))
            return;
        final HashSet<String> options = ((Pacman.PacmanInvoke)message).options;
        final HashSet<String> ignores = ((Pacman.PacmanInvoke)message).ignores;
        final ArrayList<String> packages = ((Pacman.PacmanInvoke)message).packages;
        
        final boolean nodeps    = options.contains(SYNC_NODEPS);
        final boolean asexpl    = options.contains(SYNC_ASEXPLICIT);
        final boolean asdeps    = options.contains(SYNC_ASDEPS);
        final boolean force     = options.contains(SYNC_FORCE); 
        final boolean needed    = options.contains(SYNC_NEEDED);
        final boolean dbonly    = options.contains(SYNC_DBONLY);
        final boolean recursive = options.contains(SYNC_RECURSIVE);
        final boolean upgrade   = options.contains(SYNC_UPGRADE);
        
        final Common common = new Common();
        try
        {   if (options.contains(SYNC_SEARCH))
            {   common.loadDatabase();
                PacmanQuery.search(common.databaseMap, packages, ignores, options.contains(SYNC_SEARCH));
            }
            else
                PacmanSync.sync(packages, ignores, false, nodeps, asexpl, asdeps, force, needed, dbonly, recursive, upgrade);
        }
        catch (final Throwable err)
        {   System.err.println(err.toString());
        }
    }
    
    
    /**
     * Synchronise packages
     * 
     * @param  packages   Packages to synchronise
     * @param  ignores    Packages not to synchronise
     * @param  clean      Uninstall before synchronising
     * @param  nodeps     Ignore dependencies
     * @param  asexpl     Mark dependencies as explicitly installed
     * @param  asdeps     Mark installed packages as installed as a dependency
     * @param  force      Override existing files not installed by the package itself
     * @param  needed     Ignore up to date packages
     * @param  dbonly     Do not install the files
     * @param  recursive  Recursively reinstall all dependencies
     * @param  upgrade    Include all installed non-up to date packages
     * 
     * @throws  IOException  On I/O exception
     */
    @requires("java-runtime>=6")
    public static void sync(final ArrayList<String> packages, final HashSet<String> ignores, final boolean clean, final boolean nodeps, final boolean asexpl,
                            final boolean asdeps, final boolean force, final boolean needed, final boolean dbonly, final boolean recursive, final boolean upgrade) throws IOException
    {
	/**
	 * Set that can be uniserse
	 * 
	 * @param  <E>  Set element
	 */
	class FSet<E>
	{
	    //Has default constructor
	    
	    
	    /** Underlaying map
	     */ public final HashMap<E, E> map = new HashMap<E, E>();
	    
	    /** Whether the set is the universe set
	     */ public boolean containsEverything = false;
	    
	    /** Whether the set is the empty set
	     */ public boolean containsNothing = false;
	    
	    
	    /**
	     * Adds an item to the set
	     * 
	     * @param  item  The item
	     */
	    public void add(final E item)
	    {   this.map.put(item, item);
	    }
	    
	    /**
	     * Removes an item to the set
	     * 
	     * @param  item  The item
	     */
	    public void remove(final E item)
	    {   this.map.remove(item);
	    }
	    
	    /**
	     * Checks whether the set contains an item
	     * 
	     * @param   item  The item
	     * @return        Whether the set contains the item
	     */
	    public boolean contains(final E item)
	    {   return this.containsEverything?true : this.containsNothing?false : this.map.containsKey(item);
	    }
	    
	    /**
	     * Return the intern of an item, or {@code null} is it does not exist
	     * 
	     * @param   item  The item
	     * @return        The intern of an item, or {@code null} is it does not exist
	     */
	    public E get(final E item)
	    {   return this.containsEverything?(this.map.containsKey(item) ? this.map.get(item) : item) : this.containsNothing?null : this.map.get(item);
	    }
	}
	
	final Common common = new Common();
	common.loadDatabase();
	common.loadInstalled();
	
	final FSet<VersionedPackage> explicits = new FSet<VersionedPackage>();
	if (asexpl ^ asdeps)  if (asexpl)  explicits.containsEverything = true;
	                      else         explicits.containsNothing    = true;
	for (final String pack : packages)
	    explicits.add(new VersionedPackage(pack));
	
	final FSet<VersionedPackage> skips = new FSet<VersionedPackage>();
	for (final String pack : ignores)
	    skips.add(new VersionedPackage(pack));
	if (needed)
	    for (final VersionedPackage pack : common.installedMap.values())
		skips.add(pack);
	
	final HashSet<VersionedPackage> install = new HashSet<VersionedPackage>();
	final HashSet<VersionedPackage> uninstall = new HashSet<VersionedPackage>();
	final ArrayDeque<VersionedPackage> queue = new ArrayDeque<VersionedPackage>();
	for (final String pack : packages)
	    queue.offerFirst(new VersionedPackage(pack));
	if (upgrade)
	    for (final Map.Entry<String, String> entry : PacmanQuery.getUpgradable().entrySet())
	    {
		final VersionedPackage key = new VersionedPackage(entry.getKey());
		final VersionedPackage value = new VersionedPackage(entry.getValue());
		queue.offerFirst(value);
		if (value.name.equals(key.name) == false)
		    uninstall.add(key);
	    }
	
	for (;;)
	{
	    final VersionedPackage polled = queue.pollFirst();
	    if (polled == null)
		break;
	}
	
	//  nodeps  recursive
	
	//  clean  force  dbonly
    }
    
}

