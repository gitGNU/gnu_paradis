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
package se.kth.maandree.paradis.pacman;
import se.kth.maandree.paradis.local.Properties; //Explicit
import se.kth.maandree.paradis.io.*;
import se.kth.maandree.paradis.*;

import java.util.*;
import java.io.*;


/**
 * -T invocation of the package manager
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PacmanDeptest implements Blackboard.BlackboardObserver //FIXME fix sorting (comparison)
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
    
    
    
    /**
     * Package name with verion or version bounds
     */
    public static class VersionedPackage
    {
	/**
	 * System file separator
	 */
	private final String FILE_SEPARATOR = Properties.getFileSeparator();
	
	
	
	/**
	 * Constructor
	 * 
	 * @param  pkg  Package with version or version bounds, may be a file name
	 */
	public VersionedPackage(final String pkg)
	{
	    String _pkg = pkg;
	    if (_pkg.contains(FILE_SEPARATOR))
		_pkg = _pkg.substring(_pkg.lastIndexOf(FILE_SEPARATOR) + FILE_SEPARATOR.length());
	    if (_pkg.endsWith(".pkg.xz"))
		_pkg = _pkg.substring(0, _pkg.length() - ".pkg.xz".length());
	    else if (_pkg.endsWith(".pkg"))
		_pkg = _pkg.substring(0, _pkg.length() - ".pkg".length());
	    
	    // name>low  name>=low  name=both  name<=high  name<high  name
	    // low<=name<=high  low<=name<high  low<name<=high  low<name<high
	    // high>=name>=low  high>=name>low  high>name>=low  high>name>low
	    
	    final String[] parts = _pkg.replace("<", "=").replace(">", "=").replace("==", "=").split("=");
	    if (parts.length == 3)
	    {
		this.name = parts[1];
		final String versionA = parts[0].replace(";", ":");
		final String versionB = parts[2].replace(";", ":");
		final String opA = _pkg.substring(versionA.length(), _pkg.indexOf(this.name, versionA.length()) - versionA.length());
		String opB = _pkg.substring(versionA.length() + opA.length() + this.name.length());
		opB = opB.substring(0, opB.length() - versionB.length());
		if (opA.contains(">") && opB.contains(">"))
		{
		    this.highClosed = opA.contains("=");
		    this.lowClosed = opB.contains("=");
		    this.high = versionA;
		    this.low = versionB;
		}
		else
		{
		    this.lowClosed = opA.contains("=");
		    this.highClosed = opB.contains("=");
		    this.low = versionA;
		    this.high = versionB;
		}
	    }
	    else if (parts.length == 2)
	    {
		this.name = parts[0];
		final String version = parts[1].replace(";", ":");
		final String op = _pkg.substring(this.name.length(), _pkg.length() - this.name.length() - parts[1].length());
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
		this.name = _pkg;
		this.low = this.high = null;
		this.lowClosed = this.highClosed = false;
	    }
	}
	
	
	
	/**
	 * Package name
	 */
	private final String name;
	
	/**
	 * Package version lower bound, {@code null} if unbounded
	 */
	private final String low;
	
	/**
	 * Package version upper bound, {@code null} if unbounded
	 */
	private final String high;
	
	/**
	 * Whether the lower version bound is closed
	 */
	private final boolean lowClosed;
	
	/**
	 * Whether the ypper version bound is closed
	 */
	private final boolean highClosed;
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object other)
	{
	    if ((other == null) || (other instanceof VersionedPackage == false))
		return false;
	    if (other == this)
		return true;
	    return this.name.equals(((VersionedPackage)other).name);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
	    return this.name.hashCode();
	}
	
	
	/**
	 * Test whether one instance satisficies another
	 * 
	 * @param   other  The other isntance
	 * @return         The result of the test
	 */
	public boolean intersects(final VersionedPackage other)
	{
	    if (other == null)
		return false;
	    if (other == this)
		return true;
	    if (this.name.equals(other.name) == false)
		return false;
	    
	    if (((this.low == null) && (this.high == null)) || ((other.low == null) && (other.high == null)))
		return true;
	    
	    if (((this.low == null) ^ (this.high == null)) && ((other.low == null) ^ (other.high == null)) && ((this.low == null) == (other.low == null)))
		return true;
	    
	    if ((this.low != null) && (this.high == null) && (other.high != null))
	    {   int comp = this.low.compareTo(other.high);
		return comp == 0 ? (this.lowClosed && other.highClosed) : (comp < 0);
	    }
	    
	    if ((this.low == null) && (this.high != null) && (other.low != null))
	    {   int comp = other.low.compareTo(this.high);
		return comp == 0 ? (other.lowClosed && this.highClosed) : (comp < 0);
	    }
	    
	    if ((this.low != null) && (this.high != null) && (other.low == null) && (other.high != null))
	    {   int comp = this.low.compareTo(other.high);
		return comp == 0 ? (this.lowClosed && other.highClosed) : (comp < 0);
	    }
	    
	    if ((this.low == null) && (this.high != null) && (other.low != null) && (other.high != null))
	    {   int comp = other.low.compareTo(this.high);
		return comp == 0 ? (other.lowClosed && this.highClosed) : (comp < 0);
	    }
	    
	    final boolean olc = other. lowClosed;
	    final boolean tlc = this . lowClosed;
	    final boolean thc = this .highClosed;
	    final boolean ohc = other.highClosed;
	    
	    int oltl = other.low .compareTo(this .low );  if (oltl == 0)  oltl = (olc && tlc) ? -1 : 0;
	    int olth = other.low .compareTo(this .high);  if (olth == 0)  olth = (olc && thc) ? -1 : 0;
	    int tloh = this .low .compareTo(other.high);  if (tloh == 0)  tloh = (tlc && ohc) ? -1 : 0;
	    int thoh = this .high.compareTo(other.high);  if (thoh == 0)  thoh = (thc && ohc) ? -1 : 0;
	    int tlol = this .low .compareTo(other.low );  if (tlol == 0)  tlol = (tlc && olc) ? -1 : 0;
	    int ohth = other.high.compareTo(this .high);  if (ohth == 0)  ohth = (ohc && thc) ? -1 : 0;
	    
	    if ((oltl < 0) && (tloh < 0))  return true;
	    if ((olth < 0) && (thoh < 0))  return true;
	    if ((tlol < 0) && (olth < 0))  return true;
	    if ((tloh < 0) && (ohth < 0))  return true;
	    return false;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
	    if ((this.low == null) && (this.high == null))
		return this.name;
	    if (this.low == null)   return this.name + (this.highClosed ? "<=" : "<") + this.high;
	    if (this.high == null)   return this.name + (this.lowClosed ? ">=" : ">") + this.low;
	    return this.low + (this.lowClosed ? "<=" : "<") + this.name + (this.highClosed ? "<=" : "<") + this.high;
	}
	
    }
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    @requires({"java-runtime>=6", "java-environment>=7"})
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
	    }   }
	    
	    final HashMap<String, ArrayList<String>> groupmap = new HashMap<String, ArrayList<String>>();
	    for (final String pack : enversionmap.values())
		for (final String group : PackageInfo.fromFile(PACKAGE_DIR + pack + ".pkg.xz").groups)
		{   ArrayList<String> list = groupmap.get(group);
		    if (list == null)
			groupmap.put(group, list = new ArrayList<String>());
		    list.add(pack);
		}
	    
	    final HashMap<VersionedPackage, VersionedPackage> provided = new HashMap<VersionedPackage, VersionedPackage>();
	    final HashMap<VersionedPackage, VersionedPackage> conflict = new HashMap<VersionedPackage, VersionedPackage>();
	    VersionedPackage tmp;
	    
	    for (final String pack : installed.keySet)
	    {   final PackageInfo info = PackageInfo.fromFile(PACKAGE_DIR + pack + ".pkg.xz");
		provided.put(tmp = new VersionedPackage(pack), tmp);
		for (final String p : info.provides)   provided.put(tmp = new VersionedPackage(p), tmp);
		for (final String c : info.conflicts)  conflict.put(tmp = new VersionedPackage(c), tmp);
	    }
	    
	    final ArrayDeque<String> $packages = new ArrayDeque<String>();
	    for (final String pack : packages)
		if (groupmap.contains(pack) == false)
		    $packages.offerLast(pack);
		else
		    for (final String pac : groupmap.get(pack))
			$packages.offerLast(pac);
	    
	    while ($packages.isEmpty() == false) //FIMXE resolve updates
	    {   final String pac = $packages.pollFirst();
		final String pack = pac.contains("=") ? pac : enversionmap.get(pac);
		if (provided.get(tmp = new VersionedPackage(pack)) != null)
		{
		    if (tmp.intersects(provided.get(tmp)))
			continue;
		    System.out.println(pack + " is in version conflict with " + conflict.get(tmp).toString());
		    return;
		}
		final PackageInfo info = PackageInfo.fromFile(PACKAGE_DIR + pack + ".pkg.xz");
		
		if ((tmp = new VersionedPackage(pack)).intersects(conflict.get(tmp)))
		{   System.out.println(pack + " conflicts with " + conflict.get(tmp).toString());
		    return;
		}
		provided.put(tmp = new VersionedPackage(pack), tmp);
		for (final String p : info.provides)
		{   if ((tmp = new VersionedPackage(pack)).intersects(conflict.get(tmp)))
		    {   System.out.println(pack + " conflicts with " + conflict.get(tmp).toString());
			return;
		    }
		    provided.put(tmp = new VersionedPackage(p), tmp);
		}
		for (final String c : info.conflicts)
		    conflict.put(tmp = new VersionedPackage(c), tmp);
		
		for (final String d : info.dependencies) //FIXME multiple choose dependencies
		    $packages.offerLast(dep);
	    }
	    
	    System.out.println("Passes depedency test");
	    return;
	}
	catch (final Throwable err)
        {   System.err.println(err.toString());
	    System.out.println("Could not load information needed to test dependencies.");
	}
    }
    
    
}

