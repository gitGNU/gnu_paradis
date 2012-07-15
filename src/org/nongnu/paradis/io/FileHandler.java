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
package org.nongnu.paradis.io;
import org.nongnu.paradis.local.Properties; //Explicit
import org.nongnu.paradis.*;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.io.*;


/**
 * Class for file reading &amp; writing
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
@requires("java-environment>=7")
public class FileHandler
{
    /**
     * Non-constructor
     */
    private FileHandler()
    {
        assert false : "You may not create instances of this class [FileHandler].";
    }
    
    
    
    /**
     * Opens an internal file stream for reading
     * 
     * @param   path  The file path
     * @return        The file stream
     */
    public static InputStream readInternalFileStream(final String path)
    {
        for (final String $path : Properties.getClassPaths())
            try
            {
                final File file = new File($path);
                if ((file.exists() && file.isFile()) == false)
                    continue;
                
                URLClassLoader classLoader = new URLClassLoader(new URL[] { file.toURI().toURL() });
                InputStream stream = classLoader.getResourceAsStream(path);
                
                if (stream != null)
                    return stream;
            }
            catch (final Throwable err)
            {   //Do nothing
            }
        
        return ClassLoader.getSystemResourceAsStream(path);
    }
    
    
    /**
     * Opens an external file stream for reading
     * 
     * @param   path  The file path
     * @return        The file stream
     * 
     * @throws  IOException  On problem reading the file
     */
    public static InputStream readExternalFileStream(final String path) throws IOException
    {   return new FileInputStream(getFileName(path));
    }
    
    /**
     * Opens a file stream for writing
     * 
     * @param   path  The file path
     * @return        The file stream
     * 
     * @throws  IOException  On problem writing the file
     */
    public static OutputStream writeFileStream(final String path) throws IOException
    {   return new FileOutputStream(getFileName(path));
    }
    
    /**
     * Reads an external file
     * 
     * @param   path  The file path
     * @return        The content of a file
     * 
     * @throws  IOException  On problem reading the file
     */
    public static String readExternalFile(final String path) throws IOException
    {   return readExternalFile(path, null);
    }
    
    /**
     * Reads an external file
     * 
     * @param   path      The file path
     * @param   encoding  The text encoding, {@code null} for default (UTF-8)
     * @return            The content of a file
     * 
     * @throws  IOException  On problem reading the file
     */
    public static String readExternalFile(final String path, final String encoding) throws IOException
    {   return new String(readExternalFileBytes(path), encoding == null ? "UTF-8" : encoding);
    }
    
    
    /**
     * Reads an external file
     * 
     * @param   path  The file path
     * @return        The content of a file
     * 
     * @throws  IOException  On problem reading the file
     */
    public static byte[] readExternalFileBytes(final String path) throws IOException
    {   try (final InputStream is = readExternalFileStream(path))
        {
            final ArrayList<byte[]> old = new ArrayList<byte[]>();
            final ArrayDeque<int[]> sizes = new ArrayDeque<int[]>();
            int ptr = 0, size = 0;
            for (int av; (av = is.available()) != 0;)
            {   final byte[] buf = new byte[av];
                size += av = is.read(buf, 0, av);
                old.add(buf);
                sizes.offerLast(new int[] { av });
            }
            if (old.size() == 1)
                return old.get(0);
            final byte[] rc = new byte[size];
            for (final byte[] buf : old)
            {   System.arraycopy(buf, 0, rc, ptr, size = sizes.pollFirst()[0]);
                ptr += size;
            }
            return rc;
    }   }
    
    
    /**
     * Reads an external file, if it does not exist an internal file will be read instead
     * 
     * @param   path  The file path
     * @return        The content of a file
     * 
     * @throws  IOException  On problem reading the file
     * 
     * @see  #readFileAlt(String, String)
     */
    public static String readFile(final String path) throws IOException
    {   return externalFileExists(path) ? readExternalFile(path) : readInternalFile(path);
    }
    
    /**
     * Reads an external file, if it does not exist an internal file will be read instead
     * 
     * @param   path         The file path
     * @param   encoding     The text encoding
     * @return               The content of a file
     * 
     * @throws  IOException  On problem reading the file
     * 
     * @see  #readFileAlt(String, String, String)
     */
    public static String readFile(final String path, final String encoding) throws IOException
    {   return externalFileExists(path) ? readExternalFile(path, encoding) : readInternalFile(path, encoding);
    }
    
    /**
     * Reads an external file, if it does not exist an internal file will be read instead
     * 
     * @param   path  The file path
     * @return        The content of a file
     * 
     * @throws  IOException  On problem reading the file
     * 
     * @see  #readFileBytesAlt(String, String)
     */
    public static byte[] readFileBytes(final String path) throws IOException
    {   return externalFileExists(path) ? readExternalFileBytes(path) : readInternalFileBytes(path);
    }
    
    /**
     * Reads an external file, if it does not exist an internal file will be read instead
     * 
     * @param   externalPath  The external file path
     * @param   internalPath  The internal file path
     * @return                The content of a file
     * 
     * @throws  IOException   On problem reading the file
     * 
     * @see  #readFile(String)
     */
    public static String readFileAlt(final String externalPath, final String internalPath) throws IOException
    {   return externalFileExists(externalPath) ? readExternalFile(externalPath) : readInternalFile(internalPath);
    }
    
    /**
     * Reads an external file, if it does not exist an internal file will be read instead
     * 
     * @param   externalPath  The external file path
     * @param   internalPath  The internal file path
     * @param   encoding      The text encoding
     * @return                The content of a file
     * 
     * @throws  IOException   On problem reading the file
     * 
     * @see  #readFile(String, String)
     */
    public static String readFileAlt(final String externalPath, final String internalPath, final String encoding) throws IOException
    {   return externalFileExists(externalPath) ? readExternalFile(externalPath, encoding) : readInternalFile(internalPath, encoding);
    }
    
    /**
     * Reads an external file, if it does not exist an internal file will be read instead
     *
     * @param   externalPath  The external file path
     * @param   internalPath  The internal file path
     * @return                The content of a file
     * 
     * @throws  IOException   On problem reading the file
     * 
     * @see  #readFileBytes(String)
     */
    public static byte[] readFileBytesAlt(final String externalPath, final String internalPath) throws IOException
    {   return externalFileExists(externalPath) ? readExternalFileBytes(externalPath) : readInternalFileBytes(internalPath);
    }
    
    /**
     * Reads an internal file
     * 
     * @param   path  The file path
     * @return        The content of a file
     * 
     * @throws  IOException  On problem reading the file
     */
    public static String readInternalFile(final String path) throws IOException
    {   return readInternalFile(path, null);
    }
    
    /**
     * Reads an internal file
     * 
     * @param   path      The file path
     * @param   encoding  The text encoding, {@code null} for default (UTF-8)
     * @return            The content of a file
     * 
     * @throws  IOException  On problem reading the file
     */
    public static String readInternalFile(final String path, final String encoding) throws IOException
    {   return new String(readInternalFileBytes(path), encoding == null ? "UTF-8" : encoding);
    }
    
    
    /**
     * Reads an internal file
     * 
     * @param   path  The file path
     * @return        The content of a file
     * 
     * @throws  IOException  On problem reading the file
     */
    public static byte[] readInternalFileBytes(final String path) throws IOException
    {   try (final InputStream is = readInternalFileStream(path))
        {
            final ArrayList<byte[]> old = new ArrayList<byte[]>();
            final ArrayDeque<int[]> sizes = new ArrayDeque<int[]>();
            int ptr = 0, size = 0;
            for (int av; (av = is.available()) != 0;)
            {   final byte[] buf = new byte[av];
                size += av = is.read(buf, 0, av);
                old.add(buf);
                sizes.offerLast(new int[] { av });
            }
            if (old.size() == 1)
                return old.get(0);
            final byte[] rc = new byte[size];
            for (final byte[] buf : old)
            {   System.arraycopy(buf, 0, rc, ptr, size = sizes.pollFirst()[0]);
                ptr += size;
            }
            return rc;
    }   }
    
    
    /**
     * Writes a file
     * 
     * @param   path      The file path
     * @param   content   The desired file content
     * @param   encoding  The text encoding
     * 
     * @throws  IOException  On problem writing the file
     */
    public static void writeFile(final String path, final String content, final String encoding) throws IOException
    {   String p = getFileName(path);
        (new File(p)).getParentFile().mkdirs();
        try (final OutputStream os = writeFileStream(p))
        {   os.write(content.getBytes(encoding == null ? "UTF-8" : encoding));
            os.flush();
    }   }
    
    
    /**
     * Writes a file
     *
     * @param   path     The file path
     * @param   content  The desired file content
     * 
     * @throws  IOException  On problem writing the file
     */
    public static void writeFile(final String path, final String content) throws IOException
    {   writeFile(path, content, null);
    }
    
    
    /**
     * Writes a file
     * 
     * @param   path     The file path
     * @param   content  The desired file content
     * 
     * @throws  IOException  On problem writing the file
     */
    public static void writeFile(final String path, final byte[] content) throws IOException
    {   try (final OutputStream os = writeFileStream(path))
        {   os.write(content);
            os.flush();
    }   }
    
    
    /**
     * Creates an empty file in the default temporary-file directory, using the given prefix and suffix to generate its name
     * 
     * @param   pattern       The prefix string to be used in generating the file's name; must be at least three characters long
     * @param   surfix        The suffix string to be used in generating the file's name; may be {@code null}, in which case the suffix ".tmp" will be used
     * @param   delectOnExit  Whether or not the file should be deleted or not on exit
     * @return                A {@link File} instance, representing a temporary file
     * 
     * @throws  IllegalArgumentException  If the prefix argument contains fewer than three characters
     * @throws  IOException               If a file could not be created
     * @throws  SecurityException         If a security manager exists and its {@link SecurityManager#checkWrite(String)} method does not allow a file to be created
     * @throws  SecurityException         If a security manager exists and its {@link SecurityManager#checkDelete(String)} method denies delete access to the file
     */
    public static File createTempFile(final String pattern, final String surfix, final boolean delectOnExit) throws IOException
    {   final File temp = File.createTempFile(pattern, surfix);
        if (delectOnExit)
            temp.deleteOnExit();
        return temp;
    }
    
    
    /**
     * Gets whether an external file exists
     * 
     * @param   path  The file
     * @return        Whether the external file exists
     */
    public static boolean externalFileExists(final String path)
    {   return (new File(path)).exists();
    }
    
    /**
     * Gets whether an internal file exists
     * 
     * @param   path  The file
     * @return        Whether the internal file exists
     */
    public static boolean internalFileExists(final String path)
    {   return readInternalFileStream(path) != null;
    }
    
    /**
     * Gets whether an external or alt. internal file exists
     * 
     * @param   externalPath  The external file
     * @param   internalPath  The internal file
     * @return                Whether the external or alt. internal file exists
     */
    public static boolean fileExists(final String externalPath, final String internalPath)
    {   return externalFileExists(externalPath) || internalFileExists(internalPath);
    }
    
    /**
     * Gets whether an external, but not an internal or vice versa version of a file exists
     * 
     * @param   externalPath  The external file
     * @param   internalPath  The internal file
     * @return                Whether an external, but not an internal or vice versa version of a file exists
     */
    public static boolean fileExistsExclusivly(final String externalPath, final String internalPath)
    {   return externalFileExists(externalPath) ^ internalFileExists(internalPath);
    }
    
    /**
     * Gets whether an external, but not an internal version of a file exists
     * 
     * @param   externalPath  The external file
     * @param   internalPath  The internal file
     * @return                Whether an external, but not an internal version of a file exists
     */
    public static boolean externalfileExistsExclusivly(final String externalPath, final String internalPath)
    {   return externalFileExists(externalPath) && ! internalFileExists(internalPath);
    }
    
    /**
     * Gets whether an internal, but not an external version of a file exists
     * 
     * @param   externalPath  The external file
     * @param   internalPath  The internal file
     * @return                Whether an internal, but not an external version of a file exists
     */
    public static boolean internalfileExistsExclusivly(final String externalPath, final String internalPath)
    {   return internalFileExists(internalPath) && ! externalFileExists(externalPath);
    }
    
    /**
     * Gets whether an external or alt. internal file exists
     * 
     * @param   path  The file
     * @return        Whether the external or alt. internal file exists
     */
    public static boolean fileExists(final String path)
    {   return fileExists(path, path);
    }
    
    /**
     * Gets whether an external, but not an internal or vice versa version of a file exists
     * 
     * @param   path  The file
     * @return        Whether an external, but not an internal or vice versa version of a file exists
     */
    public static boolean fileExistsExclusivly(final String path)
    {   return fileExistsExclusivly(path, path);
    }
    
    /**
     * Gets whether an external, but not an internal version of a file exists
     * 
     * @param   path  The file
     * @return        Whether an external, but not an internal version of a file exists
     */
    public static boolean externalfileExistsExclusivly(final String path)
    {   return externalfileExistsExclusivly(path, path);
    }
    
    /**
     * Gets whether an internal, but not an external version of a file exists
     * 
     * @param   path  The file
     * @return        Whether an internal, but not an external version of a file exists
     */
    public static boolean internalfileExistsExclusivly(final String path)
    {   return internalfileExistsExclusivly(path, path);
    }
    
    
    /**
     * Gets a file from the current working folder
     * 
     * @param   file  The file
     * @return        A file from the current working folder
     */
    public static String getFileName(final String file)
    {   if (file.equals("~"))
            return Properties.getHome();
        String rc = file.replace("/", Properties.getFileSeparator());
        if (rc.startsWith("~" + Properties.getFileSeparator()))
            rc = Properties.getHome() + rc.substring(1);
        return rc;
    }
    
}

