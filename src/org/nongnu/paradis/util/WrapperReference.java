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
package org.nongnu.paradis.util;


/**
 * A wrapper reference is a strong reference wrapping another reference, allowing independent non-strong references
 * 
 * @param  <T>  The element type of the reference
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
public class WrapperReference<T>
{
    /**
     * Constructor
     * 
     * @param  item  The referenced item
     */
    public WrapperReference(final T item)
    {
        this.item = item;
    }
    
    
    
    /**
     * The referenced item
     */
    private T item;
    
    
    
    /**
     * Gets the referenced item
     * 
     * @return  The referenced item
     */
    public T get()
    {
        return this.item;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(final Object other)
    {
        if ((other == null) || (other instanceof WrapperReference == false))
            return false;
        if (other == this)
            return true;
        if (this.item == null)
            return ((WrapperReference)other).item == null;
        return this.item.equals(((WrapperReference)other).item);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return this.item == null ? 0 : this.item.hashCode();
    }
    
}

