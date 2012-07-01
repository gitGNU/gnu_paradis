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
package se.kth.maandree.paradis.plugin;
import se.kth.maandree.paradis.*;

import java.util.*;


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
    private static final String PACKAGES_FILE = Pacman.PACKAGES_FILE;
    
    
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
    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
    {
	if ((message instanceof Pacman.PacmanInvoke == false) || (((Pacman.PacmanInvoke)message).masteropt.equals(DATABASE) == false))
	    return;
	final HashSet<String> options = ((Pacman.PacmanInvoke)message).options;
	final ArrayList<String> packages = ((Pacman.PacmanInvoke)message).packages;
    }
    
}

