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
package se.kth.maandree.paradis.net;


/**
 * Packet sending path information interface
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public interface Cast
{
    /**
     * Lists an {@link UUID} to have received, is is currently getting, a copy of the packet
     * 
     * @param  uuid  The {@link UUID}
     */
    public void addReceived(final UUID uuid);
    
    /**
     * Gets whether an {@link UUID} is known to already have copy or currently gettign a copy
     * 
     * @param   uuid  The {@link UUID}
     * @return        Whether the {@link UUID} is known to already have copy or currently gettign a copy
     */
    public boolean hasReceived(final UUID uuid);
    
}

