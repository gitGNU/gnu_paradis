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
package se.kth.maandree.paradis.net;
import se.kth.maandree.paradis.local.*;


/**
 * Network configurations handler
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class NetConf
{
    /**
     * The hive for the configurations managed by this class
     */
    private static final String HIVE = "paradis.net";
    
    
    
    /**
     * Non-constructor
     */
    private NetConf()
    {
	assert false : "You may not create instances of this class [NetConf].";
    }
    
    
    
    /**
     * Class initialiser
     */
    static
    {
	createDefaults();
    }
    
    
    
    /**
     * Create default values for all missing settings
     */
    public static void createDefaults()
    {
	Configurations.defaultSetting(HIVE, "timeout", "4");
	Configurations.defaultSetting(HIVE, "connections", "100");
    }
    
    
    /**
     * Gets the ping timeout time in seconds
     * 
     * @return  The timeout time
     */
    public static int getTimeout()
    {
	final String value = Configurations.getSetting(HIVE, "timeout");
	int rc = 0;
	try
	{   rc = Integer.parseInt(value);
	}
	catch (final Throwable err)
	{   System.err.println("Unparsable value for timeout setting: " + value);
	    System.err.println("Timeout set to default: 4");
	    setTimeout(rc = 4);
	}
	if (rc <= 0)
	{   System.err.println("Negative timeout is not allowed: " + value);
	    System.err.println("Timeout set to default: 4");
	    setTimeout(rc = 4);
	}
	else if (rc >= 1000)
	{   System.err.println("Timeout is extremely high: " + value);
	    System.err.println("Timeout set to default: 4");
	    setTimeout(rc = 4);
	}
	else if (rc > 60)
	{   System.err.println("Timeout is too high for system: " + value);
	    System.err.println("Timeout set to default: 4");
	    setTimeout(rc = 4);
	}
	else if (rc >= 20)
	{   System.err.println("Timeout is very high: " + value);
	    System.err.println("Timeout set to default: 4");
	    setTimeout(rc = 4);
	}
	return rc;
    }
    
    /**
     * Sets the ping timeout time
     * 
     * @param  value  The new timeout time in seconds
     */
    public static void setTimeout(final int value)
    {
	Configurations.setSetting(HIVE, "timeout", Integer.toString(value));
    }
    
    
    /**
     * Gets the ping timeout time in seconds
     * 
     * @return  The timeout time
     */
    public static int getConnections()
    {
	final String value = Configurations.getSetting(HIVE, "connections");
	int rc = 0;
	try
	{   rc = Integer.parseInt(value);
	}
	catch (final Throwable err)
	{   System.err.println("Unparsable value for number of connections setting: " + value);
	    System.err.println("Number of connections to default: 100");
	    setTimeout(rc = 100);
	}
	if (rc <= 0)
	{   System.err.println("Negative number of connections is not allowed: " + value);
	    System.err.println("Number of connections set to default: 100");
	    setTimeout(rc = 100);
	}
	else if (rc > 1200)
	{   System.err.println("Number of connections is very high: " + value);
	    System.err.println("Number of connections set to default: 100");
	    setTimeout(rc = 100);
	}
	return rc;
    }
    
    /**
     * Sets the ping timeout time
     * 
     * @param  value  The new timeout time in seconds
     */
    public static void setConnections(final int value)
    {
	Configurations.setSetting(HIVE, "connections", Integer.toString(value));
    }
    
}

