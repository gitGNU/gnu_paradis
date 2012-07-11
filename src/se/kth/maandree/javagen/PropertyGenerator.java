/**
 * javagen — Collection of small code generators for Java
 * 
 * Public Domain 2012, by Mattias Andrée (maandree@kth.se)
 */
package se.kth.maandree.javagen;

import java.io.*;
import java.util.*;


/**
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PropertyGenerator
{
    public static String output;
    
    
    public static void main(final String... args) throws Exception
    {
	final ArrayDeque<String> files = new ArrayDeque<String>();
	
	boolean dash = false;
	boolean o = false;
	
	for (final String arg : args)
	    if (o)
	    {   output = arg;
		o = false;
	    }
	    else if (dash)
	    {   files.offerLast(arg);
	    }
	    else if (arg.equals("-o"))
	    {   o = true;
	    }
	    else if (arg.equals("--"))
	    {   dash = true;
	    }
	    else
	    {   files.offerLast(arg);
	    }
	
	for (final String file : files)
	{
	    System.err.println("Starts generating from " + file);
	    
	    final InputStream stream = new BufferedInputStream(new FileInputStream(new File(file)));
	    final Scanner sc = new Scanner(stream);
	    
	    final ArrayDeque<String> words = new ArrayDeque<String>();
	    
	    while (sc.hasNextLine())
	    {
		String line = sc.nextLine().replace("\t", " ");
		
		while (line.contains("  "))   line = line.replace("  ", " ");
		if    (line.startsWith(" "))  line = line.substring(1);		
		if    (line.endsWith(" "))    line = line.substring(0, line.length() - 1);
		
		if (line.startsWith("#"))
		    continue;
		
		if (line.isEmpty())
		    continue;
		
		for (final String word : line.split(" "))
		    if (word.equals("."))
		    {
			final String klass = words.pollFirst();
			final String[] _words = new String[words.size()];
			words.toArray(_words);
			generate(klass, _words);
			words.clear();
		    }
		    else
			words.add(word);
	    }
	    
	    stream.close();
	}
    }
    
    
    public static void generate(final String klass, final String... words) throws Exception
    {
	System.err.println("Starts generating " + klass);
	
	final String pkg = klass.substring(0, klass.lastIndexOf('.'));
	final String cls = klass.substring(klass.lastIndexOf('.') + 1);
	
	final Vector<String> types = new Vector<String>();
	final Vector<String> names = new Vector<String>();
	final Vector<String> props = new Vector<String>();
	
	for (int i = 0, n = words.length; i < n; )
	{
	    final boolean plus = words[i].equals("+");
	    if (plus)
		i++;
	    types.add(words[i++]);
	    final String name = words[i++];
	    names.add(name);
	    if (plus)
		props.add(words[i++]);
	    else
		props.add(name.substring(0, 1).toUpperCase() + name.substring(1));
	}
	
	String file = output;
	if (file.endsWith("/") == false)
	    file += "/";
	file += pkg.replace(".", "/");
	
	(new File(file)).mkdirs();
	
	file += "/" + cls + ".java";
	
	final StringBuilder buf = new StringBuilder();
	
	int namelen = 0;
	for (final String name : names)
	    if (namelen < name.length())
		namelen = name.length();
	final char[] pad = new char[namelen];
	Arrays.fill(pad, ' ');
	
	int n = props.size();
	
	buf.append("package ");
	buf.append(pkg);
	buf.append(";\n\n\npublic class");
	buf.append(cls);
	buf.append("\n{\n");
	buf.append("    /**\n");
	buf.append("     * Constructor\n");
	buf.append("     * \n");
	for (int i = 0; i < n; i++)
	{
	    buf.append("     * @param  ");
	    buf.append(names.get(i));
	    buf.append(new String(pad, 0, namelen - names.get(i).length()));
	    buf.append("  \n");
	}
	buf.append("     */\n");
	buf.append("    public ");
	buf.append(cls);
	buf.append("(");
	for (int i = 0; i < n; i++)
	{
	    if (i > 0)
		buf.append(", ");
	    buf.append("final ");
	    buf.append(types.get(i));
	    buf.append(" ");
	    buf.append(names.get(i));
	}
	buf.append(")\n    {\n");
	for (int i = 0; i < n; i++)
	{
	    buf.append("        this.");
	    buf.append(names.get(i));
	    buf.append(" = ");
	    buf.append(names.get(i));
	    buf.append(";\n");
	}
	buf.append("    }\n\n\n\n");
	for (int i = 0; i < n; i++)
	{
	    buf.append("    /**\n");
	    buf.append("     * \n");
	    buf.append("     */\n");
	    buf.append("    private ");
	    buf.append(types.get(i));
	    buf.append(" ");
	    buf.append(names.get(i));
	    buf.append(";\n\n");
	}
	buf.append("\n\n");
	for (int i = 0; i < n; i++)
	{
	    buf.append("    /**\n");
	    buf.append("     * \n");
	    buf.append("     * \n");
	    buf.append("     * @return  \n");
	    buf.append("     */\n");
	    buf.append("    public ");
	    buf.append(types.get(i));
	    if      (types.get(i).equals("boolean"))            buf.append(" is");
	    else if (types.get(i).equals("Boolean"))            buf.append(" is");
	    else if (types.get(i).equals("java.lang.Boolean"))  buf.append(" is");
	    else
		buf.append(" get");
	    buf.append(props.get(i));
	    buf.append("()\n");
	    buf.append("    {\n");
	    buf.append("        return this.");
	    buf.append(names.get(i));
	    buf.append(";\n");
	    buf.append("    }\n\n");
	    
	    buf.append("    /**\n");
	    buf.append("     * \n");
	    buf.append("     * \n");
	    buf.append("     * @param  value  \n");
	    buf.append("     */\n");
	    buf.append("    public void set");
	    buf.append(props.get(i));
	    buf.append("(final ");
	    buf.append(types.get(i));
	    buf.append(" value)\n");
	    buf.append("    {\n");
	    buf.append("        this.");
	    buf.append(names.get(i));
	    buf.append(" = value;\n");
	    buf.append("    }\n\n");
	}
	buf.append("}\n\n");
	
	
	System.err.println("Writting class " + klass + " to " + file);
	final OutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(file)));
	
	stream.write(buf.toString().getBytes("UTF-8"));
	
	stream.flush();
	stream.close();
    }
    
}
