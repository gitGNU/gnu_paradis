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
import org.nongnu.paradis.net.*;
import org.nongnu.paradis.io.*;
import org.nongnu.paradis.*;

import org.tukaani.xz.*;

import java.io.*;


/**
 * Installable package info
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public final class PackageInfo
{
    //TODO unimplementied fields: isFreeSoftware arch os backup checksums category uuid
    /**
     * Constructor
     * 
     * @param  optionalSystemDependencies  Optional system dependencies
     * @param  optionalDependencies        Optional paradis dependencies
     * @param  systemDependencies          Manditory system dependencies
     * @param  dependencies                Manditory paradis dependencies
     * @param  packageEpoch                Package epoch number
     * @param  packageVersion              Package version
     * @param  packageRelease              Package release number
     * @param  packageName                 Package name
     * @param  packageDesc                 Short package description
     * @param  packageDescription          Long package description
     * @param  provides                    Logical packages proviced by this package
     * @param  replaces                    Physical packages replaced by this package
     * @param  conflicts                   Package conflicting with this package
     * @param  containsSource              Whether the package contains source files
     * @param  containsBinary              Whether the package contains executable files
     * @param  licenses                    Licenses this packages uses
     * @param  isFreeSoftware              Whether the package is Free Software
     * @param  isGPL3compat                Whether the package is GNU General Public License v3+ compatible
     * @param  url                         Upstream URL
     * @param  arch                        Supported architectures
     * @param  os                          Supported operating systems
     * @param  groups                      Package groups in which this package is included
     * @param  files                       Files included in the package
     * @param  backup                      Which files of which to make backup
     * @param  checksums                   File checksums
     * @param  category                    Package category
     * @param  uuid                        Package UUID
     */
    public PackageInfo(final String[] optionalSystemDependencies, final String[] optionalDependencies, final String[] systemDependencies, final String[] dependencies,
		       final int      packageEpoch,               final String   packageVersion,       final int      packageRelease,     final String packageName,
		       final String   packageDesc,                final String   packageDescription,   final String[] provides,           final String[] replaces,
		       final String[] conflicts,                  final boolean  containsSource,       final boolean  containsBinary,     final String[] licenses,
		       final boolean  isFreeSoftware,             final boolean  isGPL3compat,         final String   url,                final String[] arch,
		       final String[] os,                         final String[] groups,               final String[] files,              final boolean[] backup,
		       final String[] checksums,                  final String   category,             final UUID     uuid)
    {
        assert optionalSystemDependencies != null;  this.optionalSystemDependencies = optionalSystemDependencies;
        assert optionalDependencies       != null;  this.optionalDependencies       = optionalDependencies;
        assert systemDependencies         != null;  this.systemDependencies         = systemDependencies;
        assert dependencies               != null;  this.dependencies               = dependencies;
                                                    this.packageEpoch               = packageEpoch;
        assert packageVersion             != null;  this.packageVersion             = packageVersion;
                                                    this.packageRelease             = packageRelease;
        assert packageName                != null;  this.packageName                = packageName;
        assert packageDesc                != null;  this.packageDesc                = packageDesc;
        assert packageDescription         != null;  this.packageDescription         = packageDescription;
        assert provides                   != null;  this.provides                   = provides;
        assert replaces                   != null;  this.replaces                   = replaces;
        assert conflicts                  != null;  this.conflicts                  = conflicts;
                                                    this.containsSource             = containsSource;
                                                    this.containsBinary             = containsBinary;
        assert licenses                   != null;  this.licenses                   = licenses;
                                                    this.isFreeSoftware             = isFreeSoftware;
                                                    this.isGPL3compat               = isGPL3compat;
        assert url                        != null;  this.url                        = url;
        assert arch                       != null;  this.arch                       = arch;
        assert os                         != null;  this.os                         = os;
        assert groups                     != null;  this.groups                     = groups;
        assert files                      != null;  this.files                      = files;
        assert backup                     != null;  this.backup                     = backup;
        assert checksums                  != null;  this.checksums                  = checksums;
        assert category                   != null;  this.category                   = category;
        assert uuid                       != null;  this.uuid                       = uuid;
    }
    
    
    
    /**
     * Protocol for transfering {@link PackageInfo}s
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    public static class PackageInfoTransferProtocol implements TransferProtocol<PackageInfo>
    {
        //Has default constructor
        
        
        
        /**
         * {@inheritDoc}
         */
        @Override
        public PackageInfo read(final TransferInputStream stream) throws IOException
        {   return new PackageInfo(stream.readObject(String[].class),
                                   stream.readObject(String[].class),
                                   stream.readObject(String[].class),
                                   stream.readObject(String[].class),
                                   stream.readLen(),
                                   stream.readObject(String.class),
                                   stream.readLen(),
                                   stream.readObject(String.class),
                                   stream.readObject(String.class),
                                   stream.readObject(String.class),
                                   stream.readObject(String[].class),
                                   stream.readObject(String[].class),
                                   stream.readObject(String[].class),
                                   stream.readBoolean(),
                                   stream.readBoolean(),
                                   stream.readObject(String[].class),
                                   stream.readBoolean(),
                                   stream.readBoolean(),
                                   stream.readObject(String.class),
                                   stream.readObject(String[].class),
                                   stream.readObject(String[].class),
                                   stream.readObject(String[].class),
                                   stream.readObject(String[].class),
                                   stream.readObject(boolean[].class),
                                   stream.readObject(String[].class),
                                   stream.readObject(String.class),
                                   stream.readObject(UUID.class));
        }
        
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void write(final PackageInfo data, final TransferOutputStream stream) throws IOException
        {   stream.writeObject(data.optionalSystemDependencies);
            stream.writeObject(data.optionalDependencies);
            stream.writeObject(data.systemDependencies);
            stream.writeObject(data.dependencies);
            stream.writeLen(data.packageEpoch);
            stream.writeObject(data.packageVersion);
            stream.writeLen(data.packageRelease);
            stream.writeObject(data.packageName);
            stream.writeObject(data.packageDesc);
            stream.writeObject(data.packageDescription);
            stream.writeObject(data.provides);
            stream.writeObject(data.replaces);
            stream.writeObject(data.conflicts);
            stream.writeBoolean(data.containsSource);
            stream.writeBoolean(data.containsBinary);
            stream.writeObject(data.licenses);
            stream.writeBoolean(data.isFreeSoftware);
	    stream.writeBoolean(data.isGPL3compat);
            stream.writeObject(data.url);
            stream.writeObject(data.arch);
            stream.writeObject(data.os);
            stream.writeObject(data.groups);
            stream.writeObject(data.files);
            stream.writeObject(data.backup);
            stream.writeObject(data.checksums);
            stream.writeObject(data.category);
            stream.writeObject(data.uuid);
        }
        
    }
    
    
    
    /**
     * <p>Optional system dependencies</p>
     * <b>Element syntax:</b>
     * <ul>
     *   <li><code>package: purpose</code></li>
     *   <li><code>package&lt;version: purpose</code></li>
     *   <li><code>package&lt;=version: purpose</code></li>
     *   <li><code>package=version: purpose</code></li>
     *   <li><code>package&gt;=version: purpose</code></li>
     *   <li><code>package&gt;version: purpose</code></li>
     * </ul>
     */
    public final String[] optionalSystemDependencies;
    
    /**
     * <p>Optional paradis dependencies</p>
     * <b>Element syntax:</b>
     * <ul>
     *   <li><code>package: purpose</code></li>
     *   <li><code>package&lt;version: purpose</code></li>
     *   <li><code>package&lt;=version: purpose</code></li>
     *   <li><code>package=version: purpose</code></li>
     *   <li><code>package&gt;=version: purpose</code></li>
     *   <li><code>package&gt;version: purpose</code></li>
     * </ul>
     */
    public final String[] optionalDependencies;
    
    /**
     * <p>Manditory system dependencies</p>
     * <b>Element syntax:</b>
     * <ul>
     *   <li><code>package</code></li>
     *   <li><code>package&lt;version</code></li>
     *   <li><code>package&lt;=version</code></li>
     *   <li><code>package=version</code></li>
     *   <li><code>package&gt;=version</code></li>
     *   <li><code>package&gt;version</code></li>
     * </ul>
     */
    public final String[] systemDependencies;
    
    /**
     * <p>Manditory paradis dependencies</p>
     * <b>Element syntax:</b>
     * <ul>
     *   <li><code>package</code></li>
     *   <li><code>package&lt;version</code></li>
     *   <li><code>package&lt;=version</code></li>
     *   <li><code>package=version</code></li>
     *   <li><code>package&gt;=version</code></li>
     *   <li><code>package&gt;version</code></li>
     * </ul>
     */
    public final String[] dependencies;
    
    /**
     * Package epoch number: non-negative, default is 0
     */
    public final int packageEpoch;
    
    /**
     * <p>Package version</p>
     * <b>Element syntax:</b>
     * <ul>
     *   <li><code>STABIL</code></li>
     *   <li><code>STABIL-rcNUMBER</code></li>
     * </ul>
     * STABIL: non-negative integers separated '.' (one dot)<br/>
     * NUMBER: positive integer
     */
    public final String packageVersion;
    
    /**
     * Package release number: non-negative, default is 0
     */
    public final int packageRelease;
    
    /**
     * Package name: lower case a-z alphanumberical with non-initial, non-terminal hyphens
     */
    public final String packageName;
    
    /**
     * Short package description: one line, at most 80 characters
     */
    public final String packageDesc;
    
    /**
     * Long package description: full multiline description
     */
    public final String packageDescription;
    
    /**
     * <p>Logical packages proviced by this package</p>
     * <b>Element syntax:</b>
     * <ul>
     *   <li><code>package</code></li>
     *   <li><code>package&lt;version</code></li>
     *   <li><code>package&lt;=version</code></li>
     *   <li><code>package=version</code></li>
     *   <li><code>package&gt;=version</code></li>
     *   <li><code>package&gt;version</code></li>
     * </ul>
     */
    public final String[] provides;
    
    /**
     * <p>Physical packages replaced by this package</p>
     * <b>Element syntax:</b>
     * <ul>
     *   <li><code>package</code></li>
     *   <li><code>package&lt;version</code></li>
     *   <li><code>package&lt;=version</code></li>
     *   <li><code>package=version</code></li>
     *   <li><code>package&gt;=version</code></li>
     *   <li><code>package&gt;version</code></li>
     * </ul>
     */
    public final String[] replaces;
    
    /**
     * <p>Package conflicting with this package</p>
     * <b>Element syntax:</b>
     * <ul>
     *   <li><code>package</code></li>
     *   <li><code>package&lt;version</code></li>
     *   <li><code>package&lt;=version</code></li>
     *   <li><code>package=version</code></li>
     *   <li><code>package&gt;=version</code></li>
     *   <li><code>package&gt;version</code></li>
     * </ul>
     */
    public final String[] conflicts;
    
    /**
     * Whether the package contains source files
     */
    public final boolean containsSource;
    
    /**
     * Whether the package contains executable files
     */
    public final boolean containsBinary;
    
    /**
     * <p>Licenses this packages uses</p>
     * <b>Examples:</b>
     * <ul>
     *   <li><code>AGPL3</code></li>
     *   <li><code>GPL3</code></li>
     *   <li><code>GPL2</code></li>
     *   <li><code>EPL</code></li>
     *   <li><code>MIT</code></li>
     *   <li><code>WTFPL</code></li>
     *   <li><code>Public Domain</code></li>
     * </ul>
     */
    public final String[] licenses;
    
    /**
     * Whether the package is Free Software
     */
    public final boolean isFreeSoftware;
    
    /**
     * Whether the package is GNU General Public License v3+ compatible
     */
    public final boolean isGPL3compat;
    
    /**
     * Upstream URL
     */
    public final String url;
    
    /**
     * <p>Supported architectures</p>
     * <b>Examples:</b>
     * <ul>
     *   <li><code>any</code></li>
     *   <li><code>ia-32</code></li>
     *   <li><code>x86-64</code></li>
     *   <li><code>ia-64</code></li>
     *   <li><code>ppc</code></li>
     *   <li><code>ppc64</code></li>
     *   <li><code>sparc32</code></li>
     *   <li><code>sparc64</code></li>
     *   <li><code>arm</code></li>
     *   <li><code>hppa</code></li>
     *   <li><code>mips</code></li>
     *   <li><code>sh</code></li>
     *   <li><code>s390</code></li>
     *   <li><code>s390x</code></li>
     *   <li><code>alpha</code></li>
     * </ul>
     * An element may have the prefix ! (a bang) if it is known not to be supported
     */
    public final String[] arch;
    
    /**
     * <p>Supported operating systems</p>
     * <b>Examples:</b>
     * <ul>
     *   <li><code>any</code></li>
     *   <li><code>gnulinux</code></li>
     *   <li><code>gnuhurd</code></li>
     *   <li><code>macos</code></li>
     *   <li><code>macos 9</code></li>
     *   <li><code>macosx</code></li>
     *   <li><code>windows</code></li>
     *   <li><code>windows nt</code></li>
     *   <li><code>windows 7</code></li>
     * </ul>
     * An element may have the prefix ! (a bang) if it is known not to be supported
     */
    public final String[] os;
    
    /**
     * Package groups in which this package is included
     */
    public final String[] groups;
    
    /**
     * Files included in the package: / is paradis root, and / is also the file seperator (directories)
     */
    public final String[] files;
    
    /**
     * Which files of which to make backup
     */
    public final boolean[] backup;
    
    /**
     * <p>File checksums</p>
     * <p><b>Element syntax:</b> list of '|' (one pipe) separated <code>algorithm:checksum</code>,
     * whether <code>algorithm</code> is in lowercase and <code>checksum</code> is in upper case
     * dashless hexadecimal.</p>
     */
    public final String[] checksums;
    
    /**
     * Package category
     */
    public final String category;
    
    /**
     * Package UUID: used to ensure uniqueness of package independent on version, epoch and release
     */
    public final UUID uuid;
    
    
    
    /**
     * Loads an {@link PackageInfo} from a file, that may be compressed with xz
     * 
     * @param   file  The file to load
     * @return        The loaded {@link PackageInfo}
     * 
     * @throws  IOException  On I/O exception
     */
    @requires({"java-environment>=7", "xz-java"})
    public static PackageInfo fromFile(final File file) throws IOException
    {
        try (final InputStream fis = new FileInputStream(file) ; final TransferInputStream tis = new TransferInputStream(file.getAbsolutePath().endsWith(".xz") ? new XZInputStream(fis) : fis))
        {   return tis.readObject(PackageInfo.class);
        }
    }
    
    /**
     * Loads an {@link PackageInfo} from a file, that may be compressed with xz
     * 
     * @param   file  The file to load
     * @return        The loaded {@link PackageInfo}
     * 
     * @throws  IOException  On I/O exception
     */
    public static PackageInfo fromFile(final String file) throws IOException
    {
        return fromFile(new File(file));
    }
    
}

