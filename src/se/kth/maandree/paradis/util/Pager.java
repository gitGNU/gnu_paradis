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

import java.io.*;


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
     * Starts text pager
     * 
     * @param  pager  The users pager of choice
     * @param  title  The title of the document
     * @param  text   The text to page
     */
    public static void page(final String pager, final String title, final String text)
    {
	page(pager == null ? "less" : pager.isEmpty() ? "less" : pager, text);
	
	
	//TODO: lets make a pager
	
	//allow another pager (not if 'pager' is null or 'pager' is empty)
	
	//`stty size` --> rows cols
	//save stty
	//init terminal
	//hide cursor
	//`stty -icanon -echo`
	
	//split text into lines
	//  remove all ANSI escape sequences except colours and boldness
	//  split colouring and boldness so the colour of a character can
	//  absolutly be determined by only examine that line
	
	//h --> help
	//l --> reload terminal size and reprint
	//arrows --> navigate
	//home/end/pgup/pgdn --> navigate
	//c --> toggle colours and boldness   //TODO: lets add this stuff to all documents
	//q --> quit
	//esc esc es --> quit
	//enter --> quit
	//space --> paragraph down
	
	//show title
	//show content
	//show status bar
	//  h for help
	//  q for quit
	//  all/top/bot/??%
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
    private static void page(final String pager, final String text)
    {
	try //TODO: This is the Java 6 way.
	{
	    String cmd = pager + " -r > " + (new File("/dev/stdout")).getCanonicalPath();
	    
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

