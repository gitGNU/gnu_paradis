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
 * Instances of this class represents keys in a INI-file
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
class Key implements Serializable
{
    /**
     * Desired by {@link Serializable}
     */
    private static final long serialVersionUID = 1L;
    
    
    
    /**
     * Constructor
     *
     * @param  name   Name of the key
     * @param  value  Value of the key
     */
    public Key(final String name, final String value)
    {
        this.name = name;
        this.value = value;
    }
    
    
    
    /**
     * Value of the key
     */
    private String value;
    
    /**
     * Name of the key
     */
    private final String name;
    
    
    
    /**
     * Gets the name of the key
     *
     * @return  The name of the key
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Gets the value of the key
     *
     * @return  The value of the key
     */
    public String getValue()
    {
        return this.value;
    }
    
    /**
     * Sets the value of the key
     *
     * @param  newValue  The new value of the key
     */
    public void setValue(final String newValue)
    {
        this.value = newValue;
    }
    
}

