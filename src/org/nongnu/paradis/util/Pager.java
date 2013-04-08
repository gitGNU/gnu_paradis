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
package org.nongnu.paradis.util;
import org.nongnu.paradis.io.*;
import org.nongnu.paradis.*;

import java.io.*;
import java.util.*;


/**
 * Simple text pager for terminals
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
public class Pager
{
    /**
     * Non-constructor
     */
    private Pager()
    {
        assert false : "You may not create instances of this class [Pager].";
    }
    
    
    
    /**
     * Starts a text pager
     * 
     * @param  pager  The users pager of choice
     * @param  title  The title of the document
     * @param  file   The file to page
     */
    public static void pageFile(final String pager, final String title, final String file)
    {
        if (FileHandler.fileExists(file) == false)
        {
            final String errText = "The file " + file + " is missing.\nIts title is: " + title + "\n\n";
            page(pager, "--:: FILE MISSING ::--", errText);
            return;
        }
        
        final String text;
        
        InputStream is = null;
        try
        {
            is = FileHandler.externalFileExists(file) ? FileHandler.readExternalFileStream(file) : FileHandler.readInternalFileStream(file);
            
            final Vector<byte[]> bufs = new Vector<byte[]>();
            int size = 0;
            
            for (int av; (av = is.available()) > 0;)
            {
                byte[] buf = new byte[av];
                av = is.read(buf, 0, av);
                if (av < buf.length)
                {
                    final byte[] nbuf = new byte[av];
                    System.arraycopy(buf, 0, nbuf, 0, av);
                    buf = nbuf;
                }
                size += av;
                bufs.add(buf);
            }
            
            final byte[] full = new byte[size];
            int ptr = 0;
            for (final byte[] buf : bufs)
            {
                System.arraycopy(buf, 0, full, ptr, buf.length);
                ptr += buf.length;
            }
            
            text = new String(full, "UTF-8");
        }
        catch (final Throwable err)
        {
            final String errText = "The file " + file + " could not be read.\nIts title is: " + title + "\n" + err.toString() + "\n\n";
            page(pager, "--:: FILE READ ERROR ::--", errText);
            return;
        }
        finally
        {   if (is != null)
                try
                {   is.close();
                }
                catch (final Throwable ignore)
                {   //Ignore
        }       }
        
        page(pager, title, text);
    }
    
    
    /**
     * Starts a text pager
     * 
     * @param  pager  The users pager of choice
     * @param  title  The title of the document
     * @param  text   The text to page
     */
    @requires({"coreutils:stty"})
    public static void page(final String pager, final String title, final String text)
    {
	int esc = 0;
	char[] buf = new byte[256];
	boolean bold = false;
	boolean colour = false;
	String ansi = null;
	final Vector<String> lines = new Vector<String>();
	for (int i = 0, n = text.length(); i <= n; i++)
	{   final char c = i == n ? '\n' : text.charAt(i);
	    if (ptr == buf.length)
		System.arraycopy(buf, 0, buf = new char[ptr << 1], 0, ptr);
	    if (esc == 1)
		esc = c == '[' ? 2 : 0;
	    else if (esc >= 2)
	    {
		String bf = "";
		boolean m = false;
		for (; i <= n; i++)
		{   c = text.charAt(i);
		    if ((c != ';') && (('0' > c) || (c > '9')))
		    {   m = c == 'm';
			break;
		    }
		    if ((esc == 3) || (c != '0'))
			bf += c;
		    if (c == ';')
			esc == 2;
		    else if ((esc == 2) && (c != '0'))
			esc == 3;
		}
		bf = "";
		if (m)
		{   final String[] segs = bf.split(';');
		    for (final String seg : segs)
			if (seg.equals(""))
			{   bf = bf + ';' + seg;
			    bold = colour = false;
			}
			else if (seg.equals("1"))
			{   bf = bf + ';' + seg;
			    bold = true;
			}
			else if (seg.length() >= 2)
			    if (seg.equals("21"))
			    {   bf = bf + ';' + seg;
				bold = false;
			    }
			    else if (seg.startswith("3") || seg.startswith("4"))
			    {   bf = bf + ';' + seg;
				colour = true;
			    }
		    if (bf.length() > 0)
		    {   bf = "\033[" + bf.substring(1) + 'm';
			ansi = (colour || bold) ? bf : null;
			while (ptr + bf.length() == buf.length)
			    System.arraycopy(buf, 0, buf = new char[buf.length << 1], 0, ptr);
			for (int j = 0, jn = bf.length(); j < jn; j++)
			    buf[ptr++] = bf.charAt(j);
		    }
	    }   }
	    else if ((c == '\f') || (c == '\n'))
	    {   if (colour == null)
		    lines.append(new String(buf, ptr));
		else
		    lines.append(new String(buf, ptr) + "\033[00m");
		if (c == '\f')
		{   lines.append("");
		    lines.append("");
		}
		ptr = 0;
		if (colour != null)
		    for (int j = 0, jn = colour.length(); j < jn; j++)
			buf[ptr++] = colour.charAt(j);
	    }
	    else if (c == '\033')
		esc = 1;
	    else
		buf[ptr++] = c;
	}
	
	if ((pager == null) && (pager.isEmpty() == false))
	{   final StringBuilder sb = new StringBuilder();
	    for (final String line : lines)
	    {   sb.append(line);
		sb.append('\n');
	    }
	    page(pager, sb.toString());
	    return;
	}
        
	String savedTTY = null;
	try
	{   final String[] rows_cols = exec("stty", "size").replace("\n", "").split(" ");
	    savedTTY = exec("stty", "--save").replace("\n", "");
	    System.out.print("\033[?1049h\033[?25l");
	    System.out.flush();
	    exec("stty", "-icanon", "-echo");
	    
	    final int rows = Integer.parseInt(rows_cols[0]);
	    final int cols = Integer.parseInt(rows_cols[1]);
	    
	    for (int d; (d = System.in.read()) != -1;)
	    {
		if ((d == 'h') || (d == 'H'))
		    ;//h --> help
		else if ((d == 'l') || (d == 'L') || (d == 'L' - '@'))
		    ;//l --> reload terminal size and reprint
		else if ((d == 'q') || (d == 'Q') || (d == '\n'))
		    ;//q --> quit
		else if (d == ' ')
		    ;//space --> paragraph down
		//arrows --> navigate  \e[A=↑ \e[B=↓ \e[C=→ \e[D=←
		//home(\eOH \e[2~)/end(\eOF \e[3~)/pgup(\e[5~)/pgdn(\e[6~) --> navigate
		//esc esc esc --> quit
		
		
		//show title
		//show content
		//show status bar
		//  h for help
		//  q for quit
		//  All/Top/Bot/??%
		//  first line - last line
	    }
	}
	catch (final Exception err)
	{   System.out.println("\n\n\n\n\n\n\n\n\n\n");
	    System.out.println("\n\n\n\n\n\n\n\n\n\n");
	    for (final String line : lines)
		System.out.println(line);
	}
	finally
	{   if (savedTTY != null)
	    {
		System.out.print("\033[?25h\033[?1049l");
		System.out.flush();
		exec("stty", savedTTY);
	}   }
    }
    
    
    /**
     * Prints lines to the terminal using a pager
     *
     * @param  pager  The pager to use
     * @param  text   The text to print
     */
    @requires({"sh", "java-runtime>=7"})
    private static void page(final String pager, final String text)
    {
        try
        {   String cmd = "echo '" + text.replace("'", "'\\''") + "' | " + pager;
            cmd += " > " + (new File("/dev/stdout")).getCanonicalPath();
            
            final ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", cmd);
            builder.inheritIO();
            final Process process = builder.start();
            
            process.waitFor();
            if (process.exitValue() != 0)
                throw new Exception();
        }
        catch (final Throwable err)
        {   System.out.println("Unable to page using '" + pager + "'");
        }
    }
    
    /**
     * Execute an external command
     *
     * @param   command  The command line arguments to execute
     * @return           The command's output
     * 
     * @throws  Exception  On execution error
     */
    @requires({"java-runtime>=1.5"})
    private static String exec(final String... command) throws Exception
    {
        final ProcessBuilder builder = new ProcessBuilder(cmd);
	final Process process = builder.start();
	final InputStream stream = process.getOutputStream();
        
	process.waitFor();
	if (process.exitValue() != 0)
	    throw new Exception();
	
	final byte[] buffer = new byte[256];
	for (int d, ptr = 0; (d = stream.read()) != -1;)
	{   if (ptr == buffer.length)
		System.arraycopy(buffer, 0, buffer = new byte[ptr << 1], 0, ptr);
	    buffer[ptr++] = (byte)d;
	}
	
	return new String(buffer, 0, ptr, "UTF-8");
    }

}

