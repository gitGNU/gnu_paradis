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
import se.kth.maandree.paradis.file.ini.*;
import se.kth.maandree.paradis.*;

import java.io.*;


/**
 * Paradis configurations
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Configurations
{
    private static final String FILE = "~/.paradis/paradis.conf".replace("/", Properties.getFileSeparator()).replace("~", Properties.getHome());
    
    
    
    /**
     * Non-constructor
     */
    private Configurations()
    {
	assert false : "You may not create instances of this class [Configurations].";
    }
    
    
    
    /**
     * Class initialiser
     */
    static
    {
	String content = "";
	if ((new File(FILE)).exists())
	    try
	    {   content = readFile(FILE);
	    }
	    catch (final Throwable ignore)
	    {   //Ignore
	    }
	
	conf = new INI(FILE, content);
	conf.refreshContentTree();
    }
    
    
    
    /**
     * Configurations
     */
    public static final INI conf;
    
    
    
    /**
     * Gets a setting value
     * 
     * @param   hive  The name of the hive
     * @param   key   The name of the key
     * @return        The value of the hive–key pair
     */
    public String getSetting(final String hive, final String key)
    {
	return conf.getKeyValue(hive, key);
    }
    
    
    /**
     * Sets a setting value
     * 
     * @param  hive   The name of the hive
     * @param  key    The name of the key
     * @param  value  The new value of the hive–key pair
     */
    public void setSetting(final String hive, final String key, final String value)
    {
	conf.createKey(hive, key, value, true);
    }
    
    
    /**
     * Sets a setting value if no value is set
     * 
     * @param  hive           The name of the hive
     * @param  key            The name of the key
     * @param  defaultValue   The default of the hive–key pair
     */
    public void defaultSetting(final String hive, final String key, final String defaultValue)
    {
	if (getSetting(hive, key) == null)
	    setSetting(hive, key, defaultValue);
    }
    
    
    /**
     * Saves the configurations
     * 
     * @throws  IOException  On file writing error
     */
    public static void save() throws IOException
    {
	OutputStream os = null;
	try
	{
	    os = new BufferedOutputStream(new FileOutputStream(new File(FILE)));
	    os.write(conf.getSaveString().getBytes("UTF-8"));
	    os.flush();
	}
	finally
	{   if (os != null)
		try
		{   os.close();
		}
		catch (final Throwable ignore)
		{   //Ignore
	}	}
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

