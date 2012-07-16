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
import org.nongnu.paradis.net.*;
import org.nongnu.paradis.net.messages.*;
import org.nongnu.paradis.io.*;
import org.nongnu.paradis.util.*;
import org.nongnu.paradis.*;

import java.util.*;


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
    final String[][] ARGUMENTED = {   {"-S", "--search", "--browse"},
				      {"-F", "--fetch", "--download"},
				      {"-p", "--allow-nonfree", "--allow-proprietary"},
				      {"-f", "--only-free", "--free", "--ignore-proprietary"},
				      {"-r", "--regex"},
                                  };
    
    /**
     * Options without arguments
     */
    final String[][] ARGUMENTLESS = {   {"-i", "--ignore"},
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
	// fetchpkg+fetch  fetchpkg+upload
	
	Blackboard.getInstance(null).registerObserver(new Blackboard.BlackboardObserver()
	        {
		    /**
		     * {@inheritDoc}
		     */
		    @Override
		    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
		    {
			if (message instanceof PacketReceived)
			{
			    final Packet packet = ((PacketReceived)message).packet;
			    if ((PackageServer.this.currentCommand != null) && packet.messageType.equals("fetchpkg+found"))
			    {   if (packet.message.startsWith(PackageServer.this.currentCommand))
				{
				    System.out.print('+');
				    synchronized (PackageServer.this.received)
				    {   PackageServer.this.received.add(packet);
				    }
			    }   }
			    else if (packet.messageType.equals("fetchpkg+search"))
			    {
				//final Options opts = Options.get(null, null, ARGUMENTED, ARGUMENTLESS, `command`); FIXME
			    }
			}
		    }
	        });
    }
    
    
    
    /**
     * Packet factory for this server
     */
    private PacketFactory factory = null;
    
    /**
     * Shows the current command
     */
    private volatile String currentCommand = null;
    
    /**
     * Received package search results
     */
    private final ArrayList<Packet> received = new ArrayList<Packet>();
    
    
    
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
	
	if (this.factory == null)
	{   if (NetworkServer.localUser == null)
	    {
		System.out.println("Please initialise network first: network init");
		return true;
	    }
	    this.factory = new PacketFactory(NetworkServer.localUser, false, false, (short)16); //TODO use configurations
	}
	else if (command.equals("fetchpkg reinit"))
	    this.factory = new PacketFactory(NetworkServer.localUser, false, false, (short)16);
	
	
	final Options opts = Options.get(null, null, ARGUMENTED, ARGUMENTLESS, command.substring("fetchpkg ".length()).split(" "));
	
	if (opts.containsKey("--help"))
	{
	    System.out.println("-S  --search           Search for packages");
	    System.out.println("-F  --fetch            Fetch packages");
	    System.out.println("-p  --allow-nonfree    Allow non-free packages");
	    System.out.println("-f  --only-free       +Allow only free packages (default; peers should warn if not implemented)");
	    System.out.println("-r  --regex            Use regular expression for packages");
	    System.out.println("-i  --ignore           Ignore a package");
	    System.out.println("-c  --category         Search old specified category (if implemented by peer; should warn otherwise)");
	    System.out.println("-h  --peer             Specify allowed peers [not yet implemented]"); //TODO not implemented
	    System.out.println("+h  --ignore-peer      Specify ignored peers [not yet implemented]");
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
	    
	    //FIXME print to stdout and then to pager data from this.received
	    
	    synchronized (this.received)
	    {   this.received.clear();
	    }
	}
	
	return true;
    }
    
}

