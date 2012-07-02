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
 * -R invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PacmanRemove implements Blackboard.BlackboardObserver
{
    /**
     * The directory where the packages are located
     */
    private static final String PACKAGE_DIR = Pacman.PACKAGE_DIR;
    
    /**
     * The file where the data are saved
     */
    private static final String PACKAGES_FILE = Pacman.PACKAGES_FILE;
    
    
    /** Uninstall packages
     */ private static final String REMOVE = Pacman.REMOVE;
    
    /** Uninstall packages dependent on the packages
     */ private static final String REMOVE_CASCADE = Pacman.REMOVE_CASCADE;
    
    /** Do not uninstall dependencies
     */ private static final String REMOVE_NODEPS = Pacman.REMOVE_NODEPS;
    
    /** Do not uninstall the files, only mark as uninstalled
     */ private static final String REMOVE_DBONLY = Pacman.REMOVE_DBONLY;
    
    /** Remove dependencies
     */ private static final String REMOVE_RECURSIVE = Pacman.REMOVE_RECURSIVE;
    
    /** Use regular expressions
     */ private static final String REMOVE_SEARCH = Pacman.REMOVE_SEARCH;
    
    /** Remove packages required by any installed package
     */ private static final String REMOVE_UNREQUIRED = Pacman.REMOVE_UNREQUIRED;
    
    /** Do not remove packages needed by any other package
     */ private static final String REMOVE_UNNEEDED = Pacman.REMOVE_UNNEEDED;
    
    
    
    //Has default constructor
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
    {
	if ((message instanceof Pacman.PacmanInvoke == false) || (((Pacman.PacmanInvoke)message).masteropt.equals(REMOVE) == false))
	    return;
	final HashSet<String> options = ((Pacman.PacmanInvoke)message).options;
	final HashSet<String> ignores = ((Pacman.PacmanInvoke)message).ignores;
	final ArrayList<String> packages = ((Pacman.PacmanInvoke)message).packages;
	
	//REMOVE_CASCADE
	//REMOVE_NODEPS
	//REMOVE_DBONLY
	//REMOVE_RECURSIVE
	//REMOVE_SEARCH
	//REMOVE_UNREQUIRED
	//REMOVE_UNNEEDED
    }
    
}

