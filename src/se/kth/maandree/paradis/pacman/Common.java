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
import se.kth.maandree.paradis.io.*;
import se.kth.maandree.paradis.*;

import java.util.*;
import java.io.*;


/**
 * Common package manager methods
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
@requires("java-environment>=7")
public class Common
{
    /**
     * The directory where the packages are located
     */
    private static final String PACKAGE_DIR = Pacman.PACKAGE_DIR;
    
    /**
     * The file where the data are saved
     */
    private static final String PACKAGES_FILE = Pacman.PACKAGES_FILE;
    
    
    
    //Has default constructor
    
    
    
    public final Vector<VersionedPackage> databaseVector = new Vector<>();
    public final HashSet<String> databaseSet = new HashSet<>();
    public final HashMap<VersionedPackage, VersionedPackage> databaseMap = new HashMap<>();
    public final HashMap<String, String> packageMap = new HashMap<>();
    
    public final HashMap<VersionedPackage, VersionedPackage> installedMap = new HashMap<>();
    public final HashSet<VersionedPackage> installedExplicitly = new HashSet<>();
    
    
    
    public void loadDatabase()
    {
	final String[] packs = (new File(PACKAGE_DIR)).list();
	final VersionedPackage[] vpacks = new VersionedPackage[packs.length];
	for (int i = 0, n = vpacks.length; i < n; i++)
	{
	    final String p;
	    if ((p = packs[i]).endsWith(".pkg.xz") == false)
		continue;
	    vpacks[i] = new VersionedPackage(p);
	    this.packageMap.put(vpacks[i].toString(), p.substring(0, p.length() - ".pkg.xz".length()));
	}
	Arrays.sort(vpacks);
	for (final VersionedPackage pack : vpacks)
	{
	    this.databaseVector.add(pack);
	    this.databaseSet.add(pack.toString());
	    this.databaseMap.put(pack, pack);
	}
    }
    
    
    public void loadInstalled() throws IOException
    {
	try (final TransferInputStream tis = new TransferInputStream(new FileInputStream(PACKAGES_FILE)))
	{   for (;;)
	    {
		final String pack = tis.readObject(String.class);
		if (pack.isEmpty())
		    break;
		final VersionedPackage vpack = new VersionedPackage(pack);
		this.installedMap.put(vpack, vpack);
		if (tis.readBoolean())
		    this.installedExplicitly.add(vpack);
	}   }
	catch (final FileNotFoundException ignore)
	{   //Ignore
	}
    }
    
}

