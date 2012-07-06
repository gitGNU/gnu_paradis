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
import se.kth.maandree.paradis.io.*;
import se.kth.maandree.paradis.*;

import java.util.*;
import java.io.*;


/**
 * -T invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PacmanDeptest implements Blackboard.BlackboardObserver
{
    /**
     * The directory where the packages are located
     */
    private static final String PACKAGE_DIR = Pacman.PACKAGE_DIR;
    
    /**
     * The file where the data are saved
     */
    private static final String PACKAGES_FILE = Pacman.PACKAGES_FILE;
    
    
    /** Return missing dependencies and conflicts
     */ private static final String DEPTEST = Pacman.DEPTEST;
    
    
    
    //Has default constructor
    
    
    
    public static class VersionedPackage
    {
	public VersionedPackage(final String pkg)
	{
	    // name>low  name>=low  name=both  name<=high  name<high  name
	    // low<=name<=high  low<=name<high  low<name<=high  low<name<high
	    // high>=name>=low  high>=name>low  high>name>=low  high>name>low
	    
	    final String[] parts = pkg.replace("<", "=").replace(">", "=").replace("==", "=").split("=");
	    if (parts.length == 3)
	    {
		this.name = parts[1];
		final String versionA = parts[0].replace(";", ":");
		final String versionB = parts[2].replace(";", ":");
		final String opA = pkg.substring(versionA.length(), pkg.indexOf(this.name, versionA.length()) - versionA.length());
		String opB = pkg.substring(versionA.length() + opA.length() + this.name.length());
		opB = opB.substring(0, opB.length() - versionB.length());
	    }
	    else if (parts.length == 2)
	    {
		this.name = parts[0];
		final String version = parts[1].replace(";", ":");
		final String op = pkg.substring(this.name.length(), pkg.length() - this.name.length() - parts[1].length());
		if (op.equals("<"))
		{
		    this.lowClosed = this.highClosed = false;
		    this.low = null;
		    this.high = version;
		}
		else if (op.equals("<="))
		{
		    this.lowClosed = (this.highClosed = true) == false;
		    this.low = null;
		    this.high = version;
		}
		else if (op.equals(">="))
		{
		    this.lowClosed = (this.highClosed = false) == false;
		    this.high = null;
		    this.low = version;
		}
		else if (op.equals(">"))
		{
		    this.lowClosed = this.highClosed = false;
		    this.high = null;
		    this.low = version;
		}
		else
		{
		    this.lowClosed = this.highClosed = true;
		    this.low = this.high = version;
		}
	    }
	    else
	    {
		this.low = this.high = null;
		this.lowClosed = this.highClosed = false;
	    }
	}
	
	
	
	private final String name;
	private final String low;
	private final String high;
	private final boolean lowClosed;
	private final boolean highClosed;
    }
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
    {
	if ((message instanceof Pacman.PacmanInvoke == false) || (((Pacman.PacmanInvoke)message).masteropt.equals(DEPTEST) == false))
	    return;
	final ArrayList<String> packages = ((Pacman.PacmanInvoke)message).packages;
	
	try
	{
	    final HashMap<String, String> installed = new HashMap<String, String>();
	    try (final TransferInputStream tis = new TransferInputStream(new FileInputStream(new File(PACKAGES_FILE))))
	    {   for (;;)
		{
		    final String pack = tis.readObject(String.class);
		    if (pack.isEmpty())
			break;
		    final String _pack = pack.substring(0, pack.lastIndexOf("="));
		    installed.put(_pack, pack);
	    }   }
	    
	    final HashMap<String, String> enversionmap = new HashMap<String, String>();
	    {   final String[] packs = (new File(PACKAGE_DIR)).list();
		Arrays.sort(packs);
		for (final String pack : packs)
	        {
		    if (pack.endsWith(".pkg.xz") == false)
			continue;
		    final String $pack = pack.substring(0, pack.length() - ".pkg.xz".length());
		    enversionmap.put($pack.substring(0, $pack.lastIndexOf("=")), $pack);
		}
	    }
	    
	    final HashMap<String, String> provided = new HashMap<String, String>();
	    final HashMap<String, String> conflict = new HashMap<String, String>();
	    for (final String pac : packages)
	    {
		final String pack = pac.contains("=") ? pac : enversionmap.get(pac);
		final PackageInfo info = PackageInfo.fromFile(PACKAGE_DIR + pack + ".pkg.xz");
	    }
	}
	catch (final Throwable err)
        {   System.err.println(err.toString());
	    System.out.println("Could not load information needed to test dependencies.");
	}
    }
    
    
}

