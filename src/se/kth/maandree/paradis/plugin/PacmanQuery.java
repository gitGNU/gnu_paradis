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
package se.kth.maandree.paradis.plugin;
import se.kth.maandree.paradis.io.*;
import se.kth.maandree.paradis.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;


/**
 * -Q invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PacmanQuery implements Blackboard.BlackboardObserver
{
    /**
     * The directory where the packages are located
     */
    public static final String PACKAGE_DIR = Pacman.PACKAGE_DIR;
    
    /**
     * The file where the data are saved
     */
    private static final String PACKAGES_FILE = Pacman.PACKAGES_FILE;
    
    
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
	
	final Pattern pattern;
	if (search)
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
	
	final HashMap<String, Boolean> installmap = new HashMap<String, Boolean>();
	try (final TransferInputStream tis = new TransferInputStream(new FileInputStream(new File(PACKAGES_FILE))))
	{
	    for (;;)
	    {
		final String pack = tis.readObject(String.class);
		if (pack.isEmpty())
		    break;
		final String _pack = pack.substring(0, pack.lastIndexOf("="));
		if (pattern == null)
		{   if ((packageSet.contains(_pack) == false) && (packageSet.contains(pack) == false))
			continue;
		}
		else
		{   if ((pattern.matcher(_pack).matches() == false) && (pattern.matcher(pack).matches() == false))
			continue;
		}
		final Boolean expl = Boolean.valueOf(tis.readBoolean());
		if (explicit == unrequired)
		    installmap.put(pack, expl);
		else
		    if (explicit)
		    {   if (expl == Boolean.TRUE)
			    installmap.put(pack, expl);
		    }
		    else
		    {   if (expl == Boolean.FALSE)
			    installmap.put(pack, expl);
		    }
	    }
	}
	catch (final FileNotFoundException ignore)
	{   //Ignore
	}
	catch (final Throwable err)
        {   System.err.println(err.toString());
	}
	
	final String[] inst = new String[installmap.size()];
	int ptr = 0;
	for (final String pack : installmap.keySet())
	    inst[ptr++] = pack;
	
	final Map<String, String> upgradable = getUpgradable(inst);
	final Set<String> required = getRequired();
	
	final ArrayList<String> packs = new ArrayList<String>();
	final String[] rc = new String[packs.size()];
	ptr = 0;
	for (final String pack : inst)
	    if ((unrequired == false) || installmap.get(pack) == Boolean.TRUE)
		if (upgrade == false)
		    rc[ptr++] = pack;
		else
		{
		    final String _pack = pack.substring(0, pack.lastIndexOf("="));
		    if (upgradable.containsKey(_pack))
			rc[ptr++] = pack;
		}
	    else
	    {
		final String _pack = pack.substring(0, pack.lastIndexOf("="));
		if ((required.contains(_pack) == false) || (upgrade == false) || upgradable.containsKey(_pack))
		    rc[ptr++] = pack;
	    }
	
	Arrays.sort(rc, 0, ptr);
	for (int i = 0; i < ptr; i++)
	    System.out.println(rc[i]);
    }
    
    
    /**
     * Gets a map of all upgradable packages, mapped to their replacer or never version
     * 
     * @param   installed  All installed software to check for upgradablility, version must be included
     * @return             Map of all upgradable packages, mapped to their replacer or never version
     */
    public static HashMap<String, String> getUpgradable(final String... installed)
    {
	final HashMap<String, String> rc = new HashMap<String, String>();
	try
	{
	    final String[] packs = (new File(PACKAGE_DIR)).list();
	    Arrays.sort(packs);
	    final HashMap<String, String> map = new HashMap<String, String>();
	    for (final String pack : packs)
	    {
		final String _pack = pack.substring(0, pack.lastIndexOf("="));
		map.put(_pack, pack);
	    }
	    for (final String pack : map.values())
	    {
		final String[] replaces = PackageInfo.fromFile(PACKAGE_DIR + pack + ".pkg.xz").replaces;
		for (final String replacee : replaces)
		    map.put(replacee, pack);
	    }
	    for (final String pack : installed)
		rc.put(pack, map.get(pack));
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
     */
    @requires({"java-runtime>=6", "java-environment>=7"})
    public static HashSet<String> getRequired()
    {
	final HashMap<String, String> installed = new HashMap<String, String>();
	final ArrayList<String> explicits = new ArrayList<String>();
	
	try (final TransferInputStream tis = new TransferInputStream(new FileInputStream(new File(PACKAGES_FILE))))
	{
	    for (;;)
	    {
		final String pack = tis.readObject(String.class);
		if (pack.isEmpty())
		    break;
		if (tis.readBoolean())
		    explicits.add(pack);
		final String _pack = pack.substring(0, pack.lastIndexOf("="));
		installed.put(_pack, pack);
	    }
	}
	catch (final FileNotFoundException ignore)
	{   //Ignore
	}
	catch (final Throwable err)
        {   System.err.println(err.toString());
	}
	
	final HashSet<String> provided = new HashSet<String>();
	final HashSet<String> rc = new HashSet<String>();
	final ArrayDeque<String> dependents = new ArrayDeque<String>();
	
	for (final String dependent : explicits)
	    dependents.add(dependent);
	
	while (dependents.isEmpty() == false)
	{
	    final String dependent = dependents.pollFirst();
	    if (provided.contains(dependent.substring(0, dependent.lastIndexOf("="))))
		continue;
	    rc.add(dependent);
	    
	    try
	    {
		final PackageInfo info = PackageInfo.fromFile(PACKAGE_DIR + dependent + ".pkg.xz");
		final String[] opts = info.optionalDependencies;
		final String[] deps = info.dependencies;
		
		provided.add(dependent.substring(0, dependent.lastIndexOf("=")));
		for (final String provides : info.provides)
		    provided.add(provides);
		
		for (final String[] _deps : new String[][] { deps, opts })
		    for (final String _dep : _deps)
		    {
			String dep = _dep.replace(">", "=").replace("<", "=").replace("==", "=");
			dep = dep.substring(0, dep.lastIndexOf("="));
			if (installed.containsKey(dep))
			    if (provided.contains(dep) == false)
				dependents.offerLast(installed.get(dep));
		    }
	    }
	    catch (final Throwable err)
	    {   System.err.println(err.toString());
	    }
	}
	return rc;
    }
    
}

