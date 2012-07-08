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
	    common.loadGroups();
            
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
                if (common.groupMap.containsKey(pack) == false)
                    $packages.offerLast(pack);
                else
                    for (final VersionedPackage pac : common.groupMap.get(pack))
                        $packages.offerLast(pac.toString());
            
            while ($packages.isEmpty() == false) //FIMXE resolve updates
            {   final VersionedPackage pack = common.databaseMap.get(new VersionedPackage($packages.pollFirst()));
                if (provided.get(pack) != null)
                {
                    if (pack.intersects(provided.get(pack)))
                        continue;
                    System.out.println(pack.toString() + " is in version conflict with " + conflict.get(pack).toString());
                    return;
                }
                final PackageInfo info = PackageInfo.fromFile(common.packageMap.get(pack.toString()));
                
                if ((pack).intersects(conflict.get(pack)))
                {   System.out.println(pack + " conflicts with " + conflict.get(pack).toString());
                    return;
                }
                provided.put(pack, pack);
                for (final String p : info.provides)
                {   if (pack.intersects(conflict.get(pack)))
                    {   System.out.println(pack + " conflicts with " + conflict.get(pack).toString());
                        return;
                    }
                    provided.put(tmp = new VersionedPackage(p), tmp);
                }
                for (final String c : info.conflicts)
                    conflict.put(tmp = new VersionedPackage(c), tmp);
                
                for (final String d : info.dependencies) //FIXME multiple choose dependencies
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

