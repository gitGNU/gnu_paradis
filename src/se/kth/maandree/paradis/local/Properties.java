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
package se.kth.maandree.paradis.local;


/**
 * System properties
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Properties
{
    /**
     * Non-constructor
     */
    private Properties()
    {
	assert false : "You may not create instances of this class [Properties].";
    }
    
    
    
    /**
     * Gets the home of the user, but allowes false home given by the shell
     * 
     * @return  The home of the user
     */
    public static String getHome()
    {
	String home;
	if ((home = System.getenv().get("HOME")) == null)
	    return System.getProperty("user.home");
	return home;
    }

}

