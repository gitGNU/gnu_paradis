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
package org.nongnu.paradis.servers;
import org.nongnu.paradis.pacman.*;
import org.nongnu.paradis.local.Properties; //Explicit
import org.nongnu.paradis.util.*;
import org.nongnu.paradis.net.*;
import org.nongnu.paradis.net.UUID; //Explicit
import org.nongnu.paradis.net.messages.*;
import org.nongnu.paradis.io.*;
import org.nongnu.paradis.io.PipedInputStream; //Explicit
import org.nongnu.paradis.io.PipedOutputStream; //Explicit
import org.nongnu.paradis.*;

import java.util.*;
import java.io.*;


/**
 * Package sharing server
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PackageServer extends AbstractServer
{
    /**
     * Options with arguments
     */
    static final String[][] ARGUMENTED = {   {"-S", "--search", "--browse"},
                                             {"-F", "--fetch", "--download"},
                                             {"-p", "--allow-nonfree", "--allow-proprietary"},
                                             {"-f", "--only-free", "--free", "--ignore-proprietary"},
                                             {"-r", "--regex"},
                                         };
    
    /**
     * Options without arguments
     */
    static final String[][] ARGUMENTLESS = {   {"-i", "--ignore"},
                                               {"-c", "--category"},
                                               {"-h", "--peer", "--host"},
                                               {"+h", "--ignore-peer", "--ignore-host"},
                                               {"--help"},
                                           };
    
    
    
    /**
     * Constructor
     */
    public PackageServer()
    {
        super(-1 ^ (-1 >>> 1));
        
        TransferProtocolRegister.register(String.class, "fetchpkg+found");
        TransferProtocolRegister.register(String.class, "fetchpkg+search");
        TransferProtocolRegister.register(String.class, "fetchpkg+fetch");
        TransferProtocolRegister.register(byte[].class, "fetchpkg+upload"); //FIXME  receive
        
        Blackboard.getInstance(null).registerObserver(new Blackboard.BlackboardObserver()
                {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    @requires("java-environment>=7")
                    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
                    {   if (NetworkServer.localUser == null)
                            return;
                        
                        if (message instanceof PacketReceived)
                        {
                            final Packet packet = ((PacketReceived)message).packet;
                            if ((PackageServer.this.currentCommand != null) && packet.messageType.equals("fetchpkg+found"))
                            {   if (((String)(packet.message)).startsWith(PackageServer.this.currentCommand))
                                {   synchronized (PackageServer.this.received)
                                    {
                                        System.out.print('+');
                                        PackageServer.this.received.add(packet);
                            }   }   }
                            else if (packet.messageType.equals("fetchpkg+search"))
                            {
                                final String msg = (String)(packet.message);
                                final StringBuilder buf = new StringBuilder();
                                final Options opts = Options.get(null, null, PackageServer.ARGUMENTED, PackageServer.ARGUMENTLESS, msg.split(" "));
                                
                                if (opts.containsKey("+h"))
                                    for (final String ignoredHost : opts.get("+h"))
                                        if (new UUID(ignoredHost).equals(NetworkServer.localUser.getUUID()))
                                            return;
                                
                                try (final PipedInputStream  pis    = new PipedInputStream();
                                     final PipedOutputStream pos    = new PipedOutputStream(pis);
                                     final PrintStream       stream = new PrintStream(pos);
                                     final Scanner           sc     = new Scanner(pis))
                                {
                                    stream.println(msg);
                                    final HashSet<String> options = new HashSet<String>();
                                    if (opts.containsKey("-r"))  options.add(PacmanDatabase.DATABASE_SEARCH);
                                    if (opts.containsKey("-p"))  stream.println("Warning: -p is not implemented");  //TODO not implemented
                                    if (opts.containsKey("-f"))  stream.println("Warning: -f is not implemented");  //TODO not implemented
                                    for (final String unrecognised : opts.unrecognised)
                                        stream.println("Warning: " + unrecognised + " is not recognised");
                                    PacmanDatabase.search(options, opts.files, stream);
                                    
                                    boolean first = true;
                                    while (sc.hasNextLine())
                                    {   final String line = sc.nextLine();
                                        if ((first == false) && (line.startsWith("Warning: ") == false) && opts.containsKey("-i"))
                                        {
                                            boolean ignore = false;
                                            final VersionedPackage pack = new VersionedPackage(line);
                                            for (final String ipack : opts.get("-i"))
                                                if (pack.intersects(new VersionedPackage(ipack)))
                                                {   ignore = true;
                                                    break;
                                                }
                                            if (ignore == false)
                                                { buf.append(line); buf.append("\n"); }
                                        }
                                        else
                                            { buf.append(line); buf.append("\n"); }
                                        first = false;
                                    }
                                }
                                catch (final IOException err) /* but they will not throw IOException */
                                {   err.printStackTrace(System.err);
                                }
                                
                                Blackboard.getInstance(null).broadcastMessage(new SendPacket(PackageServer.this.factory.createUnicast(buf.toString(), "fetchpkg+found", packet.cast.getSender())));
                            }
			    else if (packet.messageType.equals("fetchpkg+fetch"))
			    {
                                final String msg = (String)(packet.message);
                                final ArrayList<String> packs = new ArrayList<String>();
                                final Options opts = Options.get(null, null, PackageServer.ARGUMENTED, PackageServer.ARGUMENTLESS, msg.split(" "));
                                
                                if (opts.containsKey("+h"))
                                    for (final String ignoredHost : opts.get("+h"))
                                        if (new UUID(ignoredHost).equals(NetworkServer.localUser.getUUID()))
                                            return;
                                
                                try (final PipedInputStream  pis    = new PipedInputStream();
                                     final PipedOutputStream pos    = new PipedOutputStream(pis);
                                     final PrintStream       stream = new PrintStream(pos);
                                     final Scanner           sc     = new Scanner(pis))
                                {
                                    final HashSet<String> options = new HashSet<String>();
                                    if (opts.containsKey("-r"))  options.add(PacmanDatabase.DATABASE_SEARCH);
                                    if (opts.containsKey("-p"))  { /*TODO not implemented*/ }
                                    if (opts.containsKey("-f"))  { /*TODO not implemented*/ }
                                    PacmanDatabase.search(options, opts.files, stream);
                                    
                                    while (sc.hasNextLine())
                                    {   final String line = sc.nextLine();
					if (line.startsWith("\t") == false)
					    if (opts.containsKey("-i"))
					    {
						boolean ignore = false;
						final VersionedPackage pack = new VersionedPackage(line);
						for (final String ipack : opts.get("-i"))
						    if (pack.intersects(new VersionedPackage(ipack)))
						    {   ignore = true;
							break;
						    }
						if (ignore == false)
						    packs.add(line);
					    }
					    else
						packs.add(line);
                                    }
                                }
                                catch (final IOException err) /* but they will not throw IOException */
                                {   err.printStackTrace(System.err);
                                }
                                
				packloop:
				    for (final String pack : packs)
				    {
					final String pkg = Pacman.PACKAGE_DIR + pack + "pkg.xz";
					final String tar = Pacman.PACKAGE_DIR + pack + "tar.xz";
					final ArrayList<byte[]> buf = new ArrayList<byte[]>();
					
					buf.add((msg + "\n" + pack + ".pkg.xz\n" + pack + ".tar.xz\n\n").getBytes("UTF-8"));
					int bufsize = buf.get(0).length;
					for (final String file : new String[] { pkg, tar })
					{
					    final int size = (int)((new File(file)).length());
					    if (size != (new File(file)).length())
						continue packloop;
					    final byte[] bs = new byte[8];
					    bs[0] = (byte)((size >>> 24) & 255);
					    bs[1] = (byte)((size >>> 16) & 255);
					    bs[2] = (byte)((size >>>  8) & 255);
					    bs[3] = (byte)( size         & 255);
					    buf.add(bs);
					    bufsize += 4 + size;
					}
					
					for (final String file : new String[] { pkg, tar })
					{
					    final long size = (new File(file)).length();
					    long ptr = 0;
					    final byte[] bs = new byte[size > 4092 ? 4092 : (int)size];
					    final FileInputStream is = new FileInputStream(file);
					    while (ptr < size)
					    {
						final byte[] data = new byte[is.read(bs, 0, bs.length)];
						System.arraycopy(bs, 0, data, 0, data.length);
						ptr += data.length;
					    }
					}
				        
					final byte[] data = new byte[bufsize];
					int ptr = 0;
					for (final byte[] seg : buf)
					{   System.arraycopy(data, 0, seg, ptr, seg.length);
					    ptr += seg.length;
					}
					Blackboard.getInstance(null).broadcastMessage(new SendPacket(PackageServer.this.factory.createUnicast(data, "fetchpkg+upload", packet.cast.getSender())));
				    }
			    }
                    }   }
                });
    }
    
    
    
    /**
     * Packet factory for this server
     */
    PacketFactory factory = null;
    
    /**
     * Shows the current command
     */
    volatile String currentCommand = null;
    
    /**
     * Received package search results
     */
    final ArrayList<Packet> received = new ArrayList<Packet>();
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean invoke(final String command, final boolean consumed, final Scanner scanner)
    {
        if ((command.equals("fetchpkg") || command.startsWith("fetchpkg ")) == false)
            return false;
        if (consumed)
            return true;
	
	
	if (command.equals("fetchpkg stop"))
	{
	    this.currentCommand = null;
	    return true;
	}
        if (this.factory == null)
        {   if (NetworkServer.localUser == null)
            {
                System.out.println("Please initialise network first: network init");
                return true;
            }
            this.factory = new PacketFactory(NetworkServer.localUser, false, false, (short)16); //TODO use configurations
        }
        else if (command.equals("fetchpkg reinit"))
	{
            this.factory = new PacketFactory(NetworkServer.localUser, false, false, (short)16);
	    return true;
	}
        
        
        final Options opts = Options.get(null, null, ARGUMENTED, ARGUMENTLESS, command.substring("fetchpkg ".length()).split(" "));
        
        if (opts.containsKey("--help"))
        {
            System.out.println("'fetchpkg stop' will stop this server");
            System.out.println("-S  --search           Search for packages");
            System.out.println("-F  --fetch            Fetch packages");
            System.out.println("-p  --allow-nonfree    Allow non-free packages");
            System.out.println("-f  --only-free       +Allow only free packages (default; peers should warn if not implemented)");
            System.out.println("-r  --regex            Use regular expression for packages");
            System.out.println("-i  --ignore           Ignore a package");
            System.out.println("-c  --category         Search old specified category (if implemented by peer; should warn otherwise)");
            System.out.println("-h  --peer             Specify allowed peers [not yet implemented]"); //TODO not implemented
            System.out.println("+h  --ignore-peer      Specify ignored peers");
        }
        else if (opts.containsKey("-S") == opts.containsKey("-F"))  System.out.println("Use either --search or --fetch");
        else if (opts.unrecognised.isEmpty() == false)              System.out.println("Unrecognised option: " + opts.unrecognised.get(0));
        else if (opts.containsKey("-S"))
        {
            String cmd = (opts.containsKey("-p") || opts.containsKey("-f")) ? "" : "-f ";
            this.currentCommand = (cmd += command.substring("fetchpkg ".length())) + "\n";
            System.out.println("Waiting for responses, + marks responses, press Enter to stop waiting: ");
            Blackboard.getInstance(null).broadcastMessage(new SendPacket(this.factory.createBroadcast(cmd, "fetchpkg+search")));
            scanner.nextLine();
            this.currentCommand = null;
            
	    final StringBuilder buf = new StringBuilder();
            synchronized (this.received)
            {
		final ArrayList<String> warns = new ArrayList<String>();
		final ArrayList<String> peers = new ArrayList<String>();
		int pi = 0;
		
		final HashMap<String, StringBuilder> packmap = new HashMap<String, StringBuilder>();
		final ArrayList<String> packlist = new ArrayList<String>();
		
		for (final Packet packet : this.received)
		{
		    pi++;
		    peers.add("Peer " + pi + " = " + packet.cast.getSender().toString());
		    for (final String line : ((String)(packet.message)).split("\n"))
			if (line.startsWith("Warning:"))
			    warns.add(pi + ": " + line);
			else if (packmap.containsKey(line))
			    packmap.get(line).append(" " + pi);
			else
			{
			    packmap.put(line, new StringBuilder(Integer.toString(pi)));
			    packlist.add(line);
			}
		}
		
		final String[] packs = new String[packlist.size()];
		packlist.toArray(packs);
		Arrays.sort(packs);
		for (final String pack : packs)
		{
		    buf.append(pack);
		    buf.append(" @ ");
		    buf.append(packmap.get(pack).toString());
		    buf.append("\n");
		}
		
		buf.append("\n");
	        for (final String warn : warns)
		{
		    buf.append(warn);
		    buf.append("\n");
		} 
		buf.append("\n");
		for (final String peer : peers)
		{
		    buf.append(peer);
		    buf.append("\n");
		} 
		
		this.received.clear();
            }
	    
	    String str;
	    System.out.println(str = buf.toString()); /* sic! */
	    Pager.page(Properties.getPager(), "Found packages", str);
        }
	else if (opts.containsKey("-F"))
	{
            String cmd = (opts.containsKey("-p") || opts.containsKey("-f")) ? "" : "-f ";
            this.currentCommand = (cmd += command.substring("fetchpkg ".length())) + "\n";
            Blackboard.getInstance(null).broadcastMessage(new SendPacket(this.factory.createBroadcast(cmd, "fetchpkg+fetch")));
	}
        
        return true;
    }
    
}

