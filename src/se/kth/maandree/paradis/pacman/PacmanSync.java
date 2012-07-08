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
package se.kth.maandree.paradis.pacman;
import se.kth.maandree.paradis.*;

import java.util.*;


/**
 * -S invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PacmanSync implements Blackboard.BlackboardObserver
{
    /**
     * The directory where the packages are located
     */
    private static final String PACKAGE_DIR = Pacman.PACKAGE_DIR;
    
    /**
     * The file where the data are saved
     */
    private static final String PACKAGES_FILE = Pacman.PACKAGES_FILE;
    
    
    /** Synchronise (install or upgrade) packages
     */ private static final String SYNC = Pacman.SYNC;
    
    /** Ignore dependencies
     */ private static final String SYNC_NODEPS = Pacman.SYNC_NODEPS;
    
    /** Mark dependencies as explicitly installed
     */ private static final String SYNC_ASEXPLICIT = Pacman.SYNC_ASEXPLICIT;
    
    /** Override existing files not installed by the package itself
     */ private static final String SYNC_FORCE = Pacman.SYNC_FORCE;
    
    /** Mark installed packages as installed as a dependency
     */ private static final String SYNC_ASDEPS = Pacman.SYNC_ASDEPS;
    
    /** Ignore up to date packages
     */ private static final String SYNC_NEEDED = Pacman.SYNC_NEEDED;
    
    /** Do not install the files
     */ private static final String SYNC_DBONLY = Pacman.SYNC_DBONLY;
    
    /** Recursively reinstall all dependencies
     */ private static final String SYNC_RECURSIVE = Pacman.SYNC_RECURSIVE;
    
    /** Use regular expressions
     */ private static final String SYNC_SEARCH = Pacman.SYNC_SEARCH;
    
    /** Include all installed non-up to date packages
     */ private static final String SYNC_UPGRADE = Pacman.SYNC_UPGRADE;
    
    
    
    //Has default constructor
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
    {
        if ((message instanceof Pacman.PacmanInvoke == false) || (((Pacman.PacmanInvoke)message).masteropt.equals(SYNC) == false))
            return;
        final HashSet<String> options = ((Pacman.PacmanInvoke)message).options;
        final HashSet<String> ignores = ((Pacman.PacmanInvoke)message).ignores;
        final ArrayList<String> packages = ((Pacman.PacmanInvoke)message).packages;
        
        //SYNC_NODEPS
        //SYNC_ASEXPLICIT
        //SYNC_FORCE
        //SYNC_ASDEPS
        //SYNC_NEEDED
        //SYNC_DBONLY
        //SYNC_RECURSIVE
        //SYNC_SEARCH
        //SYNC_UPGRADE
    }
    
}

