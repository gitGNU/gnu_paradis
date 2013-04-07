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

import java.util.ArrayList;
import java.io.Serializable;


/**
 * Instances of this class represents hives in a INI-file
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
class Hive implements Serializable
{
    /**
     * Desired by {@link Serializable}
     */
    private static final long serialVersionUID = 1L;
    
    
    
    /**
     * Constructor
     *
     * @param  name  The name of the hive
     * @param  ini   Configurations instance
     */
    public Hive(final String name, INIInterface ini)
    {
        this.ini = ini;
        this.name = name;
    }
    
    
    
    /**
     * Reader/Writer instance
     */
    INIInterface ini;
    
    /**
     * The hive's name
     */
    private final String name;
    
    /**
     * Keys inside the hive
     */
    ArrayList<Key> keys = new ArrayList<Key>();
    
    
    
    /**
     * Unbuffers all keys in the hive
     */
    public void clear()
    {
        this.keys.clear();
    }
    
    /**
     * Gets the name of the hive
     *
     * @return  The name of the hive
     */
    public String getName()
    {
        return this.name;
    }
    
    
    /**
     * Gets all keys inside the hive
     *
     * @param  lines  Lines of inside the hive
     */
    public void setHive(final String... lines)
    {
        for (int index = 0, n = lines.length; index < n; index++)
            if ((lines[index].length() > 0) && (lines[index].startsWith(this.ini.getCommentNotation()) == false))
            {
                int len = lines[index].indexOf(this.ini.getKeyEnding());
                len = (len < 0) ? lines[index].length() : len;
                String keyname = lines[index].substring(0, len);
                
                len = keyname.length() + this.ini.getKeyEnding().length();
                String keyvalue = lines[index].substring(len, lines[index].length());
                
                keyname  = Escaper.unescape(keyname);
                keyvalue = Escaper.unescape(keyvalue);
                
                if (getKey(keyname) != null)
                {   if (this.ini.getLastEntryOverrides())
                        getKey(keyname).setValue(keyvalue);
                }
                else
                    newKey(keyname, keyvalue);
            }
    }
    
    
    /**
     * Returns the key the matching name, {@code null} if not existing
     *
     * @param   name  Name of the desired key
     * @return        The key the matching name, {@code null} if not existing
     */
    public Key getKey(@SuppressWarnings("hiding") final String name)
    {
        for (Key key : this.keys)
            if (key.getName().toLowerCase().equals(name.toLowerCase()))
                return key;
        
        return null;
    }
    
    /**
     * Creates a new key
     *
     * @param   name   Name of the new key
     * @param   value  Value of the new key
     * @return         The new key
     */
    public Key newKey(@SuppressWarnings("hiding") final String name, final String value)
    {
        Key key = new Key(name, value);
        this.keys.add(key);
        return key;
    }
    
}

