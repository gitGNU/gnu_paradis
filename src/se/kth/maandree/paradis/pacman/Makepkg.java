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
	final String fs = Properties.getFileSeparator();
	try
        {   if (args[0].equals("--init"))
	    {   FileHandler.writeFile(args[1] + fs + "PKGBUILD", FileHandler.readInternalFileBytes("res" + fs + "PKGBUILD.prototype"));
	    }
	    else if (args[0].equals("--edit"))
	    {   String edit = Properties.getEditor();
		if (edit == null)
		    edit = "emacs";
		exec(edit + " '" + args[1].replace("\\'", "'\\''") + fs + "PKGBUILD'");
	    }
	    else if (args[0].equals("--make"))
	    {   make(args[1]);
	    }
	    else if (args[0].equals("--reverse"))
	    {
		final PackageInfo info = PackageInfo.fromFile(args[1]);
		final StringBuilder buf = new StringBuilder();
		buf.append("optionalSystemDependencies=" + encodeArray(info.optionalSystemDependencies));
		buf.append("optionalDependencies=" + encodeArray(info.optionalDependencies));
		buf.append("systemDependencies=" + encodeArray(info.systemDependencies));
		buf.append("dependencies=" + encodeArray(info.dependencies));
		buf.append("packageEpoch=" + Integer.toString(info.packageEpoch));
		buf.append("packageVersion=" + info.packageVersion);
		buf.append("packageRelease=" + Integer.toString(info.packageRelease));
		buf.append("packageName=" + info.packageName);
		buf.append("packageDesc='" + info.packageDesc.replace("'", "'\\''") + "'");
		buf.append("packageDescription='" + info.packageDescription.replace("'", "'\\''") + "'");
		buf.append("provides=" + encodeArray(info.provides));
		buf.append("replaces=" + encodeArray(info.replaces));
		buf.append("conflicts=" + encodeArray(info.conflicts));
		buf.append("containsSource=" + (info.containsSource ? "yes" : "no"));
		buf.append("containsBinary=" + (info.containsBinary ? "yes" : "no"));
		buf.append("licenses=" + encodeArray(info.licenses));
		buf.append("isFreeSoftware=" + (info.isFreeSoftware ? "yes" : "no"));
		buf.append("url='" + info.url.replace("'", "'\\''") + "'");
		buf.append("arch=" + encodeArray(info.arch));
		buf.append("os=" + encodeArray(info.os));
		buf.append("groups=" + encodeArray(info.groups));
		buf.append("files=" + encodeArray(info.files));
		buf.append("backup=" + encodeArray(info.backup));
		buf.append("`checksums=" + encodeArray(info.checksums) + "`");
		buf.append("category='" + info.category.replace("'", "'\\''") + "'");
		buf.append("uuid=" + info.uuid.toString());
		FileHandler.writeFile(args[2] + fs + "PKGBUILD", buf.toString());
	}   }
	catch (final Throwable err)
	{   System.err.println(err.toString());
	}
    }
    
    
    /**
     * Build a package
     * 
     * @param  directory  The package directory
     */
    @requires("java-runtime>=6")
    public static void make(final String directory)
    {
	final String fs = Properties.getFileSeparator();
	final String pkgfile = directory + fs + "PKGBUILD";
	final HashMap<String, String> map = new HashMap<String, String>();
	
	// TODO populate map
	
	String[] files = parseStrings(map.get("files"));
	if (files == null)
	{
	    final ArrayList<String> list = new ArrayList<String>();
	    final ArrayDeque<String> stack = new ArrayDeque<String>();
	    stack.offerLast(directory);
	    for (String item; (item = stack.pollLast()) != null;)
	    {
		final File file = new File(item);
		if (file.isDirectory())
		{
		    final String[] subs = file.list();
		    Arrays.sort(subs);
		    for (int i = subs.length - 1; i >= 0; i--)
			stack.offerLast(item + fs + subs[i]);
		}
		else
		    if (item.equals(pkgfile) == false)
			list.add(item.substring(directory.length()).replace(fs, "/"));
	    }
	    files = new String[list.size()];
	    list.toArray(files);
	}
	
	boolean[] backup = parseBooleans(map.get("backup"));
	if (backup == null)
	{
	    final HashSet<String> fileSet = new HashSet<String>();
	    for (final String file : parseStrings(map.get("backup")))
		fileSet.add(file);
	    backup = new boolean[files.length];
	    for (int i = 0, n = files.length; i < n; i++)
		backup[i] = fileSet.contains(files[i]);
	}
	
	String checksums = map.get("checksums");
	final PackageInfo info = new PackageInfo(parseStrings(map.get("optionalSystemDependencies")),
						 parseStrings(map.get("optionalDependencies")),
						 parseStrings(map.get("systemDependencies")),
						 parseStrings(map.get("dependencies")),
						 parseInteger(map.get("packageEpoch")),
						 parseString(map.get("packageVersion")),
						 parseInteger(map.get("packageRelease")),
						 parseString(map.get("packageName")),
						 parseString(map.get("packageDesc")),
						 parseString(map.get("packageDescription")),
						 parseStrings(map.get("provides")),
						 parseStrings(map.get("replaces")),
						 parseStrings(map.get("conflicts")),
						 parseBoolean(map.get("containsSource")),
						 parseBoolean(map.get("containsBinary")),
						 parseStrings(map.get("licenses")),
						 parseBoolean(map.get("isFreeSoftware")),
						 parseString(map.get("url")),
						 parseStrings(map.get("arch")),
						 parseStrings(map.get("os")),
						 parseStrings(map.get("groups")),
						 files,
						 backup,
						 checksums != null ? parseStrings(checksums) : checksums(files),
						 parseString(map.get("category")),
						 map.containsKey("uuid") ? parseUUID(map.get("uuid")) : new UUID()
						 );
	
	// TODO create .pkg.xz and .tar.xz and print uuid install info
    }
    
    
    /**
     * Gets the checksum for files
     * 
     * @param   files  The files
     * @return         The files' checksums
     */
    public static String[] checksums(final String[] files)
    {
	return new String[0]; //TODO calculate checksums
    }
    
    
    /**
     * Converts a {@code boolean[]} to an encoded {@link String}
     * 
     * @param   array  Array to encode
     * @return         Encoded {@link String}
     */
    public static String encodeArray(final boolean[] array)
    {
	final StringBuilder rc = new StringBuilder();
	for (int i = 0, n = array.length; i < n; i++)
	    rc.append((i == 0 ? "(" : " ") + (array[i] ? "yes" : "no"));
	rc.append(")");
	return rc.toString();
    }
    
    
    /**
     * Converts a {@code String[]} to an encoded {@link String}
     * 
     * @param   array  Array to encode
     * @return         Encoded {@link String}
     */
    public static String encodeArray(final String[] array)
    {
	final StringBuilder rc = new StringBuilder();
	for (int i = 0, n = array.length; i < n; i++)
	    rc.append((i == 0 ? "('" : "' '") + array[i].replace("'", "'\\''"));
	rc.append("')");
	return rc.toString();
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

