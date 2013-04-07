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
package org.nongnu.paradis.demo;
import org.nongnu.paradis.plugin.*;

import java.util.*;


/**
 * Plugin system demo
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
public class PluginDemo
{
    /**
     * Non-constructor
     */
    private PluginDemo()
    {
        assert false : "You may not create instances of this class [PluginDemo].";
    }
    
    
    
    /**
     * This is the main entry point of the demo
     * 
     * @param  args  Startup arguments, unused
     * 
     * @throws  Exception  On error
     */
    public static void main(final String... args) throws Exception
    {
        try (final Scanner sc = new Scanner(System.in))
        {
            for (String line;;)
                if ((line = sc.nextLine()).equals("count"))
                    System.out.println("Number of plugins: " + PluginHandler.getPluginCount());
                else if (line.startsWith("get "))
                    System.out.println(PluginHandler.getPlugin(Integer.parseInt(line.substring("get ".length()))).getName());
                else if (line.startsWith("active "))
                    System.out.println(PluginHandler.isActive(Integer.parseInt(line.substring("active ".length()))));
                else if (line.startsWith("update "))
                    PluginHandler.updatePlugin(Integer.parseInt(line.substring("update ".length())));
                else if (line.startsWith("activate "))
                    PluginHandler.setActive(Integer.parseInt(line.substring("activate ".length())), true);
                else if (line.startsWith("deactivate "))
                    PluginHandler.setActive(Integer.parseInt(line.substring("deactivate ".length())), false);
                else if (line.equals("find"))
                {
                    final Vector<Integer> indices = PluginHandler.findPlugins();
                    System.out.print("Found plugins with indices:");
                    for (final Integer index : indices)
                        System.out.print("  " + index);
                    System.out.println();
                }
                else if (line.startsWith("start "))
                    PluginHandler.startPlugins();
                else if (line.isEmpty())
                    return;
        }
    }
    
}

