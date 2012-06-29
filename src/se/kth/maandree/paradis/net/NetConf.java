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
	Configurations.defaultSetting(HIVE, "delaytime", "5000");
	Configurations.defaultSetting(HIVE, "delatlimit", "20");
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
	{   System.err.println("Non-positive timeout is not allowed: " + value);
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
     * Gets the connection cache size in units
     * 
     * @return  The connection cache size
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
	    setConnections(rc = 100);
	}
	if (rc <= 0)
	{   System.err.println("Non-positive of connections is not allowed: " + value);
	    System.err.println("Number of connections set to default: 100");
	    setConnections(rc = 100);
	}
	else if (rc > 1200)
	{   System.err.println("Number of connections is very high: " + value);
	    System.err.println("Number of connections set to default: 100");
	    setConnections(rc = 100);
	}
	return rc;
    }
    
    /**
     * Sets the connection cache size
     * 
     * @param  value  The new connection cache size in units
     */
    public static void setConnections(final int value)
    {
	Configurations.setSetting(HIVE, "connections", Integer.toString(value));
    }
    
    
    /**
     * Gets the connection cache save delay time in milliseconds
     * 
     * @return  The connection cache save delay time
     */
    public static int getDelayTime()
    {
	final String value = Configurations.getSetting(HIVE, "delaytime");
	int rc = 0;
	try
	{   rc = Integer.parseInt(value);
	}
	catch (final Throwable err)
	{   System.err.println("Unparsable value for save delay setting: " + value);
	    System.err.println("Save delay set to default: 5000");
	    setDelayTime(rc = 5000);
	}
	if (rc <= 0)
	{   System.err.println("Non-positive save delay is not allowed: " + value);
	    System.err.println("Save delay set to default: 5000");
	    setDelayTime(rc = 5000);
	}
	else if (rc < 500)
	{   System.err.println("Save delay is extremely low: " + value);
	    System.err.println("Save delay set to default: 5000");
	    setDelayTime(rc = 5000);
	}
	else if (rc > 60000)
	{   System.err.println("Save delay is too high for system: " + value);
	    System.err.println("Save delay set to default: 5000");
	    setDelayTime(rc = 5000);
	}
	else if (rc >= 30000)
	{   System.err.println("Save delay is very high: " + value);
	    System.err.println("Save delay set to default: 5000");
	    setDelayTime(rc = 5000);
	}
	return rc;
    }
    
    /**
     * Sets the connection cache save delay time
     * 
     * @param  value  The new connection cache save delay time in milliseconds
     */
    public static void setDelayTime(final int value)
    {
	Configurations.setSetting(HIVE, "delaytime", Integer.toString(value));
    }
    
    
    /**
     * Gets the connection cache save delay limit in repeations
     * 
     * @return  The connection cache save delay limit
     */
    public static int getDelayLimit()
    {
	final String value = Configurations.getSetting(HIVE, "delaylimit");
	int rc = 0;
	try
	{   rc = Integer.parseInt(value);
	}
	catch (final Throwable err)
	{   System.err.println("Unparsable value for save delay limit setting: " + value);
	    System.err.println("Save delay limit set to default: 20");
	    setDelayLimit(rc = 20);
	}
	if (rc <= 0)
	{   System.err.println("Non-positive save delay limit is not allowed: " + value);
	    System.err.println("Save delay limit set to default: 20");
	    setDelayLimit(rc = 20);
	}
	else if (rc > 1000)
	{   System.err.println("Save delay limit is extremely high: " + value);
	    System.err.println("Save delay limit set to default: 20");
	    setDelayLimit(rc = 20);
	}
	return rc;
    }
    
    /**
     * Sets the connection cache save delay limit
     * 
     * @param  value  The new connection cache save delay limit in repeations
     */
    public static void setDelayLimit(final int value)
    {
	Configurations.setSetting(HIVE, "delaylimit", Integer.toString(value));
    }
    
}

