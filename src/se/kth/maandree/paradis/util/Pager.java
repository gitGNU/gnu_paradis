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
package se.kth.maandree.paradis.util;
import se.kth.maandree.paradis.*;

import java.io.*;
import java.util.*;


/**
 * Simple text pager for terminals
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
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
	if ((new File(file)).exists() == false)
	{
	    final String errText = "The file " + file + " is missing.\nIts title is: " + title + "\n\n";
	    page(pager, "--:: FILE MISSING ::--", errText);
	    return;
	}
	
	final String text;
	
	InputStream is = null;
	try
	{
	    is = new BufferedInputStream(new FileInputStream(new File(file)));
	    
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
	}	}
	
	page(pager, title, text);
    }
    
    
    /**
     * Starts a text pager
     * 
     * @param  pager  The users pager of choice
     * @param  title  The title of the document
     * @param  text   The text to page
     */
    public static void page(final String pager, final String title, final String text)
    {
	page(pager == null ? "less" : pager.isEmpty() ? "less" : pager, text);
	
	
	//TODO: lets make a pager
	
	//split text into lines
	//  remove all ANSI escape sequences except colours and boldness
	//  split colouring and boldness so the colour of a character can
	//  absolutly be determined by only examine that line
	
	//allow another pager (not if 'pager' is null or 'pager' is empty)
	
	//`stty size` --> rows cols
	//save stty
	//init terminal
	//hide cursor
	//`stty -icanon -echo`
	
	//h --> help
	//l --> reload terminal size and reprint
	//arrows --> navigate
	//home/end/pgup/pgdn --> navigate
	//c --> toggle colours and boldness   //TODO: lets add this stuff to all documents
	//q --> quit
	//esc esc esc --> quit
	//enter --> quit
	//space --> paragraph down
	
	//show title
	//show content
	//show status bar
	//  h for help
	//  q for quit
	//  All/Top/Bot/??%
	//  first line - last line
	
	//finally
	//  show cursor
	//  terminate terminal
	//  reset stty
    }
    
    
    /**
     * Prints lines to the terminal using a pager
     *
     * @param  pager  The pager to use
     * @param  text   The text to print
     */
    @requires("coreutils")
    private static void page(final String pager, final String text)
    {
	try
	{
	    String cmd = pager + " > " + (new File("/dev/stdout")).getCanonicalPath();
	    
	    final Process process = (new ProcessBuilder("/bin/sh", "-c", cmd)).start();
	    final InputStream stream = process.getErrorStream();
	    final OutputStream out = process.getOutputStream();
	    
	    out.write(text.getBytes("UTF-8"));
	    out.flush();
	    out.close();
	    
	    for (;;)
		if (stream.read() == -1)
		    break;
	    
	    process.waitFor();
	    if (process.exitValue() != 0)
		throw new Exception();
	}
	catch (final Throwable err)
	{   System.out.println("Unable to page using '" + pager + "'");
	}
    }

}

