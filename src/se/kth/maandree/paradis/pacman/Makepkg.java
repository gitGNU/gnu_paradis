/**
 *  Paradis — Ever growing network for parallel and distributed computing.
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
import se.kth.maandree.paradis.local.Properties; //Explicit
import se.kth.maandree.paradis.net.UUID;
import se.kth.maandree.paradis.io.*;
import se.kth.maandree.paradis.*;

import java.util.*;
import java.io.*;


/**
 * Package builder
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Makepkg
{
    /**
     * Non-constructor
     */
    private Makepkg()
    {
        assert false : "You may not create instances of this class [Makepkg].";
    }
    
    
    
    /**
     * Invoke the package builder
     * 
     * @param  args  Arguments
     */
    public static void main(final String... args)
    {
	try
        {   if (args[0].equals("--init"))
	    {
		FileHandler.writeFile(args[1] + "/PKGBUILD", FileHandler.readInternalFileBytes("res/PKGBUILD.prototype"));
	    }
	    else if (args[0].equals("--edit"))
	    {
		String edit = Properties.getEditor();
		if (edit == null)
		    edit = "emacs";
		exec(edit + " '" + args[1].replace("\\'", "'\\''") + "/PKGBUILD'");
	    }
	    else if (args[0].equals("--make"))
	    {
	    }
	    else if (args[0].equals("--reverse"))
	    {
	}   }
	catch (final Throwable err)
	{   System.err.println(err.toString());
	}
	
	/*
	  optionalSystemDependencies = parseStrings()
	  optionalDependencies = parseStrings()
	  systemDependencies = parseStrings()
	  dependencies = parseStrings()
	  packageEpoch = parseInteger()
	  packageVersion = parseString()
	  packageRelease = parseInteger()
	  packageName = parseString()
	  packageDesc = parseString()
	  packageDescription = parseString()
	  provides = parseStrings()
	  replaces = parseStrings()
	  conflicts = parseStrings()
	  containsSource = parseBoolean()
	  containsBinary = parseBoolean()
	  licenses = parseStrings()
	  isFreeSoftware = parseBoolean()
	  url = parseString()
	  arch = parseStrings()
	  os = parseStrings()
	  groups = parseStrings()
	  files = parseStrings() autofill
	  backup = parseBooleans(files) autofill
	  checksums = parseStrings() autofill
	  category = parseString()
	  uuid = parseUUID() autofill
	 */
    }
    
    
    public static String parseString(final String value)
    {   return null;
    }
    
    public static String[] parseStrings(final String value)
    {   return null;
    }
    
    public static int parseInteger(final String value)
    {   return 0;
    }
    
    public static boolean parseBoolean(final String value)
    {   return false;
    }
    
    public static boolean[] parseBooleans(final String value)
    {   return null;
    }
    
    public static UUID parseUUID(final String value)
    {   return null;
    }
    
    
    /**
     * Invokes another program
     * 
     * @param  cmd  The command to run
     */
    @requires({"java-runtime>=7", "sh"})
    public static void exec(final String command)
    {
	try
	{   String[] cmds = null;
	    for (final String path : Properties.getBinaryPaths())
		if ((new File(path + "/sh")).exists())
		{   cmds = new String[] { path + "/sh", "-c", command };
		    break;
		}
	    if (cmds == null)
		cmds = parseStrings("(" + command + ")");
	    final ProcessBuilder procBuilder = new ProcessBuilder(cmds);
            procBuilder.inheritIO();
            final Process process = procBuilder.start();
            process.waitFor();
	}
	catch (final Throwable err)
	{   System.err.println(err.toString());
	}
    }
    
}

