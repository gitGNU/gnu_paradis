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
package org.nongnu.paradis.file.ini;

import java.io.*;
import java.util.ArrayList;


/**
 * Reads and writes INI- and INF-files
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class INI implements INIInterface
{
    /**
     * Desired by {@link Serializable}
     */
    private static final long serialVersionUID = 1L;
    
    
    
    /**
     * Line ending
     */
    private final static String LINE_ENDING = Constants.LINE_ENDING;
    
    /**
     * Hive declaration start
     */
    private final static String HIVE_START = Constants.HIVE_START;
    
    /**
     * Hive declaration end
     */
    private final static String HIVE_END = Constants.HIVE_END;
    
    
    
    /**
     * Constructor
     *
     * @param  filename            The INI-/INF-file's filename, {@code null} if not any file
     * @param  content             The content of the file
     * @param  keyEnding           Key ending, separating the value from the key
     * @param  commentNotation     The line beginning making the line a comment
     * @param  lastEntryOverrides  {@code true} if the last entry overrides, otherwise {@code false}
     */
    public INI(final String filename, final String content, final KeyEnding keyEnding, final CommentNotation commentNotation, final boolean lastEntryOverrides)
    {
        this.iniData = new INIData();
        
        this.iniData.keyEnding = keyEnding.getValue();
        this.iniData.commentNotation = commentNotation.getValue();
        this.iniData.lastEntryOverrides = lastEntryOverrides;
        this.iniData.filetext = content;
        
        this.iniData.filename = filename;
        
        prefixText();
    }
    
    /**
     * Constructor
     *
     * @param  filename            The INI-/INF-file's filename, {@code null} if not any file
     * @param  content             The content of the file
     * @param  keyEnding           Key ending, separating the value from the key
     * @param  commentNotation     What the line beginning with that making the line a comment
     */
    public INI(final String filename, final String content, final KeyEnding keyEnding, final CommentNotation commentNotation)
    {
        this(filename, content, keyEnding, commentNotation, true);
    }
    
    /**
     * Constructor
     *
     * @param  filename            The INI-/INF-file's filename, {@code null} if not any file
     * @param  content             The content of the file
     */
    public INI(final String filename, final String content)
    {
        this(filename, content, KeyEnding.DEFAULT, CommentNotation.DEFAULT);
    }
    
    /**
     * Private cloning constructor
     *
     * @param  orginal  Item to clone
     */
    private INI(final INI orginal)
    {
        this.iniData = new INIData();
        
        this.iniData.keyEnding = orginal.iniData.keyEnding;
        this.iniData.commentNotation = orginal.iniData.commentNotation;
        this.iniData.lastEntryOverrides = orginal.iniData.lastEntryOverrides;
        this.iniData.filetext = orginal.iniData.filetext;
        this.iniData.filename = orginal.iniData.filename;
        this.iniData.signature = orginal.iniData.signature;
    }
    
    
    
    /**
     * Key endings, separating the value from the key
     */
    public static enum KeyEnding
    {
        /** * Default value " = "                */ DEFAULT          (" = "),
        /** * Default value, but unspaced "="    */ DEFAULT_UNSPACED ( "=" ),
        /** * An alternate value                 */ COLON            (" : "),
        /** * An alternate value missing spacing */ COLON_UNSPACED   ( ":" );
        
        
        
        /**
         * Constructor
         *
         * @param  value  The value
         */
        private KeyEnding(final String value)
        {
            this.value = value;
        }
        
        
        
        /**
         * The value
         */
        private String value;
        
        
        
        /**
         * Gets the value
         *
         * @return  The value
         */
        public String getValue()
        {
            return this.value;
        }
        
    }
    
    /**
     * What the line beginning with that making the line a comment
     */
    public static enum CommentNotation
    {
        /** * The default value, semicolon ";" */ DEFAULT (";"),
        /** * An alternate value, square "#"   */ SQUARE  ("#");
        
        
        
        /**
         * Constructor
         *
         * @param  value  The value
         */
        private CommentNotation(final String value)
        {
            this.value = value;
        }
        
        
        
        /**
         * The value
         */
        private String value;
        
        
        
        /**
         * Gets the value
         *
         * @return  The value
         */
        public String getValue()
        {
            return this.value;
        }
        
    }
    
    
    
    /**
     * The INI data container
     */
    final INIData iniData;
    
    
    
    /**
     * Saves an instance to a file
     *
     * @param   obj          The object that is saved
     * @param   filename     The filename of the file it is saved to
     * @throws  IOException  On error
     */
    public static void saveInstance(final INI obj, final String filename) throws IOException
    {
        ObjectOutputStream objstream = null;
        try
        {   objstream = new ObjectOutputStream(new FileOutputStream(filename));
            objstream.writeObject(obj);
        }
        finally
        {
            if (objstream != null)
                try
                {   objstream.close();
                }
                catch (final Throwable ignore)
                {   //ignore
                }
        }
    }
    
    /**
     * Saves the instance to a file
     *
     * @param   file         The filename of the file it is saved to
     * @throws  IOException  On error
     */
    public void saveInstance(final String file) throws IOException
    {
        ObjectOutputStream objstream = null;
        try
        {   objstream = new ObjectOutputStream(new FileOutputStream(file));
            objstream.writeObject(this);
        }
        finally
        {
            if (objstream != null)
                try
                {   objstream.close();
                }
                catch (final Throwable ignore)
                {   //ignore
                }
        }
    }
    
    /**
     * Loads an instance of this class from a file
     *
     * @param   file       The filename for the file to be loaded
     * @throws  Exception  On error
     * @return             The saves instance
     */
    public static INI loadInstance(final String file) throws Exception
    {
        ObjectInputStream objstream = null;
        try
        {
            objstream = new ObjectInputStream(new FileInputStream(file));
            Object obj = objstream.readObject();
            return (INI)obj;
        }
        finally
        {
            if (objstream != null)
                try
                {   objstream.close();
                }
                catch (final Throwable ignore)
                {   //ignore
                }
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public INI clone()
    {   return new INI(this);
    }
    
    /**
     * Sets the signature in the file
     * 
     * @param  signature  The new signature in the file
     */
    public void setSignature(final String signature)
    {   this.iniData.signature = signature;
    }
    
    /**
     * Gets the signature in the file
     * 
     * @return  The signature in the file
     */
    public String getSignature()
    {   return this.iniData.signature;
    }
    
    /**
     * Buffers the INI-file for higher speed, unbuffers keys when read
     */
    public void refreshContentTree()
    {   refreshContentTree(true);
    }
    
    /**
     * Buffers the INI-file for higher speed
     *
     * @param  removeOnRead  Whether keys should be unbuffered when read
     */
    public void refreshContentTree(boolean removeOnRead)
    {   this.iniData.treeRoot = new TreeRoot(removeOnRead, this);
    }
    
    
    /**
     * Gets the saving string
     *
     * @return  The saving string
     */
    public String getSaveString()
    {   clean();
        return this.iniData.signature + LINE_ENDING + this.iniData.filetext;
    }
    
    
    /**
     * Removes all entries in the ini-file
     */
    public void clear()
    {   this.iniData.filetext = "";
        if (this.iniData.treeRoot != null)
            this.iniData.treeRoot.clear();
    }
    
    /**
     * Removes all whitelines in the ini-file
     */
    public void clean()
    {   DataFixer.clean(this.iniData);
    }
    
    /**
     * Fixed the text so it is readable of the machine
     */
    private void prefixText()
    {   DataFixer.prefixText(this.iniData);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommentNotation()
    {   return this.iniData.commentNotation;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getKeyEnding()
    {   return this.iniData.keyEnding;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getLastEntryOverrides()
    {   return this.iniData.lastEntryOverrides;
    }
    
    /**
     * Creates a empty hive (autoescaped hivename)
     *
     * @param  hive  The name of the new hive
     */
    public void createHive(final String hive)
    {   createHive(hive, true);
    }
    
    /**
     * Creates a new key or changes its value (autoescaped hive- &amp; keyname &amp; value)
     *
     * @param  hive    The name of the hive (new or old)
     * @param  key     The name of the key (new or old)
     * @param  value   The value of the key, {@code null} to delete it
     */
    public void createKey(final String hive, final String key, final String value)
    {   createKey(hive, key, value, true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getHiveKeys(final String hive)
    {   return getHiveKeys(hive, true);
    }
    
    /**
     * Returns all key namess in a hive (autoescaped hivename)
     *
     * @param   hive    The name of the hive
     * @return          All key names in a hive
     */
    public String[] getHiveKeyNames(final String hive)
    {   return getHiveKeyNames(hive, true);
    }
    
    /**
     * Reads a key value (autoescaped keyname)
     *
     * @param   hive    The name of the hive
     * @param   key     The name of the key
     * @return          The key's value
     */
    public String getKeyValue(final String hive, final String key)
    {   return getKeyValue(hive, key, true);
    }
    
    /**
     * Reads a key value (autoescaped keyname)
     *
     * @param   hiveKeys  All keys in the hive
     * @param   key       The name of the key
     * @return            The key's value
     */
    public String getKeyValue(final String[] hiveKeys, final String key)
    {   return getKeyValue(hiveKeys, key, true);
    }
    
    /**
     * Splits a text into an array
     *
     * @param  text       The text to split
     * @param  delimiter  The text used as delimiter
     * @return            The text splitted
     */
    private static String[] split(String text, String delimiter)
    {
        final ArrayList<String> list = new ArrayList<String>();
        int i = 0;
        
        for (;;)
        {
            int j = text.indexOf(delimiter, i);
            
            if (j >= 0)
            {   list.add(text.substring(i, j));
                i = j += delimiter.length();
            }
            else
            {   list.add(text.substring(i, text.length()));
                break;
            }
        }
        
        final String[] rc = new String[list.size()];
        i = 0;
        for (final String item : list)
            rc[i++] = item;
        
        return rc;
    }
    
    
    /**
     * Creates a empty hive
     *
     * @param  hive    The name of the new hive
     * @param  escape  Indicates whether the hivename needs to be escaped
     */
    public void createHive(final String hive, boolean escape)
    {
        String ehive = hive;
        if (escape)
            ehive = Escaper.escape(ehive);
        
        String text = this.iniData.filetext;
        
        String tmp0 = LINE_ENDING + text.toUpperCase() + LINE_ENDING;
        String tmp1 = LINE_ENDING + HIVE_START + ehive.toUpperCase() + HIVE_END + LINE_ENDING;
        
        if (tmp0.replace(tmp1, "").equals(tmp0)) // if (Hive does not exist)
        {
            text += (text.length() > 0 ? LINE_ENDING : "") + HIVE_START + ehive.toUpperCase() + HIVE_END; //Add the hive if it not exists.
            if (this.iniData.treeRoot != null)
            this.iniData.treeRoot.newHive(Escaper.unescape(ehive.toUpperCase()));
        }
        
        this.iniData.filetext = text;
    }
    
    /**
     * Creates a new key or changes its value
     *
     * @param  hive    The name of the hive (new or old)
     * @param  key     The name of the key (new or old)
     * @param  value   The value of the key, {@code null} to delete it
     * @param  escape  Indicates whether the hive- &amp; keyname &amp; value needs to be escaped
     */
    public void createKey(final String hive, final String key, final String value, boolean escape)
    {
        String ehive = hive;
        String ekey = key;
        String evalue = value;
        if (escape)
        {
            ehive = Escaper.escape(ehive);
            ekey = Escaper.escape(ekey);
            if (evalue != null)
                evalue = Escaper.escape(evalue);
        }
        
        if (evalue != null)
        {
            if (getKeyValue(ehive, ekey, false) == null)
            {
                createHive(ehive, false);   //Creates the hive if it not exists.
                String text = this.iniData.filetext;
                String[] lines = split(text, LINE_ENDING);
                
                text = "";
                for (int index = 0; index < lines.length; index++)
                {
                    text += lines[index] + LINE_ENDING;    //Stores the line into the memory
                    if (lines[index].toLowerCase().equals(HIVE_START + ehive.toLowerCase() + HIVE_END))
                        text += ekey + this.iniData.keyEnding + evalue + LINE_ENDING;   //Adds the key.
                }
                text = text.substring(0, text.length() - LINE_ENDING.length());  //Removes the extra line.
                
                this.iniData.filetext = text;    //Stores the ini-data.
            }
            
            else if (getKeyValue(ehive, ekey, false).equals(evalue) == false)
            {
                String text = this.iniData.filetext; //Reads the ini-file.
                String[] lines = split(text, LINE_ENDING);  //Gets the ini-file's lines.
                boolean inside = false;
                
                text = "";
                for (int i = 0; i < lines.length; i++)
                    if (lines[i].toLowerCase().equals(HIVE_START + ehive.toLowerCase() + HIVE_END))
                    {
                        text += lines[i] + LINE_ENDING;    //Stores the line into the memory
                        inside = true;
                    }
                    else if (lines[i].startsWith(HIVE_START) & lines[i].endsWith(HIVE_END))
                    {
                        text += lines[i] + LINE_ENDING;    //Stores the line into the memory
                        inside = false;
                    }
                    else if (inside && lines[i].toLowerCase().startsWith(ekey.toLowerCase() + this.iniData.keyEnding))
                    {
                        text += ekey + this.iniData.keyEnding + evalue + LINE_ENDING;   //Adds the key.
                    }
                    else
                    {
                        text += lines[i] + LINE_ENDING;    //Stores the line into the memory
                    }
                text = text.substring(0, text.length() - LINE_ENDING.length());  //Removes the extra line.
                
                this.iniData.filetext = text;    //Stores the ini-data.
            }
        }
        else if (getKeyValue(ehive, ekey, false) != null)
        {
            String text = this.iniData.filetext; //Reads the ini-file.
            String[] lines = split(text, LINE_ENDING);  //Gets the ini-file's lines.
            boolean inside = false;
            
            text = "";
            for (int i = 0; i < lines.length; i++)
            {
                if (lines[i].toLowerCase().equals(HIVE_START + ehive.toLowerCase() + HIVE_END))
                {
                    text += lines[i] + LINE_ENDING;    //Stores the line into the memory
                    inside = true;
                }
                else if (lines[i].startsWith(HIVE_START) & lines[i].endsWith(HIVE_END))
                {
                    text += lines[i] + LINE_ENDING;    //Stores the line into the memory
                    inside = false;
                }
                else if ((inside & lines[i].toLowerCase().startsWith(ekey.toLowerCase() + this.iniData.keyEnding)) == false)
                {
                    text += lines[i] + LINE_ENDING;    //Stores the line into the memory
                }
                //else: delete the key
            }
            text = text.substring(0, text.length() - LINE_ENDING.length());  //Removes the extra line.
            
            this.iniData.filetext = text;    //Stores the ini-data.
        }
        
        
        if (this.iniData.treeRoot != null)
            this.iniData.treeRoot.createKey(hive, key, value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getHives()
    {
        if (this.iniData.filetext != "")
        {
            String[] lines = split(this.iniData.filetext, LINE_ENDING);
            String text = "";
            
            for (int index = 0; index < lines.length; index++)
                if (lines[index].startsWith(HIVE_START) & lines[index].endsWith(HIVE_END))
                    text += lines[index].substring(HIVE_START.length(), lines[index].length() - HIVE_END.length()) + LINE_ENDING; //Stores the hive name.
            
            if (text.isEmpty())
                return new String[0];
            
            return Escaper.unescape(split(text.substring(0, text.length() - LINE_ENDING.length()), LINE_ENDING));  //Returns all hives.
        }
        
        return new String[0];
    }
    
    /**
     * Returns all keys in a hive
     *
     * @param   hive    The name of the hive
     * @param   escape  Indicates whether the hivename needs to be escaped
     * @return          All keys in a hive
     */
    public String[] getHiveKeys(final String hive, boolean escape)
    {
        String ehive = hive;
        if (escape)
            ehive = Escaper.escape(ehive);
        
        if (this.iniData.filetext.length() > 0)    //True if the ini-file exists.
        {
            String[] lines = split(this.iniData.filetext, LINE_ENDING); 
            String text = "";
            boolean inside = false;
            
            int startLine = 0;
            if (this.iniData.lastEntryOverrides)
                for (int index = startLine; index < lines.length; index++) 
                    if (lines[index].toLowerCase().equals(HIVE_START + ehive.toLowerCase() + HIVE_END))   //True if currect hive.
                        startLine = index;
            
            for (int index = startLine; index < lines.length; index++)
                if (lines[index].toLowerCase().equals(HIVE_START + ehive.toLowerCase() + HIVE_END))   //True if currect hive.
                    inside = true;
                else if (lines[index].startsWith(HIVE_START) & lines[index].endsWith(HIVE_END)) //True if incurrect hive.
                    inside = false;
                else if (inside && !(lines[index].startsWith(this.iniData.commentNotation)))
                    text += lines[index] + LINE_ENDING;
            
            if (text.length() == 0)
                return new String[0];   //Returns zero strings if the ini-file is empty.
            
            return split(text.substring(0, text.length() - LINE_ENDING.length()), LINE_ENDING);  //Returns all keys.
        }
        
        return new String[0];   //Returns zero strings if the ini-file not exists.
    }
    
    /**
     * Returns all key namess in a hive
     *
     * @param   hive    The name of the hive
     * @param   escape  Indicates whether the hivename needs to be escaped
     * @return          All key names in a hive
     */
    public String[] getHiveKeyNames(final String hive, boolean escape)
    {
        String ehive = hive;
        if (escape)
            ehive = Escaper.escape(ehive);
        
        if (this.iniData.filetext.length() > 0)
        {
            String[] lines = getHiveKeys(ehive, false);
            String text = "";
            
            for (int index = 0; index < lines.length; index++) 
                if ((lines[index].length() > 0) && !(lines[index].startsWith(this.iniData.commentNotation)))
                {
                    int len = lines[index].indexOf(this.iniData.keyEnding);
                    len = (len < 0) ? lines[index].length() : len;
                    text += lines[index].substring(0, len) + LINE_ENDING;
                }
            
            if (text.length() > 0)
                return new String[0];   //Returns zero strings if the ini-file is empty.
            
            return Escaper.unescape(split(text.substring(0, text.length() - LINE_ENDING.length()), LINE_ENDING));  //Returns all keys.
        }
        return new String[0];   //Returns zero strings if the ini-file not exists.
    }
    
    /**
     * Reads a key value
     *
     * @param   hive    The name of the hive
     * @param   key     The name of the key
     * @param   escape  Indicates whether the hive- &amp; keyname needs to be escaped
     * @return          The key's value
     */
    public String getKeyValue(final String hive, final String key, boolean escape)
    {
        String uhive = hive, ukey = key;
        if (!escape)
        {
            uhive = Escaper.unescape(uhive);
            ukey = Escaper.unescape(ukey);
        }
        
        String rc = null;
        
        if (this.iniData.treeRoot != null)
            rc = this.iniData.treeRoot.getKey(uhive, ukey);
        
        if (rc == null)
            rc = getKeyValue(getHiveKeys(hive, escape), key, escape);
        
        return rc;
    }
    
    /**
     * Reads a key value
     *
     * @param   hiveKeys  All keys in the hive
     * @param   key       The name of the key
     * @param   escape    Indicates whether the keyname needs to be escaped
     * @return            The key's value
     */
    public String getKeyValue(final String[] hiveKeys, final String key, boolean escape)
    {
        String ekey = key;
        if (escape)
            ekey = Escaper.escape(ekey);
        
        String rc = null;   //Sets 'null' as value if the key don't exists.
        
        for (int index = 0; index < hiveKeys.length; index++)
            if (hiveKeys[index].toLowerCase().startsWith(ekey.toLowerCase() + this.iniData.keyEnding))
            {
                rc = hiveKeys[index].substring(this.iniData.keyEnding.length() + ekey.length(), hiveKeys[index].length());
                if ((this.iniData.lastEntryOverrides) == false)
                    break;
            }
        
        return Escaper.unescape(rc);
    }
    
}

