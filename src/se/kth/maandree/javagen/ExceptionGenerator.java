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
public class ExceptionGenerator
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
	    final String[] stack = new String[1024];
	    int level = 0;
	    boolean kill = false;
	    
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
		    if (word.equals(">"))
		    {
			kill = false;
			level++;
		    }
		    else if (word.equals("<"))
			stack[level--] = null;
		    else if (word.equals("."))
		    {
			if (level > 0)
			{   generate(stack[level], stack[level - 1], words);
			    kill = true;
			}
		    }
		    else if (kill || (stack[level] == null))
		    {
			kill = false;
			stack[level] = word;
		    }
		    else
			words.add(word);
	    }
	    
	    stream.close();
	}
    }
    
    
    public static void generate(final String klass, final String superKlass, final ArrayDeque<String> words) throws Exception
    {
	System.err.println("Starts generating " + klass + " <== " + superKlass);
	
	final String pkg = klass.substring(0, klass.lastIndexOf('.'));
	final String cls = klass.substring(klass.lastIndexOf('.') + 1);
	final String spr = superKlass.replace("$", ".");
	
	final ArrayDeque<String> paramt = new ArrayDeque<String>();
	final ArrayDeque<String> params = new ArrayDeque<String>();
	final ArrayDeque<String> inhert = new ArrayDeque<String>();
	final ArrayDeque<String> props0= new ArrayDeque<String>();
	final ArrayDeque<String> propt1= new ArrayDeque<String>();
	final ArrayDeque<String> props1= new ArrayDeque<String>();
	final ArrayDeque<String> propt2= new ArrayDeque<String>();
	final ArrayDeque<String> props2= new ArrayDeque<String>();
	final ArrayDeque<String> propx2= new ArrayDeque<String>();
	
	boolean prop = false;
	boolean propx = false;
	
	for (;;)
	{
	    final String word = words.pollFirst();
	    if (word == null)
		break;
	    
	    if (word.equals("!"))
	    {   prop = true;
	    }
	    else if (word.equals("!!"))
	    {   propx = true;
	    }
	    else if (prop)
	    {   prop = false;
		final String type = word;
		final String name = words.pollFirst();
		System.err.println("! " + type + " " + name);
		paramt.offerLast(type);
		params.offerLast(name);
		props0.offerLast(name);
		propt1.offerLast(type);
		props1.offerLast(name);
		propt2.offerLast(type);
		props2.offerLast(name);
		propx2.offerLast(name.substring(0, 1).toUpperCase() + name.substring(1));
	    }
	    else if (propx)
	    {   propx = false;
		final String type = word;
		final String name = words.pollFirst();
		final String upnm = words.pollFirst();
		System.err.println("!! " + type + " " + name + " " + upnm);
		paramt.offerLast(type);
		params.offerLast(name);
		props0.offerLast(name);
		propt1.offerLast(type);
		props1.offerLast(name);
		propt2.offerLast(type);
		props2.offerLast(name);
		propx2.offerLast(upnm);
	    }
	    else
	    {   final String type = word;
		final String name = words.pollFirst();
		System.err.println("? " + type + " " + name);
		paramt.offerLast(type);
		params.offerLast(name);
		inhert.offerLast(name);
	    }
	}
	
	String file = output;
	if (file.endsWith("/") == false)
	    file += "/";
	file += pkg.replace(".", "/");
	
	(new File(file)).mkdirs();
	
	file += "/" + cls + ".java";
	
	final StringBuilder buf = new StringBuilder();
	
	
	buf.append("package ");
	buf.append(pkg);
	buf.append(";\n\n");
	
	buf.append("@SuppressWarnings(\"serial\")\n");
	buf.append("public class ");
	buf.append(cls);
	buf.append(" extends ");
	buf.append(spr);
	buf.append("\n{\n");
	
	buf.append("    public ");
	buf.append(cls);
	buf.append("(");
	while (params.isEmpty() == false)
	{
	    buf.append("final ");
	    buf.append(paramt.pollFirst());
	    buf.append(" ");
	    buf.append(params.pollFirst());
	    if (params.isEmpty() == false)
		buf.append(", ");
	}
	buf.append(")\n");
	buf.append("    {   super(");
	while (inhert.isEmpty() == false)
	{
	    buf.append(inhert.pollFirst());
            if (inhert.isEmpty() == false)
                buf.append(", ");
	}
	buf.append(");\n");
	while (props0.isEmpty() == false)
	{
	    final String name = props0.pollFirst();
	    buf.append("        this.");
	    buf.append(name);
	    buf.append(" = ");
	    buf.append(name);
	    buf.append(";\n");
	}
	buf.append("    }\n\n");
	
	while (props1.isEmpty() == false)
	{
	    final String type = propt1.pollFirst();
	    final String name = props1.pollFirst();
	    buf.append("    private final ");
	    buf.append(type);
	    buf.append(" ");
	    buf.append(name);
	    buf.append(";\n");
	    if (props1.isEmpty())
		buf.append("\n\n");
	}
	
	while (props2.isEmpty() == false)
	{
	    final String type = propt2.pollFirst();
	    final String name = props2.pollFirst();
	    buf.append("    public final ");
	    buf.append(type);
	    if (type.equals("boolean") || type.equals("Boolean") || type.equals("java.lang.Boolean"))
		buf.append(" is");
	    else
		buf.append(" get");
	    buf.append(propx2.pollFirst());
	    buf.append("()\n");
	    buf.append("    {   return this.");
	    buf.append(name);
	    buf.append(";\n");
	    buf.append("    }\n");
	    if (props2.isEmpty())
		buf.append("\n");
	}
	
	buf.append("}\n\n");
	
	
	System.err.println("Writting class " + klass + " to " + file);
	final OutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(file)));
	
	stream.write(buf.toString().getBytes("UTF-8"));
	
	stream.flush();
	stream.close();
    }
    
}
