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
package org.nongnu.paradis.pacman;
import org.nongnu.paradis.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;


/**
 * -Q invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
public class PacmanQuery implements Blackboard.BlackboardObserver //FIXME Print package information on (-i, --info)
{
    /**
     * The directory where the packages are located
     */
    public static final String PACKAGE_DIR = Pacman.PACKAGE_DIR;
    
    
    /** Search for installed packages
     */ private static final String QUERY = Pacman.QUERY;
    
    /** Only return explicitly installed packages
     */ private static final String QUERY_EXPLICIT = Pacman.QUERY_EXPLICIT;
    
    /** Use regular expressions
     */ private static final String QUERY_SEARCH = Pacman.QUERY_SEARCH;
    
    /** Only return unrequired packages
     */ private static final String QUERY_UNREQUIRED = Pacman.QUERY_UNREQUIRED;
    
    /** Do not return up to date packages
     */ private static final String QUERY_UPGRADE = Pacman.QUERY_UPGRADE;
    
    
    
    //Has default constructor
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
    {
        if ((message instanceof Pacman.PacmanInvoke == false) || (((Pacman.PacmanInvoke)message).masteropt.equals(QUERY) == false))
            return;
        final HashSet<String> options = ((Pacman.PacmanInvoke)message).options;
        final ArrayList<String> packages = ((Pacman.PacmanInvoke)message).packages;
        final HashSet<String> packageSet = new HashSet<String>();
        for (final String pack : packages)
            packageSet.add(pack);
        
        final boolean explicit   = options.contains(QUERY_EXPLICIT);
        final boolean search     = options.contains(QUERY_SEARCH);
        final boolean unrequired = options.contains(QUERY_UNREQUIRED);
        final boolean upgrade    = options.contains(QUERY_UPGRADE);
        
        try
        {
            final Pattern pattern;
            if (search == false)
                pattern = null;
            else
            {
                final StringBuilder buf = new StringBuilder();
                boolean first = true;
                for (final String pack : packages)
                {
                    if (first == false)
                        buf.append("|");
                    first = false;
                    buf.append(pack);
                }
                pattern = Pattern.compile(buf.toString());
            }
            
            final Common common = new Common();
            common.loadInstalled();
            
            final ArrayList<VersionedPackage> tmp = new ArrayList<VersionedPackage>();
            
            if (pattern == null)
            {   for (final VersionedPackage pack : common.installedMap.values())
                    if ((packageSet.contains(pack.name) == false) && (packageSet.contains(pack.toString()) == false))
                        tmp.add(pack);
                    else if ((explicit ^ unrequired) && (common.installedExplicitly.contains(pack) ^ explicit))
                        tmp.add(pack);
            }
            else
                for (final VersionedPackage pack : common.installedMap.values())
                    if ((pattern.matcher(pack.name).matches() == false) && (pattern.matcher(pack.toString()).matches() == false))
                        tmp.add(pack);
                    else if ((explicit ^ unrequired) && (common.installedExplicitly.contains(pack) ^ explicit))
                        tmp.add(pack);
            
            for (final VersionedPackage pack : tmp)
                common.installedMap.remove(pack);
            
            final Map<String, String> upgradable = getUpgradable();
            final Set<String> required = getRequired();
            
            final String[] rc = new String[common.installedExplicitly.size()];
            int ptr = 0;
            for (final VersionedPackage pack : common.installedMap.keySet())
                if ((unrequired == false) || common.installedExplicitly.contains(pack))
                {
                    if ((upgrade == false) || upgradable.containsKey(pack.name))
                        rc[ptr++] = pack.toString();
                }
                else
                    if ((required.contains(pack.name) == false) || (upgrade == false) || upgradable.containsKey(pack.name))
                        rc[ptr++] = pack.toString();
            
            for (int i = 0; i < ptr; i++)
                System.out.println(rc[i]);
        }
        catch (final Throwable err)
        {   System.err.println(err.toString());
        }
    }
    
    
    /**
     * Gets a map of all upgradable packages, mapped to their replacer or never version
     * 
     * @return  Map of all upgradable packages, mapped to their replacer or never version
     */
    public static HashMap<String, String> getUpgradable()
    {
        final HashMap<String, String> rc = new HashMap<String, String>();
        try
        {
            final Common common = new Common();
            common.loadDatabase();
            
            for (final VersionedPackage pack : common.databaseMap.values())
                rc.put(pack.name, pack.toString());
            
            common.loadReplacers();
            for (final Map.Entry<VersionedPackage, VersionedPackage> pair : common.replaceMap.entrySet())
                rc.put(pair.getKey().toString(), pair.getValue().toString());
        }
        catch (final Throwable err)
        {   System.err.println(err.toString());
        }
        return rc;
    }
    
    
    /**
     * Gets a set of all required packages
     * 
     * @return  Set of all required packages
     * 
     * @throws  IOException  On I/O exception
     */
    @requires("java-runtime>=6")
    public static HashSet<String> getRequired() throws IOException
    {
        final HashSet<String> rc = new HashSet<String>();
        final Common common = new Common();
        common.loadInstalled();
        
        final HashSet<VersionedPackage> provided = new HashSet<VersionedPackage>();
        final ArrayDeque<VersionedPackage> dependents = new ArrayDeque<VersionedPackage>();
        
        for (final VersionedPackage dependent : common.installedExplicitly)
            dependents.add(dependent);
        
        while (dependents.isEmpty() == false)
        {
            final VersionedPackage dependent = dependents.pollFirst();
            if (provided.contains(dependent))
                continue;
            rc.add(dependent.name);
            
            try
            {
                final PackageInfo info = PackageInfo.fromFile(common.packageMap.get(dependent));
                final String[] opts = info.optionalDependencies;
                final String[] deps = info.dependencies;
                
                provided.add(dependent);
                for (final String provides : info.provides)
                    provided.add(new VersionedPackage(provides));
                
                for (final String[] _deps : new String[][] { deps, opts })
                    for (final String _dep : _deps)
                    {
                        final VersionedPackage dep = new VersionedPackage(_dep);
                        if (common.installedMap.containsKey(dep))
                            if (provided.contains(dep) == false)
                                dependents.offerLast(common.installedMap.get(dep));
                    }
            }
            catch (final Throwable err)
            {   System.err.println(err.toString());
            }
        }
        return rc;
    }
    
    
    /**
     * Search for packages
     * 
     * @param  everything  All packages
     * @param  packages    Wanted packages
     * @param  ignores     Unwanted packages
     * @param  regex       Whether to use regular expressions
     */
    public static void search(final HashMap<VersionedPackage, VersionedPackage> everything, final ArrayList<String> packages, final HashSet<String> ignores, final boolean regex)
    {
        final ArrayList<VersionedPackage> packs = new ArrayList<VersionedPackage>();
        for (final String pack : packages)
            packs.add(new VersionedPackage(pack));
        
        final HashMap<VersionedPackage, VersionedPackage> skips = new HashMap<VersionedPackage, VersionedPackage>();
        VersionedPackage tmp;
        for (final String pack : ignores)
            skips.put(tmp = new VersionedPackage(pack), tmp);
        
        if (regex == false)
            for (final VersionedPackage pack : packs)
            {
                if (((tmp = skips.get(pack)) != null) && tmp.intersects(pack))
                    continue;
                if (((tmp = everything.get(pack)) == null) || (tmp.intersects(pack) == false))
                    continue;
                System.out.println(tmp.toString());
            }
        else
        {
            final Pattern pattern;
            final StringBuilder buf = new StringBuilder();
            boolean first = true;
            for (final VersionedPackage pack : packs)
            {
                if (first == false)
                    buf.append("|");
                first = false;
                buf.append(pack.toString());
            }
            pattern = Pattern.compile(buf.toString());
            
            for (final VersionedPackage pack : everything.values())
            {
                if (((tmp = skips.get(pack)) != null) && tmp.intersects(pack))
                    continue;
                if (pattern.matcher(pack.toString()).matches() || pattern.matcher(pack.name).matches())
                    System.out.println(pack.toString());
            }
        }
    }
    
}

