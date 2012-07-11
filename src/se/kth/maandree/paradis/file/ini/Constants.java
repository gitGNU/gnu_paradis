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
package se.kth.maandree.paradis.file.ini;


/**
 * This class holds constants used in the classes
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
class Constants
{
    /**
     * Line ending
     */
    final static String LINE_ENDING = "\n";
    
    /**
     * Hive declaration start
     */
    final static String HIVE_START = "[";
    
    /**
     * Hive declaration end
     */
    final static String HIVE_END = "]";
    
    /**
     * Escapement character
     */
    final static char ESCAPE_CHAR = '\\';
    
    /**
     * The characater used to continue the previouse line
     */
    final static char PREV_LINE_CONTINUER = ',';
    
    /**
     * The characters in the hexadecimal number system
     */
    final static String HEXADEC = "0123456789ABCDEF";
    
    
    
    /**
     * Non-constructor
     */
    private Constants()
    {
        assert false : "You may not create instances of this class [Constants].";
    }
    
}

