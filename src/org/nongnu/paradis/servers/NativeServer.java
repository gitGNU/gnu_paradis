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
package org.nongnu.paradis.servers;
import org.nongnu.paradis.pacman.*;
import org.nongnu.paradis.local.Properties; //Explicit
import org.nongnu.paradis.util.*;
import org.nongnu.paradis.io.*;
import org.nongnu.paradis.*;


/**
 * Paradis native server
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class NativeServer extends AbstractServer
{
    /**
     * Constructor
     */
    public NativeServer()
    {
        super(-1 ^ (-1 >>> 1));
    }
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    @requires("java-runtime>=7")
    public boolean invoke(final String command, final boolean consumed)
    {
        if (command.equals("help"))
        {
            if (consumed)
                return true;
            System.out.println("COMMANDS:     DESCRIPTIONS:");
            System.out.println("help          Leads here...");
            System.out.println("show c        Show copyright notice.");
            System.out.println("show w        Show warranty notice.");
            System.out.println("show l        Show licenses.");
            System.out.println("version       Print version information about this program.");
            System.out.println("credits       Shows the credits of this program and all aktiv plug-ins.");
        }
        else if (command.equals("show c"))
        {
            if (consumed)
                return true;
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
        else if (command.equals("show w"))
        {
            if (consumed)
                return true;
            System.out.println("This program is distributed in the hope that it will be useful,");
            System.out.println("but WITHOUT ANY WARRANTY; without even the implied warranty of");
            System.out.println("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the");
            System.out.println("GNU Affero General Public License for more details.");
        }
        else if (command.equals("show l"))
        {
            if (consumed)
                return true;
            final String pager = Properties.getPager();
                    
            if      (FileHandler.fileExists("res/COPYING+colour"))  Pager.pageFile(pager, "Copyright information", "res/COPYING+colour");
            else if (FileHandler.fileExists("res/COPYING"       ))  Pager.pageFile(pager, "Copyright information", "res/COPYING");
            else                                                    Pager.pageFile(pager, "Copyright information", "COPYING");
                    
            if      (FileHandler.fileExists("res/LICENSE.AGPL3+colour"))  Pager.pageFile(pager, "GNU Affero General Public License v3", "res/LICENSE.AGPL3+colour");
            else if (FileHandler.fileExists("res/LICENSE.AGPL3"       ))  Pager.pageFile(pager, "GNU Affero General Public License v3", "res/LICENSE.AGPL3");
            else                                                          Pager.pageFile(pager, "GNU Affero General Public License v3", "LICENSE.AGPL3");
                    
            if      (FileHandler.fileExists("res/LICENSE.GPL3+colour"))   Pager.pageFile(pager, "GNU General Public License v3", "res/LICENSE.GPL3+colour");
            else if (FileHandler.fileExists("res/LICENSE.GPL3"       ))   Pager.pageFile(pager, "GNU General Public License v3", "res/LICENSE.GPL3");
            else                                                          Pager.pageFile(pager, "GNU General Public License v3", "LICENSE.GPL3");
                    
            if      (FileHandler.fileExists("res/LICENSE.GPL2+colour"))   Pager.pageFile(pager, "GNU General Public License v2", "res/LICENSE.GPL2+colour");
            else if (FileHandler.fileExists("res/LICENSE.GPL2"       ))   Pager.pageFile(pager, "GNU General Public License v2", "res/LICENSE.GPL2");
            else                                                          Pager.pageFile(pager, "GNU General Public License v2", "LICENSE.GPL2");
        }
        else if (command.equals("version"))
        {
            if (consumed)
                return true;
            System.out.println("Package:   " + Program.PACKAGE);
            System.out.println("Fork path: " + Program.FORK);
            System.out.println("Version:   " + Program.VERSION);
            System.out.println();
            System.out.println("Website:   " + Program.WEBSITE);
        }
        else if (command.equals("credits"))
        {
            if (consumed)
                return true;
            System.out.println("Paradis — Ever growing network for parallell and distributed computing.");
            System.out.println("Copyright © 2012  Mattias Andrée");
        }
        else if (command.equals("shell"))
        {
            if (consumed)
                return true;
	    String shell = Properties.getShell();
	    if ((shell == null) || shell.isEmpty())
		System.out.println("Impossible to determine shell, your shell should export SHELL to it's file name (e.g. bash).");
	    else
		try
		{   final ProcessBuilder builder = new ProcessBuilder(shell);
		    builder.inheritIO();
		    builder.start().waitFor();
		}
		catch (final Throwable err)
		{   System.out.print("Cannot manage to start shell");
		}
        }
        else if (command.equals("pacman") || command.startsWith("pacman "))
        {
            if (consumed)
                return true;
            Pacman.main(command.equals("pacman") ? new String[0] : command.substring("pacman ".length()).split(" "));
        }
        else if (command.equals("makepkg") || command.startsWith("makepkg "))
        {
            if (consumed)
                return true;
            Makepkg.main(command.equals("makepkg") ? new String[0] : command.substring("makepkg ".length()).split(" "));
        }
        else
            return false;
        
        return true;
    }
    
}

