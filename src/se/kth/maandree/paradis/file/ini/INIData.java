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


/**
 * Instances of this class holds data for <code>INI</code>
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
class INIData
{
    //Has default constructor
    
    
    
    /**
     * Key name - key value separator
     */
    String keyEnding;
    
    /**
     * Whether the last key/hive with same name should override the earlier
     */
    boolean lastEntryOverrides;
    
    /**
     * Comment line notation
     */
    String commentNotation;
    
    /**
     * The data in the ini-file as string format.
     */
    String filetext;
    
    /**
     * Buffered INI-file content
     */
    TreeRoot treeRoot = null;
    
    /**
     * Filename of the ini-file.
     */
    String filename;
    
    /**
     * The signature in the file
     */
    String signature = "";
    
}

