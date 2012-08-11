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
package org.nongnu.paradis.util;
import org.nongnu.paradis.*;

import java.util.*;


/**
 * Option parsing utility
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
@SuppressWarnings("serial")
public class Options extends HashMap<String, ArrayList<String>>
{
    //Has default constructor
    
    
    
    /**
     * Options in order of input
     */
    public final Vector<String> options = new Vector<String>();
    
    /**
     * Options' arguments in order of input, {@code null} is added on argumentless options
     */
    public final Vector<String> arguments = new Vector<String>();
    
    /**
     * Files in order of input
     */
    public final ArrayList<String> files = new ArrayList<String>();
    
    /**
     * Unrecognised options in order of input
     */
    public final Vector<String> unrecognised = new Vector<String>();
    
    
    
    /**
     * Parse options
     * 
     * @param   masterArgumented    Array of matrices akin to the <tt>argumented</tt>-matrix, except it is only used if no options as been fetched yet, and
     *                              even indexed (zero-based) matrices are one row matrices that if matched, the next (odd index) matrix is merged into
     *                              <tt>argumented</tt>. Naturally the match will be registrered as an used option.
     * @param   masterArgumentless  Just like <tt>masterArgumented</tt>, except it contains options without arguments
     * @param   argumented          Matrix (irregular) of all common options with arguments, each row (first dimension) describes options, for each cell in a row,
     *                              a synonym option is put. The first column contains all standard synonym, those that is used in the code when reading the data.
     * @param   argumentless        Just like <tt>argumented</tt>, except it contains the common options without arguments
     * @param   args                All passed arguments, excluding the name of executable
     * @return                      An instance of this class describing the options and arguments
     */
    @requires("java-runtime>=6")
    public static Options get(final String[][][] masterArgumented, final String[][][] masterArgumentless, final String[][] argumented, final String[][] argumentless, final String[] args)
    {
        final Options rc = new Options();
        
        final HashMap<String, String>     argumentlessMap       = new HashMap<String, String>();
        final HashMap<String, String>     argumentedMap         = new HashMap<String, String>();
        final HashMap<String, String[][]> masterArgumentedMap   = new HashMap<String, String[][]>();
        final HashMap<String, String[][]> masterArgumentlessMap = new HashMap<String, String[][]>();
        final HashMap<String, String>     masterArgumentPreMap  = new HashMap<String, String>();
        
        if (argumentless != null)  for (final String[] array : argumentless)  for (final String element : array)  argumentlessMap.put(element, array[0]);
        if (argumented   != null)  for (final String[] array : argumented)    for (final String element : array)  argumentedMap  .put(element, array[0]);
        
        if (masterArgumented != null)
            for (int i = 0, n = masterArgumented.length; i < n; i += 2)
            {   final String[] array = masterArgumented[i][0];    for (final String element : array)  masterArgumentPreMap.put(element, array[0]);
                masterArgumentedMap  .put(array[0], masterArgumented[i | 1]);
            }
        if (masterArgumentless != null)
            for (int i = 0, n = masterArgumentless.length; i < n; i += 2)
            {   final String[] array = masterArgumentless[i][0];  for (final String element : array)  masterArgumentPreMap.put(element, array[0]);
                masterArgumentlessMap.put(array[0], masterArgumentless[i | 1]);
            }
        
        final ArrayDeque<String> deque = new ArrayDeque<String>();
        for (final String arg : args)
            deque.offerLast(arg);
        
        boolean dashed = false;     /*  --  */
        boolean tmpdashed = false;  /*  ++  */
        int get = 0;
        int dontget = 0;
        
        for (String arg, tmp; (arg = deque.pollFirst()) != null;)
            if ((get > 0) && (dontget == 0))
            {   get--;
                rc.arguments.add(arg);
            }
            else if (tmpdashed)
            {   rc.files.add(arg);
                tmpdashed = false;
            }
            else if (dashed)            rc.files.add(arg);
            else if (arg.equals("++"))  tmpdashed = true;
            else if (arg.equals("--"))  dashed = true;
            else if ((arg.startsWith("-") || arg.startsWith("+")) && (arg.length() != 1))
                if (((arg.startsWith("--") || arg.startsWith("++")) == false) && (arg.length() != 2))
                {
                    String sign = arg.substring(0, 1);
                    for (int i = arg.length() - 1; i >= 1; i--)
                        deque.offerFirst(sign + arg.charAt(i));
                    dontget = arg.length() - 1;
                }
                else
                {
                    if (dontget > 0)
                        dontget--;
                    if (rc.options.isEmpty() && masterArgumentPreMap.containsKey(arg))
                    {
                        for (final String[] array : masterArgumentedMap  .get(masterArgumentPreMap.get(arg)))
                            for (final String element : array)  argumentedMap  .put(element, array[0]);
                        for (final String[] array : masterArgumentlessMap.get(masterArgumentPreMap.get(arg)))
                            for (final String element : array)  argumentlessMap.put(element, array[0]);
                        rc.options.add(argumentlessMap.get(arg));
                        rc.arguments.add(null);
                    }
                    else if (argumentlessMap.containsKey(arg))
                    {   rc.options.add(argumentlessMap.get(arg));
                        rc.arguments.add(null);
                    }
                    else if (arg.contains("=") && argumentedMap.containsKey(tmp = arg.substring(0, arg.indexOf("="))))
                    {   rc.options.add(argumentedMap.get(tmp));
                        rc.arguments.add(arg.substring(arg.indexOf("=") + 1));
                    }
                    else if ((arg.contains("=") == false) && argumentedMap.containsKey(arg))
                    {   rc.options.add(argumentedMap.get(arg));
                        get++;
                    }
                    else
                        rc.unrecognised.add(arg);
                }
            else
                rc.files.add(arg);
        
        for (int i = 0, n = rc.options.size(); i < n; i++)
        {   ArrayList<String> list = rc.get(rc.options.get(i));
            if (list == null)
                rc.put(rc.options.get(i), list = new ArrayList<String>());
            list.add(rc.arguments.get(i));
        }
        
        return rc;
    }
    
}

