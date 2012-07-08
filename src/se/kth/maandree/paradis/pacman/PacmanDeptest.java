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
import se.kth.maandree.paradis.io.*;
import se.kth.maandree.paradis.*;

import java.util.*;
import java.io.*;


/**
 * -T invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PacmanDeptest implements Blackboard.BlackboardObserver //FIXME fix sorting (comparison)
{
    /**
     * The directory where the packages are located
     */
    private static final String PACKAGE_DIR = Pacman.PACKAGE_DIR;
    
    /**
     * The file where the data are saved
     */
    private static final String PACKAGES_FILE = Pacman.PACKAGES_FILE;
    
    
    /** Return missing dependencies and conflicts
     */ private static final String DEPTEST = Pacman.DEPTEST;
    
    
    
    //Has default constructor
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    @requires({"java-runtime>=6", "java-environment>=7"})
    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
    {
        if ((message instanceof Pacman.PacmanInvoke == false) || (((Pacman.PacmanInvoke)message).masteropt.equals(DEPTEST) == false))
            return;
        final ArrayList<String> packages = ((Pacman.PacmanInvoke)message).packages;
        
        try
        {
            final HashMap<String, String> installed = new HashMap<String, String>();
            try (final TransferInputStream tis = new TransferInputStream(new FileInputStream(new File(PACKAGES_FILE))))
            {   for (;;)
                {
                    final String pack = tis.readObject(String.class);
                    if (pack.isEmpty())
                        break;
                    final String _pack = pack.substring(0, pack.lastIndexOf("="));
                    installed.put(_pack, pack);
            }   }
            
            final HashMap<String, String> enversionmap = new HashMap<String, String>();
            {   final String[] packs = (new File(PACKAGE_DIR)).list();
                Arrays.sort(packs);
                for (final String pack : packs)
                {
                    if (pack.endsWith(".pkg.xz") == false)
                        continue;
                    final String $pack = pack.substring(0, pack.length() - ".pkg.xz".length());
                    enversionmap.put($pack.substring(0, $pack.lastIndexOf("=")), $pack);
            }   }
            
            final HashMap<String, ArrayList<String>> groupmap = new HashMap<String, ArrayList<String>>();
            for (final String pack : enversionmap.values())
                for (final String group : PackageInfo.fromFile(PACKAGE_DIR + pack + ".pkg.xz").groups)
                {   ArrayList<String> list = groupmap.get(group);
                    if (list == null)
                        groupmap.put(group, list = new ArrayList<String>());
                    list.add(pack);
                }
            
            final HashMap<VersionedPackage, VersionedPackage> provided = new HashMap<VersionedPackage, VersionedPackage>();
            final HashMap<VersionedPackage, VersionedPackage> conflict = new HashMap<VersionedPackage, VersionedPackage>();
            VersionedPackage tmp;
            
            for (final String pack : installed.keySet())
            {   final PackageInfo info = PackageInfo.fromFile(PACKAGE_DIR + pack + ".pkg.xz");
                provided.put(tmp = new VersionedPackage(pack), tmp);
                for (final String p : info.provides)   provided.put(tmp = new VersionedPackage(p), tmp);
                for (final String c : info.conflicts)  conflict.put(tmp = new VersionedPackage(c), tmp);
            }
            
            final ArrayDeque<String> $packages = new ArrayDeque<String>();
            for (final String pack : packages)
                if (groupmap.containsKey(pack) == false)
                    $packages.offerLast(pack);
                else
                    for (final String pac : groupmap.get(pack))
                        $packages.offerLast(pac);
            
            while ($packages.isEmpty() == false) //FIMXE resolve updates
            {   final String pac = $packages.pollFirst();
                final String pack = pac.contains("=") ? pac : enversionmap.get(pac);
                if (provided.get(tmp = new VersionedPackage(pack)) != null)
                {
                    if (tmp.intersects(provided.get(tmp)))
                        continue;
                    System.out.println(pack + " is in version conflict with " + conflict.get(tmp).toString());
                    return;
                }
                final PackageInfo info = PackageInfo.fromFile(PACKAGE_DIR + pack + ".pkg.xz");
                
                if ((tmp = new VersionedPackage(pack)).intersects(conflict.get(tmp)))
                {   System.out.println(pack + " conflicts with " + conflict.get(tmp).toString());
                    return;
                }
                provided.put(tmp = new VersionedPackage(pack), tmp);
                for (final String p : info.provides)
                {   if ((tmp = new VersionedPackage(pack)).intersects(conflict.get(tmp)))
                    {   System.out.println(pack + " conflicts with " + conflict.get(tmp).toString());
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

