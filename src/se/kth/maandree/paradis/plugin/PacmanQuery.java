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
 * -Q invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PacmanQuery implements Blackboard.BlackboardObserver
{
    /**
     * The directory where the packages are located
     */
    private static final String PACKAGE_DIR = Pacman.PACKAGE_DIR;
    
    /**
     * The file where the data are saved
     */
    private static final String PACKAGES_FILE = Pacman.PACKAGES_FILE;
    
    
    /** Search for installed packages
     */ private static final String QUERY = Pacman.QUERY;
    
    /** Only return explicitly installed packages
     */ private static final String QUERY_EXPLICIT = Pacman.QUERY_EXPLICIT;
    
    /** Use regular expressions
     */ private static final String QUERY_SEARCH = Pacman.QUERY_SEARCH;
    
    /** Only return unrequired packages
     */ private static final String QUERY_UNREQUIRED = Pacman.QUERY_UNREQUIRED;
    
    /** Do not return up to date packages
     */ private static final String QUERY_UPGRADE = Pacman.QUERY_UPGRADE;
    
    
    
    //Has default constructor
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
    {
	if ((message instanceof Pacman.PacmanInvoke == false) || (((Pacman.PacmanInvoke)message).masteropt.equals(QUERY) == false))
	    return;
	final HashSet<String> options = ((Pacman.PacmanInvoke)message).options;
	final ArrayList<String> packages = ((Pacman.PacmanInvoke)message).packages;
    }
    
}

