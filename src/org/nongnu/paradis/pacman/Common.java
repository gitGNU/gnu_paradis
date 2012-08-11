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
package org.nongnu.paradis.pacman;
import org.nongnu.paradis.local.Properties; //Explicit
import org.nongnu.paradis.io.*;
import org.nongnu.paradis.*;

import com.ice.tar.*;
import org.tukaani.xz.*;

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
    
    /**
     * File root for installed files
     */
    public static final String FILE_ROOT = Pacman.FILE_ROOT;
    
    
    
    //Has default constructor
    
    
    
    /**
     * Vector of all available packages
     */
    public final Vector<VersionedPackage> databaseVector = new Vector<>();
    
    /**
     * Set of all available packages
     */
    public final HashSet<String> databaseSet = new HashSet<>();
    
    /**
     * Map from packages to latest versions of the packages
     */
    public final HashMap<VersionedPackage, VersionedPackage> databaseMap = new HashMap<>();
    
    /**
     * Map from packages to files
     */
    public final HashMap<String, File> packageMap = new HashMap<>();
    
    
    /**
     * Map from packages to installed versions of the packages
     */
    public final HashMap<VersionedPackage, VersionedPackage> installedMap = new HashMap<>();
    
    /**
     * Set of explicitly installed packages
     */
    public final HashSet<VersionedPackage> installedExplicitly = new HashSet<>();
    
    /**
     * Map from group to packages
     */
    public final HashMap<String, Vector<VersionedPackage>> groupMap = new HashMap<>();
    
    /**
     * Map from logical (or physical) package to physical packages
     */
    public final HashMap<VersionedPackage, HashSet<VersionedPackage>> provideMap = new HashMap<>();
    
    /**
     * Map from replacees to replacers
     */
    public final HashMap<VersionedPackage, VersionedPackage> replaceMap = new HashMap<>();
    
    
    
    /**
     * Populates members for the package database
     */
    public void loadDatabase()
    {
        loadDatabase(null);
    }
    
    /**
     * Populates members for the package database
     * 
     * @param  filter  File filter
     */
    public void loadDatabase(final FilenameFilter filter)
    {
        final String[] packs = filter == null ? (new File(PACKAGE_DIR)).list() : (new File(PACKAGE_DIR)).list(filter);
        final VersionedPackage[] vpacks = new VersionedPackage[packs.length];
        for (int i = 0, n = vpacks.length; i < n; i++)
        {
            final String p;
            if ((p = packs[i]).endsWith(".pkg.xz") == false)
                continue;
            vpacks[i] = new VersionedPackage(p);
            this.packageMap.put(vpacks[i].toString(), new File(PACKAGE_DIR + p));
        }
        Arrays.sort(vpacks);
        final HashSet<String> done = new HashSet<String>();
        for (final String item : this.packageMap.keySet())
            done.add(item);
        for (final VersionedPackage pack : vpacks)
        {
            this.databaseVector.add(pack);
            this.databaseSet.add(pack.toString());
            this.databaseMap.put(pack, pack);
            if (done.contains(pack.name) == false)
                this.packageMap.put(pack.name, this.packageMap.get(packs.toString()));
        }
    }
    
    
    /**
     * Populates members for the installed packaged
     * 
     * @throws  IOException  On I/O exception
     */
    public void loadInstalled() throws IOException
    {
        try (final TransferInputStream tis = new TransferInputStream(new FileInputStream(PACKAGES_FILE)))
        {   for (;;)
            {
                final String pack = tis.readObject(String.class);
                if (pack.isEmpty())
                    break;
                final VersionedPackage vpack = new VersionedPackage(pack);
                this.packageMap.put(vpack.toString(), new File(PACKAGE_DIR + pack.replace(":", ";") + ".pkg.xz"));
                this.packageMap.put(vpack.name,       new File(PACKAGE_DIR + pack.replace(":", ";") + ".pkg.xz"));
                this.installedMap.put(vpack, vpack);
                if (tis.readBoolean())
                    this.installedExplicitly.add(vpack);
        }   }
        catch (final FileNotFoundException ignore)
        {   //Ignore
        }
    }
    
    
    /**
     * Populate {@link #groupMap}
     * 
     * @throws  IOException  On I/O exception
     */
    public void loadGroups() throws IOException
    {
        for (final VersionedPackage pack : this.databaseMap.values())
            for (final String group : PackageInfo.fromFile(this.packageMap.get(pack.toString())).groups)
            {   Vector<VersionedPackage> list = this.groupMap.get(group);
                if (list == null)
                    this.groupMap.put(group, list = new Vector<VersionedPackage>());
                list.add(pack);
            }
    }
    
    
    /**
     * Populate {@link #provideMap}
     * 
     * @throws  IOException  On I/O exception
     */
    public void loadProviders() throws IOException
    {
         if (this.replaceMap.size() == 0)
            loadReplacers();
        
        for (final VersionedPackage provider : this.databaseMap.values())
            for (final String providee : PackageInfo.fromFile(this.packageMap.get(provider.toString())).provides)
            {   HashSet<VersionedPackage> list = this.provideMap.get(new VersionedPackage(providee));
                if (list == null)
                    this.provideMap.put(new VersionedPackage(providee), list = new HashSet<VersionedPackage>());
                list.add(provider);
            }
        
        if (this.replaceMap.size() == 0)
            return;
        
        VersionedPackage replacee;
        for (final HashSet<VersionedPackage> providers : this.provideMap.values())
            for (final Map.Entry<VersionedPackage, VersionedPackage> pair : this.replaceMap.entrySet())
                if (providers.contains(replacee = pair.getKey()) && providers.contains(pair.getValue()))
                    providers.remove(replacee);
    }
    
    
    /**
     * Populate {@link #replaceMap}
     * 
     * @throws  IOException  On I/O exception
     */
    public void loadReplacers() throws IOException
    {
        if (this.replaceMap.size() > 0)
            return;
        for (final VersionedPackage replacer : this.databaseMap.values())
            for (final String replacee : PackageInfo.fromFile(this.packageMap.get(replacer.toString())).replaces)
                this.replaceMap.put(new VersionedPackage(replacee), replacer);
    }
    
    
    /**
     * Uninstall package
     * 
     * @param  pack    The package
     * @param  dbonly  Do not remove files
     * 
     * @throws  IOException  On I/O exception
     */
    public void uninstall(final VersionedPackage pack, final boolean dbonly) throws IOException
    {
        if (this.installedMap.containsKey(pack) == false)
        {   System.out.println(pack.name + " was not installed");
            return;
        }
        this.installedMap.remove(pack);
        this.installedExplicitly.remove(pack);
        if (dbonly == false)
        {
            final String fs = Properties.getFileSeparator();
            final PackageInfo info = PackageInfo.fromFile(this.packageMap.get(pack));
            for (final String file : info.files)
                try
                {   final File f = new File((FILE_ROOT + (file.startsWith("/") ? file.substring(1) : file)).replace("/", fs));
                    if ((f.exists() && f.isDirectory()) == false)
                        f.delete();
                }
                catch (final Throwable err)
                {   System.out.println(err.toString());
                }
        }
    }
    
    
    /**
     * Install package
     * 
     * @param  pack      The package
     * @param  explicit  Install as explicitly installed (not dependency)
     * @param  dbonly    Do not install files
     * @param  force     Force installation of files
     * 
     * @throws  IOException  On I/O exception
     */
    public void install(final VersionedPackage pack, final boolean explicit, final boolean dbonly, final boolean force) throws IOException
    {
        final VersionedPackage prev = this.installedMap.get(pack);
        this.installedMap.put(pack, pack);
        if (explicit)  this.installedExplicitly.add(pack);
        else           this.installedExplicitly.remove(pack);
        if (dbonly == false)
        {
            final PackageInfo iprev = prev == null ? null : PackageInfo.fromFile(this.packageMap.get(prev));
            final String fs = Properties.getFileSeparator();
            final HashSet<String> installed = new HashSet<String>();
            if (iprev != null)
                for (final String file : iprev.files)
                    installed.add((FILE_ROOT + (file.startsWith("/") ? file.substring(1) : file)).replace("/", fs));
            
            final PackageInfo info = PackageInfo.fromFile(this.packageMap.get(pack));
            for (final String file : info.files)
            {
                final String f = (FILE_ROOT + (file.startsWith("/") ? file.substring(1) : file)).replace("/", fs);
                if (installed.contains(f))
                    continue;
                if (force || ((new File(f)).exists() == false))
                    installed.add(f);
                else
                    System.out.println("Skipping " + file + ", installed by another package");
            }
            
            String tarfile = this.packageMap.get(pack).getAbsolutePath();
            tarfile = tarfile.substring(0, tarfile.length() - ".pkg.xz".length()) + ".tar.xz";
            
            try (final TarInputStream tar = new TarInputStream(new XZInputStream(new FileInputStream(tarfile))))
            {   for (TarEntry entry; (entry = tar.getNextEntry()) != null;)
                {
                    final String top = entry.getName().replace("\\", "/").replace("/", fs);
                    String dest = FILE_ROOT + (top.startsWith(fs) ? top.substring(fs.length()) : top);
                    if (dest.endsWith(fs))
                        dest = dest.substring(0, dest.length() - fs.length());
                    final File destFile = new File(dest);
                    
                    if (entry.isDirectory())
                    {
                        destFile.mkdirs();
                        continue;
                    }
                    if (installed.contains(dest) == false)
                        continue;
                    if (destFile.getParentFile().exists() == false)
                        destFile.getParentFile().mkdirs();
                    
                    try (final FileOutputStream out = new FileOutputStream(destFile))
                    {   tar.copyEntryContents(out);
                        /* skipping mode, owner, links &c */
                    }
            }   }
        }
    }
    
    
    /**
     * Stores {@link #installedMap}
     * 
     * @throws  IOException  On I/O exception
     */
    public void syncInstalledMap() throws IOException
    {
        try (final TransferOutputStream tos = new TransferOutputStream(new FileOutputStream(PACKAGES_FILE)))
        {   for (final VersionedPackage pack : this.installedMap.values())
            {
                String file = this.packageMap.get(pack.toString()).getAbsolutePath().replace(";", ":");
                file = file.substring(PACKAGE_DIR.length());
                file = file.substring(0, file.length() - ".pkg.xz".length());
                tos.writeObject(file);
                tos.writeBoolean(this.installedExplicitly.contains(pack));
            }
            tos.flush();
        }
    }
    
}

