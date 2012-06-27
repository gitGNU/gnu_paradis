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
package se.kth.maandree.paradis.plugin;
import se.kth.maandree.paradis.local.Properties; //Explicit

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;


/**
 * This class is used to enable plug-in support
 * 
 * @author   Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 * @version  1.0
 */
public class PluginHandler
{
    /**
     * The name of the plug-in classes
     */
    private static final String PLUGIN_CLASS_NAME = "Plugin";
    
    /**
     * The directory where the plug-ins are located
     */
    private static final String PLUGIN_DIR = "~/.paradis/plugins".replace("/", Properties.getFileSeparator()).replace("~", Properties.getHome());
    
    /**
     * The file where the settings are saved
     */
    private static final String PLUGINS_FILE = "~/.paradis/plugins.dat".replace("/", Properties.getFileSeparator()).replace("~", Properties.getHome());
    
    
    
    /**
     * Non-constructor
     */
    private PluginHandler()
    {
	assert false : "You may not create instances of this class [PluginHandler].";
    }
    
    
    
    /**
     * The plug-ins instanciated
     */
    private static ArrayList<PluginV1> pluginInstances = new ArrayList<PluginV1>();
    
    /**
     * The plug-in files
     */
    private static ArrayList<String> pluginFiles = new ArrayList<String>();
    
    /**
     * The active plug-ins
     */
    private static ArrayList<PluginV1> activePlugins = new ArrayList<PluginV1>();
    
    
    
    /**
     * Gets the count of plug-ins
     * 
     * @return  The count of plug-ins
     */
    public static int getPluginCount()
    {   return pluginInstances.size();
    }
    
    
    /**
     * Gets a plug-in by its index
     * 
     * @param   index  The index of the plug-in
     * @return         The plug-in
     */
    public static PluginV1 getPlugin(final int index)
    {   return pluginInstances.get(index);
    }
    
    
    /**
     * Gets whether a plug-in is activated
     *
     * @param   plugin  The index of the plug-in
     * @return          Whether the plug-in is activated
     */
    public static boolean isActive(final int plugin)
    {
        boolean yes = activePlugins.contains(pluginInstances.get(plugin));
        if (yes == false)
	{
            try
	    {
		String data = readFile(PLUGINS_FILE) + "\n";
		StringBuilder buf = new StringBuilder();
		String pluginClass = pluginFiles.get(plugin);
		
		loop:
		    for (int i = 0, n = data.length(); i < n; i++)
		    {
			char chr = data.charAt(i);
			switch (chr)
		        {
			    case '\n':
				if (buf.toString().equals(pluginClass))
				{   yes = true;
				    break loop;
				}
				buf = new StringBuilder();
				break;
			    default:
				buf.append(chr);
				break;
			}
		    }
	    }
            catch (final Throwable err)
	    {
		//TODO report error
	    }
	}
        return yes;
    }
    
    
    /**
     * Sets whether a plug-in is activated
     *
     * @param  plugin  The index of the plug-in
     * @param  active  Whether the plug-in should be active
     */
    public static void setActive(final int plugin, final boolean active)
    {
        if (isActive(plugin) ^ active)
	{
	    if (active)  activePlugins.add   (pluginInstances.get(plugin));
	    else         activePlugins.remove(pluginInstances.get(plugin));
	    
	    for (;;)
                try
		{
		    String data = "";
		    for (final PluginV1 p : activePlugins)
		    {
			int indexOf = pluginInstances.indexOf(p);
			String pluginFile = pluginFiles.get(indexOf);
			data += pluginFile + "\n";
		    }
		    
		    if (data.length() > 0)
			data = data.substring(0, data.length() - "\n".length());
		    
		    OutputStream os = null;
		    try
		    {   os = new BufferedOutputStream(new FileOutputStream(new File(PLUGINS_FILE)));
			os.write(data.getBytes("UTF-8"));
			os.flush();
		    }
		    finally
		    {   if (os != null)
			    try
			    {    os.close();
			    }
			    catch (final Throwable ignore)
			    {    //Ignore
		    }       }
		    
		    break;
		}
                catch (final Throwable err)
		{
		    //TODO report error
		}
	}
    }
    
    
    /**
     * Finds all plug-ins
     */
    public static void findPlugins()
    {
	final String fs;
        final String dir = PLUGIN_DIR + (fs = Properties.getFileSeparator());
	
        String[] files = (new File(PLUGIN_DIR)).list();
        for (final String file : files)
            if (file.toLowerCase().endsWith(".jar"))
                try
		{
		    String name = dir + file;
		    name = name.substring(name.lastIndexOf(fs) + fs.length());
		    name = name.substring(0, name.length() - ".jar".length());
		    
		    pluginInstances.add(getPluginInstance(name));
		    pluginFiles.add(name);
		}
                catch (final Exception err)
		{   //Do nothing
		}
    }
    
    
    /**
     * Gets the plug-in as an instance
     *
     * @param   name       The name of the plug-in (the package name)
     * @return             The plug-in as an instance
     * @throws  Exception  If the plug-in can't be loaded
     */
    private static PluginV1 getPluginInstance(final String name) throws Exception
    {
	final String dir = PLUGIN_DIR + Properties.getFileSeparator();
	
	String file = dir + name + ".jar";
	URLClassLoader sysLoader = (URLClassLoader)(ClassLoader.getSystemClassLoader());
	Class<URLClassLoader> sysclass = URLClassLoader.class;
	
	Method method = sysclass.getDeclaredMethod("addURL", URL.class);
	method.setAccessible(true);
	method.invoke(sysLoader, (new File(file)).toURI().toURL());
	
	String path = file;
	URL url = (new File(path)).toURI().toURL();
	
	URLClassLoader klassLoader = new URLClassLoader(new URL[]{url});
	
	@SuppressWarnings("unchecked")
	Class<PluginV1> klass = (Class<PluginV1>)(klassLoader.loadClass(name + "." + PLUGIN_CLASS_NAME));
	
	return klass.newInstance();
    }
    
    
    /**
     * <p>Starts the active plugins</p>
     * <p>
     *   Run only once before any plugin is activated
     * </p>
     */
    public static void startPlugins()
    {
        try
	{   for (int i = 0, n = getPluginCount(); i < n; i++)
		if (isActive(i))
		    getPlugin(i).initialize();
	}
        catch (final Throwable err)
	{
	    //TODO report error
	}
    }
    
    
    /**
     * Read a file as text
     * 
     * @param   file  The file
     * @return        The text in the file
     * 
     * @throws  IOException  On file reading error
     */
    private static String readFile(final String file) throws IOException
    {
	final String text;
	
	InputStream is = null;
	try
	{
	    is = new BufferedInputStream(new FileInputStream(new File(file)));
	    
	    final Vector<byte[]> bufs = new Vector<byte[]>();
	    int size = 0;
	    
	    for (int av; (av = is.available()) > 0;)
	    {
		byte[] buf = new byte[av];
		av = is.read(buf, 0, av);
		if (av < buf.length)
		{
		    final byte[] nbuf = new byte[av];
		    System.arraycopy(buf, 0, nbuf, 0, av);
		    buf = nbuf;
		}
		size += av;
		bufs.add(buf);
	    }
	    
	    final byte[] full = new byte[size];
	    int ptr = 0;
	    for (final byte[] buf : bufs)
	    {
		System.arraycopy(buf, 0, full, ptr, buf.length);
		ptr += buf.length;
	    }
	    
	    text = new String(full, "UTF-8");
	}
	finally
	{   if (is != null)
		try
		{   is.close();
		}
		catch (final Throwable ignore)
		{   //Ignore
	}	}
	
	return text;
    }
    
}

