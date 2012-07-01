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
 * -U invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PacmanUpgrade implements Blackboard.BlackboardObserver
{
    /**
     * The directory where the packages are located
     */
    private static final String PACKAGE_DIR = Pacman.PACKAGE_DIR;
    
    /**
     * The file where the data are saved
     */
    private static final String PACKAGES_FILE = Pacman.PACKAGES_FILE;
    
    
    /** Remove-and-install process
     */ private static final String UPGRADE = Pacman.UPGRADE;
    
    /** Ignore dependencies
     */ private static final String UPGRADE_NODEPS = Pacman.UPGRADE_NODEPS;
    
    /** Mark dependencies as explicitly installed
     */ private static final String UPGRADE_ASEXPLICIT = Pacman.UPGRADE_ASEXPLICIT;
    
    /** Override existing files not installed by the package itself
     */ private static final String UPGRADE_FORCE = Pacman.UPGRADE_FORCE;
    
    /** Mark installed packages as installed as a dependency
     */ private static final String UPGRADE_ASDEPS = Pacman.UPGRADE_ASDEPS;
    
    /** Ignore up to date packages
     */ private static final String UPGRADE_NEEDED = Pacman.UPGRADE_NEEDED;
    
    /** Do not install the files
     */ private static final String UPGRADE_DBONLY = Pacman.UPGRADE_DBONLY;
    
    /** Recursively reinstall all dependencies
     */ private static final String UPGRADE_RECURSIVE = Pacman.UPGRADE_RECURSIVE;
    
    /** Use regular expressions
     */ private static final String UPGRADE_SEARCH = Pacman.UPGRADE_SEARCH;
    
    /** Include all installed non-up to date packages
     */ private static final String UPGRADE_UPGRADE = Pacman.UPGRADE_UPGRADE;
    
    
    
    //Has default constructor
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
    {
	if ((message instanceof Pacman.PacmanInvoke == false) || (((Pacman.PacmanInvoke)message).masteropt.equals(UPGRADE) == false))
	    return;
	final HashSet<String> options = ((Pacman.PacmanInvoke)message).options;
	final HashSet<String> ignores = ((Pacman.PacmanInvoke)message).ignores;
	final ArrayList<String> packages = ((Pacman.PacmanInvoke)message).packages;
    }
    
}

