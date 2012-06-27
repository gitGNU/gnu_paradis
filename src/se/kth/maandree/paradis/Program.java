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
package se.kth.maandree.paradis;
import se.kth.maandree.paradis.net.*;
import se.kth.maandree.paradis.util.*;
import se.kth.maandree.paradis.local.Properties; //Explicit

import java.util.*;
import java.io.File;


/**
 * The is the main class of the program
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
    public static final String WEBSITE = "www.github.com/maandree/paradis";
    
    
    
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
	final Scanner sc = new Scanner(System.in);
	while (sc.hasNextLine())
	{
	    final String line = sc.nextLine();
	    if (line.equals("help"))
	    {
		System.out.println("COMMANDS:     DESCRIPTIONS:");
		System.out.println("help          Leads here...");
		System.out.println("show c        Show copyright notice.");
		System.out.println("show w        Show warranty notice.");
		System.out.println("show l        Show licenses.");
		System.out.println("version       Print version information about this program.");
		System.out.println("credits       Shows the credits of this program and all aktiv plug-ins.");
	    }
	    else if (line.equals("show c"))
	    {
		System.out.println("Paradis — Ever growing network for parallell and distributed computing.");
		System.out.println("Copyright © 2012  Mattias Andrée");
		System.out.println();
		System.out.println("This program is free software: you can redistribute it and/or modify");
		System.out.println("it under the terms of the GNU Affero General Public License as published by");
		System.out.println("the Free Software Foundation, either version 3 of the License, or");
		System.out.println("(at your option) any later version.");
		System.out.println();
		System.out.println("You should have received a copy of the GNU Affero General Public License");
		System.out.println("along with this program.  If not, see <http://www.gnu.org/licenses/>.");
	    }
	    else if (line.equals("show w"))
	    {
		System.out.println("This program is distributed in the hope that it will be useful,");
		System.out.println("but WITHOUT ANY WARRANTY; without even the implied warranty of");
		System.out.println("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the");
		System.out.println("GNU Affero General Public License for more details.");
 	    }
	    else if (line.equals("show l"))
	    {
		final String pager = Properties.getPager();
		
		if      ((new File("res/LICENSE.AGPL3+colour")).exists())  Pager.pageFile(pager, "GNU Affero General Public License v3", "res/LICENSE.AGPL3+colour");
		else if ((new File("res/LICENSE.AGPL3"       )).exists())  Pager.pageFile(pager, "GNU Affero General Public License v3", "res/LICENSE.AGPL3");
		else                                                       Pager.pageFile(pager, "GNU Affero General Public License v3", "LICENSE.AGPL3");
		
		if      ((new File("res/LICENSE.GPL2+colour")).exists())  Pager.pageFile(pager, "GNU General Public License v2", "res/LICENSE.GPL2+colour");
		else if ((new File("res/LICENSE.GPL2"       )).exists())  Pager.pageFile(pager, "GNU General Public License v2", "res/LICENSE.GPL2");
		else                                                      Pager.pageFile(pager, "GNU General Public License v2", "LICENSE.GPL2");
	    }
	    else if (line.equals("version"))
	    {
		System.out.println("Package:   " + PACKAGE);
		System.out.println("Fork path: " + FORK);
		System.out.println("Version:   " + VERSION);
		System.out.println();
		System.out.println("Website:   " + WEBSITE);
	    }
	    else if (line.equals("credits"))
	    {
		System.out.println("Paradis — Ever growing network for parallell and distributed computing.");
		System.out.println("Copyright © 2012  Mattias Andrée");
	    }
	    else
		System.out.println("Unrecognised command.");
	}
    }
    
}

