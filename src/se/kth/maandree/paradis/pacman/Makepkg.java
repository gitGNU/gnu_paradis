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
	    {   FileHandler.writeFile(args[1] + "/PKGBUILD", FileHandler.readInternalFileBytes("res/PKGBUILD.prototype"));
	    }
	    else if (args[0].equals("--edit"))
	    {   String edit = Properties.getEditor();
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
    
    
    /**
     * Convert a coded {@link String} to a plain {@link String}
     * 
     * @param   value  {@link String} to parse
     * @return         Parsed value
     */
    public static String parseString(final String value)
    {
	if (value.startsWith("<<"))
        {   String rc = value.substring(value.indexOf("\n") + 1);
	    return rc.substring(0, rc.lastIndexOf("\n"));
	}
	final String[] words = parseStrings("(" + value + ")");
	final StringBuilder rc = new StringBuilder();
	for (int i = 0, n = words.length; i < n; i++)
	{   if (i > 0)
		rc.append(" ");
	    rc.append(words[i]);
	}
	return rc.toString();
    }
    
    
    /**
     * Convert a {@link String} to {@code String[]}
     * 
     * @param   value  {@link String} to convert
     * @return         Parsed value
     */
    public static String[] parseStrings(final String value)
    {
	final String val = value.substring(1, value.length() - 1) + " ";
	final ArrayList<String> rc = new ArrayList<String>();
	int ptr = 0, n;
	final char[] buf = new char[n = val.length()];
	
	boolean dq = false;
	boolean sq = false;
	for (int i = 0; i < n;)
	{
	    char c = val.charAt(i++);
	    if ((c == ' ') && !dq && !sq)
	    {   if (ptr == 0)
		    continue;
		rc.add(new String(buf, 0, ptr));
		ptr = 0;
	    }
	    else if (sq)
		if (c == '\'')
		    sq = false;
		else
		    buf[ptr++] = c;
	    else
	    {
		if (c == '\"')
		{   dq = !dq;
		    continue;
		}
		if ((c == '\'') && !dq)
	        {   sq = true;
		    continue;
		}
		if (c == '\\')
		    c = val.charAt(i++);
		buf[ptr++] = c;
	    }
	}
	
	final String[] _rc = new String[rc.size()];
	rc.toArray(_rc);
	return _rc;
    }
    
    
    /**
     * Convert a {@link String} to {@code int}
     * 
     * @param   value  {@link String} to convert
     * @return         Parsed value
     */
    public static int parseInteger(final String value)
    {   int rc = 0;
	for (int i = 0, n = value.length(); i < n; i++)
	    rc = (rc * 10) - (value.charAt(i) & 15);
	return -rc;
    }
    
    
    /**
     * Convert a {@link String} to {@code boolean}
     * 
     * @param   value  {@link String} to convert
     * @return         Parsed value
     */
    public static boolean parseBoolean(final String value)
    {   return value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("y") || value.equals("1");
    }
    
    
    /**
     * Convert a {@link String} to {@code boolean[]}
     * 
     * @param   value  {@link String} to convert
     * @return         Parsed value
     */
    public static boolean[] parseBooleans(final String value)
    {
	if ((value.startsWith("(") && value.endsWith(")")) == false)
	    return null;
	String tmp = value.substring(1, value.length() - 2);
	while (tmp.contains("  "))  tmp = tmp.replace("  ", " ");
	if (tmp.startsWith(" "))    tmp = tmp.substring(1);
	if (tmp.endsWith(" "))      tmp = tmp.substring(0, tmp.length() - 1);
	final String[] vals = tmp.split(" ");
	final boolean[] rc = new boolean[vals.length];
	for (int i = 0, n = vals.length; i < n; i++)
	{
	    final boolean yes = value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("y") || value.equals("1");
	    final boolean no  = value.equalsIgnoreCase("no")  || value.equalsIgnoreCase("n") || value.equals("0");
	    if (yes | no)
		rc[i] = yes;
	    else
		return null;
	}
	return rc;
    }
    
    
    /**
     * Convert a {@link String} to {@link UUID}
     * 
     * @param   value  {@link String} to convert
     * @return         Parsed value
     */
    public static UUID parseUUID(final String value)
    {
        final long[] hi_lo = new long[2];
	for (int j = 0, e = 0; j < 2; j++)
	    for (int i = 0; i < 16; i++)
	    {
		int d = value.charAt((i | (j << 4)) + e);
		if (d == '-')
		{   e++;
		    i--;
		    continue;
		}
		hi_lo[j] = (hi_lo[j] << 4) | ((d & 15) + ((d >> 6) * 10));
	    }
	
	return new UUID(hi_lo[0], hi_lo[1]);
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

