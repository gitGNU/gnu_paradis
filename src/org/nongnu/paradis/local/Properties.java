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
package org.nongnu.paradis.local;
import org.nongnu.paradis.*;

import java.io.*;


/**
 * System properties
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Properties
{
    /**
     * Non-constructor
     */
    private Properties()
    {
        assert false : "You may not create instances of this class [Properties].";
    }
    
    
    
    /**
     * Rules on how to parse line breaks
     */
    public static enum LineRule
    {
        /**
         * Ignore all line breaks
         */
        IGNORE,
        
        /**
         * Stop parsing at first line break
         */
        BREAK,
        
        /**
         * Parse line breaks as any other character
         */
        READ,
        
        /**
         * Convert line breaks to blank spaces
         */
        SPACE,
    }
    
    
    
    /**
     * Gets the home of the user, but allowes false home given by the shell
     * 
     * @return  The home of the user
     */
    public static String getHome()
    {
        String home;
        if ((home = System.getenv().get("HOME")) == null)
            return System.getProperty("user.home");
        return home;
    }
    
    
    /**
     * Gets the name of the user
     * 
     * @return  The name of the user
     */
    public static String getUser()
    {
        return System.getProperty("user.name");
    }
    
    
    /**
     * Gets the system file separator ("/" on UNIX)
     * 
     * @return  The system file separator ("/" on UNIX)
     */
    public static String getFileSeparator()
    {
        String rc = System.getProperty("file.separator");
        return rc == null ? "/" : rc;
    }
    
    
    /**
     * Gets the system path separator (":" on UNIX)
     * 
     * @return  The system path separator (":" on UNIX)
     */
    public static String getPathSeparator()
    {
        String rc = System.getProperty("path.separator");
        return rc == null ? ":" : rc;
    }
    
    
    /**
     * Gets the user's standard text editor
     * 
     * @return  The user's standard text editor, {@code null} if not defined
     */
    public static String getEditor()
    {
        return getEnvironmentVariable("EDITOR");
    }
    
    
    /**
     * Gets the user's standard text pager
     * 
     * @return  The user's standard text pager, {@code null} if not defined
     */
    public static String getPager()
    {
        return getEnvironmentVariable("PAGER");
    }
    
    
    /**
     * Gets all directories where executables are localeted
     * 
     * @return  All directories where executables are localeted
     */
    public static String[] getBinaryPaths()
    {
        return getEnvironmentVariable("PATH").split(getPathSeparator());
    }
    
    
    /**
     * Gets a custom environment variable
     * 
     * @param   variable  The name of the environment variable
     * @return            The value of the environment variable
     */
    public static String getEnvironmentVariable(final String variable)
    {
        return System.getenv().get(variable);
    }
    
    
    /**
     * Gets the program class paths
     * 
     * @return  The program class paths
     */
    public static String[] getClassPaths()
    {
        return System.getProperty("java.class.path").split(getPathSeparator());
    }
    
    
    /**
     * Gets the current working directory
     * 
     * @return  The current working directory
     */
    public static String getCurrentWorkingDirectory()
    {
        return System.getProperty("user.dir");
    }
    
    
    /**
     * Gets the terminal on which the program is running, according to the terminal itself and maybe even the user
     * 
     * @return  The terminal on which the program is running
     */
    public static String getTerminal()
    {
        return getEnvironmentVariable("TERM");
    }
    
    
    /**
     * Gets the shell the program was started from
     * 
     * @return  The shell the program was started from
     */
    public static String getShell()
    {
        return getEnvironmentVariable("SHELL");
    }
    
    
    /**
     * Returns the number of columns in the terminal according to the kernel,
     * {@link NullPointerException} can be throws if you do
     * not have coreutils or `stty` is otherwise not invokable.
     * 
     * @return  The number of columns in the terminal
     */
    @requires("coreutils")
    public static int getTerminalWidth()
    {
        return Integer.parseInt(execSystemProperty(LineRule.BREAK, "stty", "size").split(" ")[0]);
    }
    
    
    /**
     * Returns the number of lines in the terminal according to the kernel,
     * {@link NullPointerException} can be throws if you do
     * not have coreutils or `stty` is otherwise not invokable.
     * 
     * @return  The number of lines in the terminal
     */
    @requires("coreutils")
    public static int getTerminalHeight()
    {
        return Integer.parseInt(execSystemProperty(LineRule.BREAK, "stty", "size").split(" ")[0]);
    }
    
    
    /**
     * Returns the current STTY settings that you may modify,
     * {@link NullPointerException} can be throws if you do
     * not have coreutils or `stty` is otherwise not invokable.
     * 
     * @return  The current STTY settings that you may modify
     */
    @requires("coreutils")
    public static String getSTTYSettings()
    {
        String[] data = execSystemProperty(LineRule.SPACE, "stty", "-a").split(";");
        String rc = data[data.length - 1];
        while (rc.startsWith(" "))  rc = rc.substring(1);
        while (rc  .endsWith(" "))  rc = rc.substring(0, rc.length() - 1);
        while (rc.contains("  "))   rc = rc.replace("  ", " ");
        return rc;
    }
    
    
    /**
     * Gets or sets system properties by invoking another program
     * 
     * @param   lineRule  What to do with line breaks
     * @param   cmd       The command to run
     * @return            The data returned by the invoked program, {@code null} on error
     */
    @requires("java-runtime>=7")
    public static String execSystemProperty(final LineRule lineRule, final String... cmd)
    {
        try
        {
            byte[] buf = new byte[64];
            int ptr = 0;
            
            final ProcessBuilder procBuilder = new ProcessBuilder(cmd);
            procBuilder.redirectInput(ProcessBuilder.Redirect.from((new File("/dev/stdout")).getCanonicalFile()));
            final Process process = procBuilder.start();
            final InputStream stream = process.getInputStream();
            
            for (int d; (d = stream.read()) != -1; )
            {
                if (d == '\n')
                    if      (lineRule == LineRule.BREAK)   break;
                    else if (lineRule == LineRule.IGNORE)  continue;
                    else if (lineRule == LineRule.SPACE)   d = ' ';
                
                if (ptr == buf.length)
                {
                    final byte[] nbuf = new byte[ptr + 64];
                    System.arraycopy(buf, 0, nbuf, 0, ptr);
                    buf = nbuf;
                }
                buf[ptr++] = (byte)d;
            }
            
            process.waitFor();
            if (process.exitValue() != 0)
                return null;
            
            return new String(buf, 0, ptr, "UTF-8");
        }
        catch (final Throwable err)
        {
            return null;
        }
    }

}

