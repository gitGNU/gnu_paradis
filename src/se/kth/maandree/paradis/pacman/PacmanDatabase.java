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
import se.kth.maandree.paradis.local.Properties;
import se.kth.maandree.paradis.io.*;
import se.kth.maandree.paradis.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.nio.file.*;


/**
 * -D invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PacmanDatabase implements Blackboard.BlackboardObserver
{
    /**
     * The directory where the packages are located
     */
    private static final String PACKAGE_DIR = Pacman.PACKAGE_DIR;
    
    /**
     * The file where the data are saved
     */
    static final String PACKAGES_FILE = Pacman.PACKAGES_FILE;
    
    
    /** Add, remove or list packages
     */ private static final String DATABASE = Pacman.DATABASE;
    
    /** Add package
     */ private static final String DATABASE_ADD = Pacman.DATABASE_ADD;
    
    /** List only packages installed as dependencies
     */ private static final String DATABASE_DEPS = Pacman.DATABASE_DEPS;
    
    /** List only packages installed explicitly
     */ private static final String DATABASE_EXPLICIT = Pacman.DATABASE_EXPLICIT;
    
    /** Lists files install by the package
     */ private static final String DATABASE_FILES = Pacman.DATABASE_FILES;
    
    /** Only return installed pacakges
     */ private static final String DATABASE_INSTALLED = Pacman.DATABASE_INSTALLED;
    
    /** Do not return installed pacakges
     */ private static final String DATABASE_NONINSTALLED = Pacman.DATABASE_NONINSTALLED;
    
    /** Remove package, use twice to remove even if installed
     */ private static final String DATABASE_REMOVE = Pacman.DATABASE_REMOVE;
    
    /** Use regular expressions
     */ private static final String DATABASE_SEARCH = Pacman.DATABASE_SEARCH;
    
    
    
    //Has default constructor
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    @requires("java-runtime>=7")
    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
    {
	if ((message instanceof Pacman.PacmanInvoke == false) || (((Pacman.PacmanInvoke)message).masteropt.equals(DATABASE) == false))
	    return;
	final HashSet<String> options = ((Pacman.PacmanInvoke)message).options;
	final ArrayList<String> packages = ((Pacman.PacmanInvoke)message).packages;
	final HashSet<String> packageSet = new HashSet<String>();
	for (final String pack : packages)
	    packageSet.add(pack);
	
	final String fs = Properties.getFileSeparator();
	if (options.contains(DATABASE_ADD))
	    for (final String pack : packages)
		try
		{   final String base = PACKAGE_DIR + pack.substring(pack.lastIndexOf(fs) + 1);
		    Files.move((new File(pack + ".tar.xz")).toPath(), (new File(base + ".tar.xz")).toPath(), StandardCopyOption.REPLACE_EXISTING);
		    Files.move((new File(pack + ".pkg.xz")).toPath(), (new File(base + ".pkg.xz")).toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		catch (final Throwable err)
		{   System.err.println(err.toString());
		}
	else if (options.contains(DATABASE_REMOVE))
	    for (final String pack : packages)
		try
		{   final String base = PACKAGE_DIR + pack.substring(pack.lastIndexOf(fs) + 1);
		    Files.delete((new File(base + ".tar.xz")).toPath());
		    Files.delete((new File(base + ".pkg.xz")).toPath());
		}
		catch (final Throwable err)
		{   System.err.println(err.toString());
		}
	else
	{
	    final String[] packs = (new File(PACKAGE_DIR)).list(new FilenameFilter()
		    {
			/** Regex pattern
			 */ private final Pattern pattern;
			
			/** List dependency installations
			 */ private final boolean deps = options.contains(DATABASE_DEPS);
			
			/** List explicit installations
			 */ private final boolean explicit = options.contains(DATABASE_EXPLICIT);
			
			/** List installed
			 */ private final boolean installed = options.contains(DATABASE_INSTALLED) | this.deps | this.explicit;
			
			/** List non-installed
			 */ private final boolean noninstalled = options.contains(DATABASE_NONINSTALLED);
			
			/** Map with installed packages, mapping to {@link Boolean#TRUE} for explicitly installed software
			 */ private final HashMap<String, Boolean> installmap = new HashMap<String, Boolean>();
			
			
			
			/**
			 * Initialiser
			 */
			{
			    if (options.contains(DATABASE_SEARCH))
				this.pattern = null;
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
				this.pattern = Pattern.compile(buf.toString());
			    }
			    
			    if (this.installed ^ this.noninstalled)
				try (final TransferInputStream tis = new TransferInputStream(new FileInputStream(new File(PACKAGES_FILE))))
				{
				    for (;;)
				    {
					final String pack = tis.readObject(String.class);
					if (pack.isEmpty())
					    break;
					final Boolean expl = Boolean.valueOf(tis.readBoolean());
					this.installmap.put(pack, expl);
				    }
				}
				catch (final FileNotFoundException ignore)
				{   //Ignore
				}
				catch (final Throwable err)
				{   System.err.println(err.toString());
				}
			}
			
			
			
			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean accept(final File dir, final String name)
			{
			    if ((name.endsWith(".tar.xz") == false) || (name.contains("=") == false) || name.endsWith("=.tar.xz"))
				return false;
			    
			    final String _name = name.substring(0, name.length() - ".tar.xz".length());
			    
			    if (packages.isEmpty() == false)
				if (this.pattern == null)
				{
				    if (packageSet.contains(_name) == false)
					return false;
				}
				else
				    if (this.pattern.matcher(_name).matches() == false)
					return false;
			    
			    if (this.installed ^ this.noninstalled)
			    {
				final Boolean inst = this.installmap.get(_name);
				if (inst == null)
				    return this.noninstalled;
				return (this.deps == this.explicit) || ((inst == Boolean.TRUE) == this.explicit);
			    }
			    return true;
			}
		    });
	    
	    final boolean files = options.contains(DATABASE_FILES);
	    Arrays.sort(packs);
	    for (final String pkg : packs)
	    {
		final String pack = pkg.substring(0, pkg.length() - ".tar.xz".length());
		if ((new File(PACKAGE_DIR + pack + ".pkg.xz")).exists() == false)
		    continue;
		
		System.out.println(pack);
		if (files)
		    try
		    {   final PackageInfo info = PackageInfo.fromFile(PACKAGE_DIR + pack + ".pkg.xz");
			Arrays.sort(info.files);
			for (final String file : info.files)
			    System.out.println("\t" + file);
		    }
		    catch (final Throwable err)
		    {   System.err.println(err.toString());
			continue;
		    }
	    }
	}
    }
    
}

