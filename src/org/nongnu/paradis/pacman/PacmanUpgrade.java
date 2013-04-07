/**
 *  Paradis — Ever growing network for parallel and distributed computing.
 *  Copyright © 2012, 2013  Mattias Andrée (maandree@member.fsf.org)
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
package org.nongnu.paradis.pacman;
import org.nongnu.paradis.*;

import java.util.*;


/**
 * -U invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
public class PacmanUpgrade implements Blackboard.BlackboardObserver
{
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
        
        final boolean nodeps    = options.contains(UPGRADE_NODEPS);
        final boolean asexpl    = options.contains(UPGRADE_ASEXPLICIT);
        final boolean asdeps    = options.contains(UPGRADE_ASDEPS);
        final boolean force     = options.contains(UPGRADE_FORCE);
        final boolean needed    = options.contains(UPGRADE_NEEDED);
        final boolean dbonly    = options.contains(UPGRADE_DBONLY);
        final boolean recursive = options.contains(UPGRADE_RECURSIVE);
        final boolean upgrade   = options.contains(UPGRADE_UPGRADE);
        
        final Common common = new Common();
        try
        {   if (options.contains(UPGRADE_SEARCH))
            {   common.loadInstalled();
                PacmanQuery.search(common.installedMap, packages, ignores, options.contains(UPGRADE_SEARCH));
            }
            else
                PacmanSync.sync(packages, ignores, true, nodeps, asexpl, asdeps, force, needed, dbonly, recursive, upgrade);
        }
        catch (final Throwable err)
        {   System.err.println(err.toString());
        }
    }
    
}

