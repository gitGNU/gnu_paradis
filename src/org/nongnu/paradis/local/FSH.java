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
package org.nongnu.paradis.local;
import org.nongnu.paradis.*;


/**
 * Filesystem hierarchy class
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class FSH
{
    /**
     * The name of the package
     */
    private static final String PACKAGE = Program.PACKAGE;
    
    /**
     * The user's home directory
     */
    private static final String HOME = Properties.getHome();
    
    /**
     * File spearator
     */
    private static final String DIR = Properties.getFileSeparator();
    
    
    /** Host-specific configurations for large self-contained package
     */ public static String OPT_ETC = "/etc/opt/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Large self-contained package, static content
     */ public static String OPT = "/opt/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Data for services provided by the system
     */ public static String SITE = "/srv/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Transient temporary files
     */ public static String TEMP_TRANSIENT = "/tmp/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Secondary user commands
     */ public static String COMMANDS = "/usr/bin/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Secondary libraries
     */ public static String LIBRARIES = "/usr/lib/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Secondary resources directory
     */ public static String RES = "/usr/share/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Secondary resources file
     */ public static String RES_FILE = "/usr/share/misc/<pkg>".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Local user commands
     */ public static String SEC_COMMANDS = "/usr/local/bin/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Local libraries
     */ public static String SEC_LIBRARIES = "/usr/local/lib/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Local resources directory
     */ public static String SEC_RES = "/usr/local/share/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Local resources file
     */ public static String SEC_RES_FILE = "/usr/local/share/misc/<pkg>".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** User private user commands
     */ public static String USR_COMMANDS = "<home>/.local/bin/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** User private libraries
     */ public static String USR_LIBRARIES = "<home>/.local/lib/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** User private resources directory
     */ public static String USR_RES = "<home>/.local/share/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** User private resources file
     */ public static String USR_RES_FILE = "<home>/.local/share/misc/<pkg>".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** User private package configurations directory
     */ public static String CONF = "<home>/.<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** User private package configurations file
     */ public static String CONF_FILE = "<home>/.<pkg>".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Application cache data
     */ public static String CACHE = "/var/cache/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Variable state information directory
     */ public static String STATE = "/var/lib/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Variable state information file
     */ public static String STATE_FILE = "/var/lib/misc/<pkg>".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Lock files
     */ public static String LOCKS = "/var/lock/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Variable data for self-contained package
     */ public static String OPT_VAR = "/var/opt/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** PID file for data relevant to running process
     */ public static String RUN_PID = "/var/run/<pkg>.pid".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Data relevant to running process
     */ public static String RUN_DATA = "/var/run/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Application spool data (like cache, except the user should not removed files manually)
     */ public static String SPOOL = "/var/spool/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Persistent temporary files
     */ public static String TEMP_PRESISTENT = "/var/tmp/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    /** Log files
     */ public static String LOGS = "/var/log/<pkg>/".replace("/", DIR).replace("<pkg>", PACKAGE).replace("<home>", HOME);
    
    
    
    /**
     * Non-constructor
     */
    private FSH()
    {
        assert false : "You may not create instances of this class [FSH].";
    }
    
}

