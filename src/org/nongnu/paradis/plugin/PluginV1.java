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
package org.nongnu.paradis.plugin;

import javax.swing.ImageIcon;


/**
 * Plug-ins must implement this interface or a newer version.<br/>
 * Public nullary (default) construct.<br/>
 * Implemention class should be named {@code Plugin}.
 * 
 * @author   Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 * @version  1.0
 * @since    {@link PluginHandler} version 1.0
 */
public interface PluginV1
{
    /**
     * Gets the plug-in's icon, should be 32x32
     * 
     * @return  The plug-in's icon, <code>null</code> if missing
     */
    public ImageIcon getIcon();
    
    /**
     * Gets the plug-in's icon, should be 8x8
     * 
     * @return  The plug-in's icon, <code>null</code> if missing
     */
    public ImageIcon getMiniIcon();
    
    /**
     * Gets the plug-in's icon, should be 16x16
     * 
     * @return  The plug-in's icon, <code>null</code> if missing
     */
    public ImageIcon getSmallIcon();
    
    /**
     * Gets the plug-in's icon, should be 48x48
     * 
     * @return  The plug-in's icon, <code>null</code> if missing
     */
    public ImageIcon getBigIcon();
    
    /**
     * Gets the plug-in's icon, should the biggest available raster image
     * 
     * @return  The plug-in's icon, <code>null</code> if raster image is missing
     */
    public ImageIcon getBiggestIcon();
    
    /**
     * Gets the plug-in's icon
     * 
     * @param   dimension  The width and height of the icon
     * @return             The plug-in's icon, <code>null</code> if vector image is missing
     */
    public ImageIcon getCustomIcon(final int dimension);
    
    /**
     * Gets the plug-in's name
     * 
     * @return  The plug-in's name
     */
    public String getName();
    
    /**
     * Gets the plug-in's description
     * 
     * @return  The plug-in's description
     */
    public String getDescription();
    
    /**
     * Gets the version of the plug-in
     * 
     * @return  The version of the plug-in
     */
    public String getVersion();
    
    /**
     * Initialises the plug-in
     */
    public void initialise();
    
    /**
     * Terminates the plug-in
     */
    public void terminate();
    
}

