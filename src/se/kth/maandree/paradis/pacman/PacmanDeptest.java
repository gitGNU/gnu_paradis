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
 * -T invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PacmanDeptest implements Blackboard.BlackboardObserver
{
    /** Return missing dependencies and conflicts
     */ private static final String DEPTEST = Pacman.DEPTEST;
    
    
    
    //Has default constructor
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    @requires("java-runtime>=6")
    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
    {
        if ((message instanceof Pacman.PacmanInvoke == false) || (((Pacman.PacmanInvoke)message).masteropt.equals(DEPTEST) == false))
            return;
        final ArrayList<String> packages = ((Pacman.PacmanInvoke)message).packages;
        
        try
        {
	    final Common common = new Common();
	    common.loadInstalled();
	    common.loadDatabase();
            
            final HashMap<VersionedPackage, VersionedPackage> provided = new HashMap<VersionedPackage, VersionedPackage>();
            final HashMap<VersionedPackage, VersionedPackage> conflict = new HashMap<VersionedPackage, VersionedPackage>();
            VersionedPackage tmp;
            
            for (final VersionedPackage pack : common.installedMap.values())
	    {   final PackageInfo info = PackageInfo.fromFile(common.packageMap.get(pack.toString()));
                provided.put(pack, pack);
                for (final String p : info.provides)   provided.put(tmp = new VersionedPackage(p), tmp);
                for (final String c : info.conflicts)  conflict.put(tmp = new VersionedPackage(c), tmp);
            }
            
	    final ArrayDeque<String> $packages = new ArrayDeque<String>();
	    for (final String pack : packages)
		$packages.offerLast(pack);
	    
	    common.loadGroups();
	    common.loadProviders();
	    common.loadReplacers();
	    final ArrayDeque<String> choice = new ArrayDeque<String>();
            for (;;)
            {
		String polled = $packages.pollFirst();
		if (common.groupMap.containsKey(polled))
                    for (final VersionedPackage pack : common.groupMap.get(polled))
                        $packages.offerFirst(pack.toString());
		
		if (polled == null)
		{
		    if ((polled = choice.pollFirst()) == null)
			break;
		    final VersionedPackage p = new VersionedPackage(polled);
		    if (provided.get(p) != null)
		    {   if (p.intersects(provided.get(p)))
			    continue;
			System.out.println(p.toString() + " is in version conflict with " + conflict.get(p).toString());
			return;
		    }
		    if (p.intersects(conflict.get(p)))
		    {   System.out.println(p + " conflicts with " + conflict.get(p).toString());
			return;
		    }
		    
		    final Set<VersionedPackage> providers = common.provideMap.get(p);
		    int i = 0, n;
		    final VersionedPackage[] packs = new VersionedPackage[n = providers.size()];
		    for (final VersionedPackage pack : providers)
			packs[i++] = pack;
		    Arrays.sort(packs);
		    
		    System.out.println("Select provider for " + p.name + ":\n");
		    for (i = 0; i < n; i++)
			System.out.println("  " + i + ".\t" + packs[i].name);
		    System.out.println();
		    System.out.print("Enter index (default = 0): ");
		    
		    i = 0;
		    mid:
		        for (int d; ((d = System.in.read()) != '\n') && (d != -1);)
			    if (('0' <= d) && (d <= '9'))
				i = (i * 10) - (d & 15);
			    else
				for (i = 1;;)
				    if (((d = System.in.read()) == '\n') || (d == -1))
					break mid;
		    i = -i;
		    
		    if ((0 > i) || (i >= n))
			i = 0;
		    provided.put(p, packs[i]);
		}
		
		final VersionedPackage pack = common.databaseMap.get(new VersionedPackage(polled));
		
		if (common.replaceMap.containsKey(pack))
		    System.out.println(common.databaseMap.get(pack).toString() + " is replaced by " + common.replaceMap.get(pack).toString());
		
                if (provided.get(pack) != null)
                {
                    if (pack.intersects(provided.get(pack)))
                        continue;
                    System.out.println(pack.toString() + " is in version conflict with " + conflict.get(pack).toString());
                    return;
                }
                if (pack.intersects(conflict.get(pack)))
                {   System.out.println(pack + " conflicts with " + conflict.get(pack).toString());
                    return;
                }
                provided.put(pack, pack);
		
                final PackageInfo info = PackageInfo.fromFile(common.packageMap.get(pack.toString()));
                for (final String p : info.provides)
                {   if (pack.intersects(conflict.get(pack)))
                    {   System.out.println(pack + " conflicts with " + conflict.get(pack).toString());
                        return;
                    }
                    provided.put(tmp = new VersionedPackage(p), tmp);
                }
                for (final String c : info.conflicts)
                    conflict.put(tmp = new VersionedPackage(c), tmp);
                
		Set<VersionedPackage> tmpset;
                for (final String d : info.dependencies)
		    if (((tmpset = common.provideMap.get(new VersionedPackage(d))) != null) && (tmpset.size() > 1))
			choice.offerLast(d);
		    else
			$packages.offerLast(d);
            }
            
            System.out.println("Passes depedency test");
            return;
        }
        catch (final Throwable err)
        {   System.err.println(err.toString());
            System.out.println("Could not load information needed to test dependencies.");
        }
    }
    
}

