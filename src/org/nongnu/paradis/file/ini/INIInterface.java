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
package org.nongnu.paradis.file.ini;

import java.io.Serializable;


/**
 * Interface for INI needed to eliminate circle dependency
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public interface INIInterface extends Serializable
{
    /**
     * Gets the comment line notation
     *
     * @return  The comment line notation
     */
    String getCommentNotation();
    
    /**
     * Gets the key name - key value separator
     *
     * @return  The key name - key value separator
     */
    String getKeyEnding();
    
    /**
     * Get whether the last key/hive with same name should override the earlier
     *
     * @return  Whether the last key/hive with same name should override the earlier
     */
    boolean getLastEntryOverrides();
    
    
    /**
     * Returns all hive names
     *
     * @return  All hive names
     */
    String[] getHives();
    
    /**
     * Returns all keys in a hive (autoescaped hivename)
     *
     * @param   hive  The name of the hive
     * @return        All keys in a hive
     */
    String[] getHiveKeys(final String hive);
    
}

