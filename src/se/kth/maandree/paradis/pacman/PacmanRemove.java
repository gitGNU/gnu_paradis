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


/**
 * -R invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PacmanRemove implements Blackboard.BlackboardObserver
{
    /** Uninstall packages
     */ private static final String REMOVE = Pacman.REMOVE;
    
    /** Uninstall packages dependent on the packages
     */ private static final String REMOVE_CASCADE = Pacman.REMOVE_CASCADE;
    
    /** Do not uninstall dependencies
     */ private static final String REMOVE_NODEPS = Pacman.REMOVE_NODEPS;
    
    /** Do not uninstall the files, only mark as uninstalled
     */ private static final String REMOVE_DBONLY = Pacman.REMOVE_DBONLY;
    
    /** Remove dependencies
     */ private static final String REMOVE_RECURSIVE = Pacman.REMOVE_RECURSIVE;
    
    /** Use regular expressions
     */ private static final String REMOVE_SEARCH = Pacman.REMOVE_SEARCH;
    
    /** Remove packages required by any installed package
     */ private static final String REMOVE_UNREQUIRED = Pacman.REMOVE_UNREQUIRED;
    
    /** Do not remove packages needed by any other package
     */ private static final String REMOVE_UNNEEDED = Pacman.REMOVE_UNNEEDED;
    
    
    
    //Has default constructor
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    @requires("java-runtime>=6")
    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
    {
        if ((message instanceof Pacman.PacmanInvoke == false) || (((Pacman.PacmanInvoke)message).masteropt.equals(REMOVE) == false))
            return;
        final HashSet<String> options = ((Pacman.PacmanInvoke)message).options;
        final HashSet<String> ignores = ((Pacman.PacmanInvoke)message).ignores;
        final ArrayList<String> packages = ((Pacman.PacmanInvoke)message).packages;
        
        final boolean cascade    = options.contains(REMOVE_CASCADE);
        final boolean nodeps     = options.contains(REMOVE_NODEPS);
        final boolean dbonly     = options.contains(REMOVE_DBONLY);
        final boolean recursive  = options.contains(REMOVE_RECURSIVE);
        final boolean unrequired = options.contains(REMOVE_UNREQUIRED);
        final boolean unneeded   = options.contains(REMOVE_UNNEEDED);
        
        final Common common = new Common();
        try
        {   if (options.contains(REMOVE_SEARCH))
            {   common.loadInstalled();
                PacmanQuery.search(common.installedMap, packages, ignores, options.contains(REMOVE_SEARCH));
            }
            else
            {
		common.loadInstalled();
		common.loadGroups();
		
		VersionedPackage tmp;
		final HashMap<VersionedPackage, VersionedPackage> skips = new HashMap<VersionedPackage, VersionedPackage>();
		final HashMap<VersionedPackage, VersionedPackage> required = new HashMap<VersionedPackage, VersionedPackage>();
		final HashMap<VersionedPackage, HashSet<VersionedPackage>> casc = new HashMap<VersionedPackage, HashSet<VersionedPackage>>();
		HashSet<VersionedPackage> uninstall = new HashSet<VersionedPackage>();
		final ArrayDeque<VersionedPackage> queue = new ArrayDeque<VersionedPackage>();
		
		for (final String pack : ignores)
		    skips.put(tmp = new VersionedPackage(pack), tmp);
		for (final String pack : packages)
		    queue.add(new VersionedPackage(pack));
		if (unrequired || unneeded)
		    for (final String pack : PacmanQuery.getRequired())
			required.put(tmp = new VersionedPackage(pack), tmp);
		if (unrequired)
		    for (final VersionedPackage pack : common.installedMap.values())
			if (pack.intersects(required.get(pack)) == false)
			    uninstall.add(pack);
		
		if (cascade)
		    for (final VersionedPackage pack : common.installedMap.values())
		    {
			final PackageInfo info = PackageInfo.fromFile(common.packageMap.get(pack.toString()));
			for (final String[] ds : new String[][] { info.dependencies, info.optionalDependencies })
			    for (final String d : ds)
			    {
				final VersionedPackage vd = new VersionedPackage(d);
				HashSet<VersionedPackage> set = casc.get(vd);
				if (set == null)
				    casc.put(vd, set = new HashSet<VersionedPackage>());
				set.add(vd);
			    }
		    }
		
		for (;;)
		{
		    while (queue.isEmpty() == false)
		    {
			final VersionedPackage pack = queue.pollFirst();
			for (final VersionedPackage pac : common.groupMap.get(pack))
			    queue.offerFirst(pac);
			if (common.groupMap.get(pack).isEmpty() == false)    continue;
			if (pack.intersects(skips.get(pack)))                continue;
			if (common.installedMap.containsKey(pack) == false)  continue;
			final PackageInfo info = PackageInfo.fromFile(common.packageMap.get(pack.toString()));
			if (recursive)
			{   for (final String d : info.dependencies)          queue.offerLast(new VersionedPackage(d));
			    for (final String d : info.optionalDependencies)  queue.offerLast(new VersionedPackage(d));
			}
			if (cascade)
			    for (final VersionedPackage c : casc.get(pack))
				queue.offerLast(c);
		    }
		    
		    if (uninstall.isEmpty())
			break;
		    
		    for (final VersionedPackage pack : uninstall)
		    {
			    if (pack.intersects(skips.get(pack)))
				continue;
			    if (unneeded && pack.intersects(required.get(pack)))
				continue;
			    common.uninstall(pack, dbonly);
		    }
		    uninstall = new HashSet<VersionedPackage>();
		    
		    if (nodeps == false)
			for (final String spack : PacmanQuery.getRequired())
			{
			    final VersionedPackage pack = new VersionedPackage(spack);
			    if (required.containsKey(pack) == false)
			    {
				required.put(pack, pack);
				queue.offerLast(pack);
			    }
			}
		}
		
		common.syncInstalledMap();
        }   }
        catch (final Throwable err)
        {   System.err.println(err.toString());
        }
    }
    
}

