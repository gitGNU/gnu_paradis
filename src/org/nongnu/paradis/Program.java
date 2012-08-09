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
package org.nongnu.paradis;
import org.nongnu.paradis.servers.*;

import java.util.*;


/**
 * This is the main class of the program
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Program
{
    /**
     * The name of the package, this should never be modified unleash you make a totally new program
     */
    public static final String PACKAGE = "paradis";
    
    /**
     * The fork path of the package, place ready documentation before changing this to ensure uniqueness
     */
    public static final String FORK = "maandree";
    
    /**
     * The version of the package
     */
    public static final String VERSION = "0.0.1.0";
    
    /**
     * Reference to where you can get more information about this package
     */
    public static final String WEBSITE = "www.nongnu.org/paradis";
    
    
    
    /**
     * Non-constructor
     */
    private Program()
    {
        assert false : "You may not create instances of this class [Program].";
    }
    
    
    
    /**
     * This is the main entry point of the program
     * 
     * @param  args  Startup arguments, unused
     */
    public static void main(final String... args)
    {
        (new NativeServer()).hashCode(); /* .hashCode() is only used to not ge a warning with ecj */
        
        Scanner sc = null;
        try
        {   sc = new Scanner(System.in);
            while (sc.hasNextLine())
            {
                final String line = sc.nextLine().trim();
                if (line.isEmpty())
                    continue;
                
                final ServerInvoke message = new ServerInvoke(line, sc);
                Blackboard.getInstance(null).broadcastMessage(message);
                if (message.consumed == false)
                    System.out.println("Unrecognised command.");
            }
        }
        finally
        {   if (sc != null)
                try
                {    sc.close();
                }
                catch (final Throwable ignore)
                {   //Ignore
        }       }
    }
    
}

