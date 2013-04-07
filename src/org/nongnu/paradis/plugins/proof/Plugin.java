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
package org.nongnu.paradis.plugins.proof;
import org.nongnu.paradis.plugin.*;

import javax.swing.ImageIcon;


/**
 * Proof-of-concept plug-in
 * 
 * @author   Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
public class Plugin implements PluginV1
{
    //Has defualt constructor
    
    
    
    /**
     * Class initialiser
     */
    static
    {
        System.out.println("Touching se.kth.maandree.paradis.plugins.proof for the first time");
    }
    
    
    
    /**
     * Gets the plug-in's icon, should be 32x32
     * 
     * @return  The plug-in's icon, <code>null</code> if missing
     */
    @Override
    public ImageIcon getIcon()
    {   return null;
    }
    
    /**
     * Gets the plug-in's icon, should be 8x8
     * 
     * @return  The plug-in's icon, <code>null</code> if missing
     */
    @Override
    public ImageIcon getMiniIcon()
    {   return null;
    }
    
    /**
     * Gets the plug-in's icon, should be 16x16
     * 
     * @return  The plug-in's icon, <code>null</code> if missing
     */
    @Override
    public ImageIcon getSmallIcon()
    {   return null;
    }
    
    /**
     * Gets the plug-in's icon, should be 48x48
     * 
     * @return  The plug-in's icon, <code>null</code> if missing
     */
    @Override
    public ImageIcon getBigIcon()
    {   return null;
    }
    
    /**
     * Gets the plug-in's icon, should the biggest available raster image
     * 
     * @return  The plug-in's icon, <code>null</code> if raster image is missing
     */
    @Override
    public ImageIcon getBiggestIcon()
    {   return null;
    }
    
    /**
     * Gets the plug-in's icon
     * 
     * @param   dimension  The width and height of the icon
     * @return             The plug-in's icon, <code>null</code> if vector image is missing
     */
    @Override
    public ImageIcon getCustomIcon(final int dimension)
    {   return null;
    }
    
    /**
     * Gets the plug-in's name
     * 
     * @return  The plug-in's name
     */
    @Override
    public String getName()
    {   return "proof-of-concept";
    }
    
    /**
     * Gets the plug-in's description
     * 
     * @return  The plug-in's description
     */
    @Override
    public String getDescription()
    {   return "Proof-of-concept plug-in";
    }
    
    /**
     * Gets the version of the plug-in
     * 
     * @return  The version of the plug-in
     */
    @Override
    public String getVersion()
    {   return "1.0";
    }
    
    /**
     * Initialises the plug-in
     */
    @Override
    public void initialise()
    {   System.out.println("Initialising se.kth.maandree.paradis.plugins.proof");
    }
    
    /**
     * Terminates the plug-in
     */
    @Override
    public void terminate()
    {   System.out.println("Terminating se.kth.maandree.paradis.plugins.proof");
    }
    
}

