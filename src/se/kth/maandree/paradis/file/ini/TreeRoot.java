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
package se.kth.maandree.paradis.file.ini;

import java.util.ArrayList;
import java.io.Serializable;


/**
 * This is the root-level of the buffering class for the INI-file
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
class TreeRoot implements Serializable
{
    /**
     * Desried by {@link Serializable}
     */
    private static final long serialVersionUID = 1L;
    
    
    
    /**
     * Constructor
     *
     * @param  removeOnRead  Whether the instance of this class should unbuffer the keys when they have been read
     * @param  ini           Config instance representing the INI-file
     */
    public TreeRoot(boolean removeOnRead, INIInterface ini)
    {
        this.removeOnRead = removeOnRead;
        this.ini = ini;
        
        final String[] hivenames = ini.getHives();
        for (int i = 0; i < hivenames.length; i++)
            if (getHive(hivenames[i]) == null)
                newHive(hivenames[i]).setHive(ini.getHiveKeys(hivenames[i]));
    }
    
    
    
    /**
     * Reader/Writter instance
     */
    INIInterface ini;
    
    /**
     * Whether the instance of this class should
     * unbuffer the keys when they have been read
     */
    private final boolean removeOnRead;
    
    /**
     * All hives in the ini-file
     */
    private ArrayList<Hive> hives = new ArrayList<Hive>();
    
    
    
    /**
     * Clears the buffer
     */
    public void clear()
    {   this.hives.clear();
    }
    
    /**
     * Returns the hive with the matching name, {@code null} if non existing
     *
     * @param   name  The name of the desired hive
     * @return        The hive with the matching name, {@code null} if non existing
     */
    public Hive getHive(final String name)
    {
        for (Hive hive : this.hives)
            if (hive.getName().toLowerCase().equals(name.toLowerCase()))
                return hive;
        
        return null;
    }
    
    /**
     * Creates a new hive
     *
     * @param   name  The name of the now hive
     * @return        The new hive
     */
    public Hive newHive(final String name)
    {
        Hive hive = new Hive(name, this.ini);
        this.hives.add(hive);
        return hive;
    }
    
    /**
     * Creates a new key
     *
     * @param  hive   Name of the hive owning (not required to exist) the new key
     * @param  key    Name of the new key
     * @param  value  Value of the new key
     */
    public void createKey(final String hive, final String key, final String value)
    {
        if (getHive(hive) == null)
            newHive(hive).setHive(new String[0]);
        
        Hive foundHive = getHive(hive);
        if ((value != null) && (foundHive.getKey(key) == null))
            foundHive.newKey(key, value);
        else if (value != null)
            foundHive.getKey(key).setValue(value);
        else
            foundHive.keys.remove(foundHive.getKey(key));
    }
    
    /**
     * Returns the value of a key
     *
     * @param   hive  Name of the hive owning the key
     * @param   key   Name of the key
     * @return        The value of a key
     */
    public String getKey(final String hive, final String key)
    {
        if ((key == null) || (hive == null))
            return null;
        
        Hive hHive = getHive(hive);
        if (hHive == null)
            return null;
        
        Key kKey = hHive.getKey(key);
        if (kKey == null)
            return null;
        
        final String rc = kKey.getValue();
        
        if (this.removeOnRead)
            hHive.keys.remove(kKey);
        
        return rc;
    }
    
}

