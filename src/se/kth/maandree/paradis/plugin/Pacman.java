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
import se.kth.maandree.paradis.local.Properties; //Explicit
import se.kth.maandree.paradis.*;

import java.util.*;


/**
 * Package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Pacman
{
    /**
     * The directory where the packages are located
     */
    public static final String PACKAGE_DIR = "~/.paradis/packages/".replace("/", Properties.getFileSeparator()).replace("~", Properties.getHome());
    
    /**
     * The file where the data are saved
     */
    public static final String PACKAGES_FILE = "~/.paradis/packages.data".replace("/", Properties.getFileSeparator()).replace("~", Properties.getHome());
    
    
    
    /** Add, remove or list packages
     */ public static final String DATABASE = "--database"; // -D
    
    /** Add package
     */ public static final String DATABASE_ADD = "--add"; // -a
    
    /** List only packages installed as dependencies
     */ public static final String DATABASE_DEPS = "--deps"; // -d
    
    /** List only packages installed explicitly
     */ public static final String DATABASE_EXPLICIT = "--explicit"; // -e
    
    /** Lists files install by the package
     */ public static final String DATABASE_FILES = "--files"; // -f
    
    /** Only return installed pacakges
     */ public static final String DATABASE_INSTALLED = "--installed"; // -i
    
    /** Do not return installed pacakges
     */ public static final String DATABASE_NONINSTALLED = "--noninstalled"; // -n
    
    /** Remove package, use twice to remove even if installed
     */ public static final String DATABASE_REMOVE = "--remove"; // -r
    
    /** Use regular expressions
     */ public static final String DATABASE_SEARCH = "--search"; // -s
    
    
    /** Synchronise (install or upgrade) packages
     */ public static final String SYNC = "--sync"; // -S
    
    /** Ignore dependencies
     */ public static final String SYNC_NODEPS = "--nodeps"; // -d
    
    /** Mark dependencies as explicitly installed
     */ public static final String SYNC_ASEXPLICIT = "--asexplicit"; // -e
    
    /** Override existing files not installed by the package itself
     */ public static final String SYNC_FORCE = "--force"; // -f
    
    /** Ignore the package after this option
     */ public static final String SYNC_IGNORE = "--ignore";
    
    /** Mark installed packages as installed as a dependency
     */ public static final String SYNC_ASDEPS = "--asdeps"; // -i
    
    /** Ignore up to date packages
     */ public static final String SYNC_NEEDED = "--needed"; // -n
    
    /** Do not install the files
     */ public static final String SYNC_DBONLY = "--dbonly"; // -o
    
    /** Recursively reinstall all dependencies
     */ public static final String SYNC_RECURSIVE = "--recursive"; // -r
    
    /** Use regular expressions
     */ public static final String SYNC_SEARCH = "--search"; // -s
    
    /** Include all installed non-up to date packages
     */ public static final String SYNC_UPGRADE = "--upgrade"; // -u
    
    
    /** Remove-and-install process
     */ public static final String UPGRADE = "--upgrade"; // -U
    
    /** Ignore dependencies
     */ public static final String UPGRADE_NODEPS = "--nodeps"; // -d
    
    /** Mark dependencies as explicitly installed
     */ public static final String UPGRADE_ASEXPLICIT = "--asexplicit"; // -e
    
    /** Override existing files not installed by the package itself
     */ public static final String UPGRADE_FORCE = "--force"; // -f
    
    /** Ignore the package after this option
     */ public static final String UPGRADE_IGNORE = "--ignore";
    
    /** Mark installed packages as installed as a dependency
     */ public static final String UPGRADE_ASDEPS = "--asdeps"; // -i
    
    /** Ignore up to date packages
     */ public static final String UPGRADE_NEEDED = "--needed"; // -n
    
    /** Do not install the files
     */ public static final String UPGRADE_DBONLY = "--dbonly"; // -o
    
    /** Recursively reinstall all dependencies
     */ public static final String UPGRADE_RECURSIVE = "--recursive"; // -r
    
    /** Use regular expressions
     */ public static final String UPGRADE_SEARCH = "--search"; // -s
    
    /** Include all installed non-up to date packages
     */ public static final String UPGRADE_UPGRADE = "--upgrade"; // -u
    
    
    /** Uninstall packages
     */ public static final String REMOVE = "--remove"; // -R
    
    /** Uninstall packages dependent on the packages
     */ public static final String REMOVE_CASCADE = "--cascade"; // -c
    
    /** Do not uninstall dependencies
     */ public static final String REMOVE_NODEPS = "--nodeps"; // -d
    
    /** Do not uninstall the files, only mark as uninstalled
     */ public static final String REMOVE_DBONLY = "--dbonly"; // -o
    
    /** Remove dependencies
     */ public static final String REMOVE_RECURSIVE = "--recursive"; // -r
    
    /** Use regular expressions
     */ public static final String REMOVE_SEARCH = "--search"; // -s
    
    /** Remove packages required by any installed package
     */ public static final String REMOVE_UNREQUIRED = "--unrequired"; // -t
    
    /** Do not remove packages needed by any other package
     */ public static final String REMOVE_UNNEEDED = "--unneeded"; // -u
    
    
    /** Return missing dependencies and conflicts
     */ public static final String DEPTEST = "--deptest"; // -T
    
    
    /** Search for installed packages
     */ public static final String QUERY = "--query"; // -Q
    
    /** Only return explicitly installed packages
     */ public static final String QUERY_EXPLICIT = "--explicit"; // -e
    
    /** Use regular expressions
     */ public static final String QUERY_SEARCH = "--search"; // -s
    
    /** Only return unrequired packages
     */ public static final String QUERY_UNREQUIRED = "--unrequired"; // -t
    
    /** Do not return up to date packages
     */ public static final String QUERY_UPGRADE = "--upgrade"; // -u
    
    
    
    /**
     * Non-constructor
     */
    private Pacman()
    {
        assert false : "You may not create instances of this class [Pacman].";
    }
    
    
    
    /**
     * Short master options to long master options
     */
    public static final HashMap<String, String> master = new HashMap<String, String>();
    
    /**
     * Maps (long options as keys) from short options to long options
     */
    public static final HashMap<String, HashMap<String, String>> shortoptses = new HashMap<String, HashMap<String, String>>();
    
    
    
    /**
     * Type initialiser
     */
    static
    {
	final HashMap<String, String> D = new HashMap<String, String>();  shortoptses.put(DATABASE, D);
	final HashMap<String, String> S = new HashMap<String, String>();  shortoptses.put(SYNC, S);
	final HashMap<String, String> U = new HashMap<String, String>();  shortoptses.put(UPGRADE, U);
	final HashMap<String, String> R = new HashMap<String, String>();  shortoptses.put(REMOVE, R);
	final HashMap<String, String> Q = new HashMap<String, String>();  shortoptses.put(QUERY, Q);
	final HashMap<String, String> T = new HashMap<String, String>();  shortoptses.put(DEPTEST, T);
	
	master.put("D", DATABASE);          master.put("S", SYNC);        master.put("U", UPGRADE);        master.put("R", REMOVE);       master.put("Q", QUERY);        master.put("T", DEPTEST);
	D.put("a", DATABASE_ADD);           S.put("d", SYNC_NODEPS);      U.put("d", UPGRADE_NODEPS);      R.put("c", REMOVE_CASCADE);    Q.put("e", QUERY_EXPLICIT);
	D.put("d", DATABASE_DEPS);          S.put("e", SYNC_ASEXPLICIT);  U.put("e", UPGRADE_ASEXPLICIT);  R.put("d", REMOVE_NODEPS);     Q.put("s", QUERY_SEARCH);
	D.put("e", DATABASE_EXPLICIT);      S.put("f", SYNC_FORCE);       U.put("f", UPGRADE_FORCE);       R.put("o", REMOVE_DBONLY);     Q.put("t", QUERY_UNREQUIRED);
	D.put("f", DATABASE_FILES);         S.put("i", SYNC_ASDEPS);      U.put("i", UPGRADE_ASDEPS);      R.put("r", REMOVE_RECURSIVE);  Q.put("u", QUERY_UPGRADE);
	D.put("i", DATABASE_INSTALLED);     S.put("n", SYNC_NEEDED);      U.put("n", UPGRADE_NEEDED);      R.put("s", REMOVE_SEARCH);
	D.put("n", DATABASE_NONINSTALLED);  S.put("o", SYNC_DBONLY);      U.put("o", UPGRADE_DBONLY);      R.put("t", REMOVE_UNREQUIRED);
	D.put("r", DATABASE_REMOVE);        S.put("r", SYNC_RECURSIVE);   U.put("r", UPGRADE_RECURSIVE);   R.put("u", REMOVE_UNNEEDED);
	D.put("s", DATABASE_SEARCH);        S.put("s", SYNC_SEARCH);      U.put("s", UPGRADE_SEARCH);
	                                    S.put("u", SYNC_UPGRADE);     U.put("u", UPGRADE_UPGRADE);
	
	Blackboard.getInstance("pacman").registerObserver(new PacmanDatabase());
	Blackboard.getInstance("pacman").registerObserver(new PacmanSync());
	Blackboard.getInstance("pacman").registerObserver(new PacmanUpgrade());
	Blackboard.getInstance("pacman").registerObserver(new PacmanRemove());
	Blackboard.getInstance("pacman").registerObserver(new PacmanQuery());
	Blackboard.getInstance("pacman").registerObserver(new PacmanDeptest());
    }
    
    
    
    /**
     * Package manager invocation message
     */
    public static class PacmanInvoke implements Blackboard.BlackboardMessage
    {
	/**
	 * Constructor
	 * 
	 * @param  masteropt  The first long option
	 * @param  options    Invocation options
	 * @param  ignores    Packages to ignore
	 * @param  packages   Targeted packages
	 */
	public PacmanInvoke(final String masteropt, final HashSet<String> options, final HashSet<String> ignores, final ArrayList<String> packages)
	{
	    this.masteropt = masteropt;
	    this.options = options;
	    this.ignores = ignores;
	    this.packages = packages;
	}
	
	
	
	/**
	 * The first long option (excluding --ignore)
	 */
	public final String masteropt;
	
	/**
	 * Invocation (long) options
	 */
	public final HashSet<String> options;
	
	/**
	 * Packages to ignore
	 */
	public final HashSet<String> ignores;
	
	/**
	 * Targeted packages
	 */
	public final ArrayList<String> packages;
    }
    
    
    
    /**
     * Invoke the package manager
     * 
     * @param  args  Arguments
     */
    public static void main(final String... args)
    {
	String masteropt = null;
	final HashSet<String> options = new HashSet<String>();
	final HashSet<String> ignores = new HashSet<String>();
	final ArrayList<String> packages = new ArrayList<String>();
	
	HashMap<String, String> shortopts = master;
	boolean lastIsIgnore = false;
	for (final String arg : args)
	    if (lastIsIgnore)
	    {   lastIsIgnore = false;
		ignores.add(arg);
	    }
	    else if (arg.equals("--ignore"))
	    {   lastIsIgnore = true;
		ignores.add(arg);
	    }
	    else if (arg.startsWith("--"))
		if (masteropt == null)
		    shortopts = shortoptses.get(masteropt = arg);
		else
		    if (options.contains(arg))
			options.add(arg + arg);
		    else
			options.add(arg);
	    else if (arg.startsWith("-") == false)
		packages.add(arg);
	    else
		for (int i = 1, n = arg.length(); i < n; i++)
		{   final String larg = shortopts.get(arg.substring(i, 1));
		    if (masteropt == null)
			shortopts = shortoptses.get(masteropt = larg);
		    else
			if (options.contains(arg))
			    options.add(arg + arg);
			else
			    options.add(larg);
		}
	
	Blackboard.getInstance("pacman").broadcastMessage(new PacmanInvoke(masteropt, options, ignores, packages));
    }
    
    
    
    /**
     * Expands an array of arrays and elemetns
     * 
     * @param   items  An array of arrays and elements
     * @return         An array of elements
     */
    public static String[] expand(final Object... items)
    {
	final ArrayList<String> list = new ArrayList<String>();
	for (final Object item : items)
	    if (item instanceof String[])
		for (final String elem : (String[])item)
		    list.add(elem);
	    else
		list.add((String)item);
	final String[] rc = new String[list.size()];
	list.toArray(rc);
	return rc;
    }
    
}

